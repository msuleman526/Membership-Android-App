package com.appsnipp.e4solutions;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.location.Address;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.appsnipp.e4solutions.Models.Login;
import com.appsnipp.e4solutions.Steps.ContactInfoActivity;
import com.appsnipp.e4solutions.Steps.ESignatureActivity;
import com.appsnipp.e4solutions.Steps.MainActivity;
import com.appsnipp.e4solutions.Steps.PersonalInfoActivity;
import com.appsnipp.e4solutions.Steps.TakePhotoActivity;
import com.appsnipp.e4solutions.Utils.AddressParser;
import com.appsnipp.e4solutions.Utils.SessionUtil;
import com.appsnipp.e4solutions.Utils.Utils;
import com.appsnipp.e4solutions.Visitors.VisitorListActivity;

public class SplashActivity extends AppCompatActivity {

    ImageView logoImage;
    ProgressBar progressBar;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        logoImage = findViewById(R.id.logoImg);
        Login loginData = SessionUtil.getLoginData(SplashActivity.this);
        progressBar = findViewById(R.id.progressBar);

        logoImage.setImageDrawable(getResources().getDrawable(R.drawable.logo));
        Thread timer = new Thread() {
            public void run() {
                try {
                    //Display for 3 seconds
                    sleep(3000);
                } catch (InterruptedException e) {

                } finally {
                    Intent openstartingpoint = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(openstartingpoint);
                    finishAffinity();
                }
            }
        };
        timer.start();

//        if(loginData == null || loginData.data == null){
//            logoImage.setImageDrawable(getResources().getDrawable(R.drawable.logo));
//            Thread timer = new Thread() {
//                public void run() {
//                    try {
//                        //Display for 3 seconds
//                        sleep(3000);
//                    } catch (InterruptedException e) {
//
//                    } finally {
//                        Intent openstartingpoint = new Intent(SplashActivity.this, LoginActivity.class);
//                        startActivity(openstartingpoint);
//                        finishAffinity();
//                    }
//                }
//            };
//            timer.start();
//        }else {
//            if(loginData.data.tokenAuth.user.organization.theme != null){
//                changeThemeColor(loginData.data.tokenAuth.user.organization.theme.primaryColor);
//            }
//            Utils.getBitmapFromURl(loginData.data.tokenAuth.user.organization.logo, logoImage);
//            Thread timer = new Thread() {
//                public void run() {
//                    try {
//                        //Display for 3 seconds
//                        sleep(5500);
//                    } catch (InterruptedException e) {
//
//                    } finally {
//                        Intent openstartingpoint = new Intent(SplashActivity.this, MainActivity.class);
//                        startActivity(openstartingpoint);
//                        finishAffinity();
//                    }
//                }
//            };
//            timer.start();
//        }
    }



    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    void changeThemeColor(String hexColor){
        if(hexColor.charAt(0) != '#'){
            hexColor = "#" + hexColor;
        }
        try {
            int color = Color.parseColor(hexColor);
            Window window = this.getWindow();
            window.setStatusBarColor(color);
            progressBar.setIndeterminateTintList(ColorStateList.valueOf(color));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}