package com.building.frienting001;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

//환경설정-공지 화면
public class SettingNotiFragment extends android.app.Fragment {

    private ListView setting_notification_list;
    private SettingNotiAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting_notification, container, false);

        adapter = new SettingNotiAdapter();
        setting_notification_list = (ListView)view.findViewById(R.id.setting_notification_list);
        setting_notification_list.setAdapter(adapter);

        loaddata();

        return view;
    }

    //공지화면 추가해주는 것
    public void loaddata(){
        //icon 장소를 모두 Image뷰, 형태는 drawable
        adapter.add(new SettingNotiItem("(2017-10-10) 프렌팅에 오신걸 환영합니다!", "열심히 개발하고 있으니, \n많은 사랑 부탁드립니다! \n\n개발자 올림"));
        adapter.add(new SettingNotiItem("(2017-10-11) 베타버전 무료 팅 충전", "베타버전에서는 무료로 팅을 충전하실 수 있습니다."));
        adapter.add(new SettingNotiItem("(2017-10-11) 오류발견시 문의 방법", "만약 오류를 발견하시면, \nhwkim941213@gmail.com으로 문의해주세요!\n처음으로 오류를 보고해주시면 기프티콘을 드립니다!"));

    }

}
