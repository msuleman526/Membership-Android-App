package com.appsnipp.e4solutions.Steps;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.appsnipp.e4solutions.Models.Login;
import com.appsnipp.e4solutions.R;
import com.appsnipp.e4solutions.SplashActivity;
import com.appsnipp.e4solutions.Utils.SessionUtil;
import com.appsnipp.e4solutions.Utils.Utils;
import com.shuhart.stepview.StepView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ContactInfoActivity extends AppCompatActivity {

    StepView mStepsView;
    Button submitBtn;
    JSONObject obj = null;
    EditText homePhoneEdt, mobileEdt, emailEdt, occupationEdt, langaugeEdt;
    Spinner interestSpinner;
    CheckBox mailCheck, smsCheck, phoneCheck;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //for changing status bar icon colors
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        setContentView(R.layout.activity_contact_info);
        setupMultiSteps();

        homePhoneEdt = findViewById(R.id.homePhoneEdt);
        mobileEdt = findViewById(R.id.mobilePhone);
        emailEdt = findViewById(R.id.emailEdt);
        occupationEdt = findViewById(R.id.occupationEdit);
        langaugeEdt = findViewById(R.id.langugaeEdt);
        interestSpinner = findViewById(R.id.interestDropDown);
        submitBtn = findViewById(R.id.submitBtn);
        mailCheck = findViewById(R.id.mailCheckBox);
        phoneCheck = findViewById(R.id.phoneCheckBox);
        smsCheck = findViewById(R.id.smsCheckBox);

        interestSpinner.setSelection(0);

        obj = SessionUtil.getData(ContactInfoActivity.this);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                        if(obj == null){
                            obj = new JSONObject();
                        }
                        obj.put("homephone", homePhoneEdt.getText().toString());
                        obj.put("mobilephone", mobileEdt.getText().toString());
                        obj.put("occupation", occupationEdt.getText().toString());
                        obj.put("language", langaugeEdt.getText().toString());
                        obj.put("email", emailEdt.getText().toString());
                        obj.put("interests", interestSpinner.getSelectedItem().toString());

                        Intent intent = new Intent(ContactInfoActivity.this, ESignatureActivity.class);
                        SessionUtil.saveData(ContactInfoActivity.this, obj);
                        startActivity(intent);
                }catch (Exception e){
                    Toast.makeText(ContactInfoActivity.this, "There is some issue", Toast.LENGTH_SHORT).show();
                }
            }
        });

        setUpContactInfo();
        findViewById(R.id.logoutImage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SessionUtil.setSignOut(ContactInfoActivity.this);
                startActivity(new Intent(ContactInfoActivity.this, SplashActivity.class));
                finishAffinity();
            }
        });
//        Login login = SessionUtil.getLoginData(ContactInfoActivity.this);
//        if(login.data.tokenAuth.user.organization.theme != null){
//            String color = login.data.tokenAuth.user.organization.theme.primaryColor;
//            changeThemeColor(color);
//        }
        String color = "#00524c";
        changeThemeColor(color);
    }

    private void setUpContactInfo() {
        try{
            if(obj.get("homephone") != null && !obj.get("homephone").toString().equals("")){
                homePhoneEdt.setText(obj.get("homephone").toString());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        try{
            if(obj.get("mobilephone") != null && !obj.get("mobilephone").toString().equals("")){
                mobileEdt.setText(obj.get("mobilephone").toString());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        try{
            if(obj.get("occupation") != null && !obj.get("occupation").toString().equals("")){
                occupationEdt.setText(Utils.firstLetterCapital(obj.get("occupation").toString()));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        try{
            if(obj.get("language") != null && !obj.get("language").toString().equals("")){
                langaugeEdt.setText(Utils.firstLetterCapital(obj.get("language").toString()));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        try{
            if(obj.get("email") != null && !obj.get("email").toString().equals("")){
                emailEdt.setText(obj.get("email").toString());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        try{
            if(obj.get("interests") != null && !obj.get("interests").toString().equals("")){
                interestSpinner.setSelection(0);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    void setupMultiSteps (){
        List<String> stepLabels = new ArrayList<>();
        stepLabels.add("License");
        stepLabels.add("Photo");
        stepLabels.add("Profile");
        stepLabels.add("Contact");
        stepLabels.add("E-Sign");

        mStepsView = findViewById(R.id.stepsView);
        mStepsView.setSteps(stepLabels);
        mStepsView.go(3, true);
        mStepsView.setOnStepClickListener(new StepView.OnStepClickListener() {
            @Override
            public void onStepClick(int step) {
                if(step != 3){
                    if(step == 1){
                        startActivity(new Intent(ContactInfoActivity.this, TakePhotoActivity.class));
                        finishAffinity();
                    }else if(step == 2){
                        startActivity(new Intent(ContactInfoActivity.this, PersonalInfoActivity.class));
                        finishAffinity();
                    }
                    else if(step == 4){
                        startActivity(new Intent(ContactInfoActivity.this, ESignatureActivity.class));
                        finishAffinity();
                    }
                    else if(step == 0) {
                        Utils.checkDataOnFirstPage(ContactInfoActivity.this, ContactInfoActivity.this);
                    }
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    void changeThemeColor(String hexColor){
        if(hexColor.charAt(0) != '#'){
            hexColor = "#" + hexColor;
        }
        try {
            int color = Color.parseColor(hexColor);
            mStepsView.getState()
                    .selectedCircleColor(color)
                    .nextTextColor(color)
                    .nextStepLineColor(color)
                    .doneCircleColor(color)
                    .doneStepLineColor(color)
                    .selectedTextColor(color)
                    .doneTextColor(color)
                    .commit();

            Window window = this.getWindow();
            window.setStatusBarColor(color);
            submitBtn.setBackgroundTintList(ColorStateList.valueOf(color));

            homePhoneEdt.setTextColor(color);
            mobileEdt.setTextColor(color);
            emailEdt.setTextColor(color);
            occupationEdt.setTextColor(color);
            langaugeEdt.setTextColor(color);
            mailCheck.setTextColor(color);
            mailCheck.setButtonTintList(ColorStateList.valueOf(color));
            phoneCheck.setTextColor(color);
            phoneCheck.setButtonTintList(ColorStateList.valueOf(color));
            smsCheck.setTextColor(color);
            smsCheck.setButtonTintList(ColorStateList.valueOf(color));
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}