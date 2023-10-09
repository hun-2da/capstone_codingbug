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
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class sign_up extends AppCompatActivity {

    private static final String TAG = "register";
    private boolean isExistBlank = false;
    private boolean isPWSame = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        Button btn_register2 = findViewById(R.id.btn_register);
        EditText edit_id = findViewById(R.id.edit_id);
        EditText edit_pw = findViewById(R.id.edit_pw);
        EditText edit_pw_re = findViewById(R.id.edit_pw_re);

        btn_register2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "회원가입 버튼 클릭");

                String id = edit_id.getText().toString();
                String pw = edit_pw.getText().toString();
                String pw_re = edit_pw_re.getText().toString();

                if (id.isEmpty() || pw.isEmpty() || pw_re.isEmpty()) {
                    isExistBlank = true;
                } else {
                    if (pw.equals(pw_re)) {
                        isPWSame = true;
                    }
                }

                if (!isExistBlank && isPWSame) {
                    Toast.makeText(sign_up.this, "회원가입 성공", Toast.LENGTH_SHORT).show();

                    SharedPreferences sharedPreferences = getSharedPreferences("file name", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("id", id);
                    editor.putString("pw", pw);
                    editor.apply();

                    Intent intent = new Intent(sign_up.this, dialog01.class);
                    startActivity(intent);
                } else {
                    if (isExistBlank) {
                        dialog("blank");
                    } else if (!isPWSame) {
                        dialog("not same");
                    }
                }
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
                    Log.d(TAG, "다이얼로그 확인 버튼 클릭");
                }
            }
        };

        dialog.setPositiveButton("확인", dialog_listener);
        dialog.show();
    }
}