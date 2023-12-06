package com.capstone.codingbug.pagerFragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.os.AsyncTask;

import android.widget.Button;
import android.widget.EditText;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.graphics.Color;
import androidx.appcompat.app.AppCompatActivity;

import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapPolyLine;
import com.skt.Tmap.TMapView;
import com.skt.Tmap.TMapPolygon;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import com.capstone.codingbug.R;

public class MyLocation_Fragment extends Fragment {

    private EditText editText1;
    private EditText editText2;
    private TMapView tMapView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_my_location_, container, false);

        editText1 = view.findViewById(R.id.editText1);
        editText2 = view.findViewById(R.id.editText2);

        Button button1 = view.findViewById(R.id.button1);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String s1 = editText1.getText().toString();
                String s2 = editText2.getText().toString();
                new NetworkTask().execute(s1, s2);
            }
        });

        LinearLayout linearLayoutTmap = (LinearLayout) view.findViewById(R.id.llt);
        tMapView = new TMapView(getActivity().getApplicationContext());

        tMapView.setSKTMapApiKey("6QqIU9fnZUao65WJCM7ptafry6XfQovT1PoVoB4a");
        linearLayoutTmap.addView(tMapView);

        // Inflate the layout for this fragment
        return view;
    }

    private class NetworkTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            try {
                OkHttpClient client = new OkHttpClient();

                TMapPoint startPoint = getCoordinate(params[0]);
                TMapPoint endPoint = getCoordinate(params[1]);

                MediaType mediaType = MediaType.parse("application/json");
                RequestBody body = RequestBody.create(mediaType, "{" +
                        "\"startX\":" + startPoint.getLongitude() + "," +
                        "\"startY\":" + startPoint.getLatitude() + "," +
                        "\"endX\":" + endPoint.getLongitude() + "," +
                        "\"endY\":" + endPoint.getLatitude() + "," +
                        "\"angle\":20,\"speed\":30," +
                        "\"endPoiId\":\"10001\"," +
                        "\"reqCoordType\":\"WGS84GEO\"," +
                        "\"startName\":\"%EC%B6%9C%EB%B0%9C\"," +
                        "\"endName\":\"%EB%8F%84%EC%B0%A9\"," +
                        "\"searchOption\":\"10\"," +
                        "\"resCoordType\":\"WGS84GEO\"," +
                        "\"sort\":\"index\"}");

                Request request = new Request.Builder()
                        .url("https://apis.openapi.sk.com/tmap/routes/pedestrian?version=1&callback=function")
                        .post(body)
                        .addHeader("accept", "application/json")
                        .addHeader("content-type", "application/json")
                        .addHeader("appKey", "6QqIU9fnZUao65WJCM7ptafry6XfQovT1PoVoB4a")
                        .build();

                Response response = client.newCall(request).execute();
                return response.body().string();

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String jsonString) {
            if (jsonString != null) {
                try {
                    JSONObject jsonObject = new JSONObject(jsonString);

                    if (jsonObject.has("features")) {
                        JSONArray features = jsonObject.getJSONArray("features");

                        ArrayList<TMapPoint> pointList = new ArrayList<>();

                        for (int i = 0; i < features.length(); i++) {
                            JSONObject feature = features.getJSONObject(i);
                            JSONObject geometry = feature.getJSONObject("geometry");
                            String type = geometry.getString("type");

                            if ("Point".equals(type)) {
                                JSONArray coordinates = geometry.getJSONArray("coordinates");
                                double latitude = coordinates.getDouble(1);
                                double longitude = coordinates.getDouble(0);
                                pointList.add(new TMapPoint(latitude, longitude));
                            } else if ("LineString".equals(type)) {
                                JSONArray coordinates = geometry.getJSONArray("coordinates");
                                for (int j = 0; j < coordinates.length(); j++) {
                                    JSONArray point = coordinates.getJSONArray(j);
                                    double latitude = point.getDouble(1);
                                    double longitude = point.getDouble(0);
                                    pointList.add(new TMapPoint(latitude, longitude));
                                }
                            }
                        }

                        TMapPolyLine line = new TMapPolyLine();
                        line.setLineColor(Color.BLUE);

                        for (TMapPoint point : pointList) {
                            line.addLinePoint(point);
                        }
                        tMapView.addTMapPolyLine("line1", line);

                        if (!pointList.isEmpty()) {
                            TMapPoint centerPoint = pointList.get(0);
                            tMapView.setCenterPoint(centerPoint.getLongitude(), centerPoint.getLatitude());
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private TMapPoint getCoordinate(String address) {
        try {
            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url("https://apis.openapi.sk.com/tmap/geo/convertAddress?version=1&searchTypCd=NtoO&reqAdd=" + address + "&reqMulti=S&resCoordType=WGS84GEO")
                    .get()
                    .addHeader("accept", "application/json")
                    .addHeader("appKey", "e8wHh2tya84M88aReEpXCa5XTQf3xgo01aZG39k5")
                    .build();

            Response response = client.newCall(request).execute();
            String jsonString = response.body().string();

            JSONObject jsonObject = new JSONObject(jsonString);
            JSONObject convertAdd = jsonObject.getJSONObject("ConvertAdd");
            JSONObject newAddress = convertAdd.getJSONObject("newAddressList");
            JSONArray newLatLonArray = newAddress.getJSONArray("newAddress");
            JSONObject newLatLon = newLatLonArray.getJSONObject(0);
            double newLat = newLatLon.getDouble("newLat");
            double newLon = newLatLon.getDouble("newLon");

            return new TMapPoint(newLat, newLon);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}