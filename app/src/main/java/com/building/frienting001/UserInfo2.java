package com.building.frienting001;

import java.io.Serializable;
import java.security.Timestamp;
import java.util.List;

/**
 * Created by LG_ on 2017-08-20.
 */

public class UserInfo2 implements Serializable {
    private String phone;
    private String email;
    private String password;
    private String imagePath;
    private String name;
    private String sex;
    private String birth;
    private String job;
    private String place;
    private String nickname;
    private List<String> personality;
    private List<InterestItem> interest;
    private String introduction;
    private String recommendation;
    private int Ting;
    private List<Integer> chargeTing;

    private String FirebaseUserUid;

    private Timestamp SigninDate;
    private List<Timestamp> PromiseTime;

    private int reputation;
    private List<ReviewDialogItem2> review;
    private int report;

    //리크루팅 진행중인 변수 하나 더 만들고, 채팅 프레그먼트에서 검색하는 옵션 바꿔주기
    private List<String> recruiting;
    private List<String> finish_writing_recruitment;
    private List<String> finish_recruitment;

    public UserInfo2() {
    }

    public UserInfo2(String phone, String email, String password, String imagePath, String name, String sex, String birth, String job, String place, String nickname, List<String> personality, List<InterestItem> interest, String introduction, String recommendation, int ting, List<Integer> chargeTing, String firebaseUserUid, Timestamp signinDate, List<Timestamp> promiseTime, int reputation, List<ReviewDialogItem2> review, int report, List<String> recruiting, List<String> finish_writing_recruitment, List<String> finish_recruitment) {
        this.phone = phone;
        this.email = email;
        this.password = password;
        this.imagePath = imagePath;
        this.name = name;
        this.sex = sex;
        this.birth = birth;
        this.job = job;
        this.place = place;
        this.nickname = nickname;
        this.personality = personality;
        this.interest = interest;
        this.introduction = introduction;
        this.recommendation = recommendation;
        Ting = ting;
        this.chargeTing = chargeTing;
        FirebaseUserUid = firebaseUserUid;
        SigninDate = signinDate;
        PromiseTime = promiseTime;
        this.reputation = reputation;
        this.review = review;
        this.report = report;
        this.recruiting = recruiting;
        this.finish_writing_recruitment = finish_writing_recruitment;
        this.finish_recruitment = finish_recruitment;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getBirth() {
        return birth;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public List<String> getPersonality() {
        return personality;
    }

    public void setPersonality(List<String> personality) {
        this.personality = personality;
    }

    public List<InterestItem> getInterest() {
        return interest;
    }

    public void setInterest(List<InterestItem> interest) {
        this.interest = interest;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getRecommendation() {
        return recommendation;
    }

    public void setRecommendation(String recommendation) {
        this.recommendation = recommendation;
    }

    public int getTing() {
        return Ting;
    }

    public void setTing(int ting) {
        Ting = ting;
    }

    public List<Integer> getChargeTing() {
        return chargeTing;
    }

    public void setChargeTing(List<Integer> chargeTing) {
        this.chargeTing = chargeTing;
    }

    public String getFirebaseUserUid() {
        return FirebaseUserUid;
    }

    public void setFirebaseUserUid(String firebaseUserUid) {
        FirebaseUserUid = firebaseUserUid;
    }

    public Timestamp getSigninDate() {
        return SigninDate;
    }

    public void setSigninDate(Timestamp signinDate) {
        SigninDate = signinDate;
    }

    public List<Timestamp> getPromiseTime() {
        return PromiseTime;
    }

    public void setPromiseTime(List<Timestamp> promiseTime) {
        PromiseTime = promiseTime;
    }

    public int getReputation() {
        return reputation;
    }

    public void setReputation(int reputation) {
        this.reputation = reputation;
    }

    public List<ReviewDialogItem2> getReview() {
        return review;
    }

    public void setReview(List<ReviewDialogItem2> review) {
        this.review = review;
    }

    public int getReport() {
        return report;
    }

    public void setReport(int report) {
        this.report = report;
    }

    public List<String> getRecruiting() {
        return recruiting;
    }

    public void setRecruiting(List<String> recruiting) {
        this.recruiting = recruiting;
    }

    public List<String> getFinish_writing_recruitment() {
        return finish_writing_recruitment;
    }

    public void setFinish_writing_recruitment(List<String> finish_writing_recruitment) {
        this.finish_writing_recruitment = finish_writing_recruitment;
    }

    public List<String> getFinish_recruitment() {
        return finish_recruitment;
    }

    public void setFinish_recruitment(List<String> finish_recruitment) {
        this.finish_recruitment = finish_recruitment;
    }
}