package com.capstone.codingbug;

import androidx.annotation.NonNull;
import android.Manifest;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.capstone.codingbug.pagerFragments.MyLocation_Fragment;
import com.capstone.codingbug.pagerFragments.ReadLocation_Fragment;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;

import java.util.ArrayList;
import java.util.List;
import androidx.room.Room;

public class MainActivity extends AppCompatActivity {
    ViewPager2 viewPager2;
    //Dialog dialog01;
    private AppDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "app-database").build();
        //커스텀 다이얼 로그
        //dialog01 = new Dialog(MainActivity.this);       // Dialog 초기화

        add_permission();
        //startActivity(new Intent(getApplicationContext(), dialog01.class)); 권한 승인시 다이어로그가 뜨도록 작성하여씁



        //dialog01.requestWindowFeature(Window.FEATURE_NO_TITLE); // 타이틀 제거
        //dialog01.setContentView(R.layout.dialog01);             // xml 레이아웃 파일과 연결
        //dialog01.show(); // 다이얼로그 띄우기
    }
    public AppDatabase getDatabase() {
        return db;
    }
    /**위험 권한을 승인받기 위한 메소드*/
    public void add_permission(){
        TedPermission.create().
                setPermissionListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        Log.d("권한", "승인");
                        startActivity(new Intent(getApplicationContext(), sign_in.class));
                        /* 그냥 activity띄움 만약 customdialog로 만들꺼면 dialog를 상속받은 후 .show해서 사용, 검색시 라이브러리를 사용하는 예들이 있을 수 있으니 구분해서 사용*/
                    }
                    @Override
                    public void onPermissionDenied(List<String> deniedPermissions) {
                        Log.d("권한", "미승인");
                        finish();
                    }
                })
                .setDeniedMessage("해당 퍼미션이 없음.")
                .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                .check();
    }


    /**프래그먼트를 뷰페이지에 넣기위한 메소드*/
    public void set_fragment(){
        viewPager2 = findViewById(R.id.myviewpager); //xml에 있는 뷰페이저 연결
        viewPager2.setOffscreenPageLimit(1);

        MyPagerAdapter pagerAdapter = new MyPagerAdapter(getSupportFragmentManager(),getLifecycle());

        MyLocation_Fragment locationFragment = new MyLocation_Fragment();
        pagerAdapter.addItem(locationFragment);//location fragment를 pager index0에 저장

        ReadLocation_Fragment readLocationFragment = new ReadLocation_Fragment();
        pagerAdapter.addItem(readLocationFragment);//보호자페이지(로케이션을 읽어들여 지도에 띄워줄 fragment)

        viewPager2.setAdapter(pagerAdapter);
    }

    /**뷰페이저를 관리해줄 어댑터 클래스*/
    class MyPagerAdapter extends FragmentStateAdapter{

        /**프래그먼트들을 저장을 해둘 리스트*/
        private ArrayList<Fragment> items = new ArrayList<>();
        public MyPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
            super(fragmentManager, lifecycle);
        }
        /**콜백 메소드*/
        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return items.get(position);
        }

        /**프래그먼트 개수를 리턴(사이즈를 지정해 줘야 작동)*/
        @Override
        public int getItemCount() {
            return items.size();
        }

        /**arraylist에 프래그먼트를 저장해줄 메소드*/
        public void addItem(Fragment item){
            items.add(item);
        }
    }
}