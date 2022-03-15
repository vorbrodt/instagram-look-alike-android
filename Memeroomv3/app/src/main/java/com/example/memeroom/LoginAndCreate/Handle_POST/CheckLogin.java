package com.example.memeroom.LoginAndCreate.Handle_POST;

import android.os.AsyncTask;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
/*
Sends a login request to the server
*/
public class CheckLogin extends AsyncTask<String, Void, String> {

    private final String email;
    private final String password;
    private String userToken;
    public AsyncTaskResponse delegate = null;


    public  CheckLogin(String email, String password) {
        this.email = email;
        this.password = password;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        delegate.pressedLogin(result, userToken);
    }

    @Override
    protected String doInBackground(String... strings) {
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("https://memero0m.herokuapp.com/login");

            List<NameValuePair> nameValuePairs = new ArrayList<>(2);
            nameValuePairs.add(new BasicNameValuePair("email", email));
            nameValuePairs.add(new BasicNameValuePair("password", password));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            HttpResponse response = httpclient.execute(httppost);
            String token = EntityUtils.toString(response.getEntity());
            ObjectMapper mapper = new ObjectMapper();
            try {
                Map map = mapper.readValue(token, Map.class);
                userToken = map.get("access_token").toString();}

            catch (Exception e) {
                System.out.println("Error: " + e);
            }
            //return status code
            return Integer.toString(response.getStatusLine().getStatusCode());
        }
        catch (Exception e) {
            System.out.println("Error: " + e);
        }
        return null;
}}
