package com.building.frienting001;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.vending.billing.IInAppBillingService;
import com.building.frienting001.util.IabHelper;
import com.building.frienting001.util.IabResult;
import com.building.frienting001.util.Inventory;
import com.building.frienting001.util.Purchase;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.FirebaseApp;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class PaymentActivity extends AppCompatActivity {

    private TextView history;
    private TextView now_ting;
    private TextView free;
    private Button btn1, btn2, btn3, btn4, btn5, btn6;

    private int ting_flag;
    private int count = 0;
    private static final String TAG = "PaymentActivity";

    private AdView mAdView;
    private UserInfo userInfo;

    private DatabaseReference userDBReference;

    private FirebaseAnalytics firebaseAnalytics;

    IInAppBillingService mService;
    IabHelper mHelper;
    ServiceConnection mServiceConn = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
        }
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = IInAppBillingService.Stub.asInterface(service);
        }
    };

    IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
        public void onConsumeFinished(Purchase purchase, IabResult result) {

            // mHelper가 소거되었다면 종료
            if (mHelper == null) return;

            if (result.isSuccess()) {
                // 성공적으로 소진되었다면 상품의 효과를 게임상에 적용합니다.
                userInfo.setTing(userInfo.getTing() + ting_flag);
                userDBReference.child(userInfo.getFirebaseUserUid()).setValue(userInfo);
            }
            else {}
        }
    };

    private boolean verifyDeveloperPayload(Purchase p) {
        String payload = p.getDeveloperPayload();
        return true;
    }
    IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        @Override
        public void onQueryInventoryFinished(IabResult result, Inventory inv) {

            if (mHelper == null) return;
            if (result.isFailure()) {
                //getPurchases() 실패했을때
                return;
            }

            //해당 아이템 구매 여부 체크
            Purchase purchase1 = inv.getPurchase("1000ting");
            if (purchase1 != null && verifyDeveloperPayload(purchase1)) {
                try {
                    mHelper.consumeAsync(inv.getPurchase("1000ting"), mConsumeFinishedListener);
                } catch (Exception e) {
                }
            }
            Purchase purchase2 = inv.getPurchase("500ting");
            if (purchase2 != null && verifyDeveloperPayload(purchase2)) {
                try {
                    mHelper.consumeAsync(inv.getPurchase("500ting"), mConsumeFinishedListener);
                } catch (Exception e) {
                }
            }
            Purchase purchase3 = inv.getPurchase("300ting");
            if (purchase3 != null && verifyDeveloperPayload(purchase3)) {
                try {
                    mHelper.consumeAsync(inv.getPurchase("300ting"), mConsumeFinishedListener);
                } catch (Exception e) {
                }
            }
            Purchase purchase4 = inv.getPurchase("100ting");
            if (purchase4 != null && verifyDeveloperPayload(purchase4)) {
                try {
                    mHelper.consumeAsync(inv.getPurchase("100ting"), mConsumeFinishedListener);
                } catch (Exception e) {
                }
            }
            Purchase purchase5 = inv.getPurchase("70ting");
            if (purchase5 != null && verifyDeveloperPayload(purchase5)) {
                try {
                    mHelper.consumeAsync(inv.getPurchase("70ting"), mConsumeFinishedListener);
                } catch (Exception e) {
                }
            }
            Purchase purchase6 = inv.getPurchase("30ting");
            if (purchase6 != null && verifyDeveloperPayload(purchase6)) {
                try {
                    mHelper.consumeAsync(inv.getPurchase("30ting"), mConsumeFinishedListener);
                } catch (Exception e) {
                }
            }
        }
    };

    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        @Override
        public void onIabPurchaseFinished(IabResult result, Purchase info) {
            if (mHelper == null) return;
            if (result.isFailure()) {return;}

            if (info.getSku().equals("1000ting")){
                try {mHelper.consumeAsync(info, mConsumeFinishedListener);}
                catch (Exception e){}
            }else if(info.getSku().equals("500ting")){
                try {mHelper.consumeAsync(info, mConsumeFinishedListener);}
                catch (Exception e){}
            }else if(info.getSku().equals("300ting")){
                try {mHelper.consumeAsync(info, mConsumeFinishedListener);}
                catch (Exception e){}
            }else if(info.getSku().equals("100ting")){
                try {mHelper.consumeAsync(info, mConsumeFinishedListener);}
                catch (Exception e){}
            }else if(info.getSku().equals("70ting")){
                try {mHelper.consumeAsync(info, mConsumeFinishedListener);}
                catch (Exception e){}
            }else if(info.getSku().equals("30ting")){
                try {mHelper.consumeAsync(info, mConsumeFinishedListener);}
                catch (Exception e){}
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //상태창
        if (Build.VERSION.SDK_INT >=21) {
            Window window = getWindow();
            Drawable background = ResourcesCompat.getDrawable(getResources(),R.drawable.gradient,null);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setBackgroundDrawable(background);
            window.setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.color_transparent));
        }

        //유저정보 받기
        final Intent receive = getIntent();
        userInfo = (UserInfo) receive.getSerializableExtra("userInfo");

        //액션바 설정
        LayoutInflater actionbarInflater =LayoutInflater.from(this);
        View customView = actionbarInflater.inflate(R.layout.actionbar, null);
        TextView actionbar_name = (TextView)customView.findViewById(R.id.actionbar_name);
        TextView actionbar_now = (TextView)customView.findViewById(R.id.actionbar_now_ting);
        ImageView actionbar_back = (ImageView) customView.findViewById(R.id.actionbar_back);
        LinearLayout actionbar_container = (LinearLayout)customView.findViewById(R.id.actionbar_ting_container);
        actionbar_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        actionbar_now.setText(""+userInfo.getTing());
        actionbar_name.setText("팅 충전하기");

        ActionBar.LayoutParams params = new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(customView, params);
        getSupportActionBar().setHomeButtonEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        Toolbar parent = (Toolbar)customView.getParent();
        parent.setContentInsetsAbsolute(0,0);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        //구글 admob
        MobileAds.initialize(this, "ca-app-pub-7101905843238574~8811716481");
        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when when the user is about to return
                // to the app after tapping on an ad.
            }
        });

        //액션바 설정
        ActionBar actionBar = getSupportActionBar();
        actionBar.setElevation(3);

        firebaseAnalytics = FirebaseAnalytics.getInstance(this);

        //UserDB연결
        FirebaseApp userApp = FirebaseApp.getInstance("user");
        userDBReference = FirebaseDatabase.getInstance(userApp).getReference().child("user");

        //선언부
        history = (TextView)findViewById(R.id.payment_history);
        now_ting = (TextView)findViewById(R.id.payment_now_ting);
        free = (TextView)findViewById(R.id.payment_free);
        btn1 = (Button)findViewById(R.id.payment_1);
        btn2 = (Button)findViewById(R.id.payment_2);
        btn3 = (Button)findViewById(R.id.payment_3);
        btn4 = (Button)findViewById(R.id.payment_4);
        btn5 = (Button)findViewById(R.id.payment_5);
        btn6 = (Button)findViewById(R.id.payment_6);

        final int ting = userInfo.getTing();
        now_ting.setText(""+ting);

        //무료충전소
        free.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PaymentActivity.this, FreeChargingActivity.class);
                intent.putExtra("userInfo", userInfo);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });

        Bundle params1 = new Bundle();
        params1.putString("UserUid", userInfo.getFirebaseUserUid());
        params1.putLong("OpenTime", System.currentTimeMillis());
        firebaseAnalytics.logEvent("PaymentActivity", params1);

        //인앱결제
        Intent serviceIntent = new Intent("com.android.vending.billing.InAppBillingService.BIND");
        serviceIntent.setPackage("com.android.vending");
        bindService(serviceIntent, mServiceConn, Context.BIND_AUTO_CREATE);

        String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAgHa/Fg0vo77nX4Do/kvUfasj6MQ4hQ5JdKx/Npp2erEmvFbLQ9T2gBQ72xFo5iZF1ShDCT+FA+GPPB0zZNKnEzjDf3oWXQp60T7VMMKKlk3i62dZbB0BXXibjtFdLPoYX/ORUvdcGGGQh3maO/yXeE2U76gKbIb/bY8bVNWG/uMXlnGU3JmpGhdAb0N/rqG3JHVPJUqG1jKjdPKPSe97aQ5GSTZQJrjh2QGKGvR4aj5QfhCKi8tQlOBaD67AKtTj+tAJy9jtS2HaTUYuHGPJdqQPIt1uaDcVm+5pCH+uVbmfXob4i5UXJ0KjutIyAiIDPoxbPuVCjbYcmKN7jBAPxwIDAQAB";
        mHelper = new IabHelper(this, base64EncodedPublicKey);
        mHelper.enableDebugLogging(true);
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                if (!result.isSuccess()) {return;}
                if (mHelper == null) return;

                try {mHelper.queryInventoryAsync(mGotInventoryListener);}
                catch (Exception e){}

            }});

        //리스너 선언
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (count ==0) {
                    switch (v.getId()) {
                        case R.id.payment_1:makingDialog("팅 충전", "팅 충전을 완료하시겠습니까?", 1000);break;
                        case R.id.payment_2:makingDialog("팅 충전", "팅 충전을 완료하시겠습니까?", 500);break;
                        case R.id.payment_3:makingDialog("팅 충전", "팅 충전을 완료하시겠습니까?", 300);break;
                        case R.id.payment_4:makingDialog("팅 충전", "팅 충전을 완료하시겠습니까?", 100);break;
                        case R.id.payment_5:makingDialog("팅 충전", "팅 충전을 완료하시겠습니까?", 70);break;
                        case R.id.payment_6:makingDialog("팅 충전", "팅 충전을 완료하시겠습니까?", 30);break;
                    }
                    count++;
                }else{Toast.makeText(getApplicationContext(), "베타버전에서는 1일 1회만 충전가능합니다.", Toast.LENGTH_SHORT).show();}
            }
        };

        btn1.setOnClickListener(listener);
        btn2.setOnClickListener(listener);
        btn3.setOnClickListener(listener);
        btn4.setOnClickListener(listener);
        btn5.setOnClickListener(listener);
        btn6.setOnClickListener(listener);
    }

    public void makingDialog(String title, String message, final int flag){
        AlertDialog.Builder builder = new AlertDialog.Builder(PaymentActivity.this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(false);
        //builder.setIcon(R.mipmap.ic_launcher);

        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ting_flag = flag;
                //userInfo.setTing(userInfo.getTing() + flag);
                //userDBReference.child(userInfo.getFirebaseUserUid()).setValue(userInfo);

                switch (flag){
                    case 1000 : buyItem("1000ting");break;
                    case 500 : buyItem("500ting");break;
                    case 300 : buyItem("300ting");break;
                    case 100 : buyItem("100ting");break;
                    case 70 : buyItem("70ting");break;
                    case 30 : buyItem("30ting");break;

                }
                dialogInterface.cancel();
                //finish()
            }
        });

        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if(mServiceConn != null){ unbindService(mServiceConn); }
    }



    private void buyItem(String item) {
        /*
        try {
            Bundle buyIntentBundle = mService.getBuyIntent(3, getPackageName(), item, "inapp", "ting");
            PendingIntent pendingIntent = buyIntentBundle.getParcelable("BUY_INTENT");
            if (pendingIntent != null) {
                try {mHelper.launchPurchaseFlow(this, item, 1001, mPurchaseFinishedListener, "ting");}
                catch (Exception e){}
            } else {}

        } catch (RemoteException e) {
            e.printStackTrace();
        }
        */

        try {mHelper.launchPurchaseFlow(this, item, 1001, mPurchaseFinishedListener, "ting");}
        catch (Exception e){}

        Bundle params1 = new Bundle();
        params1.putString("UserUid", userInfo.getFirebaseUserUid());
        params1.putString("BuyItem", item);
        params1.putLong("BuyTime", System.currentTimeMillis());
        firebaseAnalytics.logEvent("PaymentActivity", params1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (mHelper == null) return;
        if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
            //처리할 결과물이 아닐 경우 이곳으로 빠져 기본처리를 하도록한다
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

}

