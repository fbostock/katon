package fjdb.battlegame.units;

public class Player extends Unit {
    boolean engaged = false;
    public Player() {
        //TODO remove glyph from unit
        super(null);
        setHealth(1000);
        setAttack(5);
    }

    public boolean isEngaged() {
        return engaged;
    }

    public void setEngaged(boolean engaged) {
        this.engaged = engaged;
    }
}
