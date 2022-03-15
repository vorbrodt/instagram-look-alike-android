package com.example.memeroom.LoggedIn.MenuFragments.ProfileFragments;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.memeroom.LoggedIn.MemeRoomActivity;
import com.example.memeroom.LoggedIn.MenuFragments.MenuResponseHandlers.DisplayProfile;

import com.example.memeroom.R;


/*
Fragment used for displaying a profile on the application 
*/
public class ProfileFragment extends Fragment{
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Context mContext = inflater.getContext();
        String email = ((MemeRoomActivity) mContext).getUserEmail();
        String token = ((MemeRoomActivity) mContext).getUserToken();
        DisplayProfile dP = new DisplayProfile(email,token);
        dP.profileInfoDelegate = ((MemeRoomActivity) mContext);
        dP.execute();


        return inflater.inflate(R.layout.fragment_profile, container,false);
    }


}

