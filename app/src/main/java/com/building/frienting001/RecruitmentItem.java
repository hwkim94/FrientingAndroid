package com.building.frienting001;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.ArrayList;

//글쓰기를 했을 때, 해당 정보를 담아주는 클래스
public class RecruitmentItem implements Serializable, Comparable{

    private String imagePath = "";
    private String title = "";
    private ArrayList<String> activity = new ArrayList<>();
    private ArrayList<String> place = new ArrayList<>();
    private String helloTime = "";
    private String goodbyeTime = "";
    private String text = "";
    private String hashTag = "";
    private String nickname = "";
    private ReviewDialogItem review;
    private String recruitment_key = "";
    private String writer_uid = "";
    private ArrayList<String> applicant_uid = new ArrayList<>();
    public int search_count = 0;
    public String finished = "False";

    private int detail1, detail2;

    public RecruitmentItem() {

    }

    public ArrayList<String> getPlace() {
        return place;
    }

    public String getPlaceName(){
        return this.place.get(0) + " " + this.place.get(1) + " " + this.place.get(2);
    }

    public void setPlace(String city1, String city2, String city3) {
        place.add(city1);
        place.add(city2);
        place.add(city3);
    }

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

    public ArrayList<String> getActivity() {
        return activity;
    }

    public void setActivity(String activity, String detail) {
        this.activity.add(activity);
        this.activity.add(detail);
    }

    public String getHelloTime() {
        return helloTime;
    }

    public String getTimeSearched(){
        String result = "";
        int hour = (int)this.getHelloTime().charAt(3) - 56;
        int minute = (int)this.getHelloTime().charAt(4) - 56;
        if(hour >= 12){
            hour = hour - 12;
            result = Integer.toString(hour) + ":" + Integer.toString(minute) + "PM";
        }
        else{
            result = Integer.toString(hour) + ":" + Integer.toString(minute) + "AM";
        }

        hour = (int)this.getGoodbyeTime().charAt(3) - 56;
        minute = (int)this.getGoodbyeTime().charAt(4) - 56;
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
        int year = (int)(this.getHelloTime().charAt(0)) - 56 + 2000;
        int month = (int)(this.getGoodbyeTime().charAt(1)) - 56;
        int day = (int)(this.getGoodbyeTime().charAt(2)) - 56;

        return year + "." + month + "." + day;
    }

    public void setHelloTime(char year, char month, char day, char hour, char minute) {
        this.helloTime = year + "" + month + "" + day + "" + hour + "" + minute;
    }

    public String getGoodbyeTime() {
        return goodbyeTime;
    }

    public void setGoodbyeTime(char year, char month, char day, char hour, char minute) {
        this.goodbyeTime = year + "" + month + "" + day + "" + hour + "" + minute;
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

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public ReviewDialogItem getReview() {
        return review;
    }

    public void setReview(ReviewDialogItem review) {
        this.review = review;
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

    /*public void setApplicant_uid(String applicant_uid) {
        this.applicant_uid = applicant_uid;
    }*/

    public int getDetail1() {
        return detail1;
    }

    public void setDetail1(int detail1) {
        this.detail1 = detail1;
    }

    public int getDetail2() {
        return detail2;
    }

    public void setDetail2(int detail2) {
        this.detail2 = detail2;
    }

    @Override
    public int compareTo(@NonNull Object o) {
        int search_count = ((RecruitmentItem)o).search_count;
        if(this.search_count != search_count) {
            return -(this.search_count - search_count); // 내림차순
        }
        for(int i = 0; i < 5; i++){
            if(this.getHelloTime().charAt(i) != ((RecruitmentItem) o).getHelloTime().charAt(i)){
                return this.getHelloTime().charAt(i) - ((RecruitmentItem) o).getHelloTime().charAt(i); // 오름차순
            }
        }
        return 0;
    }
}
