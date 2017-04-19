package tv.sportssidekick.sportssidekick.model.wall;

import com.fasterxml.jackson.annotation.JsonProperty;

import tv.sportssidekick.sportssidekick.model.sharing.SharingManager;

/**
 * Created by Filip on 4/6/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class WallNewsShare extends WallBase{

    @JsonProperty("vidUrl")
    private String vidUrl;

    @Override
    public void setEqualTo(WallBase item) {
        super.setEqualTo(item);
        this.vidUrl = ((WallNewsShare)item).vidUrl;

    }

    @Override
    public SharingManager.ItemType getItemType() {
        return null;
    }
}
