package fjdb.battlegame;

import com.google.common.collect.Lists;
import fjdb.battlegame.coords.GridManager;
import fjdb.battlegame.coords.Location;
import fjdb.battlegame.coords.Route;
import fjdb.battlegame.coords.RouteMaker;
import fjdb.battlegame.units.Player;
import fjdb.battlegame.units.Unit;

import java.util.*;
import java.util.concurrent.*;

/**
 * Created by francisbostock on 18/11/2017.
 */
public class GameEngine {
    private static final ExecutorService service = Executors.newFixedThreadPool(10);
    private static final int DELAY = 200;

    /* This should act as a listener to whatever updates are required when the GameEngine makes changes. */
    private GraphicsEngine _graphicsEngine;
    private GridManager _gridManager;
    private final Map<Unit, MovementAction> moveActions = new ConcurrentHashMap<>();
    private final List<Fight> _fights = Collections.synchronizedList(new ArrayList<>());

    /*
    Initially model a single straight path between Player A and enemy X.
    Player A moves arbitrarily left, enemy X stationary.
    If the player enters a square with the enemy, they fight.
    If the player retreats, the fight stops.
     */

    public void start() {
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                processEvents();
            }
        }, 0, DELAY);
    }


    public GameEngine(GridManager gridManager) {
        _gridManager = gridManager;
    }

    public void setGraphicsEngine(GraphicsEngine graphicsEngine) {
        _graphicsEngine = graphicsEngine;
    }


    public void updateUnitLocation(Unit unit, Location location) {

        Location startLocation = unit.getLocation();
        Route route = new RouteMaker(_gridManager).getRoutes(startLocation, location).get(0);
        System.out.println(String.format("Adding movement for %s", unit));
        //TODO before commiting the action (by adding to the moveActions map), need to perform checks to see
        //if the action can be sent e.g. if the move is possible, or the unit is not busy with something else.
        MovementAction previous = moveActions.put(unit, new MovementAction(_graphicsEngine, unit, route));
        if (previous != null) {
            previous.cancel();
        }
        System.out.println(String.format("Added movement for %s", unit));

//        service.submit(new Runnable() {
//            @Override
//            public void run() {
//                for (Location loc : route) {
//                    Future<Void> submit = service.submit(new Callable<Void>() {
//                        @Override
//                        public Void call() {
//                            unit.setLocation(loc);
//                            _graphicsEngine.updateUnit(unit);
//                            try {
//                                Thread.sleep(DELAY);
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            }
//                            return null;
//                        }
//                    });
//                    try {
//                        submit.get();
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    } catch (ExecutionException e) {
//                        e.printStackTrace();
//                    }
//
//                }
//
//            }
//        });
    }


    private void processEvents() {
        //process movements
        Collection<MovementAction> movements = moveActions.values();
        Iterator<MovementAction> iterator = movements.iterator();
        while (iterator.hasNext()) {
            MovementAction movement = iterator.next();
            if (movement.complete()) {
                iterator.remove();
            } else {
                movement.next();
            }
        }
        //engage fights
        for (Unit unit : _graphicsEngine._unitGlyphs.keySet()) {
            if (unit instanceof Player) {
                Player player = (Player) unit;
                if (!player.isEngaged()) {
                Location location = unit.getLocation();
                //TODO check the location for adjacent locations which have enemies.
                //engage in battle

                Set<Location> adjacentLocs = _gridManager.getAdjacent(location);
                List<Unit> units = _gridManager.getUnits(adjacentLocs);
                if (!units.isEmpty()) {
                    //TODO engage in battle with given units.
                    //but not other player units
                    MovementAction movementAction = moveActions.get(unit);
                    if (movementAction != null) {
                        movementAction.cancel();
                    }
                    for (Unit other : units) {
                        MovementAction movement = moveActions.get(other);
                        if (movement != null) {
                            movement.cancel();
                        }
                    }

                    engageFight(player, units);
                }
                }
            }

        }
        //continue fights
        Set<Unit> killedUnits = new HashSet<>();
        synchronized (_fights) {

            Iterator<Fight> fightIterator = _fights.iterator();
            while (fightIterator.hasNext()) {
                Fight fight = fightIterator.next();
                boolean result = fight.iteration();
                if (result) {
                    fightIterator.remove();
                    fight.getPlayer().setEngaged(false);
                }
                killedUnits.addAll(fight.getDeadUnits());
            }
        }
        for (Unit killedUnit : killedUnits) {
            System.out.println(String.format("Unit killed %s", killedUnit));
            killUnit(killedUnit);
        }





        /*
        Events to process
        1) carry out any movement for players - movement from one location to another
        2) carry out any movement for enemies - movement from one location to another
        3) identify new fights between players/enemies
        4) carry out existing fights between players/enemies

        When the game starts, a clock should start ticking over and running the events processor.
        As players commit actions, they will be added to an impending list of actions to process.


         */
    }

    private void engageFight(Player player, List<Unit> units) {
        player.setEngaged(true);
        _fights.add(new Fight(player, units));
    }

    private void killUnit(Unit unit) {
        unit.dispose();
        _graphicsEngine.removeUnit(unit);
    }

    private static class Fight {

        private final Player player;
        private final List<Unit> units;
        private final List<Unit> deadUnits = new ArrayList<>();

        public Fight(Player player, List<Unit> units) {

            this.player = player;
            this.units = Lists.newArrayList(units);
        }
        public boolean iteration() {
            int attack = player.getAttack();

            for (Unit unit : units) {
                player.adjustHealth(-unit.getAttack());
            }
            if (player.getHealth() <=0) {
                deadUnits.add(player);
                return true;
            }
            Unit unit = units.get(0);
            unit.adjustHealth(-attack);
            if (unit.getHealth() <=0) {
                units.remove(0);
                deadUnits.add(unit);
            }
            return units.isEmpty();
        }

        public List<Unit> getDeadUnits() {
            return deadUnits;
        }


        public Player getPlayer() {
            return player;
        }
    }


    private static class MovementAction {
        private GraphicsEngine graphicsEngine;
        private Unit unit;
        Route route;
        private final Iterator<Location> iterator;
        private boolean cancelled = false;

        public MovementAction(GraphicsEngine graphicsEngine, Unit unit, Route route) {
            this.graphicsEngine = graphicsEngine;
            this.unit = unit;
            this.route = route;
            iterator = route.iterator();
            if (unit.getLocation().equals(route.first())) {
                iterator.next();
            }
        }

        public void next() {
            if (iterator.hasNext()) {
                unit.setLocation(iterator.next());
                //TODO the graphics updates should be done as part of unit.setLocation. i.e. it should delegate to the
                //node/glyph in the unit. Setting the node properties will then update the graphics. A bridge pattern
                //will be used in the construction of units to abstract away the details of the graphics updates.
                graphicsEngine.updateUnit(unit);
            }
        }

        public boolean complete() {
            return cancelled || !iterator.hasNext();
        }

        public void cancel() {
            cancelled = true;
        }


    }


}
