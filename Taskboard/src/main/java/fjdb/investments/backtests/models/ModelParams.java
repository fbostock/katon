package fjdb.investments.backtests.models;

import fjdb.util.TypedMap;

public class ModelParams {

    /* Indicates whether the model allows putting on a single position at a time, or if multiple positions are allowed.*/
    private boolean singleClip = true;

    /* The number of concurrent positions allowed, if singlePosition is true*/
    private int clipsize = 1;

    /*Initial amount to trade. This is split if positions is greater than 1. Defaults to £1000*/
    private double initialAmount = 1000;

    private boolean printTrading = false; 
    
    public ModelParams() {
    }

    public boolean isSingleClip() {
        return singleClip;
    }

    public void setSingleClip(boolean singlePosition) {
        this.singleClip = singlePosition;
    }

    public int getClips() {
        return clipsize;
    }

    public void setClips(int clipSize) {
        this.clipsize = clipSize;
    }

    public double getInitialAmount() {
        return initialAmount;
    }

    public void setInitialAmount(double initialAmount) {
        this.initialAmount = initialAmount;
    }
    
    public boolean printTrading() {
        return printTrading;
    }
    
    public void setPrintTrading(boolean printTrading) {
        this.printTrading = printTrading;
    }

    private final TypedMap typedMap = new TypedMap();

    public <T> void setParameter(ModelParameter<T> key, T value) {
        typedMap.put(key, value);
    }

    public <T> void setParameter(ModelParameter<T> key) {
        typedMap.put(key, key.getDefault());
    }

    public <T> T getParameter(ModelParameter<T> key) {
        return typedMap.get(key);
    }
    public <T> T getParameterOrDefault(ModelParameter<T> key) {
        T value = typedMap.get(key);
        return value == null ? key.getDefault() : value;
    }


}
