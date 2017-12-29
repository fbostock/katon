package fjdb.battlegame.commands;

import fjdb.battlegame.MainGame;
import fjdb.battlegame.coords.Location;

/**
 * Created by francisbostock on 18/11/2017.
 */
public class MoveCommand extends Command {

    private final MainGame.Unit unitToMove;
    private final Location to;

    public MoveCommand(MainGame.Unit unitToMove, Location to) {
        this.unitToMove = unitToMove;
        this.to = to;
    }

    public MainGame.Unit getUnit() {
        return unitToMove;
    }

    public Location getTo() {
        return to;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    //        public MoveCommand(Unit unitToMove, Unit targetUnit) {
//            //a special form of move, where the target location may potentially change.
//        }
}
