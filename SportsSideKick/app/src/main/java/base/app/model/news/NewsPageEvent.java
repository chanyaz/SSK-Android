package base.app.model.news;

import java.util.List;

import base.app.model.wall.WallNews;

/**
 * Created by Filip on 2/3/2017.
 */

public class NewsPageEvent {

    public NewsPageEvent(List<WallNews> values) {
        this.values = values;
    }

    public List<WallNews> getValues() {
        return values;
    }

    public NewsPageEvent setValues(List<WallNews> values) {
        this.values = values;
        return this;
    }

    List<WallNews> values;
}
