package com.example.memeroom.LoggedIn.FollowInfo;

import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
/*
Class made to request the database for information about
who the user is following.
*/
public class DisplayFollowing extends AsyncTask<String, Void, String> {

    private final String userEmail;
    private final String userToken;
    public FollowInterface followDelegate;

    public DisplayFollowing(String userEmail, String userToken) {
        this.userEmail = userEmail;
        this.userToken = userToken;
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected String doInBackground(String... strings) {
        HttpClient httpclient = new DefaultHttpClient();
        HttpGet httpget= new HttpGet("https://memero0m.herokuapp.com/download/following/info/"+userEmail);
        httpget.addHeader("Authorization", "Bearer " + userToken);

        try {
            HttpResponse response =httpclient.execute(httpget);
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
    Runs a function in follow activity to display who the user is following.
    */
    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if (result != null){
            try {
                JSONObject json = new JSONObject(result);
                String followingNames = json.getString("following");
                followDelegate.getFollowingNames(followingNames);
            }
            catch (Exception e) {
                System.out.println("Exception:       " + e);
            }}
    }
}
