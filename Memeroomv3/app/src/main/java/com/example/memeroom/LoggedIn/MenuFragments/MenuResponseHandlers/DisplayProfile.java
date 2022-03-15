package com.example.memeroom.LoggedIn.MenuFragments.MenuResponseHandlers;

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
Sends a request to the so get the users profile
*/
public class DisplayProfile extends AsyncTask<String, Void, String> {

    private final String email;
    private final String userToken;
    public PressedObject profileInfoDelegate = null;

    public DisplayProfile(String email, String userToken) {
        this.email = email;
        this.userToken = userToken;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected String doInBackground(String... strings) {
        HttpClient httpclient = new DefaultHttpClient();
        HttpGet httpget= new HttpGet("https://memero0m.herokuapp.com/download/profile/info/" + email);
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
    The respond is sent to MemeRoomActivity to be displayed on the screen.
    */
    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if (result != null){
            try {
            JSONObject json = new JSONObject(result);
            String location =  json.getString("location");
            profileInfoDelegate.getLocation(location);
            String profilePic = json.getString("profilePic");
            profileInfoDelegate.getProfilePicture(profilePic);
            String allMemeInfo = json.getString("allMemes");
            profileInfoDelegate.getUploadedMemes(allMemeInfo);

            }
        catch (Exception e) {
            System.out.println("Error: " + e);
        }}
    }


}
