package base.app.data.wall.news;

import java.util.List;

import base.app.data.wall.WallNews;

/**
 * Created by Filip on 2/3/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class RumoursPageEvent {

    List<WallNews> values;

    public RumoursPageEvent(List<WallNews> values) {
        this.values = values;
    }

    public List<WallNews> getValues() {
        return values;
    }

    public RumoursPageEvent setValues(List<WallNews> values) {
        this.values = values;
        return this;
    }
}
