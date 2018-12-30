package com.building.frienting001;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

//채팅창에 채팅을 붙여주는 어댑터
public class ChattingMessageAdapter extends ArrayAdapter<ChattingMessageItem>{

    private String user_uid;

    public ChattingMessageAdapter(Context context, int resource, List<ChattingMessageItem> objects, String uid) {
        super(context, resource, objects);
        this.user_uid = uid;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Context context = parent.getContext();

        //view 생성 - LayoutInflater 이용
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.chatting_message_item, parent, false);
        }

        //생성된 view에 데이터를 설정해 주는 코드
        ChattingMessageItem data = getItem(position);//i번 위치의 데이터를 추출
        ImageView message_photo = (ImageView) convertView.findViewById(R.id.message_photo);
        ImageView message_profile = (ImageView) convertView.findViewById(R.id.message_profile);
        TextView message_name = (TextView) convertView.findViewById(R.id.message_name);
        TextView message_text = (TextView) convertView.findViewById(R.id.message_text);
        TextView message_my_text = (TextView) convertView.findViewById(R.id.message_my_text);


        if(user_uid.equals(data.getWriter_uid())){
            //내가 보낸 메세지일 경우
            message_profile.setVisibility(View.GONE);
            message_name.setVisibility(View.GONE);

            /*if (data.getChatting_photo().equals("")) {
            } else {
                //사진 전송
                message_photo.setVisibility(View.VISIBLE);
                Glide.with(convertView.getContext()).load(data.getChatting_photo()).into(message_photo);
                message_text.setVisibility(View.GONE);
                message_my_text.setVisibility(View.GONE);
            }*/

            message_my_text.setVisibility(View.VISIBLE);
            message_text.setVisibility(View.GONE);
            message_my_text.setText(data.getChatting_text());
            message_my_text.setBackgroundResource(R.drawable.ninepatch_mine_small);

        }else {
            //다른 사람이 보낸 메세지일 경우

            if (data.getChatting_photo().equals("")) {
            } else {
                //사진 전송
                //message_photo.setVisibility(View.VISIBLE);
                Glide.with(convertView.getContext()).load(data.getChatting_photo()).into(message_photo);
                //message_text.setVisibility(View.GONE);
                //message_my_text.setVisibility(View.GONE);
            }

            //if (data.getChatting_profile().equals("")) {
            //} else {Glide.with(convertView.getContext()).load(data.getChatting_profile()).into(message_profile);}

            message_text.setVisibility(View.VISIBLE);
            message_name.setText(data.getChatting_name());
            message_text.setText(data.getChatting_text());
            message_my_text.setVisibility(View.GONE);
            message_text.setBackgroundResource(R.drawable.ninepatch_not_mine_small);
        }
        return convertView;
    }
}