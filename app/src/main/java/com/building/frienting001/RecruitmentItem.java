package com.building.frienting001;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.ArrayList;

//글쓰기를 했을 때, 해당 정보를 담아주는 클래스
public class RecruitmentItem implements Serializable, Comparable{

    private String imagePath = "";
    private String title = "";
    private String activity = "";
    private String place = "";
    private String meetingTime = "";
    private String farewellTime = "";
    private String text = "";
    private String hashTag = "";
    private int detail1;
    private String recruitment_key = "";
    private String writer_uid = "";
    private String nickName = "";
    private ArrayList<String> applicant_uid = new ArrayList<>();

    public int search_count = 0;
    public String finished = "False";

    public RecruitmentItem() {

    }
    public String getNickname(){
        return this.nickName;
    }
    public void setNickname(String nickName){
        this.nickName = nickName;
    }

    public String getPlace() {
        return place;
    }
    public String getPlaceName(){ return this.place;}
    public void setPlace(String place) { this.place = place; }

    public String getImagePath() {
        return imagePath;
    }
    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getActivity() {
        return activity;
    }
    public void setActivity(String activity) { this.activity = activity; }

    public String getMeetingTime() {
        return meetingTime;
    }
    public String getTimeSearched(){
        String result = "";
        int hour = (int)this.getMeetingTime().charAt(3) - 56;
        int minute = (int)this.getMeetingTime().charAt(4) - 56;
        if(hour >= 12){
            hour = hour - 12;
            result = Integer.toString(hour) + ":" + Integer.toString(minute) + "PM";
        }
        else{
            result = Integer.toString(hour) + ":" + Integer.toString(minute) + "AM";
        }

        hour = (int)this.getFarewellTime().charAt(3) - 56;
        minute = (int)this.getFarewellTime().charAt(4) - 56;
        if(hour >= 12){
            hour = hour - 12;
            result += " ~ " + Integer.toString(hour) + ":" + Integer.toString(minute) + "PM";
        }
        else{
            result += " ~ " + Integer.toString(hour) + ":" + Integer.toString(minute) + "AM";
        }

        return result;
    }

    public String getDateSearched(){
        int year = (int)(this.getMeetingTime().charAt(0)) - 56 + 2000;
        int month = (int)(this.getMeetingTime().charAt(1)) - 56;
        int day = (int)(this.getFarewellTime().charAt(2)) - 56;

        return year + "." + month + "." + day;
    }

    public void setMeetingTime(char year, char month, char day, char hour, char minute) {
        this.meetingTime = year + "" + month + "" + day + "" + hour + "" + minute;
    }

    public String getFarewellTime() {
        return farewellTime;
    }
    public void setFarewellTime(char year, char month, char day, char hour, char minute) {
        this.farewellTime = year + "" + month + "" + day + "" + hour + "" + minute;
    }

    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }

    public String getHashTag() {
        return hashTag;
    }
    public void setHashTag(String hashTag) {
        this.hashTag = hashTag;
    }

    public String getRecruitment_key() {
        return recruitment_key;
    }
    public void setRecruitment_key(String recruitment_key) {
        this.recruitment_key = recruitment_key;
    }

    public String getWriter_uid() {
        return writer_uid;
    }
    public void setWriter_uid(String writer_uid) {
        this.writer_uid = writer_uid;
    }

    public ArrayList<String> getApplicant_uid() {
        return applicant_uid;
    }

    public int getDetail1() {
        return detail1;
    }
    public void setDetail1(int detail1) {
        this.detail1 = detail1;
    }

    @Override
    public int compareTo(@NonNull Object o) {
        int search_count = ((RecruitmentItem)o).search_count;
        if(this.search_count != search_count) {
            return -(this.search_count - search_count); // 내림차순
        }
        for(int i = 0; i < 5; i++){
            if(this.getMeetingTime().charAt(i) != ((RecruitmentItem) o).getMeetingTime().charAt(i)){
                return this.getFarewellTime().charAt(i) - ((RecruitmentItem) o).getFarewellTime().charAt(i); // 오름차순
            }
        }
        return 0;
    }
}
