package tv.sportssidekick.sportssidekick.model.club;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Filip on 1/25/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class ClubTVModel {

    private static ClubTVModel instance;

    public ArrayList<TvCategory> getTvCategories() {
        return tvCategories;
    }

    public ArrayList<TvChannel> getTvChannels() {
        return tvChannels;
    }

    ArrayList<TvCategory> tvCategories;
    ArrayList<TvChannel> tvChannels;

    public static ClubTVModel getInstance(){
        if(instance==null){
            instance = new ClubTVModel();
        }
        return instance;
    }

    private ClubTVModel(){
        tvCategories = new ArrayList<>();
        tvChannels = new ArrayList<>();
        for(int i = 0; i< 30; i++){
            ArrayList<TvChannel> demoTvChannels = new ArrayList<>();
            for(int j=0; j<30; j++){
                TvChannel demoChannel = new TvChannel("tv_channel_" + i + "_" + j, "TV Channel caption " + j,"www.url.com",new Date(),"www.url.com");
                demoTvChannels.add(demoChannel);
                tvChannels.add(demoChannel);
            }
            tvCategories.add(new TvCategory("This is demo caption of TV Channel cell " + i,"channel_" + i,demoTvChannels));
        }
    }

    public TvCategory getTVCategoryById(String id){
        for(TvCategory category : tvCategories){
            if(id.equals(category.getId())){
                return category;
            }
        }
        return null;
    }
    public TvChannel getTvChannelById(String id){
        for(TvChannel channel : tvChannels){
            if(id.equals(channel.getId())){
                return channel;
            }
        }
        return null;
    }

}
