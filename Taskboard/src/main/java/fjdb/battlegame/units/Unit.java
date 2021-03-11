package fjdb.battlegame.units;

import fjdb.battlegame.NodeFactory;
import fjdb.battlegame.coords.Location;

public abstract class Unit {
    private Location _location = Location.NULL;
    private NodeFactory.Glyph _glyph;
    boolean isSelected = false;
    private int health;
    private int attack;

    public Unit(NodeFactory.Glyph glyph) {
        _glyph = glyph;
    }

    public void setLocation(Location location) {
        _location.remove(this);
        _location = location;
        _location.addUnit(this);
    }

    public Location getLocation() {
        return _location;
    }

    public NodeFactory.Glyph getGlyph() {
        return _glyph;
    }

    public Position getPosition() {
        return new Position(_glyph.getTranslateX(), _glyph.getTranslateY());
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public int adjustHealth(int delta) {
        this.health +=delta;
        return health;
    }

    public int getAttack() {
        return attack;
    }

    public void setAttack(int attack) {
        this.attack = attack;
    }

    public void dispose() {
        setLocation(Location.NULL);
    }
}
