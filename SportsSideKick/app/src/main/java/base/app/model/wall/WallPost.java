package base.app.model.wall;

/**
 * Created by Filip on 1/7/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import base.app.model.sharing.SharingManager;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class WallPost extends WallBase {

    @JsonProperty("vidUrl")
    private String vidUrl;

    @JsonProperty("vidUrl")
    public String getVidUrl() {
        return vidUrl;
    }

    @JsonProperty("vidUrl")
    public void setVidUrl(String vidUrl) {
        this.vidUrl = vidUrl;
    }


    @Override
    public void setEqualTo(WallBase item) {
        super.setEqualTo(item);
        this.vidUrl = ((WallPost)item).vidUrl;
    }

    @Override
    public SharingManager.ItemType getItemType(){
        return SharingManager.ItemType.WallPost;
    }
}