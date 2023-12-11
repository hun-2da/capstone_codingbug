package com.capstone.codingbug.pagerFragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.os.AsyncTask;

import android.widget.Button;
import android.widget.EditText;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.graphics.Color;
import androidx.appcompat.app.AppCompatActivity;

import com.skt.Tmap.TMapCircle;
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

import com.capstone.codingbug.database_mysql.DatabaseConnection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class MyLocation_Fragment extends Fragment {

    private EditText editText1;
    private EditText editText2;
    private TMapView tMapView;

    private TMapPolygon tMapPolygon;
    private ArrayList<TMapPoint> alTMapPoint;

    /**입력 주소창을 찾기 위한 띄워줄 frameLayout*/
    FrameLayout frameLayout;

    Button path_button;

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


        path_button = view.findViewById(R.id.path_Button);
        frameLayout = view.findViewById(R.id.path_frameLayout);

        /**주소창 닫기 버튼 클릭시*/
        view.findViewById(R.id.path_close_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                frameLayout.setVisibility(View.INVISIBLE);
               path_button.setVisibility(View.VISIBLE);
            }
        });

        /**주소 띄우기 버튼*/
        path_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                frameLayout.setVisibility(View.VISIBLE);
                path_button.setVisibility(View.INVISIBLE);
            }
        });



        LinearLayout linearLayoutTmap = (LinearLayout) view.findViewById(R.id.llt);
        tMapView = new TMapView(getActivity().getApplicationContext());

        tMapView.setSKTMapApiKey("6QqIU9fnZUao65WJCM7ptafry6XfQovT1PoVoB4a");
        linearLayoutTmap.addView(tMapView);

        //위험지역 원으로 표시
        new FetchDangerZoneDataTask().execute();

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

                double px = -((endPoint.getLongitude() - startPoint.getLongitude())*0.3);
                double py = (endPoint.getLatitude() - startPoint.getLatitude())*0.3;

                ArrayList<TMapPoint> alTMapPoint = new ArrayList<>();

                alTMapPoint.add(new TMapPoint(startPoint.getLatitude()+px+py, startPoint.getLongitude()+py+px));
                alTMapPoint.add(new TMapPoint(endPoint.getLatitude()+px-py, endPoint.getLongitude()+py-px));
                alTMapPoint.add(new TMapPoint(endPoint.getLatitude()-px-py, endPoint.getLongitude()-py-px));
                alTMapPoint.add(new TMapPoint(startPoint.getLatitude()-px+py, startPoint.getLongitude()-py+px));


                tMapPolygon = new TMapPolygon();
                tMapPolygon.setLineColor(Color.BLUE);
                tMapPolygon.setPolygonWidth(2);
                tMapPolygon.setAreaColor(Color.GRAY);
                tMapPolygon.setAreaAlpha(100);
                for (int i = 0; i < alTMapPoint.size(); i++) {
                    tMapPolygon.addPolygonPoint(alTMapPoint.get(i));
                }
                tMapView.addTMapPolygon("Line1", tMapPolygon);

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

                            alTMapPoint = new ArrayList<>(pointList);
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

    //위험지역 정보 Circle로 그리기
    private class FetchDangerZoneDataTask extends AsyncTask<Void, Void, ArrayList<DangerZoneInfo>> {

        @Override
        protected ArrayList<DangerZoneInfo> doInBackground(Void... voids) {
            // 데이터베이스에서 위험 지역 데이터 가져오기
            ArrayList<DangerZoneInfo> dangerZoneList = new ArrayList<>();

            try {
                DatabaseConnection dbConnection = new DatabaseConnection(getActivity());
                Statement statement = dbConnection.get_mysql();
                ResultSet resultSet = statement.executeQuery("SELECT latitude, longitude, level FROM Danger_Location");

                while (resultSet.next()) {
                    double latitude = resultSet.getDouble("latitude");
                    double longitude = resultSet.getDouble("longitude");
                    int level = resultSet.getInt("level");
                    dangerZoneList.add(new DangerZoneInfo(latitude, longitude, level));
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }

            return dangerZoneList;
        }

        // 위험 지역을 나타내는 TMapCircle 추가
        @Override
        protected void onPostExecute(ArrayList<DangerZoneInfo> dangerZoneList) {
            int circleIndex = 1;
            for (DangerZoneInfo dangerZoneInfo : dangerZoneList) {
                TMapCircle dangerZoneCircle = new TMapCircle();
                dangerZoneCircle.setCenterPoint(new TMapPoint(dangerZoneInfo.getLatitude(), dangerZoneInfo.getLongitude()));
                dangerZoneCircle.setRadius(100);
                dangerZoneCircle.setCircleWidth(2);

                // level에 따라 원의 색깔을 설정
                int level = dangerZoneInfo.getLevel();
                switch (level) {
                    case 4:
                        dangerZoneCircle.setLineColor(Color.rgb(255, 179, 0));
                        dangerZoneCircle.setAreaColor(Color.rgb(255, 179, 0));
                        break;
                    case 5:
                        dangerZoneCircle.setLineColor(Color.rgb(255, 154, 0));
                        dangerZoneCircle.setAreaColor(Color.rgb(255, 154, 0));
                        break;
                    case 6:
                        dangerZoneCircle.setLineColor(Color.rgb(255, 102, 0));
                        dangerZoneCircle.setAreaColor(Color.rgb(255, 102, 0));
                        break;
                    case 7:
                        dangerZoneCircle.setLineColor(Color.rgb(255, 68, 0));
                        dangerZoneCircle.setAreaColor(Color.rgb(255, 68, 0));
                        break;
                    case 8:
                        dangerZoneCircle.setLineColor(Color.rgb(255, 0, 0));
                        dangerZoneCircle.setAreaColor(Color.rgb(255, 0, 0));
                        break;
                    case 9:
                        dangerZoneCircle.setLineColor(Color.rgb(153, 0, 0));
                        dangerZoneCircle.setAreaColor(Color.rgb(153, 0, 0));
                        break;
                    case 10:
                        dangerZoneCircle.setLineColor(Color.rgb(51, 0, 0));
                        dangerZoneCircle.setAreaColor(Color.rgb(51, 0, 0));
                        break;
                    default:
                        dangerZoneCircle.setLineColor(Color.BLUE);
                        dangerZoneCircle.setAreaColor(Color.GRAY);
                }

                dangerZoneCircle.setAreaAlpha(100);
                tMapView.addTMapCircle("circle" + circleIndex, dangerZoneCircle);
                circleIndex++;

                if (alTMapPoint != null) {
                    for (DangerZoneInfo dangerZone: dangerZoneList) {
                        TMapPoint dangerZonePoint = new TMapPoint(dangerZone.getLatitude(), dangerZone.getLongitude());

                        if (tMapPolygon != null && isPointInsidePolygon(dangerZonePoint, tMapPolygon.getPolygonPoint())==true){
                            ArrayList<TMapPoint> polygonPoints = new ArrayList<>();
                            polygonPoints.add(new TMapPoint(dangerZonePoint.getLatitude() + 0.01, dangerZonePoint.getLongitude() + 0.01));
                            polygonPoints.add(new TMapPoint(dangerZonePoint.getLatitude() - 0.01, dangerZonePoint.getLongitude() + 0.01));
                            polygonPoints.add(new TMapPoint(dangerZonePoint.getLatitude() - 0.01, dangerZonePoint.getLongitude() - 0.01));
                            polygonPoints.add(new TMapPoint(dangerZonePoint.getLatitude() + 0.01, dangerZonePoint.getLongitude() - 0.01));

                            TMapPolygon tMapPolygon = new TMapPolygon();
                            tMapPolygon.setLineColor(Color.RED);
                            tMapPolygon.setPolygonWidth(2);
                            tMapPolygon.setAreaColor(Color.GRAY);
                            tMapPolygon.setAreaAlpha(100);
                            for (int i = 0; i < polygonPoints.size(); i++) {
                                tMapPolygon.addPolygonPoint(polygonPoints.get(i));
                            }
                            tMapView.addTMapPolygon("Line1", tMapPolygon);
                        }
                    }
                }

            }

        }

        // 점이 다각형 안에 속하는지 확인하는 메서드
        private boolean isPointInsidePolygon(TMapPoint point, ArrayList<TMapPoint> polygonPoints) {
            if(((polygonPoints.get(0).getLatitude()<=point.getLatitude()&&point.getLatitude()<=polygonPoints.get(1).getLatitude())&&
                    ((polygonPoints.get(0).getLongitude()<=point.getLongitude()&&point.getLongitude()<=polygonPoints.get(1).getLongitude())))||
                    ((polygonPoints.get(0).getLatitude()>=point.getLatitude()&&point.getLatitude()>=polygonPoints.get(1).getLatitude())&&
                    ((polygonPoints.get(0).getLongitude()>=point.getLongitude()&&point.getLongitude()>=polygonPoints.get(1).getLongitude()))))
                return true;
            else
                return false;
        }

    }

    // 위험 지역 정보를 담는 클래스
    class DangerZoneInfo {
        private double latitude;
        private double longitude;
        private int level;

        public DangerZoneInfo(double latitude, double longitude, int level) {
            this.latitude = latitude;
            this.longitude = longitude;
            this.level = level;
        }

        public double getLatitude() {
            return latitude;
        }

        public double getLongitude() {
            return longitude;
        }

        public int getLevel() {
            return level;
        }
    }

}