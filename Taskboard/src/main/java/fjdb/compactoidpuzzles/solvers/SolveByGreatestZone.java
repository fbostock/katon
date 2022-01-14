package fjdb.compactoidpuzzles.solvers;

import fjdb.compactoidpuzzles.TileGrid;

public class SolveByGreatestZone extends Solver {

    public int solveGrid(TileGrid grid) {

        /*
        TODO Take the grid, pick the biggest zone, remove that. Then repeat with the remaining grid.
         */
        int turnCount = 0;

        //select a tile at random
//        List<GameTile> initialTiles = new ArrayList<GameTile>(grid.tilesToPositions.keySet());
//        initialTiles = ListUtil.randomiseOrder(initialTiles);
//
//        while (grid.countTiles() > 0 && turnCount < 1000) {
//            GameTile tile = initialTiles.get(0);
//            initialTiles.remove(0);
//
//            Position position = grid.getPosition(tile);
//            if (position == null) {
//                continue; //tile no longer in grid, so remove a new one
//            }
//            positionsSelected.add(position);
//
//            HashSet<GameTile> tileAndNeighbours = grid.removeTileAndNeighbours(tile);
//            for (GameTile gameTile : tileAndNeighbours) {
//                gameTile.destroy();
//            }
//            grid.updateQuadrants();
//            turnCount++;
//        }
//
//        if (grid.countTiles() == 0) {
////            System.out.println("Completed grid in " + turnCount + " turns");
//        } else {
//            System.out.println("Reached turn limit with " + grid.countTiles() + " tiles left");
//        }
//
        return turnCount;

    }
}
