package tv.sportssidekick.sportssidekick.model.news;

import java.util.List;

/**
 * Created by Filip on 2/3/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class RumoursPageEvent {

    public RumoursPageEvent(List<NewsItem> values) {
        this.values = values;
    }

    public List<NewsItem> getValues() {
        return values;
    }

    public RumoursPageEvent setValues(List<NewsItem> values) {
        this.values = values;
        return this;
    }

    List<NewsItem> values;
}
