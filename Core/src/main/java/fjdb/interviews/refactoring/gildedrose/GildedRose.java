package fjdb.interviews.refactoring.gildedrose;

class GildedRose {
    private static final String agedBrie = "Aged Brie";
    Item[] items;
    private static final String concertTickets = "Backstage passes to a TAFKAL80ETC concert";
    private static final String sulfuras = "Sulfuras, Hand of Ragnaros";


    public GildedRose(Item[] items) {
        this.items = items;
    }

    public void updateQuality() {

        for (Item item : items) {
            item.updateQuality();
        }

//        for (int i = 0; i < items.length; i++) {
//            if (!items[i].name.equals(agedBrie)
//                    && !items[i].name.equals(concertTickets)) {
//                if (items[i].quality > 0) {
//                    if (!items[i].name.equals(sulfuras)) {
//                        items[i].quality = items[i].quality - 1;
//                    }
//                }
//            } else {
//                if (items[i].quality < 50) {
//                    items[i].quality = items[i].quality + 1;
//
//                    if (items[i].name.equals(concertTickets)) {
//                        if (items[i].sellIn < 11) {
//                            if (items[i].quality < 50) {
//                                items[i].quality = items[i].quality + 1;
//                            }
//                        }
//
//                        if (items[i].sellIn < 6) {
//                            if (items[i].quality < 50) {
//                                items[i].quality = items[i].quality + 1;
//                            }
//                        }
//                    }
//                }
//            }

//            if (!items[i].name.equals(sulfuras)) {
//                items[i].sellIn = items[i].sellIn - 1;
//            }

//            if (items[i].sellIn < 0) {
//                if (!items[i].name.equals(agedBrie)) {
//                    if (!items[i].name.equals(concertTickets)) {
//                        if (items[i].quality > 0) {
//                            if (!items[i].name.equals(sulfuras)) {
//                                items[i].quality = items[i].quality - 1;
//                            }
//                        }
//                    } else {
//                        //TODO looks like a bug, check intention
////                        items[i].quality = items[i].quality - items[i].quality;
//                        items[i].quality = 0;
//                    }
//                } else {
//                    if (items[i].quality < 50) {
//                        items[i].quality = items[i].quality + 1;
//                    }
//                }
//            }


//        }
    }

    private static class AgedBrie extends Item {

        public AgedBrie(String name, int sellIn, int quality) {
            super(name, sellIn, quality);
        }


        @Override
        protected int qualityChange() {
            return sellIn < 0 ? 2 : 1;
        }

        @Override
        public void updateQuality() {
            sellIn--;

            quality += qualityChange();
            quality = Math.min(50, quality);
        }

    }

    private static class BackstagePasses extends Item {

        public BackstagePasses(int sellIn, int quality) {
            super(concertTickets, sellIn, quality);
        }

        protected int qualityChange() {
            if (sellIn <= 5) return 3;
            if (sellIn <= 10) return 2;
            return 1;
        }

        @Override
        public void updateQuality() {
            sellIn--;

            if (sellIn < 0) {
                quality = 0;
            } else {
                quality += qualityChange();
                quality = Math.min(quality, 50);
            }
        }

    }

    private static class Sulfurus extends Item {

        public Sulfurus(int sellIn, int quality) {
            super(sulfuras, sellIn, quality);
        }

        @Override
        public void updateQuality() {

        }
    }

    private static class NormalItem extends Item {

        public NormalItem(String name, int sellIn, int quality) {
            super(name, sellIn, Math.min(quality, 50));
        }

//        @Override
//        protected int qualityChange() {
//            return sellIn < 0 ? 2 : 1;
//        }
//
//        public void updateQuality() {
//            sellIn--;
//
//            quality -= qualityChange();
//            quality = Math.max(quality, 0);
//        }
    }

    private static class ConjuredItem extends Item {

        public ConjuredItem(String name, int sellIn, int quality) {
            super(name, sellIn, quality);
        }

        @Override
        protected int qualityChange() {
            return sellIn < 0 ? 4 : 2;
        }

//        @Override
//        public void updateQuality() {
//
//        }
    }


}
