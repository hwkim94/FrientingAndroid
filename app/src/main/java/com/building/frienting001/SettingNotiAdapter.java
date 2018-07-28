package com.building.frienting001;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

//환경설정-공지 어댑터
public class SettingNotiAdapter extends BaseAdapter {

    private List<SettingNotiItem> list = new ArrayList<>();
    public void add(SettingNotiItem item){
        list.add(item);
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Context context = parent.getContext();

        //view 생성 - LayoutInflater 이용
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.setting_notification_item, parent, false);
        }

        //생성된 view에 데이터를 설정해 주는 코드
        SettingNotiItem data = list.get(position);//i번 위치의 데이터를 추출

        TextView setting_noti_title = (TextView) convertView.findViewById(R.id.setting_noti_title);
        final TextView setting_noti_text = (TextView) convertView.findViewById(R.id.setting_noti_text);

        setting_noti_title.setText(data.getTitle());
        setting_noti_text.setText(data.getText());

        setting_noti_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (setting_noti_text.getVisibility() ==View.GONE){
                    setting_noti_text.setVisibility(View.VISIBLE);
                }else{
                    setting_noti_text.setVisibility(View.GONE);
                }
            }
        });

        return convertView;
    }
}
