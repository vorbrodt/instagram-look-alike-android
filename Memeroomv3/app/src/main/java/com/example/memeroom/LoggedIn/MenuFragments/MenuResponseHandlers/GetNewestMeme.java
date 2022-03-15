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
requests the newest meme a specific user has posted.
*/
public class GetNewestMeme extends AsyncTask<String, Void, String> {

    private final String usernamePressed;
    private final String userToken;
    public PressedObject profileInfoDelegate = null;

    public GetNewestMeme(String usernamePressed, String userToken) {
        this.usernamePressed = usernamePressed;
        this.userToken = userToken;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected String doInBackground(String... strings) {
        HttpClient httpclient = new DefaultHttpClient();
        HttpGet httpget= new HttpGet("https://memero0m.herokuapp.com/newest/post/" + usernamePressed);
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
    Sends back the information to MemeRoomActivity to be displayed.
    */
    
    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        try {
            JSONObject json = new JSONObject(result);
            String memPic = json.getString("newest_meme");
            profileInfoDelegate.getNewestMeme(memPic);
        }
       catch (Exception e) {
                System.out.println("Error: " + e);
            }
        }

    }
