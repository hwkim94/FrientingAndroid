package com.building.frienting001;

import android.app.Fragment;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import java.util.ArrayList;

public class MainFragment extends Fragment {
    private LinearLayoutManager layoutManager;
    private ViewPager viewPager;
    private View view;
    private MainTagAdapter adapter;
    int current = 0;
    private EditText searchText;
    private Button searchBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ActionBar actionbar = ((NavigationActivity) getActivity()).getSupportActionBar();
        //actionbar.hide();
        view = inflater.inflate(R.layout.fragment_main, container, false);
        //actionbar.show();

        searchText = (EditText) view.findViewById(R.id.searchText_main);
        searchBtn = (Button) view.findViewById(R.id.searchBtn_main);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomNavigationView bottomNavigationView;
                bottomNavigationView = (BottomNavigationView)getActivity().findViewById(R.id.navigation);
                bottomNavigationView.setSelectedItemId(R.id.navigation_bulletinboard);
            }
        });

        int[] mResources = {
                R.drawable.main_1, R.drawable.main_2, R.drawable.main_3, R.drawable.main_4, R.drawable.main_5, R.drawable.main_6,
        };
        // 어댑터 설정을 완료했으면, 어댑터가 가진 정보를 뷰에 넘겨준다.
        adapter = new MainTagAdapter(this.getActivity(), mResources);
        viewPager = (ViewPager) view.findViewById(R.id.image_list);
        viewPager.setAdapter(adapter);

        final ArrayList<ImageView> dots = new ArrayList<>();
        dots.add((ImageView) view.findViewById(R.id.dot1));
        dots.add((ImageView) view.findViewById(R.id.dot2));
        dots.add((ImageView) view.findViewById(R.id.dot3));
        dots.add((ImageView) view.findViewById(R.id.dot4));
        dots.add((ImageView) view.findViewById(R.id.dot5));
        dots.add((ImageView) view.findViewById(R.id.dot6));

        for (int i = 0; i < dots.size(); i++) {
            dots.get(i).setImageResource(R.drawable.dot_2);
        }
        dots.get(0).setImageResource(R.drawable.dot_1);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
            @Override
            public void onPageSelected(int position) {
                dots.get(position).setImageResource(R.drawable.dot_1);
                if (position > 0) {
                    dots.get(position - 1).setImageResource(R.drawable.dot_2);
                }
                if (position < dots.size() - 1) {
                    dots.get(position + 1).setImageResource(R.drawable.dot_2);
                }
            }
            public void onPageScrollStateChanged(int state) {
            }
        });
        return view;
    }
}