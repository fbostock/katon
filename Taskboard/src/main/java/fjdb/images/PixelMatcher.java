package fjdb.images;

import com.google.common.collect.Lists;
import fjdb.threading.Threading;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.statistics.HistogramDataset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Target picture broken down into 10 by 10 pixel tiles
 * Input pics cropped to 100 by 100 pixel tiles.
 * <p>
 * Convert input pics to 10 by 10 pixels images, (try simple color average at first), then compare to target picture tiles,
 * find best matches (somehow?), then the final output will be the unconverted input tile.
 * <p>
 * e.g., say target picture is 100 by 200 - this becomes 10 by 20 tiles.
 * We find matches from input pics, to get 200 matching tiles.
 * the final output is the 200 pictures corresponding to those 200 tiles, giving a 1000 by 2000 pixel picture.
 */
public class PixelMatcher {

    private static final Logger log = LoggerFactory.getLogger(PixelMatcher.class);

    //            private static final TileConfiguration TILE_CONFIGURATION = new TileConfiguration(100, 100, 10, 10);
//    public static final TileConfiguration TILE_CONFIGURATION = new TileConfiguration(42, 28, 10, 10);
//    public static final TileConfiguration TILE_CONFIGURATION = new TileConfiguration(60, 40, 10, 10);
        private static final TileConfiguration TILE_CONFIGURATION = new TileConfiguration(84, 56, 10, 10);
//    public static final TileConfiguration OUTPUT_CONFIGURATION = new TileConfiguration(168, 112, -1, -1);
//    public static final TileConfiguration OUTPUT_CONFIGURATION = new TileConfiguration(336, 224, -1, -1);
    public static final TileConfiguration OUTPUT_CONFIGURATION = new TileConfiguration(504, 336, -1, -1);
//    public static final TileConfiguration OUTPUT_CONFIGURATION = new TileConfiguration(278, 196, -1, -1);
    public static final Integer BEST_MATCH_LIST_SIZE = 10;

