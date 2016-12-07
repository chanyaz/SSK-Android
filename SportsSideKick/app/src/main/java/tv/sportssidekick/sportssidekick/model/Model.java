package tv.sportssidekick.sportssidekick.model;

import com.google.firebase.database.FirebaseDatabase;

public class Model {

    private static Model instance;

    FirebaseDatabase ref;

    public static Model getInstance(){
        if(instance==null){
            instance = new Model();
        }
        return instance;
    }

    private Model() {
        ref = FirebaseDatabase.getInstance();
    }


    public FirebaseDatabase getDatabase(){
        return ref;
    }

}
