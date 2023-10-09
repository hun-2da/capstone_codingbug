package com.capstone.codingbug;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;
import android.app.Dialog;
import android.os.Bundle;
import android.view.Window;
import com.capstone.codingbug.pagerFragments.MyLocation_Fragment;
import com.capstone.codingbug.pagerFragments.ReadLocation_Fragment;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ViewPager2 viewPager2;
    Dialog dialog01;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        set_fragment();
        //커스텀 다이얼 로그
        dialog01 = new Dialog(MainActivity.this);       // Dialog 초기화
        dialog01.requestWindowFeature(Window.FEATURE_NO_TITLE); // 타이틀 제거
        dialog01.setContentView(R.layout.dialog01);             // xml 레이아웃 파일과 연결
        dialog01.show(); // 다이얼로그 띄우기
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