package com.building.frienting001;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Calendar;

public class Frequent {
    public void hideStatusBar(Activity activity){
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = activity.getWindow();
            Drawable background = ResourcesCompat.getDrawable(activity.getResources(),R.drawable.gradient,null);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setBackgroundDrawable(background);
            window.setStatusBarColor(ContextCompat.getColor(activity.getApplicationContext(), R.color.color_transparent));
        }
    }
    //알림 다이얼로그 만들기
    public void showDialog(String title, String message, Context context){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(false);

        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        builder.create().show();
    }

    public boolean time_finished(String helloTime) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.YEAR) - 2000 + 56;
        int month = calendar.get(Calendar.MONTH) + 1 + 56;
        int day = calendar.get(Calendar.DAY_OF_MONTH) + 56;
        int hour_now = calendar.get(Calendar.HOUR_OF_DAY) + 56;
        int minute = calendar.get(Calendar.MINUTE) + 56;

        String now = (char) hour + "" + (char) month + "" + (char) day + "" + (char) hour_now + "" + (char) minute;

        for (int i = 0; i < 5; i++) {
            if (now.charAt(i) > helloTime.charAt(i)) {
                return true;
            } else if (now.charAt(i) < helloTime.charAt(i)) {
                return false;
            }
        }
        return false;
    }
}
