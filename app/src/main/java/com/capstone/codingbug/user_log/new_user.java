package com.capstone.codingbug.user_log;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.capstone.codingbug.MainActivity;
import com.capstone.codingbug.R;
import com.capstone.codingbug.database_mysql.keyword.UserDB;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class new_user extends AppCompatActivity {
    private boolean isExistBlank = false;
    private boolean isPWSame = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newuser);

        Button btn_register1 = findViewById(R.id.btn_register);

        EditText edit_id = findViewById(R.id.edit_id);
        EditText edit_name = findViewById(R.id.edit_name);
        EditText edit_pn = findViewById(R.id.edit_pn);
        EditText edit_pw = findViewById(R.id.edit_pw);
        EditText edit_pw_re = findViewById(R.id.edit_pw_re);
        EditText user_mail = findViewById(R.id.email_edittext);

        btn_register1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = edit_id.getText().toString();
                String name = edit_name.getText().toString();
                String pn = edit_pn.getText().toString();
                String pw = edit_pw.getText().toString();
                String pw_re = edit_pw_re.getText().toString();
                String mail_s = user_mail.getText().toString();

                if (id.isEmpty() || pw.isEmpty() || pw_re.isEmpty()) {
                    isExistBlank = true;
                } else {
                    if (pw.equals(pw_re)) {
                        isPWSame = true;
                    }
                }

                if (!isExistBlank && isPWSame) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Statement mydb_query = MainActivity.statement; // 연결해 두었던 Statement 객체를 mainActivity에서 불러옴

                            try {
                                /*String query =
                                        "SELECT * FROM "
                                                + UserDB.USER_TABLE_NAME+
                                                " WHERE "+
                                                UserDB.USER_ID+ " = '" + id + "' ";

                                ResultSet resultSet = mydb_query.executeQuery(query);*/
                                String query = "SELECT * FROM " + UserDB.USER_TABLE_NAME + " WHERE " + UserDB.USER_ID + " = ?";
                                PreparedStatement pS = MainActivity.databaseConnection.create_pStatement(query);
                                pS.setString(1, id);
                                ResultSet resultSet = pS.executeQuery();

                                if(!resultSet.next()){
                                    /*String my_query = "insert into " + UserDB.USER_TABLE_NAME +
                                            " value ('"+id+"','"+pn+"','"+name+"','"+mail_s+"','"+pn+"')";
                                    mydb_query.executeUpdate(my_query);*/
                                    String my_query = "INSERT INTO " + UserDB.USER_TABLE_NAME + " VALUES (?, ?, ?, ?, ?)";
                                    PreparedStatement pS2 = MainActivity.databaseConnection.create_pStatement(my_query);
                                    pS2.setString(1, id);
                                    pS2.setString(2, pw);
                                    pS2.setString(3, name);
                                    pS2.setString(4, mail_s);
                                    pS2.setString(5, pn);
                                    pS2.executeUpdate();


                                    Log.d("회원가입","성공");
                                    finish();

                                }else{
                                    Log.e("회원가입","실패");
                                    Handler handler = new Handler(Looper.getMainLooper());
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            MainActivity.print(getApplicationContext(),"존재하는 ID입니다. ID를 변경해 주십시오");
                                        }
                                    });

                                }

                            }catch(SQLException exception){
                                Log.e("에러 발생","쿼리에 문제가 생김 다시 확인 바람 : " + exception.getMessage());
                            }
                        }
                    }).start();

                } else {
                    if (isExistBlank) {
                        dialog("blank");
                    } else if (!isPWSame) {
                        dialog("not same");
                    }
                }
            }
        });
        findViewById(R.id.btn_register2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


    }

    private void dialog(String type) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        if (type.equals("blank")) {
            dialog.setTitle("회원가입 실패");
            dialog.setMessage("입력란을 모두 작성해주세요");
        } else if (type.equals("not same")) {
            dialog.setTitle("회원가입 실패");
            dialog.setMessage("비밀번호가 다릅니다");
        }

        DialogInterface.OnClickListener dialog_listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == DialogInterface.BUTTON_POSITIVE) {
                    //Log.d(TAG, "다이얼로그 확인 버튼 클릭");
                }
            }
        };

        dialog.setPositiveButton("확인", dialog_listener);
        dialog.show();
    }
}