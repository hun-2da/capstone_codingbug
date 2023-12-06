package com.capstone.codingbug.pagerFragments;

import android.Manifest;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.capstone.codingbug.MainActivity;
import com.capstone.codingbug.R;
import com.capstone.codingbug.database_mysql.keyword.ParentUserTable;
import com.capstone.codingbug.database_mysql.keyword.UserDB;
import com.capstone.codingbug.localdb.LocalDataBaseHelper;
import com.skt.Tmap.TMapGpsManager;
import com.skt.Tmap.TMapPoint;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class SetFragment extends Fragment {
    EditText editText;
    LocalDataBaseHelper localdb;
    SQLiteDatabase ldb;
    String phone1;
    String p_phone = "";
    Switch smsSwitch;

    Handler mainHandler = new Handler(Looper.getMainLooper());

    boolean message_boolean = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_set, container, false);
        editText = view.findViewById(R.id.phone_editText);



        localdb = new LocalDataBaseHelper(getContext().getApplicationContext());// 로컬 데이터베이스 생성
        ldb = localdb.getReadableDatabase();


        smsSwitch = (Switch)view.findViewById(R.id.switch1);
        smsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    String[] columns = {LocalDataBaseHelper.parent_phone};
                    Cursor cursor = ldb.query(LocalDataBaseHelper.table, columns, null, null, null, null, null);

                    if (cursor.moveToFirst())
                        p_phone = cursor.getString(0);

                    message_boolean = true;
                    Context context = getActivity().getApplicationContext();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            sms_start(p_phone,context);
                        }
                    }).start();


                } else {
                    message_boolean = false;
                }
            }
        });

        /*String phone_number = "SELECT " + UserDB.USER_PHONE +
                " FROM " + UserDB.DATABASE_NAME + "." + UserDB.USER_TABLE_NAME + " UL" +
                " JOIN " + ParentUserTable.TABLE_NAME + " PU ON PU." + ParentUserTable.PARENT_USER_ID + " = UL." + UserDB.USER_ID +
                " WHERE PU." + ParentUserTable.MY_ID + " = ?";

        PreparedStatement pS = MainActivity.databaseConnection.create_pStatement(phone_number);
        */

        Handler mainHandler = new Handler(Looper.getMainLooper());




        //MainActivity.print(getContext().getApplicationContext(),my_id);
         //   editText.setText(p_phone);

        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.e("RDS","1");
                String[] columns = {LocalDataBaseHelper.my_id};
                Cursor cursor = ldb.query(LocalDataBaseHelper.table, columns, null, null, null, null, null);
                String my_id = "";
                if (cursor.moveToFirst())
                    my_id = cursor.getString(0);

                String query = "SELECT " + ParentUserTable.PARENT_USER_ID + " FROM " + ParentUserTable.TABLE_NAME + " WHERE " + ParentUserTable.MY_ID + " = ?";
                try {
                    PreparedStatement preparedStatement = MainActivity.databaseConnection.create_pStatement(query);
                    preparedStatement.setString(1, my_id);
                    ResultSet resultSet = preparedStatement.executeQuery();

                    if (resultSet.next()) {
                        String parentUser_phone = resultSet.getString(ParentUserTable.PARENT_USER_ID);
                        //MainActivity.print(getContext().getApplicationContext(),parentUserId);

                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                editText.setText(parentUser_phone);
                            }
                        });


                    } else {
                        Log.e("RDS","parent_phone 불러오기 실패");
                    }
                } catch (SQLException e) {
                    Log.e("sql","실패" + e.getMessage());
                }
                Log.e("RDS","2");
            }
        }).start();




            /*try {
                //pS.setString(1, myId);
                //ResultSet resultSet = pS.executeQuery();
                String phoneNumber = resultSet.getString(UserDB.USER_PHONE);
                editText.setText(phoneNumber);

            } catch (SQLException e) {
                Log.e("로컬디비","parent_phone 불러오기 실패");
            }*/
        //}




        //editText.setText();*/


