package base.app.data.content.share;

/**
 * Created by Filip on 8/23/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class DeeplinkManager {

    private DeeplinkManager instance;

    private DeeplinkManager(){}

    public DeeplinkManager getInstance(){
        if(instance==null){
            instance = new DeeplinkManager();
        }
        return instance;
    }
}
