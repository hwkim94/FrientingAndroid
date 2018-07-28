package com.building.frienting001;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * Created by LG_ on 2017-07-18.
 */

//글쓰기를 했을 때, 해당 정보를 담아주는 클래스
public class RecruitmentItem implements Serializable, Comparable{

    private String imagePath;
    private String title;
    private String activity;
    private String city1;
    private String city2;
    private String city3;
    private String helloDate;
    private String helloTime;
    private String goodbyeTime;
    private String text;
    private String hashTag;
    private String nickname;
    private ReviewDialogItem review;
    private String recruitment_key;
    private String writer_uid;
    private String applicant_uid;

    private int detail1, detail2;
    private boolean is_finished;

    public RecruitmentItem() {
    }

    public RecruitmentItem(String imagePath, String title, String activity, String city1, String city2, String city3, String helloDate, String helloTime, String goodbyeTime, String text, String hashTag, String nickname, ReviewDialogItem review, String recruitment_key, String writer_uid, String applicant_uid, int detail1, int detail2, boolean is_finished) {
        this.imagePath = imagePath;
        this.title = title;
        this.activity = activity;
        this.city1 = city1;
        this.city2 = city2;
        this.city3 = city3;
        this.helloDate = helloDate;
        this.helloTime = helloTime;
        this.goodbyeTime = goodbyeTime;
        this.text = text;
        this.hashTag = hashTag;
        this.nickname = nickname;
        this.review = review;
        this.recruitment_key = recruitment_key;
        this.writer_uid = writer_uid;
        this.applicant_uid = applicant_uid;
        this.detail1 = detail1;
        this.detail2 = detail2;
        this.is_finished = is_finished;
    }

    public String getCity1() {
        return city1;
    }

    public void setCity1(String city1) {
        this.city1 = city1;
    }

    public String getCity2() {
        return city2;
    }

    public void setCity2(String city2) {
        this.city2 = city2;
    }

    public String getCity3() {
        return city3;
    }

    public void setCity3(String city3) {
        this.city3 = city3;
    }

    public boolean getIs_finished() {
        return is_finished;
    }

    public void setIs_finished(boolean is_finished) {
        this.is_finished = is_finished;
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

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public String getHelloDate() {
        return helloDate;
    }

    public void setHelloDate(String helloDate) {
        this.helloDate = helloDate;
    }

    public String getHelloTime() {
        return helloTime;
    }

    public void setHelloTime(String helloTime) {
        this.helloTime = helloTime;
    }

    public String getGoodbyeTime() {
        return goodbyeTime;
    }

    public void setGoodbyeTime(String goodbyeTime) {
        this.goodbyeTime = goodbyeTime;
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

    public String getApplicant_uid() {
        return applicant_uid;
    }

    public void setApplicant_uid(String applicant_uid) {
        this.applicant_uid = applicant_uid;
    }

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
        String compareDate = ((RecruitmentItem)o).getHelloDate();
        String compareTime =  ((RecruitmentItem)o).getHelloTime();

        String[] lst1 = compareDate.split("/");
        List<String> array1 =Arrays.asList(lst1);
        String[] lst2 = compareTime.split(" ");
        List<String> array2 =Arrays.asList(lst2);

        int temp;
        if (lst2[0].equals("AM")){
            temp = 0;
        }else{
            temp = 12;
        }

        String[] lst3 = helloDate.split("/");
        List<String> array3 =Arrays.asList(lst3);
        String[] lst4 = helloTime.split(" ");
        List<String> array4 =Arrays.asList(lst4);

        int temp2;
        if (lst4[0].equals("AM")){
            temp2 = 0;
        }else{
            temp2 = 12;
        }
        int val1 = Integer.parseInt(array1.get(0))*365*24+ Integer.parseInt(array1.get(1))*30*24 + Integer.parseInt(array1.get(2))*24 + temp + Integer.parseInt(array2.get(1));
        int this_val = Integer.parseInt(array3.get(0))*365*24+ Integer.parseInt(array3.get(1))*30*24 + Integer.parseInt(array3.get(2))*24 + temp2 + Integer.parseInt(array4.get(1));

        return this_val - val1;
    }
}
