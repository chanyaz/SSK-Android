package base.app.data.wall;

/**
 * Created by Filip on 1/7/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import base.app.data.sharing.SharingManager;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class WallPost extends WallItem {

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
    public void setEqualTo(WallItem item) {
        super.setEqualTo(item);
        this.vidUrl = ((WallPost)item).vidUrl;
    }

    @Override
    public SharingManager.ItemType getItemType(){
        return SharingManager.ItemType.WallPost;
    }
}