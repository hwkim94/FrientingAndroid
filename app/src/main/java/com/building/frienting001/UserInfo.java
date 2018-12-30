package com.building.frienting001;


import java.io.Serializable;
import java.util.ArrayList;

public class UserInfo implements Serializable {
    public String phone = "";
    public String display_name = "";
    public String image_path = "";
    // 회원 가입시 요구되는 정보

    public String name = "";
    public String gender = "";
    public String job = "";
    public String introduction = "";
    public String recommendation = "";
    public int Ting = 0;
    public String chargeTing = "";
    public ArrayList<String> personality = new ArrayList<String>();
    //public ArrayList<String> interest = new ArrayList<String>();
    //public ArrayList<Integer> birth_date = new ArrayList<Integer>(); // 기본 타입은 <Integer> 이렇게 안해줘도 알아먹는듯
    //public ArrayList<String> place = new ArrayList<String>();

    public String FirebaseUserUid = "";
    public String FirebaseRecruitmentUid = "";
    public String FirebaseChattingUid = "";
    public String FirebaseLogUid = "";

    public String sign_in_date = "";
    //public String promise_time = "";

    public int reputation = 0;
    //public ReviewDialogItem review;
    public int report = 0;

    //리크루팅 진행중인 변수 하나 더 만들고, 채팅 프레그먼트에서 검색하는 옵션 바꿔주기
    public ArrayList<ArrayList<String>> recruit_progress = new ArrayList<>(); // ID, meeting, farewell
    public String finish_writing_recruitment = "";
    public ArrayList<String> recruit_finished = new ArrayList<>(); // ID

    /*public ArrayList<String> recruit_master = new ArrayList<>();
    public ArrayList<String> recruit_guest = new ArrayList<>();
    public ArrayList<String> recruit_finished = new ArrayList<>(); // 결국 My를 열어볼 때만 시간을 체크하고 공고 ID를 지워주면 그만!!*/

    public UserInfo() {
        // for initialization
    }
}
