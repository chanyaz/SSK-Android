package tv.sportssidekick.sportssidekick.model.friendship;

import android.text.TextUtils;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import tv.sportssidekick.sportssidekick.model.Model;
import tv.sportssidekick.sportssidekick.model.UserInfo;

/**
 * Created by Filip on 12/27/2016.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class PeopleSearchManager {

    private static PeopleSearchManager instance;

    public static PeopleSearchManager getInstance(){
        if(instance==null){
            instance = new PeopleSearchManager();
        }
        return instance;
    }

    private PeopleSearchManager(){

    }

    private String lastSearchKey;

    /**
     * SearchPeople - get a matching list of SSK users that matches the search string
     * @param  searchString search string
     *
     */

    public void searchPeople(String searchString){
        lastSearchKey = searchString.toLowerCase();

        if(TextUtils.isEmpty(searchString)){
            return;
        }
        List<UserInfo> usersInfo = new ArrayList<>();

        Model.getInstance().getRef()
                .getReference("userSearchKeys")
                .orderByChild("firstNameL")
                .startAt(lastSearchKey)
                .endAt(lastSearchKey + "\\u{f8ff}")
                .limitToFirst(10)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            String uid = child.getKey();
                            UserInfo info = Model.getInstance().getCachedUserInfoById(uid);
                            // TODO Emit results?
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) { }
                });

        Model.getInstance().getRef()
                .getReference("userSearchKeys")
                .orderByChild("lastNameL")
                .startAt(lastSearchKey)
                .endAt(lastSearchKey + "\\u{f8ff}")
                .limitToFirst(10)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            String uid = child.getKey();
                            UserInfo info = Model.getInstance().getCachedUserInfoById(uid);
                            // TODO Emit results?
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) { }
                });

        Model.getInstance().getRef()
                .getReference("userSearchKeys")
                .orderByChild("nicNameL")
                .startAt(lastSearchKey)
                .endAt(lastSearchKey + "\\u{f8ff}")
                .limitToFirst(10)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            String uid = child.getKey();
                            UserInfo info = Model.getInstance().getCachedUserInfoById(uid);
                            // TODO Emit results?
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) { }
                });
    }
}
