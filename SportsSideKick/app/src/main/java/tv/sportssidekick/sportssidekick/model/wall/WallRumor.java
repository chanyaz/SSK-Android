package tv.sportssidekick.sportssidekick.model.wall;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import tv.sportssidekick.sportssidekick.model.sharing.SharingManager;

/**
 * Created by Filip on 1/10/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true,value={"type"})
public class WallRumor extends WallNews {

    // Same as News

    @Override
    public SharingManager.ItemType getItemType(){
        return SharingManager.ItemType.News;
    }

}
