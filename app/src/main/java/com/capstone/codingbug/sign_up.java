package com.capstone.codingbug;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

public class sign_up extends AppCompatActivity {

    private static final String TAG = "sign_up";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        Button btn_register2 = findViewById(R.id.btn_register);
        EditText edit_name = findViewById(R.id.edit_name);
        EditText edit_pn = findViewById(R.id.edit_pn);
        EditText edit_pw = findViewById(R.id.edit_pw);
        EditText edit_pw_re = findViewById(R.id.edit_pw_re);

        btn_register2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "회원가입 버튼 클릭");

                String name = edit_name.getText().toString();
                String pn = edit_pn.getText().toString();
                String pw = edit_pw.getText().toString();
                String pw_re = edit_pw_re.getText().toString();

                if (name.isEmpty() || pn.isEmpty() || pw.isEmpty() || pw_re.isEmpty()) {
                    dialog("blank");
                } else if (!pw.equals(pw_re)) {
                    dialog("not same");
                } else {
                    // 회원가입 성공
                    User newUser = new User();
                    newUser.setUsername(name);
                    newUser.setPn(Integer.parseInt(pn));
                    newUser.setPw(pw);

                    // Room 데이터베이스에 사용자 추가
                    AppDatabase db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "app-database").allowMainThreadQueries().build();
                    UserDao userDao = db.userDao();
                    userDao.insertUser(newUser);

                    Toast.makeText(sign_up.this, "회원가입 성공", Toast.LENGTH_SHORT).show();

                    // 나머지 코드 (성공적인 회원가입 후 실행)
                    Intent intent = new Intent(sign_up.this, sign_in.class);
                    startActivity(intent);
                    finish();
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