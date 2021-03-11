package fjdb.battlegame;

import fjdb.battlegame.commands.Command;
import fjdb.battlegame.commands.MoveCommand;
import fjdb.battlegame.coords.Location;

public class Deprecated {





    //#############################
    //Everything below was old ideas, which may become completely surpassed by what comes above.
    //#############################


    /**
     * Submit a command to the command queue.
     *
     * @param command
     */
    public void submitCommand(Command command) {
        if (command instanceof MoveCommand) {
            processMove((MoveCommand) command);
            //TODO add to the Move queue. A worker will take the command from the queue, construct a route, and then
            //use that route to build a movement action. That action will then be executed. The action will need to be
            //cancellable, and time bound i.e. it should take some units of time specified by the GameClock. At each game
            //unit, an update should be sent to the graphicsEngine, and a check should be made to see if the command
            //has been cancelled. The action should probably have a reference to the original command that initialised it.
            //That way the command can be cancelled, and the action can check that and halt.
        }

    }

    private void processMove(MoveCommand moveCommand) {
        //construct a route
        SimpleRoute simpleRoute = new SimpleRoute(moveCommand.getUnit().getLocation(), moveCommand.getTo());
        simpleRoute.getLength();
        //build a movement action
    }

    private static class MovementAction {
        private Command command;
        private RouteIF route;

        public MovementAction(Command inputCommand, RouteIF route) {
            command = inputCommand;
            this.route = route;
        }

        public boolean isCancelled() {
            return command.isCancelled();
        }
    }


    public void executeCommand(Command command) {

        if (command instanceof MoveCommand) {
            MoveCommand moveCommand = (MoveCommand) command;
            moveCommand.getUnit().setLocation(moveCommand.getTo());
        }
    }




    public static interface RouteIF {
        /**
         * Return the start position of the route
         */
        Location getStart();

        /**
         * Return the end/destination of the route
         */
        Location getEnd();

        /**
         * Return the total length of the route, that is, the number of location between the start and end points inclusive.
         */
        int getLength();


    }

    /**
     * An implementation of RouteIF which merely stores the start and end points, with no space in between.
     */
    public static class SimpleRoute implements RouteIF {

        private final Location start;
        private final Location end;

        public SimpleRoute(Location start, Location end) {
            this.start = start;
            this.end = end;
        }

        @Override
        public Location getStart() {
            return start;
        }

        @Override
        public Location getEnd() {
            return end;
        }

        @Override
        public int getLength() {
            return 2;
        }


    }


}
