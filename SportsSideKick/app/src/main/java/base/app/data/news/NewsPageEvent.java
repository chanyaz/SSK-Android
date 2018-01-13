package base.app.data.news;

import java.util.List;

import base.app.data.wall.News;

/**
 * Created by Filip on 2/3/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class NewsPageEvent {

    public NewsPageEvent(List<News> values) {
        this.values = values;
    }

    public List<News> getValues() {
        return values;
    }

    public NewsPageEvent setValues(List<News> values) {
        this.values = values;
        return this;
    }

    List<News> values;
}
