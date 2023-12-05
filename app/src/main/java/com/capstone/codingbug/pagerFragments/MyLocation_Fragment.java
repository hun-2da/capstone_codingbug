package com.capstone.codingbug.pagerFragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapPolyLine;
import com.skt.Tmap.TMapView;
import com.skt.Tmap.TMapPolygon;

import com.capstone.codingbug.R;

public class MyLocation_Fragment extends Fragment {
    private TMapView tMapView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_my_location_, container, false);

        LinearLayout linearLayoutTmap = (LinearLayout) view.findViewById(R.id.llt);
        tMapView = new TMapView(getActivity().getApplicationContext());

        tMapView.setSKTMapApiKey("6QqIU9fnZUao65WJCM7ptafry6XfQovT1PoVoB4a");
        linearLayoutTmap.addView(tMapView);

        // Inflate the layout for this fragment
        return view;
    }
}