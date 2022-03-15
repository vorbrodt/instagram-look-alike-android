package com.example.memeroom.LoggedIn.MenuFragments.MenuResponseHandlers;

import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
/*
Post a request to get a specific users profile
*/
public class DisplaySearchProfile  extends AsyncTask<String, Void, String> {

    private final String usernameSearch;
    private final String token;

    public PressedObject searchProfileDelegate = null;

    public DisplaySearchProfile(String usernameSearch, String token) {
        this.usernameSearch = usernameSearch;
        this.token = token;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected String doInBackground(String... strings) {
        HttpClient httpclient = new DefaultHttpClient();
        HttpGet httpget= new HttpGet("https://memero0m.herokuapp.com/username/search/"+usernameSearch);
        httpget.addHeader("Authorization", "Bearer " + token);

        try {
            HttpResponse response = httpclient.execute(httpget);
            System.out.println("Status kod:   "+ response.getStatusLine().getStatusCode());

            BufferedReader in = new BufferedReader(new InputStreamReader(
                    response.getEntity().getContent(), StandardCharsets.UTF_8));

            return org.apache.commons.io.IOUtils.toString(in);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    /*
    The response is passed on to MemeRoomActivity to be displayed.
    */
    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if (result != null) {
            try {
                searchProfileDelegate.getSearchProfile(result);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}
