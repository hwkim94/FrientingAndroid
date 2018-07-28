package com.building.frienting001;

import java.io.Serializable;

public class UserInfo implements Serializable{
    private String phone = "";
    private String email = "";
    private String password = "";
    private String imagePath = "";
    private String name = "";
    private String sex = "";
    private String birth = "";
    private String job = "";
    private String place = "";
    private String nickname = "";
    private String personality = "";
    private String interest = "";
    private String introduction = "";
    private String recommendation = "";
    private int Ting = 0;
    private String chargeTing = "";

    private String FirebaseUserUid = "";
    private String FirebaseRecruitmentUid = "";
    private String FirebaseChattingUid = "";
    private String FirebaseLogUid = "";

    private String SigninDate = "";
    private String PromiseTime = "";

    private int reputation = 0;
    private ReviewDialogItem review;
    private int report = 0;

    //리크루팅 진행중인 변수 하나 더 만들고, 채팅 프레그먼트에서 검색하는 옵션 바꿔주기
    private String recruiting = "";
    private String finish_writing_recruitment = "";
    private String finish_recruitment = "";

    public UserInfo() {}

    public UserInfo(String phone, String email, String password, String imagePath, String name, String sex, String birth, String job, String place, String nickname, String personality, String interest, String introduction, String recommendation, int ting, String chargeTing, String firebaseUserUid, String firebaseRecruitmentUid, String firebaseChattingUid, String firebaseLogUid, String signinDate, String promiseTime, int reputation, ReviewDialogItem review, int report, String recruiting, String finish_writing_recruitment, String finish_recruitment) {
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
        FirebaseRecruitmentUid = firebaseRecruitmentUid;
        FirebaseChattingUid = firebaseChattingUid;
        FirebaseLogUid = firebaseLogUid;
        SigninDate = signinDate;
        PromiseTime = promiseTime;
        this.reputation = reputation;
        this.review = review;
        this.report = report;
        this.recruiting = recruiting;
        this.finish_writing_recruitment = finish_writing_recruitment;
        this.finish_recruitment = finish_recruitment;
    }

    public String getPromiseTime() {
        return PromiseTime;
    }

    public void setPromiseTime(String promiseTime) {
        PromiseTime = promiseTime;
    }

    public String getInterest() {
        return interest;
    }

    public void setInterest(String interest) {
        this.interest = interest;
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

    public String getPersonality() {
        return personality;
    }

    public void setPersonality(String personality) {
        this.personality = personality;
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

    public String getChargeTing() {
        return chargeTing;
    }

    public void setChargeTing(String chargeTing) {
        this.chargeTing = chargeTing;
    }

    public String getFirebaseUserUid() {
        return FirebaseUserUid;
    }

    public void setFirebaseUserUid(String firebaseUserUid) {
        FirebaseUserUid = firebaseUserUid;
    }

    public String getFirebaseRecruitmentUid() {
        return FirebaseRecruitmentUid;
    }

    public void setFirebaseRecruitmentUid(String firebaseRecruitmentUid) {
        FirebaseRecruitmentUid = firebaseRecruitmentUid;
    }

    public String getFirebaseChattingUid() {
        return FirebaseChattingUid;
    }

    public void setFirebaseChattingUid(String firebaseChattingUid) {
        FirebaseChattingUid = firebaseChattingUid;
    }

    public String getFirebaseLogUid() {
        return FirebaseLogUid;
    }

    public void setFirebaseLogUid(String firebaseLogUid) {
        FirebaseLogUid = firebaseLogUid;
    }

    public String getSigninDate() {
        return SigninDate;
    }

    public void setSigninDate(String signinDate) {
        SigninDate = signinDate;
    }


    public int getReputation() {
        return reputation;
    }

    public void setReputation(int reputation) {
        this.reputation = reputation;
    }

    public ReviewDialogItem getReview() {
        return review;
    }

    public void setReview(ReviewDialogItem review) {
        this.review = review;
    }

    public int getReport() {
        return report;
    }

    public void setReport(int report) {
        this.report = report;
    }

    public String getRecruiting() {
        return recruiting;
    }

    public void setRecruiting(String recruiting) {
        this.recruiting = recruiting;
    }

    public String getFinish_writing_recruitment() {
        return finish_writing_recruitment;
    }

    public void setFinish_writing_recruitment(String finish_writing_recruitment) {
        this.finish_writing_recruitment = finish_writing_recruitment;
    }

    public String getFinish_recruitment() {
        return finish_recruitment;
    }

    public void setFinish_recruitment(String finish_recruitment) {
        this.finish_recruitment = finish_recruitment;
    }
}
