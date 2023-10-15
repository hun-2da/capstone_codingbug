package com.capstone.codingbug;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class sign_in extends AppCompatActivity {
    private static final String TAG = "dialog01";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        // 로그인 버튼
        Button btn_login = findViewById(R.id.btn_login);
        EditText edit_id = findViewById(R.id.edit_id);
        EditText edit_pw = findViewById(R.id.edit_pw);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // EditText로부터 입력된 값을 받아온다
                String id = edit_id.getText().toString();
                String pw = edit_pw.getText().toString();

                // SharedPreferences로부터 저장된 id, pw 가져오기
                SharedPreferences sharedPreference = getSharedPreferences("file name", Context.MODE_PRIVATE);
                String savedId = sharedPreference.getString("id", "");
                String savedPw = sharedPreference.getString("pw", "");

                // 유저가 입력한 id, pw값과 SharedPreferences로 불러온 id, pw값 비교
                if (id.equals(savedId) && pw.equals(savedPw)&&(id != "" || pw != "")) {
                    // 로그인 성공 다이얼로그 보여주기
                    dialog("success");
                    new MainActivity().set_fragment();
                    Intent intent = new Intent(sign_in.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    // 로그인 실패 다이얼로그 보여주기
                    dialog("fail");
                }
            }
        });

        // 회원가입 버튼
        Button btn_register = findViewById(R.id.btn_register);
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(sign_in.this, sign_up.class);
                startActivity(intent);
            }
        });
    }

    // 로그인 성공/실패 시 다이얼로그를 띄워주는 메소드
    private void dialog(String type) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        if (type.equals("success")) {
            dialog.setTitle("로그인 성공");
            dialog.setMessage("로그인 성공!");
        } else if (type.equals("fail")) {
            dialog.setTitle("로그인 실패");
            dialog.setMessage("아이디와 비밀번호를 확인해주세요");
        }

        DialogInterface.OnClickListener dialogListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == DialogInterface.BUTTON_POSITIVE) {
                    Log.d(TAG, "다이얼로그 확인 버튼 클릭");
                }
            }
        };

        dialog.setPositiveButton("확인", dialogListener);
        dialog.show();
    }
}
