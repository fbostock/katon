package fjdb.compactoidpuzzles;

import java.util.Random;

public class TileProducer {

    private final Random random = new Random();
    public GameTile makeTile(int x, int y, int type) {
        GameTile gameTile = new GameTile();
        gameTile.type = type;
        return gameTile;
    }

    public GameTile makeTile(int xPos, int yPos) {
        return makeTile(xPos, yPos, random.nextInt(4));
    }
}
