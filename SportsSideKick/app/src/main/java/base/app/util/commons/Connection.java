package base.app.util.commons;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;

import base.app.R;
import base.app.util.AlertDialogManager;

/**
 * Created by Filip on 5/9/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class Connection {

    private Connection(){ }

    private static boolean reachable(Context context){
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    /**
     *  Create dialog that alerts the User about no internet connectivity
     * @return internet connectivity
     */
    public static boolean alertIfNotReachable(final Context context, View.OnClickListener clickListener){
        boolean isReachable = reachable(context);
        if(!isReachable){
            AlertDialogManager.getInstance().showAlertDialog(
                    context.getResources().getString(R.string.no_connection),
                    context.getResources().getString(R.string.internet_needed),
                    null,
                    clickListener
            );
        }
        return isReachable;
    }
}
