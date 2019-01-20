package com.building.frienting001;


import java.io.Serializable;
import java.util.ArrayList;

public class UserInfo implements Serializable {
    // 회원 가입시 요구되는 정보
    public String name = "";
    public String phone = "";
    public String sex = "";
    public int ting = 0;
    public String FirebaseUserUid = "";
    public String sign_in_date = "";

    // 등록되어야 하는 정보
    public String nickname = "";
    public String image_path = "";
    public String job = "";
    public String introduction = "";

    //리크루팅 진행중인 변수 하나 더 만들고, 채팅 프레그먼트에서 검색하는 옵션 바꿔주기
    public ArrayList<ArrayList<String>> recruit_progress = new ArrayList<>(); // ID, meeting, farewell
    public String finish_writing_recruitment = "";
    public ArrayList<String> recruit_finished = new ArrayList<>(); // ID


    public UserInfo() {
        // for initialization
    }

}