    //File names best10_84x56_to_168x112 means when finding matches, the least used of the best 10 is used. Main image broken down into
    //tiles of 84by56 to find matches, and matching images for each tile are drawn to a scale 168x112 (resulting in 4fold increase in output
    //image size relative to initial).
//TODO try 672 by 448 for 84 56 tiles.
    public static void main(String[] args) {

//        BufferedImage bufferedImage = new BufferedImage(70 * 336, 70 * 224, BufferedImage.TYPE_3BYTE_BGR);
//        BufferedImage bufferedImage = new BufferedImage(101 * 300, 101 * 224, BufferedImage.TYPE_3BYTE_BGR);
//        BufferedImage bufferedImage = new BufferedImage(50 * 504, 50 * 336, BufferedImage.TYPE_3BYTE_BGR);
//        if (true) return;
        //TODO play around with best list - possibly up to all the images, as it would be nice to have a high variety.
        /*TODO ideally, I want all the pictures to be included, even if some are included multiple times while others
        are only used once.
        Thoughts:
        1 require each picture to be used, before allowing second uses. Would result in lots of bad matches
        2 for each input, find the best match in the main image, and apply that. Once all have been applied, repeat for the second
        pass, and so on (would need to practise on a low tile number first)
        3 like 2, but have some threshold for a match, e.g. for a given input we find the best match, but if that match is
        not good enough, we skip. Practise on a low tile number first.

        After a first passing using maximumInputs algorithm, iterate through the Main image tiles. For each one if the left (x-1)
        or above (y-1) candidate tiles is the same, then try the next best candidate which is neither left or above. This would attempt
        to remove adjacent candidates.

        Try with CJ's photos as well.


         */
        //get main image

        //get input images.

        //crop input images to 100 by 100 pixels.

        //convert input images to 10 by 10 pixels.

        //break up main target image into 10 by 10 pixel tiles.

        //find best matches.

        //take matching 100 by 100 pixel pics, and put them together.


        /*
        TODO
            when trying to use as many inputs as possible, perhaps we have different matching thresholds. e.g. For the first match, less than 1000000. But
            for the second match, less than 1200000.
            So, for the files used just once, we need to identify what the next best match is.

4200 by 2800
tiles of 42 by 28?
         */

        File folder = new File("C:\\Users\\franc\\PixelImages\\Anniversary");

        List<File> inputFiles = getInputFiles(folder);
//        MatchingAlgorithm matchingAlgorithm = MatchingAlgorithms.BestForMainImageMatch;
        MatchingAlgorithm matchingAlgorithm = MatchingAlgorithms.MaximumCandidateTileUse;
//        MatchingAlgorithm matchingAlgorithm = MatchingAlgorithms.MaximumCandidateTileUseRemoveAdjacent;

        File mainFile = new File(folder, "Kate & Frankie Bostock - Sat 6th August 2011_164.jpg");
        MainImage mainImage = new MainImage(mainFile, TILE_CONFIGURATION.getPictureWidth(), TILE_CONFIGURATION.getPictureHeight());

        List<CandidateImage> inputSubImages = Lists.newArrayList();

        long startTime = System.nanoTime();
        inputSubImages.addAll(getSubImages(inputFiles, TILE_CONFIGURATION));
        log.info("Time to create subimages from {} files: {} ms", inputFiles.size(), (System.nanoTime() - startTime) / (1000 * 1000));

//        inputSubImages.addAll(getSquareSubImages(inputFiles, TILE_CONFIGURATION));

        Map<BufferedImage, CandidateImage> smallToOriginal = new HashMap<>();
        List<BufferedImage> smallTiles = Lists.newArrayList();
        for (CandidateImage candidate : inputSubImages) {
            //resize the input images from 100 by 100 to 10 by 10
//            BufferedImage resize = PixelImageUtils.resize(bufferedImage.getOriginal(), TILE_CONFIGURATION.tileWidth(), TILE_CONFIGURATION.tileHeight());
            BufferedImage resized = candidate.getTileImage();
            smallTiles.add(resized);
            smallToOriginal.put(resized, candidate);
        }

//        startTime = System.nanoTime();
//        testImageMatches(mainImage, smallToOriginal);
//        log.info("Time to find best matches for each candidate: {} ms", (System.nanoTime() - startTime) / (1000 * 1000));

//        if (true) return;

        MatchResult result = matchingAlgorithm.findMatches(mainImage, inputSubImages);
        BufferedImage finalResult = result.makeImage();
        log.info("About to write image {} MB", Runtime.getRuntime().totalMemory() / (1024 * 1024));
//        System.out.println("About to write image " + Runtime.getRuntime().totalMemory() / (1024 * 1024) + " MB");#
        try {
            boolean written = ImageIO.write(finalResult, "jpg", new File(folder,
//                    String.format("%s_CJ_2_best%s_%sx%s_to_%sx%s.jpg", matchingAlgorithm.algoName(), BEST_MATCH_LIST_SIZE, TILE_CONFIGURATION.getPictureWidth(), TILE_CONFIGURATION.getPictureHeight(), OUTPUT_CONFIGURATION.getPictureWidth(), OUTPUT_CONFIGURATION.getPictureHeight())));
                    String.format("%s_best%s_%sx%s_to_%sx%s_%sby%stiles.jpg", matchingAlgorithm.algoName(), BEST_MATCH_LIST_SIZE, TILE_CONFIGURATION.getPictureWidth(), TILE_CONFIGURATION.getPictureHeight(), OUTPUT_CONFIGURATION.getPictureWidth(), OUTPUT_CONFIGURATION.getPictureHeight(), result.getImageGrid().getWidth(), result.getImageGrid().getHeight())));
        } catch (IOException e) {
            e.printStackTrace();
        }

        Grid<CandidateImage> imageGrid = result.getImageGrid();
        File used = new File(folder, "used");

        Set<CandidateImage> unique = new HashSet<>();
        imageGrid.forEach(unique::add);
        for (CandidateImage image : unique) {
            File inputFile = image.getInputFile();
            File copyFile = new File(used, inputFile.getName());
            try {
                if (!copyFile.exists()) {
                    Files.copy(inputFile.toPath(), copyFile.toPath());
                }
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }

    }


    private static Collection<? extends BufferedImage> getSquareSubImages(List<File> inputFiles, TileConfiguration tileConfiguration) {
        //TODO this should return 100 by 100 pixel images.
        List<BufferedImage> inputSubImages = Lists.newArrayList();
        for (File inputFile : inputFiles) {
            //TODO crop input file to 100 by 100 pixels
            try {
//                BufferedImage bufferedImage = PixelImageUtils.cropSquare(100, inputFile);
                BufferedImage bufferedImage = PixelImageUtils.cropFixRatio(tileConfiguration.getPictureWidth(), tileConfiguration.getPictureHeight(), ImageIO.read(inputFile));
                bufferedImage = PixelImageUtils.resize(bufferedImage, tileConfiguration.getPictureWidth(), tileConfiguration.getPictureHeight());
                inputSubImages.add(bufferedImage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return inputSubImages;
    }

    private static Collection<CandidateImage> getSubImages(List<File> inputFiles, TileConfiguration tileConfiguration) {
        //TODO check input files for valid candidates. For starters, we will use landscapes (later, could try including portraits where
        //we effectively use the top half or so.

        Map<File, CandidateImage> fileMap = new ConcurrentHashMap<>();
        List<CandidateImage> inputSubImages = Lists.newArrayList();
        List<Runnable> runnables = Lists.newArrayList();
        for (File inputFile : inputFiles) {
            runnables.add(() -> {
                try {
                    BufferedImage read = ImageIO.read(inputFile);

                    int widthOrig = read.getWidth();
                    int heightOrig = read.getHeight();
                    int width = read.getWidth();
                    int height = read.getHeight();
                    boolean requireTopHalf = false;
                    if (height > width) {//portrait - try taking the top half of the picture
                        read = read.getSubimage(0, 0, width, height / 2);
                        width = read.getWidth();
                        height = read.getHeight();
                        requireTopHalf = true;
                    }
                    if (width > height) {//checking for landscapes
                        boolean finalRequireTopHalf = requireTopHalf;
                        ImageMaker imageMaker = new ImageMaker() {
                            @Override
                            public BufferedImage makeImage(File file) {
                                BufferedImage read = null;
                                try {
                                    read = ImageIO.read(file);
                                    if (finalRequireTopHalf) {
                                        read = read.getSubimage(0, 0, read.getWidth(), read.getHeight() / 2);
                                    }
                                    BufferedImage bufferedImage = PixelImageUtils.cropFixRatio(tileConfiguration.getPictureWidth(), tileConfiguration.getPictureHeight(), read);
                                    return bufferedImage;
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                return null;
                            }
                        };
                        //TODO crop image to a fixed multiple of the tile configuration x/y sizes
                        BufferedImage bufferedImage = PixelImageUtils.cropFixRatio(tileConfiguration.getPictureWidth(), tileConfiguration.getPictureHeight(), read);
//                        BufferedImage resizedBufferedImage = PixelImageUtils.resize(bufferedImage, tileConfiguration.getPictureWidth(), tileConfiguration.getPictureHeight());
                        BufferedImage resizedBufferedImage = PixelImageUtils.resize(bufferedImage, tileConfiguration.tileWidth(), tileConfiguration.tileHeight());
                        fileMap.put(inputFile, new CandidateImage(inputFile, resizedBufferedImage, imageMaker));

                    } else {
//                        fileMap.put(inputFile, null);
                        System.out.println("File not used" + inputFile);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
        Threading.run(runnables);
        for (File inputFile : inputFiles) {

            CandidateImage candidate = fileMap.get(inputFile);
            if (candidate == null) {
                System.out.println(inputFile);
            } else {
                inputSubImages.add(candidate);
            }
        }

        return inputSubImages;
    }

    interface ImageMaker {
        BufferedImage makeImage(File inputFile);
    }


    private static void testImageMatches(MainImage mainImage, Map<BufferedImage, CandidateImage> smallToOriginal) {
        Map<CandidateImage, List<Integer>> colorVectors = new HashMap<>();
        Map<CandidateImage, Map.Entry<MainImage.Tile, Double>> matches = new HashMap<>();
        Map<BufferedImage, AtomicInteger> counts = new HashMap<>();
        for (Map.Entry<BufferedImage, CandidateImage> entry : smallToOriginal.entrySet()) {
            colorVectors.put(entry.getValue(), MainImage.Tile.getColors(entry.getKey()));

        }

//        Grid<CandidateImage> bufferedImageGrid = new Grid<>(mainImage.getTilesAcross(), mainImage.getTilesHigh());
        List<MainImage.Tile> tiles = mainImage.getTiles();
//        ArrayList<Double> values = Lists.newArrayList();

        //TODO iterate over candidates, and for each, for the best match in the main image.
        for (Map.Entry<CandidateImage, List<Integer>> entry : colorVectors.entrySet()) {
            CandidateImage key = entry.getKey();
            List<Integer> candidateColors = entry.getValue();
//            BufferedImage bestImage = null;
//            double min = Double.MAX_VALUE;
            MatchingAlgorithms.BestScore<MainImage.Tile> bestScore = new MatchingAlgorithms.BestScore<>(1);

            for (MainImage.Tile tile : tiles) {
                Double sum = 0.0;
                List<Integer> colors = tile.getColors();
                for (int i = 0; i < colors.size(); i++) {
                    Integer imageVal = colors.get(i);
                    Integer candidateVal = candidateColors.get(i);
                    sum += Math.pow(Math.abs(imageVal - candidateVal), 2);
                }
                bestScore.put(tile, sum);

            }

//            values.addAll(bestScore.getAllBestScores().values());
            LinkedHashMap<MainImage.Tile, Double> bestScores = bestScore.getBestScores();
            Map.Entry<MainImage.Tile, Double> bestTileScore = bestScores.entrySet().iterator().next();
            matches.put(key, bestTileScore);
        }

//        for (Map.Entry<BufferedImage, CandidateImage> entry : smallToOriginal.entrySet()) {
//            BufferedImage key = entry.getKey();
//
//        }

        Set<CandidateImage> candidateImages = matches.keySet();
        List<CandidateImage> collect = candidateImages.stream().sorted(Comparator.comparing(CandidateImage::getInputFile)).collect(Collectors.toList());
        for (CandidateImage candidateImage : collect) {
            Map.Entry<MainImage.Tile, Double> score = matches.get(candidateImage);
            log.info("Candidate {} (x,y) ({},{}): {}", candidateImage.getInputFile(), score.getKey().getxImageIndex(), score.getKey().getxImageIndex(), score.getValue());
        }

        //TODO for each candidate, perhaps take the min, max, average comparisons.
        //Then, for all candidates, find the best match, the worse, the average or anything else that may help to get a feel for the data.

        double min = Double.MAX_VALUE;
        CandidateImage minCandidate = null;
        CandidateImage maxCandidate = null;
        double max = 0.0;
        ArrayList<Double> bestValues = Lists.newArrayList();


        int subMillion = 0;
        for (Map.Entry<CandidateImage, Map.Entry<MainImage.Tile, Double>> entry : matches.entrySet()) {
            Double value = entry.getValue().getValue();
            if (value < 1000000) subMillion++;
            bestValues.add(value);
            if (value < min) {
                min = value;
                minCandidate = entry.getKey();
            }
            if (value > max) {
                max = value;
                maxCandidate = entry.getKey();
            }
        }

        log.info("Min {}: {}. Max {} : {}. Below 1m: {}", minCandidate, min, maxCandidate, max, subMillion);


        HistogramDataset histogramDataset = new HistogramDataset();
//        HistogramDataset allDatahistogram = new HistogramDataset();


        double[] bestData = new double[bestValues.size()];
        for (int i = 0; i < bestValues.size(); i++) {
            Double aDouble = bestValues.get(i);
            bestData[i] = aDouble;
        }

        histogramDataset.addSeries("tiles", bestData, 40);


        JFreeChart histogram = ChartFactory.createHistogram("Best values", "", "count", histogramDataset, PlotOrientation.VERTICAL, false, false, false);
        ChartPanel chartPanel = new ChartPanel(histogram);
//        double[] allData = new double[values.size()];
//        for (int i = 0; i < values.size(); i++) {
//            Double aDouble = values.get(i);
//            allData[i] = aDouble;
//        }
//        allDatahistogram.addSeries("tiles", allData, 200);
//        ChartPanel chartPanel2 = new ChartPanel(ChartFactory.createHistogram("All values", "", "count", allDatahistogram, PlotOrientation.VERTICAL, false, false, false));

        JFrame frame = new JFrame("");
        frame.setPreferredSize(new Dimension(500, 500));
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        JTabbedPane tabs = new JTabbedPane();
        JPanel panel = new JPanel();
        panel.add(chartPanel);
        tabs.addTab("Best", panel);
        JPanel panel2 = new JPanel();
//        panel2.add(chartPanel2);
        tabs.addTab("All", panel2);
        frame.add(tabs);
        frame.pack();
        frame.setVisible(true);
        /*
        A cut off of 1000,000 (10^7) would capture
         */
    }

    //    private static MatchResult findMatches2(MainImage mainImage, Map<BufferedImage, CandidateImage> smallToOriginal) {
//    private static MatchResult findMatches2(MainImage mainImage, List<CandidateImage> inputCandidates) {
//        Map<CandidateImage, List<Integer>> colorVectors = new HashMap<>();
//        Map<CandidateImage, AtomicInteger> counts = new HashMap<>();
//        for (CandidateImage candidateImage : inputCandidates) {
//            colorVectors.put(candidateImage, MainImage.Tile.getColors(candidateImage.getTileImage()));
//            counts.put(candidateImage, new AtomicInteger(0));
//
//        }
////        for (Map.Entry<BufferedImage, CandidateImage> entry : smallToOriginal.entrySet()) {
////        }
//
//
////        for (BufferedImage input : smallToOriginal.keySet()) {
////            colorVectors.put(input, MainImage.Tile.getColors(input));
////            counts.put(input, new AtomicInteger(0));
////        }
//
//        Grid<CandidateImage> bufferedImageGrid = new Grid<>(mainImage.getTilesAcross(), mainImage.getTilesHigh());
//        List<MainImage.Tile> tiles = mainImage.getTiles();
//        List<MainImage.Tile> unmatched = Lists.newArrayList(tiles);
//
////        Collection<CandidateImage> candidatesAll = colorVectors.keySet();
//        List<CandidateImage> candidates = Lists.newArrayList(inputCandidates);
//
//
//        boolean progressMade = true;
//        int counter = 0;
//        int lastUnmatched = unmatched.size();
//        while (progressMade && !unmatched.isEmpty()) {
//            int change = lastUnmatched - unmatched.size();
//            lastUnmatched = unmatched.size();
//            int nonnullElements = bufferedImageGrid.getNonnullElements();
//            int nulls = tiles.size() - bufferedImageGrid.getNonnullElements();
//            log.info("Finding matches for candidates iteration: {} ({} tiles remaining - change {}) non-nulls {} nulls {}", counter++, unmatched.size(), change, nonnullElements, nulls);
//            progressMade = false;
//            Iterator<CandidateImage> candidatesIterator = candidates.iterator();
//            while (candidatesIterator.hasNext()) {
//                CandidateImage candidate = candidatesIterator.next();
//                List<Integer> candidateValues = colorVectors.get(candidate);
//                BestScore<MainImage.Tile> bestScore = new BestScore<>(1);
//
//
//                for (MainImage.Tile tile : unmatched) {
//                    List<Integer> colors = tile.getColors();
//                    double sum = 0.0;
//                    for (int i = 0; i < colors.size(); i++) {
//                        Integer imageVal = colors.get(i);
//                        Integer candidateVal = candidateValues.get(i);
//                        sum += Math.pow(Math.abs(imageVal - candidateVal), 2);
//                    }
//                    bestScore.put(tile, sum);
//                }
//
//                Map.Entry<MainImage.Tile, Double> entry = bestScore.getBestScores().entrySet().iterator().next();
//                if (entry.getValue() < 1000000) {
//                    MainImage.Tile key = entry.getKey();
//                    if (candidate == null) {
//                        throw new RuntimeException();
//                    }
//                    CandidateImage candidateImage = bufferedImageGrid.get(key.getxImageIndex(), key.getyImageIndex());
//                    if (candidateImage != null) {
//                        System.out.println("This should be null!!!!");
//                    }
//                    bufferedImageGrid.put(key.getxImageIndex(), key.getyImageIndex(), candidate);
//                    counts.get(candidate).incrementAndGet();
//                    int prevSize = unmatched.size();
//                    unmatched.remove(key);
//                    if (prevSize - 1 != unmatched.size()) {
//                        System.out.println("!!");
//                    }
//                    if (bufferedImageGrid.get(key.getxImageIndex(), key.getyImageIndex()) == null) {
//                        System.out.println("!!??");
//                    }
//                    progressMade = true;
//                } else {
//                    //no suitable matches found for candidate so remove for future iterations.
//                    candidatesIterator.remove();
//                }
//
//            }
//
//        }
//
//        if (!unmatched.isEmpty()) {
//            log.info("Still to find matches for {} unmatched tiles", unmatched.size());
//            for (MainImage.Tile tile : unmatched) {
//                BestScore<CandidateImage> bestScore = new BestScore<>(1);
//                List<Integer> colors = tile.getColors();
//                for (Map.Entry<CandidateImage, List<Integer>> entry : colorVectors.entrySet()) {
//                    List<Integer> value = entry.getValue();
//                    double sum = 0;
//                    for (int i = 0; i < colors.size(); i++) {
//                        Integer imageVal = colors.get(i);
//                        Integer candidateVal = value.get(i);
//                        sum += Math.pow(Math.abs(imageVal - candidateVal), 2);
//                    }
//                    bestScore.put(entry.getKey(), sum);
//
//                }
//                CandidateImage best = bestScore.getBest().get(0);
//                bufferedImageGrid.put(tile.getxImageIndex(), tile.getyImageIndex(), best);
//                counts.get(best).incrementAndGet();
//            }
//        }
//
//        int width = bufferedImageGrid.getWidth();
//        int height = bufferedImageGrid.getHeight();
//        for (int i = 0; i < width; i++) {
//            for (int j = 0; j < height; j++) {
//                CandidateImage candidateImage = bufferedImageGrid.get(i, j);
//                if (candidateImage == null) {
//                    System.out.println("AARRGGHH");
//                }
//            }
//        }
//
//        /*
//        iterate through the candidates. For each candidate, find the best match in the main image. If that match is under a million,
//        mark that as accepted. Move on to next candidate.
//        After one iteration, repeat,
//         */
//
//        log.info("Print matching summary");
//        List<CandidateImage> collect = Lists.newArrayList(inputCandidates);
//        collect.sort(new Comparator<CandidateImage>() {
//            @Override
//            public int compare(CandidateImage o1, CandidateImage o2) {
//                return Integer.compare(counts.get(o1).get(), counts.get(o2).get());
//            }
//        });
//        for (CandidateImage candidate : collect) {
//            log.info("File {} used {} times", candidate.getInputFile(), counts.get(candidate).get());
//        }
//
//        printCounts(counts);
//
//        return new MatchResult(bufferedImageGrid, OUTPUT_CONFIGURATION.getPictureWidth(), OUTPUT_CONFIGURATION.getPictureHeight());
//    }


    /**
     * Find tile matches for main image. For each main image tile, find the best candidate match. To reduce some bias, the least used
     * of the 10 best matches is used as a match.
     *
     * @return
     */
//    private static MatchResult findMatches(MainImage mainImage, List<CandidateImage> candidateImages) {
//        //TODO algorithm to construct the mainImage from the input images.
//
//        Map<CandidateImage, List<Integer>> colorVectors = new HashMap<>();
//        Map<CandidateImage, AtomicInteger> counts = new HashMap<>();
//        for (CandidateImage input : candidateImages) {
//            colorVectors.put(input, MainImage.Tile.getColors(input.getTileImage()));
//            counts.put(input, new AtomicInteger(0));
//        }
//
//        Grid<CandidateImage> bufferedImageGrid = new Grid<>(mainImage.getTilesAcross(), mainImage.getTilesHigh());
//        List<MainImage.Tile> tiles = mainImage.getTiles();
//        int progress = 0;
//        for (MainImage.Tile tile : tiles) {
//            BestScore<CandidateImage> bestScore = new BestScore<>(BEST_MATCH_LIST_SIZE);
//            List<Integer> colors = tile.getColors();
//            BufferedImage bestImage = null;
//            double min = Double.MAX_VALUE;
//            for (Map.Entry<CandidateImage, List<Integer>> entry : colorVectors.entrySet()) {
//                List<Integer> value = entry.getValue();
//                double sum = 0;
//                for (int i = 0; i < colors.size(); i++) {
//                    Integer imageVal = colors.get(i);
//                    Integer candidateVal = value.get(i);
//                    sum += Math.pow(Math.abs(imageVal - candidateVal), 2);
//                }
//                bestScore.put(entry.getKey(), sum);
////                if (sum < min) {
////                    min = sum;
////                    bestImage = entry.getKey();
////                }
//            }
//            List<CandidateImage> best = bestScore.getBest();
//            List<Integer> countValues = Lists.newArrayList();
//            for (CandidateImage bufferedImage : best) {
//                countValues.add(counts.get(bufferedImage).get());
//            }
//            CandidateImage imageToUse = best.get(getLowestIndex(countValues));
//            counts.get(imageToUse).incrementAndGet();
////            bufferedImageGrid.put(tile.getxImageIndex(), tile.getyImageIndex(), smallToOriginal.get(bestImage));
//            bufferedImageGrid.put(tile.getxImageIndex(), tile.getyImageIndex(), imageToUse);
//            progress++;
//            if (progress % 100 == 0) {
//                log.info("Completed {} of {}", progress, tiles.size());
////                System.out.println(String.format("Completed %s of %s", progress, tiles.size()));
//            }
//        }
//
//        printCounts(counts);
//
//        //Here, the width and height we pass in represents the number of pixels to be used for each tile from their original
//        //cropped dimensions.
//        return new MatchResult(bufferedImageGrid, OUTPUT_CONFIGURATION.getPictureWidth(), OUTPUT_CONFIGURATION.getPictureHeight());
//    }

//    private static void printCounts(Map<CandidateImage, AtomicInteger> counts) {
//        log.info("Print matching summary");
//        HashSet<CandidateImage> keys = new HashSet<>(counts.keySet());
//        List<CandidateImage> list = keys.stream().sorted(Comparator.comparingInt(o -> counts.get(o).get())).collect(Collectors.toList());
//
//        for (CandidateImage candidate : list) {
//            log.info("{}: {}", candidate.getInputFile(), counts.get(candidate));
//        }
//
//        int count = (int) counts.keySet().stream().filter(c -> counts.get(c).get() > 0).count();
//        log.info("FILES USED: {} out of {}", count, counts.size());
//    }


//    private static class BestScore<T> {
//        private int size;
//        private final TreeMap<Double, T> doubleTTreeMap;
//
//        public BestScore(int size) {
//            this.size = size;
//            doubleTTreeMap = new TreeMap<>();
//        }
//
//        public void put(T value, double score) {
//            doubleTTreeMap.put(score, value);
//        }
//
//        public List<T> getBest() {
////            NavigableSet<Double> doubles = doubleTTreeMap.navigableKeySet();
//            List<T> best = Lists.newArrayList();
//            while (best.size() < size) {
//                Map.Entry<Double, T> doubleTEntry = doubleTTreeMap.pollFirstEntry();
//
////                Double aDouble = doubles.pollFirst();
//                best.add(doubleTEntry.getValue());
//            }
//            return best;
//        }
//
//        public LinkedHashMap<T, Double> getBestScores() {
//            LinkedHashMap<T, Double> map = new LinkedHashMap<>();
//
//            while (map.size() < size) {
//                Map.Entry<Double, T> doubleTEntry = doubleTTreeMap.pollFirstEntry();
//                map.put(doubleTEntry.getValue(), doubleTEntry.getKey());
//            }
//            return map;
//        }
//
//        //TODO remove this method. The get best method modifies the tree. Alternatively, iterate through the tree above
//        //rather than poll(modifying) it.
//        public LinkedHashMap<T, Double> getAllBestScores() {
//            LinkedHashMap<T, Double> map = new LinkedHashMap<>();
//
//            for (Map.Entry<Double, T> entry : doubleTTreeMap.entrySet()) {
//                map.put(entry.getValue(), entry.getKey());
//
//            }
//            return map;
//        }
//    }
    public static BufferedImage test(List<BufferedImage> input, int newWidth, int newHeight) {
        BufferedImage dimg = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_3BYTE_BGR);

        Graphics2D g2d = dimg.createGraphics();
        int x = 0;
        int y = 0;
        for (BufferedImage bufferedImage : input) {
            g2d.drawImage(bufferedImage, x, y, null);
            x += TILE_CONFIGURATION.getPictureWidth();

            if (x >= newWidth) {
                y += TILE_CONFIGURATION.getPictureHeight();
                x = 0;
            }
        }
        g2d.dispose();

        return dimg;
    }

    public static class TileConfiguration {
        private final int pictureWidth;
        private final int pictureHeight;
        private final int tileWidth;
        private int tileHeight;

        public TileConfiguration(int pictureWidth, int pictureHeight, int tileWidth, int tileHeight) {
            this.pictureWidth = pictureWidth;
            this.pictureHeight = pictureHeight;
            this.tileWidth = tileWidth;
            this.tileHeight = tileHeight;
        }

        public int getPictureHeight() {
            return pictureHeight;
        }

        public int getPictureWidth() {
            return pictureWidth;
        }

        public int tileWidth() {
            return tileWidth;
        }

        public int tileHeight() {
            return tileHeight;
        }


    }

    private static List<File> getInputFiles(File folder) {
        File inputFolder = new File(folder, "input");
        File inputFolderCJ = new File(folder, "inputCJ");

        File[] files = inputFolder.listFiles();
        List<File> inputFiles = Lists.newArrayList(files);
        inputFiles.addAll(Arrays.asList(inputFolderCJ.listFiles()));
        return inputFiles;
    }
}
