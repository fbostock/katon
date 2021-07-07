package fjdb.images;

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static fjdb.images.PixelMatcher.BEST_MATCH_LIST_SIZE;
import static fjdb.images.PixelMatcher.OUTPUT_CONFIGURATION;

public class MatchingAlgorithms {

    private static final Logger log = LoggerFactory.getLogger(MatchingAlgorithms.class);

    public static MatchingAlgorithm BestForMainImageMatch = new MatchingAlgorithm() {
        @Override
        public MatchResult findMatches(MainImage mainImage, List<CandidateImage> candidateImages) {
            return findMatchesBestForMainImage(mainImage, candidateImages);
        }

        @Override
        public String algoName() {
            return "BestMatchForMainImageTiles";
        }
    };

    public static MatchingAlgorithm MaximumCandidateTileUse = new MatchingAlgorithm() {
        @Override
        public MatchResult findMatches(MainImage mainImage, List<CandidateImage> candidateImages) {
            return findMatchesMaximiseUseOfInputs(mainImage, candidateImages);
        }

        @Override
        public String algoName() {
            return "MaximumCandidateTileUse";
        }
    };

    public static MatchingAlgorithm MaximumCandidateTileUseRemoveAdjacent = new MatchingAlgorithm() {
        @Override
        public MatchResult findMatches(MainImage mainImage, List<CandidateImage> candidateImages) {
            return findMatchesMaximiseUseOfInputsRemoveAdjacents(mainImage, candidateImages);
        }

        @Override
        public String algoName() {
            return "MaximumCandidateTileUseRemoveAdjacent";
        }
    };


    private static MatchResult findMatchesBestForMainImage(MainImage mainImage, List<CandidateImage> candidateImages) {
        //TODO algorithm to construct the mainImage from the input images.

        Map<CandidateImage, List<Integer>> colorVectors = new HashMap<>();
        Map<CandidateImage, AtomicInteger> counts = new HashMap<>();
        for (CandidateImage input : candidateImages) {
            colorVectors.put(input, MainImage.Tile.getColors(input.getTileImage()));
            counts.put(input, new AtomicInteger(0));
        }

        Grid<CandidateImage> bufferedImageGrid = new Grid<>(mainImage.getTilesAcross(), mainImage.getTilesHigh());
        List<MainImage.Tile> tiles = mainImage.getTiles();
        int progress = 0;
        for (MainImage.Tile tile : tiles) {
            BestScore<CandidateImage> bestScore = new BestScore<>(BEST_MATCH_LIST_SIZE);
            List<Integer> colors = tile.getColors();
            BufferedImage bestImage = null;
            double min = Double.MAX_VALUE;
            for (Map.Entry<CandidateImage, List<Integer>> entry : colorVectors.entrySet()) {
                List<Integer> value = entry.getValue();
                double sum = 0;
                for (int i = 0; i < colors.size(); i++) {
                    Integer imageVal = colors.get(i);
                    Integer candidateVal = value.get(i);
                    sum += Math.pow(Math.abs(imageVal - candidateVal), 2);
                }
                bestScore.put(entry.getKey(), sum);
            }
            List<CandidateImage> best = bestScore.getBest();
            List<Integer> countValues = Lists.newArrayList();
            for (CandidateImage bufferedImage : best) {
                countValues.add(counts.get(bufferedImage).get());
            }
            CandidateImage imageToUse = best.get(getLowestIndex(countValues));
            counts.get(imageToUse).incrementAndGet();
            bufferedImageGrid.put(tile.getxImageIndex(), tile.getyImageIndex(), imageToUse);
            progress++;
            if (progress % 100 == 0) {
                log.info("Completed {} of {}", progress, tiles.size());
            }
        }

        printCounts(counts);

        //Here, the width and height we pass in represents the number of pixels to be used for each tile from their original
        //cropped dimensions.
        return new MatchResult(bufferedImageGrid, OUTPUT_CONFIGURATION.getPictureWidth(), OUTPUT_CONFIGURATION.getPictureHeight());
    }


