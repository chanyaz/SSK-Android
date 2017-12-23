package base.app.model.sharing;

/**
 * Created by Filip on 8/23/2017.
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
