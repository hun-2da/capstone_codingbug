package com.capstone.codingbug.user_log;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.capstone.codingbug.MainActivity;
import com.capstone.codingbug.R;
import com.capstone.codingbug.database_mysql.DatabaseConnection;
import com.capstone.codingbug.database_mysql.keyword.UserDB;
import com.capstone.codingbug.localdb.LocalDataBaseHelper;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class LoginPage extends AppCompatActivity {
    Statement mydb_query=null;
    Handler handler;
    AlertDialog.Builder builder;
    TextView component;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 로그인 버튼
        Button btn_login = findViewById(R.id.btn_login);
        EditText edit_id = findViewById(R.id.edit_id);
        EditText edit_pw = findViewById(R.id.edit_pw);
        component = findViewById(R.id.componentTextview);

        handler = new Handler(Looper.getMainLooper());


        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                        String id = edit_id.getText().toString();
                        String pw = edit_pw.getText().toString();

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                mydb_query = MainActivity.statement; // 연결해 두었던 Statement 객체를 mainActivity에서 불러옴

                                        try {
                                            /*String query =
                                                    "SELECT * FROM "
                                                            +UserDB.USER_TABLE_NAME+
                                                            " WHERE "+
                                                            UserDB.USER_ID+ " = '" + id + "' AND "+
                                                            UserDB.USER_PASSWORD+" = '" + pw + "'";*/

                                            String query =
                                                    "SELECT * FROM "
                                                            +UserDB.USER_TABLE_NAME+
                                                            " WHERE "+
                                                            UserDB.USER_ID+ " = ? AND "+
                                                            UserDB.USER_PASSWORD+" = ?";


                                            PreparedStatement preparedStatement = MainActivity.databaseConnection.create_pStatement(query);
                                            preparedStatement.setString(1, id);
                                            preparedStatement.setString(2, pw);
                                            ResultSet resultSet = preparedStatement.executeQuery();

                                            //ResultSet resultSet = mydb_query.executeQuery(query);
                                            // 칼럼이 유효한 경우 true 반환
                                            if(resultSet.next()){
                                                Log.d("트라이",resultSet.getString(UserDB.USER_ID));
                                                Log.d("트라이","성공");

                                                LocalDataBaseHelper localdb = new LocalDataBaseHelper(getApplicationContext()); // 로컬 데이터베이스 생성
                                                SQLiteDatabase db = localdb.getWritableDatabase();
                                                ContentValues values = new ContentValues();
                                                values.put("my_id", id);
                                                values.put("parent_mobile", "");

                                                long newRowId = db.insert("user_log", null, values);
                                                if(newRowId == -1) Log.d("로컬디비","저장되지 못했음");

                                                Handler handler = new Handler(Looper.getMainLooper());
                                                handler.post(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                MainActivity.print(getApplicationContext(),"로그인 성공");

                                                finish();
                                                    }
                                                });
                                                //dialog("success");


                                            }else{
                                                //dialog("fail");
                                                Log.e("트라이","실패");
                                                handler.post(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        component.setText("로그인에 실패하였습니다.");
                                                    }
                                                });


                                            }

                                        }catch(SQLException exception){
                                            Log.e("에러 발생","쿼리에 문제가 생김 다시 확인 바람 : " + exception.getMessage());
                                        }

                                    }

                        }).start();
                    }
                });
                // EditText로부터 입력된 값을 받아온다

       /* edit_id.setFilters(new InputFilter[] {
                new InputFilter() {
                    @Override
                    public CharSequence filter(CharSequence source, int start, int end,
                                               Spanned dest, int dstart, int dend) {

                        String blockCharacterSet = "'";

                        for (int i = start; i < end; i++) {
                            if (blockCharacterSet.contains(String.valueOf(source.charAt(i)))) {
                                return "";
                            }
                        }
                        return null;
                    }
                }
        });*/

                // SharedPreferences로부터 저장된 id, pw 가져오기
                /*
                SharedPreferences sharedPreference = getSharedPreferences("file name", Context.MODE_PRIVATE);
                String savedId = sharedPreference.getString("id", "");
                String savedPw = sharedPreference.getString("pw", "");
                */
                // 유저가 입력한 id, pw값과 SharedPreferences로 불러온 id, pw값 비교
                /*if (id.equals(savedId) && pw.equals(savedPw)&&(id == "" || pw == "")) {
                    // 로그인 성공 다이얼로그 보여주기
                    dialog("success");
                    new MainActivity().set_fragment();
                } else {
                    // 로그인 실패 다이얼로그 보여주기
                    dialog("fail");
                }*/


        // 회원가입 버튼
        Button btn_register = findViewById(R.id.btn_register);
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), new_user.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

    }
    @Override
    public void onBackPressed() {
        Log.e("백버튼","메소드 체크");
        finishAffinity();
    }
}
