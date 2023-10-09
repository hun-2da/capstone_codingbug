package com.capstone.codingbug;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class dialog01 extends AppCompatActivity {

    private static final String TAG = "dialog01";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog01);
        setLoginDialog();
    }

    // 로그인 다이얼로그를 띄우는 함수
    private void setLoginDialog() {
        // 다이얼로그 레이아웃 초기화 및 설정
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("로그인");
        dialog.setView(R.layout.dialog01); // 로그인 다이얼로그 레이아웃 설정

        // 로그인 버튼 클릭 리스너 설정
        dialog.setPositiveButton("로그인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // 다이얼로그에서 입력된 값을 처리하는 코드 작성
                EditText edit_id = ((AlertDialog) dialogInterface).findViewById(R.id.edit_id);
                EditText edit_pw = ((AlertDialog) dialogInterface).findViewById(R.id.edit_pw);

                String id = edit_id.getText().toString();
                String pw = edit_pw.getText().toString();

                // SharedPreferences로부터 저장된 id, pw 가져오기
                SharedPreferences sharedPreference = getSharedPreferences("file name", Context.MODE_PRIVATE);
                String savedId = sharedPreference.getString("id", "");
                String savedPw = sharedPreference.getString("pw", "");

                // 유저가 입력한 id, pw값과 SharedPreferences로 불러온 id, pw값 비교
                if (id.equals(savedId) && pw.equals(savedPw)) {
                    // 로그인 성공 다이얼로그 보여주기
                    showResultDialog("success");
                } else {
                    // 로그인 실패 다이얼로그 보여주기
                    showResultDialog("fail");
                }
            }
        });

        // 다이얼로그 생성 및 표시
        AlertDialog alertDialog = dialog.create();
        alertDialog.show();
    }

    // 로그인 결과 다이얼로그를 표시하는 함수
    private void showResultDialog(String type) {
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

