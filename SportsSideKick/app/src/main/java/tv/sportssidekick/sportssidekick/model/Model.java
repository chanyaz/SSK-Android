package tv.sportssidekick.sportssidekick.model;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;

import tv.sportssidekick.sportssidekick.enitity.Ticker;

public class Model {

    private static Model instance;

    public static Model getInstance(){
        if(instance==null){
            instance = new Model();
        }
        return instance;
    }

    private Model() {}

    DatabaseReference tickerReference;
    public void requestTickerInfo(){
        // Get a reference to our posts
        tickerReference = FirebaseDatabase.getInstance().getReference("ticker");

        // Attach a listener to read the data at our ticker reference
        tickerReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Ticker ticker = dataSnapshot.getValue(Ticker.class);
                EventBus.getDefault().post(ticker);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }
}
