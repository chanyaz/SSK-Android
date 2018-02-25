package base.app.data.wall;

import base.app.data.wall.sharing.SharingManager;

/**
 * Created by Filip on 4/6/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class WallSocialShare extends WallNews{

    @Override
    public SharingManager.ItemType getItemType() {
        return SharingManager.ItemType.WallPost;
    }
}
