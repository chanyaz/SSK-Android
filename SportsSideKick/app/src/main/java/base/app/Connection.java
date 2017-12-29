package base.app;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import com.keiferstone.nonet.ConnectionStatus;
import com.keiferstone.nonet.Monitor;
import com.keiferstone.nonet.NoNet;

import org.greenrobot.eventbus.EventBus;

import base.app.model.AlertDialogManager;


/**
 * Created by Filip on 5/9/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class Connection {

    public enum Status {
        notReachable,
        reachable
    }

    public class OnChangeEvent{
        Status status;
        OnChangeEvent(Status status){
            this.status = status;
        }

        public Status getStatus() {
            return status;
        }
    }

    private static Connection instance;
    private Monitor.Builder manager;

    private Status lastStatus = Status.notReachable;

    public static Connection getInstance() {
        if(instance == null){
            instance = new Connection();
        }
        return instance;
    }

    public void initialize(Context context){
          manager = NoNet.monitor(context)
                .callback(new Monitor.Callback() {
                    @Override
                    public void onConnectionEvent(int connectionStatus) {
                        if (connectionStatus == ConnectionStatus.CONNECTED) {
                            lastStatus = Status.reachable;
                            EventBus.getDefault().post(new OnChangeEvent(Status.reachable));
                        } else {
                            lastStatus = Status.notReachable;
                            EventBus.getDefault().post(new OnChangeEvent(Status.notReachable));
                        }
                    }
                });
        manager.start();
    }

    private Connection(){ }

    private boolean reachable(){
        return lastStatus == Status.reachable;
    }

    public void start(){
        manager.start();
    }

    /**
     *  Create dialog that alerts the User about no internet connectivity
     * @return internet connectivity
     */
    public boolean alertIfNotReachable(final Activity activity, View.OnClickListener clickListener){
        if(!reachable()){
            AlertDialogManager.getInstance().showAlertDialog(
                    activity.getResources().getString(R.string.no_connection_title),
                    activity.getResources().getString(R.string.no_connection_message),
                    null,
                    clickListener
            );
        }
        return reachable();
    }
}
