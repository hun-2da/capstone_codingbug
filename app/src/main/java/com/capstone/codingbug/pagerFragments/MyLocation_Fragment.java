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

        View view = inflater.inflate(R.layout.fragment_my_location_, container, false);

        LinearLayout linearLayoutTmap = view.findViewById(R.id.linearLayoutTmap);
        tMapView = new TMapView(getActivity());

        tMapView.setSKTMapApiKey("6QqIU9fnZUao65WJCM7ptafry6XfQovT1PoVoB4a");
        linearLayoutTmap.addView(tMapView);

        // Inflate the layout for this fragment
        return view;
    }
}