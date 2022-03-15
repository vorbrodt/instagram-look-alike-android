package com.example.memeroom.LoggedIn.MenuFragments.MenuResponseHandlers;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.memeroom.R;
/*
If search profile is not found this fragment is displayed.
*/
public class UserNotFound extends Fragment {
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_not_found, container, false);
    }

}
