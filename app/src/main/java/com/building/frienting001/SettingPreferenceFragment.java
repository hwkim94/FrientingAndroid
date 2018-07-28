package com.building.frienting001;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.LinearLayout;
import android.widget.ListView;

/**
 * Created by LG_ on 2017-08-08.
 */

//환경설정
public class SettingPreferenceFragment extends PreferenceFragment {

    private int xml_id;
    private int container_id;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        xml_id = getArguments().getInt("xml");
        addPreferencesFromResource(xml_id);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        container_id = getArguments().getInt("container");

        //Preference가 fragment형태로 activity에 붙을 수 있게 해주는 코드
        if (getView() != null) {
            ListView listView = (ListView) getView().findViewById(android.R.id.list);
            Adapter adapter = listView.getAdapter();

            if (adapter != null) {
                int height = 0;
                //int height = listView.getPaddingTop() + listView.getPaddingBottom();

                for (int i = 0; i < adapter.getCount(); i++) {
                    View item = adapter.getView(i, null, listView);

                    item.measure(0, 0);
                    height += item.getMeasuredHeight();
                }

                LinearLayout frame = (LinearLayout) getActivity().findViewById(container_id);

                ViewGroup.LayoutParams param = frame.getLayoutParams();
                param.height = height + (listView.getDividerHeight() * (adapter.getCount()));
                frame.setLayoutParams(param);
            }
        }
    }
}