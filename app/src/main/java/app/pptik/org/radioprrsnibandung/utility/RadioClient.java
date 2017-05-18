package app.pptik.org.radioprrsnibandung.utility;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;
import org.apache.http.entity.StringEntity;

/**
 * Created by Hafid on 5/16/2017.
 */

public class RadioClient {
    private static final String BASE_URL = "http://167.205.7.227:10/radioapi_v1/";
    private static final String BASE_URL_MIN = "http://167.205.7.227:10/radioapi_v1";
    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void get(Context context, String url, AsyncHttpResponseHandler responseHandler) {
        if (NetworkState.isOnline(context)) {
            client.get(context, getAbsoluteUrl(url), responseHandler);
        }
    }

    public static void post(Context context, String url, Header[] headers, String contentType, StringEntity params, AsyncHttpResponseHandler responseHandler) {
        if (NetworkState.isOnline(context)) {
            client.post(context, getAbsoluteUrl(url), headers, params, contentType, responseHandler);
        }
    }

    public static void cancelRequest(Context context) {
        if (NetworkState.isOnline(context)) {
            client.cancelRequests(context, true);
        }
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }
}
