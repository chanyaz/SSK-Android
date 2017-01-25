package tv.sportssidekick.sportssidekick.model.club;

import java.util.List;

/**
 * Created by Filip on 1/24/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class TvCategory {

    String name;
    String id;
    List<TvChannel> tvChannels;

    public String getName() {
        return name;
    }

    public TvCategory setName(String name) {
        this.name = name;
        return this;
    }

    public String getId() {
        return id;
    }

    public TvCategory setId(String id) {
        this.id = id;
        return this;
    }

    public List<TvChannel> getTvChannels() {
        return tvChannels;
    }

    public TvCategory setTvChannels(List<TvChannel> tvChannels) {
        this.tvChannels = tvChannels;
        return this;
    }

    public TvCategory(String name, String id, List<TvChannel> tvChannels) {
        this.name = name;
        this.id = id;
        this.tvChannels = tvChannels;
    }
}
