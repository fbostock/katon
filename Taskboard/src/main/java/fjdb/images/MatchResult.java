package fjdb.images;

import com.google.common.collect.Lists;
import fjdb.threading.Threading;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MatchResult {

    private static final Logger log = LoggerFactory.getLogger(MatchResult.class);

    private Grid<CandidateImage> imageGrid;
    private final int tileWidth;
    private final int tileHeight;

    public MatchResult(Grid<CandidateImage> imageGrid, int tileWidth, int tileHeight) {
        this.imageGrid = imageGrid;
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
    }

    public BufferedImage makeImage() {
        log.info("Making image {} MB",  + Runtime.getRuntime().totalMemory() / (1024*1024));
//        System.out.println("Making image "  + Runtime.getRuntime().totalMemory() / (1024*1024) + " MB");
        BufferedImage dimg = new BufferedImage(imageGrid.getWidth() * tileWidth, imageGrid.getHeight() * tileHeight, BufferedImage.TYPE_3BYTE_BGR);
        Graphics2D g2d = dimg.createGraphics();
        for (int i = 0; i < imageGrid.getWidth(); i++) {

            List<CandidateImage> list = Lists.newArrayList();
            for (int j = 0; j < imageGrid.getHeight(); j++) {
                list.add(imageGrid.get(i, j));
            }
            Map<Integer, BufferedImage> map = makeBatch(list);
            for (int j = 0; j < imageGrid.getHeight(); j++) {
//                PixelMatcher.CandidateImage candidateImage = imageGrid.get(i, j);
//                BufferedImage original = candidateImage.getOriginal();
//                BufferedImage image = PixelImageUtils.resize(original, tileWidth, tileHeight);
                BufferedImage image = map.get(j);
                g2d.drawImage(image, i * tileWidth, j * tileHeight, null);
            }
            log.info("Making image {} out of {} columns done", i+1, imageGrid.getWidth());
        }
        g2d.dispose();
        return dimg;
    }

    Map<Integer, BufferedImage> makeBatch(List<CandidateImage> candidates) {
        ConcurrentHashMap<Integer, BufferedImage> map = new ConcurrentHashMap<>();
        List<Runnable> runnables = Lists.newArrayList();
        for (int i = 0; i < candidates.size(); i++) {
            CandidateImage candidateImage = candidates.get(i);
            int finalI = i;
            runnables.add(() -> {
                BufferedImage original = candidateImage.getOriginal();
                BufferedImage image = PixelImageUtils.resize(original, tileWidth, tileHeight);
                map.put(finalI, image);
            });
        }
        Threading.run(runnables);
        return map;
    }

    public Grid<CandidateImage> getImageGrid() {
        return imageGrid;
    }
}
