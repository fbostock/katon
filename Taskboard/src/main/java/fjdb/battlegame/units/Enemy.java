package fjdb.battlegame.units;

import fjdb.battlegame.NodeFactory;

public class Enemy extends Unit {

    public Enemy(NodeFactory.Glyph glyph) {
        super(glyph);
        setHealth(100);
        setAttack(5);
    }
}