    private static MatchResult findMatchesMaximiseUseOfInputs(MainImage mainImage, List<CandidateImage> inputCandidates) {
        Map<CandidateImage, List<Integer>> colorVectors = new HashMap<>();
        Map<CandidateImage, AtomicInteger> counts = new HashMap<>();
        for (CandidateImage candidateImage : inputCandidates) {
            colorVectors.put(candidateImage, MainImage.Tile.getColors(candidateImage.getTileImage()));
            counts.put(candidateImage, new AtomicInteger(0));

        }

        Grid<CandidateImage> bufferedImageGrid = new Grid<>(mainImage.getTilesAcross(), mainImage.getTilesHigh());
        List<MainImage.Tile> tiles = mainImage.getTiles();
        List<MainImage.Tile> unmatched = Lists.newArrayList(tiles);

        List<CandidateImage> candidates = Lists.newArrayList(inputCandidates);

        double biggest = 0.0;

        boolean progressMade = true;
        int counter = 0;
        int lastUnmatched = unmatched.size();
        while (progressMade && !unmatched.isEmpty()) {
            int change = lastUnmatched - unmatched.size();
            lastUnmatched = unmatched.size();
            int nonnullElements = bufferedImageGrid.getNonnullElements();
            int nulls = tiles.size() - bufferedImageGrid.getNonnullElements();
            log.info("Finding matches for candidates iteration: {} ({} tiles remaining - change {}) non-nulls {} nulls {}", counter++, unmatched.size(), change, nonnullElements, nulls);
            progressMade = false;
            Iterator<CandidateImage> candidatesIterator = candidates.iterator();
            while (candidatesIterator.hasNext()) {
                CandidateImage candidate = candidatesIterator.next();
                List<Integer> candidateValues = colorVectors.get(candidate);
                BestScore<MainImage.Tile> bestScore = new BestScore<>(1);


                for (MainImage.Tile tile : unmatched) {
                    List<Integer> colors = tile.getColors();
                    double sum = 0.0;
                    for (int i = 0; i < colors.size(); i++) {
                        Integer imageVal = colors.get(i);
                        Integer candidateVal = candidateValues.get(i);
                        sum += Math.pow(Math.abs(imageVal - candidateVal), 2);
                    }
                    bestScore.put(tile, sum);
                }

                Map.Entry<MainImage.Tile, Double> entry = bestScore.getBestScores().entrySet().iterator().next();
                Double candidateValue = entry.getValue();
                Double threshold = counts.get(candidate).get() < 2 ? 1224402.0 : 1000000;
                if (candidateValue < threshold) {
                    MainImage.Tile key = entry.getKey();
                    if (candidate == null) {
                        throw new RuntimeException();
                    }
                    CandidateImage candidateImage = bufferedImageGrid.get(key.getxImageIndex(), key.getyImageIndex());
                    if (candidateImage != null) {
                        System.out.println("This should be null!!!!");
                    }
                    bufferedImageGrid.put(key.getxImageIndex(), key.getyImageIndex(), candidate);
                    counts.get(candidate).incrementAndGet();
                    int prevSize = unmatched.size();
                    unmatched.remove(key);
                    if (prevSize - 1 != unmatched.size()) {
                        System.out.println("!!");
                    }
                    if (bufferedImageGrid.get(key.getxImageIndex(), key.getyImageIndex()) == null) {
                        System.out.println("!!??");
                    }
                    progressMade = true;
                } else {
                    //no suitable matches found for candidate so remove for future iterations.
//TODO if counts is one for this candidate, what was the best match?
                    int i = counts.get(candidate).get();
                    if (i == 1) {
                        log.info("Next best match for {} was {}", candidate, candidateValue);
                        if (candidateValue > biggest) {
                            biggest = candidateValue;
                        }
                    }
                    candidatesIterator.remove();
                }

            }

        }

        log.info("Threshold for second matches could be {}", biggest);
        if (!unmatched.isEmpty()) {
            log.info("Still to find matches for {} unmatched tiles", unmatched.size());
            for (MainImage.Tile tile : unmatched) {
                BestScore<CandidateImage> bestScore = new BestScore<>(1);
                List<Integer> colors = tile.getColors();
                for (Map.Entry<CandidateImage, List<Integer>> entry : colorVectors.entrySet()) {
                    List<Integer> value = entry.getValue();
                    double sum = 0;
                    for (int i = 0; i < colors.size(); i++) {
                        Integer imageVal = colors.get(i);
                        Integer candidateVal = value.get(i);
                        sum += Math.pow(Math.abs(imageVal - candidateVal), 2);
                    }
                    bestScore.put(entry.getKey(), sum);

                }
                CandidateImage best = bestScore.getBest().get(0);
                bufferedImageGrid.put(tile.getxImageIndex(), tile.getyImageIndex(), best);
                counts.get(best).incrementAndGet();
            }
        }

        int width = bufferedImageGrid.getWidth();
        int height = bufferedImageGrid.getHeight();
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                CandidateImage candidateImage = bufferedImageGrid.get(i, j);
                if (candidateImage == null) {
                    System.out.println("AARRGGHH");
                }
            }
        }

        /*
        iterate through the candidates. For each candidate, find the best match in the main image. If that match is under a million,
        mark that as accepted. Move on to next candidate.
        After one iteration, repeat,
         */

        log.info("Print matching summary");
