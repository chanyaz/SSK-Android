package tv.sportssidekick.sportssidekick.model;

/**
 * Created by Djordje Krutil on 06/01/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

//TODO this class will be deleted
public class TemporaryWallModel {

    private int type;
    private int category;

    public TemporaryWallModel() {
    }

    public TemporaryWallModel(int type, int category) {
        this.type = type;
        this.category = category;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }
}
