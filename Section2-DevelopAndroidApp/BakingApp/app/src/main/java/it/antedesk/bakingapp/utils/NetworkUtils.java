package it.antedesk.bakingapp.utils;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Antedesk on 15/05/2018.
 */

public class NetworkUtils {

    public static String getHttpResponse(String url) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        String response = null;
        try {
            Response httpResponse = client.newCall(request).execute();
            response = httpResponse.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    /**
     * Allows to check if the app is connected to internet or not.
     * source of the below code https://goo.gl/q7gpMi
     * @param activity is the current activity that invokes this method.
     * @return a boolean value that states if the app is connected to internet or not
     */
    public static boolean isOnline(Activity activity) {
        ConnectivityManager cm =
                (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo netInfo = null;
        if (cm != null)
            netInfo = cm.getActiveNetworkInfo();

        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
