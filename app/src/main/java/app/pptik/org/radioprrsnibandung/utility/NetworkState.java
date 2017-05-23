package app.pptik.org.radioprrsnibandung.utility;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import app.pptik.org.radioprrsnibandung.R;

/**
 * Created by Hafid on 5/16/2017.
 */

public class NetworkState {
    public static boolean isOnline(Context ctx) {
        ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        boolean connect = netInfo != null && netInfo.isConnectedOrConnecting();
        if (!connect) Toast.makeText(ctx, R.string.internet_not_available, Toast.LENGTH_LONG).show();
        return connect;
    }
}