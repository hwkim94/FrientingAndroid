package com.building.frienting001;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;

//환경설정 화면
public class SettingFragment extends Fragment {
    private TextView setting_profile;
    private LinearLayout setting_email;
    private LinearLayout setting_push;
    private LinearLayout setting_friend;
    private LinearLayout setting_notice;
    private LinearLayout setting_guide;
    private LinearLayout setting_evaluate;
    private LinearLayout setting_client;
    private TextView setting_logout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_setting, container, false);

        //선언부
        setting_profile = (TextView) view.findViewById(R.id.setting_profile);
        setting_email = (LinearLayout)view.findViewById(R.id.setting_email);
        setting_push = (LinearLayout)view.findViewById(R.id.setting_push);
        setting_friend = (LinearLayout)view.findViewById(R.id.setting_friend);
        setting_notice = (LinearLayout)view.findViewById(R.id.setting_notice);
        setting_guide = (LinearLayout)view.findViewById(R.id.setting_guide);
        setting_evaluate = (LinearLayout)view.findViewById(R.id.setting_evaluate);
        setting_client = (LinearLayout)view.findViewById(R.id.setting_client);
        setting_logout = (TextView)view.findViewById(R.id.setting_logout);
        ImageView photo = (ImageView)view.findViewById(R.id.setting_photo);
        TextView name = (TextView)view.findViewById(R.id.setting_profile_name);
        TextView email = (TextView)view.findViewById(R.id.setting_profile_email);

        Bundle bundle =getArguments();
        final UserInfo userInfo = (UserInfo) bundle.getSerializable("userInfo");

        if(userInfo.getImagePath().equals("")){}
        else {Glide.with(this).load(userInfo.getImagePath()).into(photo);}

        name.setText(userInfo.getName() + " (" + userInfo.getNickname() + ")");
        email.setText(userInfo.getEmail());

        //해당 설정화면으로 이동
        setting_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SettingContainerProfileActivity.class);
                intent.putExtra("ID", "setting_profile");
                intent.putExtra("userInfo", userInfo);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });

        setting_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SettingContainerActivity.class);
                intent.putExtra("ID", "setting_email");
                intent.putExtra("userInfo", userInfo);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });

        setting_push.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SettingContainerActivity.class);
                intent.putExtra("ID", "setting_push");
                intent.putExtra("userInfo", userInfo);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });

        setting_friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SettingContainerActivity.class);
                intent.putExtra("ID", "setting_friend");
                intent.putExtra("userInfo", userInfo);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });

        setting_notice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SettingContainerActivity.class);
                intent.putExtra("ID", "setting_notice");
                intent.putExtra("userInfo", userInfo);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });

        setting_guide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SettingContainerActivity.class);
                intent.putExtra("ID", "setting_guide");
                intent.putExtra("userInfo", userInfo);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });

        setting_evaluate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SettingContainerActivity.class);
                intent.putExtra("ID", "setting_evaluate");
                intent.putExtra("userInfo", userInfo);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });

        setting_client.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SettingContainerActivity.class);
                intent.putExtra("ID", "setting_client");
                intent.putExtra("userInfo", userInfo);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });

        setting_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseApp userApp = FirebaseApp.getInstance("user"); // Retrieve secondary app.
                FirebaseAuth userAuth1 = FirebaseAuth.getInstance(userApp);

                FirebaseApp chatApp = FirebaseApp.getInstance("chatting");
                FirebaseAuth userAuth2 = FirebaseAuth.getInstance(chatApp);

                FirebaseApp logApp = FirebaseApp.getInstance("log");
                FirebaseAuth userAuth3 = FirebaseAuth.getInstance(logApp);

                FirebaseApp recruitmentApp = FirebaseApp.getInstance("recruitment");
                FirebaseAuth userAuth4 = FirebaseAuth.getInstance(recruitmentApp);

                userAuth1.signOut();
                userAuth2.signOut();
                userAuth3.signOut();
                userAuth4.signOut();

                Toast.makeText(getActivity().getApplicationContext(), "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();

                getActivity().finish();
                startActivity(new Intent(getActivity(), LoginActivity.class));
            }
        });

        return view;
    }
}