//-------------------------------------------if 안에서 RDS에 존재하는지 여부 확인 , 메시지 read해서 maker찍기 확인

        Button button = view.findViewById(R.id.phone_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {Log.e("RDS","3");
              /*
                String[] columns = {LocalDataBaseHelper.my_id};
                Cursor cursor = ldb.query(LocalDataBaseHelper.table, columns, null, null, null, null, null);
                if (cursor.moveToFirst()) {
                    String p_phone = cursor.getString(0);
                }

                    ContentValues values = new ContentValues();
                    values.put("parent_mobile", editText.getText().toString());

                    int count = ldb.update("user_log", values, null, null);
                    if(count == 0) Log.e("로컬디비","update실패");
                    */
                phone1 = editText.getText().toString();
                String query = "SELECT * FROM user_log WHERE user_phone = ?";
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String m_id = "";
                        try {
                            Log.e("RDS","번호찾기 쿼리1");
                            PreparedStatement preparedStatement = MainActivity.databaseConnection.create_pStatement(query);
                            preparedStatement.setString(1, phone1);
                            ResultSet resultSet = preparedStatement.executeQuery();
                            Log.e("RDS","번호찾기 쿼리2");

                            if (resultSet.next()) {//phone을 가진 id가 존재하지 않으면 저장하지 않음
                                String[] columns = {LocalDataBaseHelper.my_id};
                                Cursor cursor = ldb.query(LocalDataBaseHelper.table, columns, null, null, null, null, null);
                                if (cursor.moveToFirst()) {
                                    m_id = cursor.getString(0);

                                    String newParentPhone = phone1;

                                    SQLiteDatabase db = localdb.getWritableDatabase();
                                    ContentValues values = new ContentValues();

                                    values.put(LocalDataBaseHelper.parent_phone, newParentPhone);

                                    String whereClause = LocalDataBaseHelper.my_id + " = ?";
                                    String[] whereArgs = { m_id };

                                    int count = db.update(LocalDataBaseHelper.table, values, whereClause, whereArgs);

                                    if (count == 0) {
                                        Log.e("로컬디비","버튼 update실패");
                                    } else {
                                        // 업데이트 성공
                                    }

                                }

                            } else {
                                Log.e("rds","해당 폰 번호가 없음");
                                mainHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        MainActivity.print(getActivity().getApplicationContext(),"해당 번호는 가입되지 않은 휴대폰 번호입니다.");
                                    }
                                });

                            }
                        } catch (SQLException e) {
                            Log.e("로컬디비","SQLException");
                            return;

                        }
                        /*try{
                            Log.e("RDS","INSERT 쿼리1");
                            String my_query = "INSERT INTO " + ParentUserTable.TABLE_NAME + " VALUES (?, ?)";
                            //String my_query = "INSERT INTO " + ParentUserTable.TABLE_NAME + " (" + ParentUserTable.MY_ID + ", " + ParentUserTable.PARENT_USER_ID + ") VALUES (?, ?)";
                            PreparedStatement pS2 = MainActivity.databaseConnection.create_pStatement(my_query);
                            pS2.setString(1, m_id);
                            pS2.setString(2, phone1);
                            Log.e("RDS","INSERT 쿼리2");
                            pS2.executeUpdate();
                        }catch(SQLException e){
                            Log.e("RDS","INSERT 실패" + e.getMessage());
                            try {
                                String myQuery = "UPDATE " + ParentUserTable.TABLE_NAME + " SET column1 = ?, column2 = ? WHERE condition";
                               // String myQuery = "UPDATE " + ParentUserTable.TABLE_NAME + " SET " + ParentUserTable.PARENT_USER_ID + " = ? WHERE " + ParentUserTable.MY_ID + " = ?";
                                PreparedStatement pS2 = MainActivity.databaseConnection.create_pStatement(myQuery);
                                pS2.setString(1, m_id);
                                pS2.setString(2, phone1);
                                Log.e("RDS", "UPDATE 쿼리");
                                pS2.executeUpdate();
                            } catch(SQLException e2){
                                Log.e("RDS","update 실패" +  e2.getMessage());
                            }
                        }*/
                        try{
                        String myid_query = "SELECT * FROM "+ParentUserTable.TABLE_NAME+" WHERE "+ParentUserTable.MY_ID+" = ?"; //RDS에 이미 내 아이디가 존재하는지 확인
                        PreparedStatement ps_myid = MainActivity.databaseConnection.create_pStatement(myid_query);
                        ps_myid.setString(1, m_id);
                        ResultSet resultSet = ps_myid.executeQuery();
                        Log.e("RDS","번호찾기 쿼리2");

                        if (resultSet.next()) {
                            //동일한 id가 존재하는 경우 업데이트
                            try {
                                String myQuery = "UPDATE " + ParentUserTable.TABLE_NAME + " SET parent_phone = ? WHERE my_id = ?";
                                PreparedStatement pS2 = MainActivity.databaseConnection.create_pStatement(myQuery);
                                pS2.setString(1, phone1);
                                pS2.setString(2, m_id);
                                Log.e("RDS", "UPDATE 쿼리");
                                pS2.executeUpdate();
                            } catch(SQLException e2){
                                Log.e("RDS","update 실패" +  e2.getMessage());
                            }
                        }else{
                            //없다면 추가
                            Log.e("RDS","INSERT 쿼리1");
                            String my_query = "INSERT INTO " + ParentUserTable.TABLE_NAME + " (my_id, parent_phone) VALUES (?, ?)";
                            PreparedStatement pS2 = MainActivity.databaseConnection.create_pStatement(my_query);
                            pS2.setString(1, m_id);
                            pS2.setString(2, phone1);
                            Log.e("RDS","INSERT 쿼리2");
                            pS2.executeUpdate();
                            }
                        }catch(SQLException e){
                            Log.e("RDS","INSERT 실패" + e.getMessage());

                        }
                    }
                }).start();
            }
        });


        /**로그아웃 버튼. */
        Button out_button = view.findViewById(R.id.logout_button);
        out_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(getActivity())
                        .setTitle("로그아웃 하시겠습니까?")
                        .setMessage("로그아웃하면 기록이 전부 사라집니다\n로그아웃 하시겠습니까?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                // 'Yes' 버튼 클릭 시 수행할 작업을 여기에 작성합니다.
                                Toast.makeText(getActivity().getApplicationContext(), "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();

                                if(!delete_db())
                                    Log.e("로컬디비","delete실패");
                                Intent i = getActivity().getBaseContext().getPackageManager()
                                        .getLaunchIntentForPackage( getActivity().getBaseContext().getPackageName());
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(i);

                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                // 'No' 버튼 클릭 시 수행할 작업을 여기에 작성합니다.
                            }
                        })
                        .show();
            }
        });

        /**회원 탈퇴 버튼*/
        Button delete_button = view.findViewById(R.id.delete_button);
        delete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new AlertDialog.Builder(getActivity())
                        .setTitle("탈퇴...하시겠습니까?")
                        .setMessage("정말로?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                // 'Yes' 버튼 클릭 시 수행할 작업을 여기에 작성합니다.

                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        String[] columns = {LocalDataBaseHelper.my_id};
                                        Cursor cursor = ldb.query(LocalDataBaseHelper.table, columns, null, null, null, null, null);
                                        String my_id = "";
                                        if (cursor.moveToFirst())
                                            my_id = cursor.getString(0);

                                        try {
                                            String t2_query = "DELETE FROM " + ParentUserTable.TABLE_NAME + " WHERE " + ParentUserTable.MY_ID + " = ?";
                                            PreparedStatement pstmt2 = MainActivity.databaseConnection.create_pStatement(t2_query);
                                            pstmt2.setString(1, my_id);
                                            pstmt2.executeUpdate();

                                            String t1_query = "DELETE FROM " + UserDB.USER_TABLE_NAME + " WHERE " + UserDB.USER_ID + " = ?";
                                            PreparedStatement pstmt = MainActivity.databaseConnection.create_pStatement(t1_query);
                                            pstmt.setString(1, my_id);
                                            pstmt.executeUpdate();



                                            if (!delete_db())
                                                Log.e("로컬디비", "delete실패");
                                            Intent i = getActivity().getBaseContext().getPackageManager()
                                                    .getLaunchIntentForPackage(getActivity().getBaseContext().getPackageName());
                                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(i);

                                        }catch(SQLException e){
                                            Log.e("RDS","회원정보 삭제 실패" + e.getMessage());

                                        }
                                    }
                                }).start();

                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                // 'No' 버튼 클릭 시 수행할 작업을 여기에 작성합니다.
                            }
                        })
                        .show();
            }
        });


        return view;
    }

    /*Handler mainHandler = new Handler(Looper.getMainLooper());
    public void sms_start(String phone,Context context){
        while(message_boolean){
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    TMapGpsManager tMapGps = new TMapGpsManager(context);
                    tMapGps.setMinTime(2000);
                    tMapGps.setMinDistance(5);
                    tMapGps.setProvider(tMapGps.GPS_PROVIDER);
                    tMapGps.OpenGps();
                    TMapPoint point = tMapGps.getLocation();
                    double latitude = point.getLatitude();
                    double longitude = point.getLongitude();

                    String message = "안심귀갓길 : ("+latitude+","+longitude+")";
                    sendSMS(phone,message);
                }
            });



            try {
                Thread.sleep(1000);  // 3분
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }*/

    double latitude = 0;
    double longitude = 0;

    public void sms_start(String phone, Context context) {


        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();

            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };

        while (message_boolean) {
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED
                            && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {

                        return;
                    }
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 5, locationListener);
                }
            });
            try {
                Thread.sleep(6000);
                String message = "안심귀갓길 : (" + latitude + "," + longitude + ")";
                sendSMS(context, phone, message);


                Thread.sleep(294000);  // 총 5분에 한번씩
            }catch(Exception e){}

        }//asd
    }//asdfe
    public void sendSMS(Context context,String phoneNumber, String message) {
        SmsManager smsManager = SmsManager.getDefault();
        PendingIntent sentPI;
        String SENT = "SMS_SENT";

        sentPI = PendingIntent.getBroadcast(context, 0,new Intent(SENT), PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);

        smsManager.sendTextMessage(phoneNumber, null, message, sentPI, null);
    }
    public boolean delete_db(){
        boolean result = getActivity().getApplicationContext().deleteDatabase(LocalDataBaseHelper.NAME);
        return result;
    }
}