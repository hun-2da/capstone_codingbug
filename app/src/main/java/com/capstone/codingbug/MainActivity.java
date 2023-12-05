package com.capstone.codingbug;

import androidx.annotation.NonNull;
import android.Manifest;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.capstone.codingbug.database_mysql.DatabaseConnection;
import com.capstone.codingbug.localdb.LocalDataBaseHelper;
import com.capstone.codingbug.pagerFragments.MyLocation_Fragment;
import com.capstone.codingbug.pagerFragments.ReadLocation_Fragment;
import com.capstone.codingbug.pagerFragments.SetFragment;
import com.capstone.codingbug.user_log.LoginPage;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;

import java.io.File;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    //ViewPager2 viewPager2;
    BottomNavigationView bottomNavigationView;
    LinearLayout container;
    Button button;

    //Dialog dialog01;
    //private Connection connection=null;
    public static Statement statement=null;
    public static DatabaseConnection  databaseConnection= null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //커스텀 다이얼 로그
        //dialog01 = new Dialog(MainActivity.this);       // Dialog 초기화

        bottomNavigationView = findViewById(R.id.bottombar);
        button = findViewById(R.id.button);

        /*로딩 화면에서 데이터베이스 connect시도 만약 실패시 무한로딩 또는 종료시키는 코드안에 추가*/
        for(int i=0;i<20;i++) { Log.e("시도중",Integer.toString(i)+"시도중");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    databaseConnection = new DatabaseConnection(getApplicationContext());
                    statement = databaseConnection.get_mysql();

                }
            }).start();
            // 현재 코드는 스레드로 날려 응답받기 전에 connection이 null인지 확인함으로 무조건 한번의 sleep이 일어남으로 수정 필요
            if(statement!=null) break;
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        }if(statement == null) finish();


        add_permission();
        //startActivity(new Intent(getApplicationContext(), dialog01.class)); 권한 승인시 다이어로그가 뜨도록 작성하여씁

        //dialog01.requestWindowFeature(Window.FEATURE_NO_TITLE); // 타이틀 제거
        //dialog01.setContentView(R.layout.dialog01);             // xml 레이아웃 파일과 연결
        //dialog01.show(); // 다이얼로그 띄우기




        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }

    /**위험 권한을 승인받기 위한 메소드*/
    public void add_permission(){
        TedPermission.create().
                setPermissionListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        Log.d("권한", "승인");

                        boolean isExist = isDatabaseExist(getApplicationContext(), LocalDataBaseHelper.NAME);

                        if(!isExist){
                            Intent intent = new Intent(getApplicationContext(), LoginPage.class);
                            //intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);

                        }

                        set_fragment();
                        /* 그냥 activity띄움 만약 customdialog로 만들꺼면 dialog를 상속받은 후 .show해서 사용, 검색시 라이브러리를 사용하는 예들이 있을 수 있으니 구분해서 사용*/
                    }
                    @Override
                    public void onPermissionDenied(List<String> deniedPermissions) {
                        Log.d("권한", "미승인");
                        finish();
                    }
                })
                .setDeniedMessage("해당 퍼미션이 없음.")
                .setPermissions(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.RECEIVE_SMS,
                        Manifest.permission.SEND_SMS)
                .check();
    }


    /**프래그먼트를 바텀 네비게션에 연결위한 메소드*/
    public void set_fragment(){
        //viewPager2 = findViewById(R.id.myviewpager); //xml에 있는 뷰페이저 연결
        //viewPager2.setOffscreenPageLimit(1);

        //MyPagerAdapter pagerAdapter = new MyPagerAdapter(getSupportFragmentManager(),getLifecycle());

        MyLocation_Fragment locationFragment = new MyLocation_Fragment();
        //pagerAdapter.addItem(locationFragment);//location fragment를 pager index0에 저장

        ReadLocation_Fragment readLocationFragment = new ReadLocation_Fragment();
        //pagerAdapter.addItem(readLocationFragment);//보호자페이지(로케이션을 읽어들여 지도에 띄워줄 fragment)

        SetFragment setFragment = new SetFragment();


        getSupportFragmentManager().beginTransaction().replace(R.id.Container,readLocationFragment).commit();

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.tab3) {
                    button.setVisibility(View.INVISIBLE);
                    //print(getApplicationContext(), "설정 탭입니다. ");
                    getSupportFragmentManager().beginTransaction().replace(R.id.Container, setFragment).commit();
                    return true;
                }
                else {
                    button.setVisibility(View.VISIBLE);
                    if (item.getItemId() == R.id.tab2) {
                        //print(getApplicationContext(), "보호자 탭입니다. ");
                        getSupportFragmentManager().beginTransaction().replace(R.id.Container, readLocationFragment).commit();//androidx.core.R.id.action_container
                        return true;
                    } else if(item.getItemId() == R.id.tab1){
                        button.setVisibility(View.VISIBLE);
                        //print(getApplicationContext(),"길찾기 기능 탭입니다. ");
                        getSupportFragmentManager().beginTransaction().replace(R.id.Container,locationFragment).commit();
                        return true;
                    }
                    else
                        return false;
                }
            }
        });

        //viewPager2.setAdapter(pagerAdapter);
    }

   /* //뷰페이저를 관리해줄 어댑터 클래스
    class MyPagerAdapter extends FragmentStateAdapter{

        *//**프래그먼트들을 저장을 해둘 리스트*//*
        private ArrayList<Fragment> items = new ArrayList<>();
        public MyPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
            super(fragmentManager, lifecycle);
        }
        *//**콜백 메소드*//*
        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return items.get(position);
        }
        *//**프래그먼트 개수를 리턴(사이즈를 지정해 줘야 작동)*//*
        @Override
        public int getItemCount() {
            return items.size();
        }

        *//**arraylist에 프래그먼트를 저장해줄 메소드*//*
        public void addItem(Fragment item){
            items.add(item);
        }
    }*/

    /**로컬 데이터베이스가 존재하는지 확인하는 메소드*/
    public boolean isDatabaseExist(Context context, String dbName) {
        File dbFile = context.getDatabasePath(dbName);
        return dbFile.exists();
    }

    public static void print(Context context, String message){
        Toast.makeText(context,message,Toast.LENGTH_SHORT).show();
    }

}