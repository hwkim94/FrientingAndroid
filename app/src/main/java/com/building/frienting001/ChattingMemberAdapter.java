package com.building.frienting001;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


//리뷰화면 리스트 어댑터
public class ChattingMemberAdapter extends BaseAdapter{
    private List<ChattingMemberItem> list = new ArrayList<>();
    private UserInfo userInfo;
    private RecruitmentItem recruitmentItem;

    public void setUserInfo(UserInfo userInfo){
        this.userInfo = userInfo;
    };
    public void setRecruitmentItem(RecruitmentItem recruitmentItem){
        this.recruitmentItem = recruitmentItem;
    };

    public void add(ChattingMemberItem item){
        list.add(item);
    }

    public void  additem(ChattingMemberItem item){
        list.add(item);
        notifyDataSetChanged();
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
    public View getView(int position, View convertView, ViewGroup parent) {
        final Context context = parent.getContext();

        //view 생성 - LayoutInflater 이용
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.chatting_member_item, parent, false);
        }

        //생성된 view에 데이터를 설정해 주는 코드
        final ChattingMemberItem data = list.get(position);//i번 위치의 데이터를 추출
        String name = data.getName();
        String icon_uid = data.getIcon();
        String contents= data.getContents();

        TextView nickname = (TextView) convertView.findViewById(R.id.chatting_member_name);
        ImageView icon = (ImageView) convertView.findViewById(R.id.chatting_member_icon);
        TextView profile = (TextView)convertView.findViewById(R.id.chatting_member_contents);
        Button btn = (Button)convertView.findViewById(R.id.chatting_member_review);

        nickname.setText(name);
        profile.setText(contents);

        if (icon_uid.equals("")) {}
        else {Glide.with(convertView.getContext()).load(icon_uid).into(icon);}

        /*btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (data.getUser().getFirebaseUserUid().equals(userInfo.getFirebaseUserUid())){
                    Toast.makeText(v.getContext(), "본인에게 리뷰를 남길 수 없습니다.", Toast.LENGTH_SHORT).show();
                }else {
                    String[] lst1 = recruitmentItem.getHelloDate().split("/");
                    String[] lst2 = recruitmentItem.getGoodbyeTime().split(" ");

                    int part;
                    if(lst2[0].equals("AM")){part = 0;}
                    else{part = 12;}

                    int time = Integer.parseInt(lst1[0])*365*24+ Integer.parseInt(lst1[1])*30*24 + Integer.parseInt(lst1[2])*24 + part + Integer.parseInt(lst2[1]) + Integer.parseInt(lst2[3])/60;
                    Calendar cal = Calendar.getInstance();
                    int now = (cal.get(Calendar.DAY_OF_YEAR)-2000)*365*24 + (cal.get(Calendar.MONTH)+1)*30*24 + cal.get(Calendar.DATE)*24 + cal.get(Calendar.HOUR_OF_DAY) + cal.get(Calendar.MINUTE)/60;

                    if(now-time >=0) {
                        Intent intent = new Intent(context, ReviewActivity.class);
                        //intent.putExtra("user", data.getUser());
                        //intent.putExtra("userInfo", userInfo);
                        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        ActivityCompat.startActivity(context, intent, null);
                    }else{
                        Toast.makeText(v.getContext(), "헤어진 시간 이후 리뷰를 남길 수 있습니다.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });*/

        return convertView;
    }
}
