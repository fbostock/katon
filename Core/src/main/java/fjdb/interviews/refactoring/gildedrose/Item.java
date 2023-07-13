package fjdb.interviews.refactoring.gildedrose;

public class Item {

    protected String name;

    protected int sellIn;

    protected int quality;

    public Item(String name, int sellIn, int quality) {
        this.name = name;
        this.sellIn = sellIn;
        this.quality = quality;
    }

    @Override
    public String toString() {
        return this.name + ", " + this.sellIn + ", " + this.quality;
    }

    protected int qualityChange() {
        return sellIn < 0 ? 2 : 1;
    }
        //TODO add default implementation
    public void updateQuality() {
        sellIn--;

        quality -= qualityChange();
        quality = Math.max(quality, 0);
    }

}
