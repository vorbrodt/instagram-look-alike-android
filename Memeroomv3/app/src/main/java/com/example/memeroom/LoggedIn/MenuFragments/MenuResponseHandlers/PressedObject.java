package com.example.memeroom.LoggedIn.MenuFragments.MenuResponseHandlers;


import org.json.JSONException;
/*
Used for other classes to send data to MemeRoomActivity
*/
public interface PressedObject {
    void getLocation(String location);
    void getProfilePicture(String pic);
    void getSearchProfile(String profileInfo) throws JSONException;
    void getUploadedMemes(String allInfo);
    void getNewsFeed(String username,String time);
    void getNewestMeme(String memePic);
    void noFollowingMsg();
}
