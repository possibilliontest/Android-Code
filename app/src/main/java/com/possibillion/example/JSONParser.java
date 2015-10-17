package com.possibillion.example;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

/**
 * Created by Chekhra on 10/17/15.
 */
public class JSONParser {

    OkHttpClient mClient;
    Context mContext;
    private static final String TAG = "JSONParser";
    private static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    SharedPreferences shared;
    SharedPreferences.Editor edit;
    boolean vehicleIsAssigned = false;

    /**
     * Initiates OkHttpClient
     *
     * @param context Context
     * @param client  OkHttpClient
     */
    public JSONParser(Context context, OkHttpClient client) {
        this.mClient = client;
        this.mContext = context;
        shared = mContext.getSharedPreferences(PreferenceConstants.PREFERENCE_NAME, Context.MODE_PRIVATE);
        edit = shared.edit();
        edit.apply();
    }

    /**
     * get request code
     *
     * @param url String
     * @return String response
     * @throws IOException
     */
    public String doGetRequest(String url) throws IOException {
        Log.d("URL", url);
        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response = mClient.newCall(request).execute();
        return response.body().string();
    }

}
