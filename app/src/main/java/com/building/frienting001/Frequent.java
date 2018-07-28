package com.building.frienting001;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.view.Window;
import android.view.WindowManager;

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
}
