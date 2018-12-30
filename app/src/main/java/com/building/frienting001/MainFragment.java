package com.building.frienting001;

import android.app.Fragment;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import java.util.ArrayList;

public class MainFragment extends Fragment {
    private LinearLayoutManager layout_manager;
    private ViewPager view_pager;
    private View main_view;
    private MainTagAdapter main_adapter;
    private EditText search_input;
    private Button search_button;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //ActionBar actionbar = ((NavigationActivity) getActivity()).getSupportActionBar();
        //actionbar.hide();
        //actionbar.show();
        main_view = inflater.inflate(R.layout.fragment_main, container, false);
        search_input = (EditText)main_view.findViewById(R.id.main_search_input);
        search_button = (Button)main_view.findViewById(R.id.main_search_button);
        search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomNavigationView bottomNavigationView;
                bottomNavigationView = (BottomNavigationView)getActivity().findViewById(R.id.navigation);
                bottomNavigationView.setSelectedItemId(R.id.navigation_bulletinboard);
            }
        });

        int[] main_images = {
                R.drawable.main_1, R.drawable.main_2, R.drawable.main_3, R.drawable.main_4, R.drawable.main_5, R.drawable.main_6,
        };
        // 어댑터 설정을 완료했으면, 어댑터가 가진 정보를 뷰에 넘겨준다.
        main_adapter = new MainTagAdapter(this.getActivity(), main_images);
        view_pager = (ViewPager)main_view.findViewById(R.id.image_pager);
        view_pager.setAdapter(main_adapter);

        final ArrayList<ImageView> dots = new ArrayList<>();
        dots.add((ImageView) main_view.findViewById(R.id.dot1));
        dots.add((ImageView) main_view.findViewById(R.id.dot2));
        dots.add((ImageView) main_view.findViewById(R.id.dot3));
        dots.add((ImageView) main_view.findViewById(R.id.dot4));
        dots.add((ImageView) main_view.findViewById(R.id.dot5));
        dots.add((ImageView) main_view.findViewById(R.id.dot6));
        for (int i = 0; i < dots.size(); i++) {
            dots.get(i).setImageResource(R.drawable.dot_2); // white dot - not selected
        }
        dots.get(0).setImageResource(R.drawable.dot_1); // black dot - selected
        view_pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }
            @Override
            public void onPageSelected(int position) {
                dots.get(position).setImageResource(R.drawable.dot_1); // selected
                if (position > 0) { // right direction
                    dots.get(position - 1).setImageResource(R.drawable.dot_2);
                }
                if (position < dots.size() - 1) { // left direction
                    dots.get(position + 1).setImageResource(R.drawable.dot_2);
                }
            }
            public void onPageScrollStateChanged(int state) { }
        });
        return main_view;
    }
}