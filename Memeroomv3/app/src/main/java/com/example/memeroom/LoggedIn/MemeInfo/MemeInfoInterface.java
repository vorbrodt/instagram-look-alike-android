package com.example.memeroom.LoggedIn.MemeInfo;
/*
Interfaced used for communication between other classes and DisplayMemeActivity
*/
public interface MemeInfoInterface {
    void getMemeInfo(String title, String likes, String dislikes, String comments);
}
