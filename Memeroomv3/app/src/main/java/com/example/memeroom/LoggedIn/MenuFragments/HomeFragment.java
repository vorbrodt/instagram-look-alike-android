package com.example.memeroom.LoggedIn.MenuFragments;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.memeroom.LoggedIn.MemeRoomActivity;
import com.example.memeroom.LoggedIn.MenuFragments.MenuResponseHandlers.GetNewsFeed;
import com.example.memeroom.R;

/*
Fragment shown when user press "Home" menu item and 
display user's NewsFeed
*/
public class HomeFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Context mContext = inflater.getContext();
        String email = ((MemeRoomActivity) mContext).getUserEmail();
        String token = ((MemeRoomActivity) mContext).getUserToken();
        GetNewsFeed gnf = new GetNewsFeed(email,token);


        gnf.profileInfoDelegate = ((MemeRoomActivity) mContext);
        gnf.execute();



        return inflater.inflate(R.layout.fragment_home, container,false);
    }
}