//        List<CandidateImage> collect = Lists.newArrayList(inputCandidates);
//        collect.sort(Comparator.comparingInt(o -> counts.get(o).get()));
//        for (CandidateImage candidate : collect) {
//            log.info("File {} used {} times", candidate.getInputFile(), counts.get(candidate).get());
//        }

        printCounts(counts);

        for (int y = 0; y < bufferedImageGrid.getHeight(); y++) {
            for (int x = 0; x < bufferedImageGrid.getWidth(); x++) {
                CandidateImage candidateImage = bufferedImageGrid.get(x, y);
                if (counts.get(candidateImage).get()==1) {
                    log.info("Single use of {} at {}, {} (x, y)", candidateImage, x, y);
                }
            }
        }
        return new MatchResult(bufferedImageGrid, OUTPUT_CONFIGURATION.getPictureWidth(), OUTPUT_CONFIGURATION.getPictureHeight());
    }

    /**
     * This algorithm is like findMatchesMaximiseUseOfInputs but attempts to remove adjacent tiles which are the same.
     * However, this creates a chequered pattern as a result, and doesn't really do anything to improve the use of tiles, so
     * I don't think it's worth pursuing.
     */
    private static MatchResult findMatchesMaximiseUseOfInputsRemoveAdjacents(MainImage mainImage, List<CandidateImage> inputCandidates) {
        Map<CandidateImage, List<Integer>> colorVectors = new HashMap<>();
        Map<CandidateImage, AtomicInteger> counts = new HashMap<>();
        for (CandidateImage candidateImage : inputCandidates) {
            colorVectors.put(candidateImage, MainImage.Tile.getColors(candidateImage.getTileImage()));
            counts.put(candidateImage, new AtomicInteger(0));

        }

        Grid<CandidateImage> bufferedImageGrid = new Grid<>(mainImage.getTilesAcross(), mainImage.getTilesHigh());
        List<MainImage.Tile> tiles = mainImage.getTiles();
        List<MainImage.Tile> unmatched = Lists.newArrayList(tiles);

        List<CandidateImage> candidates = Lists.newArrayList(inputCandidates);


        boolean progressMade = true;
        int counter = 0;
        int lastUnmatched = unmatched.size();
        while (progressMade && !unmatched.isEmpty()) {
            int change = lastUnmatched - unmatched.size();
            lastUnmatched = unmatched.size();
            int nonnullElements = bufferedImageGrid.getNonnullElements();
            int nulls = tiles.size() - bufferedImageGrid.getNonnullElements();
            log.info("Finding matches for candidates iteration: {} ({} tiles remaining - change {}) non-nulls {} nulls {}", counter++, unmatched.size(), change, nonnullElements, nulls);
            progressMade = false;
            Iterator<CandidateImage> candidatesIterator = candidates.iterator();
            while (candidatesIterator.hasNext()) {
                CandidateImage candidate = candidatesIterator.next();
                List<Integer> candidateValues = colorVectors.get(candidate);
                BestScore<MainImage.Tile> bestScore = new BestScore<>(1);


                for (MainImage.Tile tile : unmatched) {
                    List<Integer> colors = tile.getColors();
                    double sum = 0.0;
                    for (int i = 0; i < colors.size(); i++) {
                        Integer imageVal = colors.get(i);
                        Integer candidateVal = candidateValues.get(i);
                        sum += Math.pow(Math.abs(imageVal - candidateVal), 2);
                    }
                    bestScore.put(tile, sum);
                }

                Map.Entry<MainImage.Tile, Double> entry = bestScore.getBestScores().entrySet().iterator().next();
                if (entry.getValue() < 1000000) {
                    MainImage.Tile key = entry.getKey();
                    if (candidate == null) {
                        throw new RuntimeException();
                    }
                    CandidateImage candidateImage = bufferedImageGrid.get(key.getxImageIndex(), key.getyImageIndex());
                    if (candidateImage != null) {
                        System.out.println("This should be null!!!!");
                    }
                    bufferedImageGrid.put(key.getxImageIndex(), key.getyImageIndex(), candidate);
                    counts.get(candidate).incrementAndGet();
                    int prevSize = unmatched.size();
                    unmatched.remove(key);
                    if (prevSize - 1 != unmatched.size()) {
                        System.out.println("!!");
                    }
                    if (bufferedImageGrid.get(key.getxImageIndex(), key.getyImageIndex()) == null) {
                        System.out.println("!!??");
                    }
                    progressMade = true;
                } else {
                    //no suitable matches found for candidate so remove for future iterations.
                    candidatesIterator.remove();
                }

            }

        }

        Grid<MainImage.Tile> tileGrid = mainImage.getTileGrid();
        for (int y = 0; y < tileGrid.getHeight(); y++) {
            for (int x = 0; x < tileGrid.getWidth(); x++) {
                MainImage.Tile tile = tileGrid.get(x, y);
                CandidateImage candidateImage = bufferedImageGrid.get(x, y);
                Set<CandidateImage> candidatesAroundTile = new HashSet<>();
                CandidateImage left = x > 0 ? bufferedImageGrid.get(x - 1, y) : null;
                CandidateImage above = y > 0 ? bufferedImageGrid.get(x, y - 1) : null;
                candidatesAroundTile.add(left);
                candidatesAroundTile.add(above);
                candidatesAroundTile.remove(null);
                if (candidateImage == null || candidatesAroundTile.contains(candidateImage)) {

                    BestScore<CandidateImage> bestScore = new BestScore<>(3);
//                    for (CandidateImage image : candidatesAroundTile) {
//                        bestScore.put(image, 0.0);
//                    }
                    List<Integer> colors = tile.getColors();
                    for (Map.Entry<CandidateImage, List<Integer>> entry : colorVectors.entrySet()) {
                        List<Integer> value = entry.getValue();
                        double sum = 0;
                        for (int i = 0; i < colors.size(); i++) {
                            Integer imageVal = colors.get(i);
                            Integer candidateVal = value.get(i);
                            sum += Math.pow(Math.abs(imageVal - candidateVal), 2);
                        }
                        bestScore.put(entry.getKey(), sum);

                    }
                    List<CandidateImage> best = bestScore.getBest();
                    best.removeAll(candidatesAroundTile);
                    CandidateImage newCandidate = best.get(0);
                    bufferedImageGrid.put(tile.getxImageIndex(), tile.getyImageIndex(), newCandidate);
                    counts.get(newCandidate).incrementAndGet();
                    if (candidateImage != null) {
                        counts.get(candidateImage).decrementAndGet();

                    }
                }


            }
        }


        int width = bufferedImageGrid.getWidth();
        int height = bufferedImageGrid.getHeight();
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                CandidateImage candidateImage = bufferedImageGrid.get(i, j);
                if (candidateImage == null) {
                    System.out.println("AARRGGHH");
                }
            }
        }

        log.info("Print matching summary");
        List<CandidateImage> collect = Lists.newArrayList(inputCandidates);
        collect.sort(Comparator.comparingInt(o -> counts.get(o).get()));
        for (CandidateImage candidate : collect) {
            log.info("File {} used {} times", candidate.getInputFile(), counts.get(candidate).get());
        }

        printCounts(counts);

        return new MatchResult(bufferedImageGrid, OUTPUT_CONFIGURATION.getPictureWidth(), OUTPUT_CONFIGURATION.getPictureHeight());
    }


    private static void printCounts(Map<CandidateImage, AtomicInteger> counts) {
        log.info("Print matching summary");
        HashSet<CandidateImage> keys = new HashSet<>(counts.keySet());
        List<CandidateImage> list = keys.stream().sorted(Comparator.comparingInt(o -> counts.get(o).get())).collect(Collectors.toList());

        for (CandidateImage candidate : list) {
            log.info("{}: {}", candidate.getInputFile(), counts.get(candidate));
        }

        int count = (int) counts.keySet().stream().filter(c -> counts.get(c).get() > 0).count();
        log.info("FILES USED: {} out of {}", count, counts.size());
    }


    public static class BestScore<T> {
        private int size;
        private final TreeMap<Double, T> doubleTTreeMap;

        public BestScore(int size) {
            this.size = size;
            doubleTTreeMap = new TreeMap<>();
        }

        public void put(T value, double score) {
            doubleTTreeMap.put(score, value);
        }

        public List<T> getBest() {
//            NavigableSet<Double> doubles = doubleTTreeMap.navigableKeySet();
            List<T> best = Lists.newArrayList();
            while (best.size() < size) {
                Map.Entry<Double, T> doubleTEntry = doubleTTreeMap.pollFirstEntry();

//                Double aDouble = doubles.pollFirst();
                best.add(doubleTEntry.getValue());
            }
            return best;
        }

        public LinkedHashMap<T, Double> getBestScores() {
            LinkedHashMap<T, Double> map = new LinkedHashMap<>();

            while (map.size() < size) {
                Map.Entry<Double, T> doubleTEntry = doubleTTreeMap.pollFirstEntry();
                map.put(doubleTEntry.getValue(), doubleTEntry.getKey());
            }
            return map;
        }

        //TODO remove this method. The get best method modifies the tree. Alternatively, iterate through the tree above
        //rather than poll(modifying) it.
        public LinkedHashMap<T, Double> getAllBestScores() {
            LinkedHashMap<T, Double> map = new LinkedHashMap<>();

            for (Map.Entry<Double, T> entry : doubleTTreeMap.entrySet()) {
                map.put(entry.getValue(), entry.getKey());

            }
            return map;
        }
    }

    private static int getLowestIndex(List<Integer> values) {
        int lowestIndex = -1;
        int lowest = Integer.MAX_VALUE;
        for (int i = 0; i < values.size(); i++) {
            Integer integer = values.get(i);
            if (integer < lowest) {
                lowest = integer;
                lowestIndex = i;
            }
        }
        return lowestIndex;
    }

}
