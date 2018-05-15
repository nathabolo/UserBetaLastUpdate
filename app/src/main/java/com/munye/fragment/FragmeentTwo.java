package com.munye.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.munye.user.R;

public class FragmeentTwo extends Fragment {

    public static FragmeentTwo newInstance() {
        FragmeentTwo fragment = new FragmeentTwo();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_fragmeent_two, container, false);
    }

}
