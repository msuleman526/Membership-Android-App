package com.appsnipp.e4solutions.Steps;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
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
import java.util.Calendar;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;

public class PersonalInfoActivity extends AppCompatActivity {

    StepView mStepsView;
    EditText firstNameEdt, lastNameEdt, surNameEdt, addressEdt, subburbEdt, dateOfBirthEdt,licenseNumberEdt, expiryEdt, stateEdt, postCodeEdt, countryEdt, preferredName;
    Button submitBtn;
    JSONObject obj;
    CircleImageView imageView;
    Spinner membershipType;
    Spinner membershipTier;
    Login login = null;

    private DatePickerDialog datePickerDialog;
    private Calendar calendar = Calendar.getInstance();
    Boolean isDobSelected = false;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //for changing status bar icon colors
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        setContentView(R.layout.activity_personal_info);
        setupMultiSteps();

        membershipType = findViewById(R.id.membershipType);
        firstNameEdt = findViewById(R.id.firstEdt);
        lastNameEdt = findViewById(R.id.lastEdt);
        surNameEdt = findViewById(R.id.surEdt);
        addressEdt = findViewById(R.id.address);
        subburbEdt = findViewById(R.id.suburbEdt);
        dateOfBirthEdt = findViewById(R.id.dobEdt);
        licenseNumberEdt = findViewById(R.id.licenseNumber);
        expiryEdt = findViewById(R.id.expiry);
        stateEdt = findViewById(R.id.state);
        postCodeEdt = findViewById(R.id.post);
        countryEdt = findViewById(R.id.country);
        submitBtn = findViewById(R.id.submitBtn);
        imageView = findViewById(R.id.profileImage);
        membershipTier = findViewById(R.id.membershipTier);
        membershipTier.setSelection(0);
        preferredName = findViewById(R.id.preferredName);


        setUpProfileData();

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if(obj == null){
                        obj = new JSONObject();
                    }
                    if(checkAllFieldsFilled()){
                        obj.put("title", "");
                        obj.put("firstName", firstNameEdt.getText().toString());
                        obj.put("lastName", lastNameEdt.getText().toString());
                        obj.put("surName", surNameEdt.getText().toString());
                        obj.put("preferredName", preferredName.getText().toString());
                        obj.put("suburb", subburbEdt.getText().toString());
                        obj.put("dob", dateOfBirthEdt.getText().toString());
                        obj.put("license_no", licenseNumberEdt.getText().toString());
                        obj.put("license_number", licenseNumberEdt.getText().toString());
                        obj.put("expiry", expiryEdt.getText().toString());
                        obj.put("state", stateEdt.getText().toString());
                        obj.put("postcode", postCodeEdt.getText().toString());
                        obj.put("country", countryEdt.getText().toString());
                        obj.put("addressVal", addressEdt.getText().toString());
                        obj.put("address", addressEdt.getText().toString());

                        Intent intent = new Intent(PersonalInfoActivity.this, ContactInfoActivity.class);
                        SessionUtil.saveData(PersonalInfoActivity.this, obj);
                        startActivity(intent);
                    }else{
                        new SweetAlertDialog(PersonalInfoActivity.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Data Required")
                                .setContentText("Please fill atleast firstName, lastName, dob")
                                .show();
                    }

                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(PersonalInfoActivity.this, "There is some issue", Toast.LENGTH_SHORT).show();
                }
            }
        });

        findViewById(R.id.logoutImage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SessionUtil.setSignOut(PersonalInfoActivity.this);
                startActivity(new Intent(PersonalInfoActivity.this, SplashActivity.class));
                finishAffinity();
            }
        });

//        login = SessionUtil.getLoginData(PersonalInfoActivity.this);
//        if(login.data.tokenAuth.user.organization.theme != null){
//            String hexColor = "#00524c";
//            if(hexColor.charAt(0) != '#'){
//                hexColor = "#" + hexColor;
//            }
//            try {
//                changeThemeColor(Color.parseColor(hexColor));
//                setUpMembershipType(Color.parseColor(hexColor));
//            }catch (Exception e){
//                e.printStackTrace();
//            }
//        }else{
//            setUpMembershipType(getResources().getColor(R.color.primaryTextColor));
//        }
        String hexColor = "#00524c";
        if(hexColor.charAt(0) != '#'){
            hexColor = "#" + hexColor;
        }
        try {
            changeThemeColor(Color.parseColor(hexColor));
            setUpMembershipType(Color.parseColor(hexColor));
        }catch (Exception e){
            e.printStackTrace();
        }

        datePickerDialog = new DatePickerDialog(
                this,
                dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        dateOfBirthEdt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isDobSelected = true;
                datePickerDialog.show();
            }
        });

        expiryEdt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isDobSelected = false;
                datePickerDialog.show();
            }
        });
    }

    private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            // Handle the selected date
            String selectedDate = String.format("%04d-%02d-%02d", year, monthOfYear + 1, dayOfMonth);
            if(isDobSelected){
                dateOfBirthEdt.setText(selectedDate);
            }else{
                expiryEdt.setText(selectedDate);
            }
        }
    };

    private void setUpMembershipType(int color) {
        List<String> membershipTypes = new ArrayList<String>();
        membershipTypes.add("Select Membership Type");

        if(login != null && login.data.tokenAuth.user.organization.membershipType != null && !login.data.tokenAuth.user.organization.membershipType.equals("") && !login.data.tokenAuth.user.organization.membershipType.equals(" ")){
            String[] types = login.data.tokenAuth.user.organization.membershipType.split(",");
            for(int i=0;i<types.length;i++){
                membershipTypes.add(types[i]);
            }
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, membershipTypes);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        membershipType.setAdapter(dataAdapter);

        membershipType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                TextView selectedTextView = (TextView) view;
                selectedTextView.setTextColor(color);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        membershipTier.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                TextView selectedTextView = (TextView) view;
                selectedTextView.setTextColor(color);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private boolean checkAllFieldsFilled() {
        Boolean check = true;
        if(firstNameEdt.getText().toString().equals("") || lastNameEdt.getText().toString().equals("") || dateOfBirthEdt.getText().toString().equals("")){
            check = false;
        }
        return check;
    }

    void setUpProfileData(){
        try {
            obj = SessionUtil.getData(PersonalInfoActivity.this);
            String photo = obj.get("photo").toString();
            final byte[] decodedBytes = Base64.decode(photo, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
            imageView.setImageBitmap(decodedByte);
        }catch (Exception e){}

        try {
            String[] nameData = obj.get("name").toString().split(" ");
            String lastname = "";
            firstNameEdt.setText(Utils.firstLetterCapital(nameData[0]));

            try{
                lastNameEdt.setText(Utils.firstLetterCapital(nameData[1]));
                lastname = nameData[2];
                if(nameData[3] != "" && nameData[3] != null){
                    lastname += " "+nameData[3];
                }
                if(nameData[4] != "" && nameData[4] != null){
                    lastname += " "+nameData[4];
                }
            }catch(Exception e){Log.d("ERROR", e.toString());}

            surNameEdt.setText(Utils.firstLetterCapital(lastname));

        }catch (Exception e){
            try {
                firstNameEdt.setText(Utils.firstLetterCapital(obj.get("firstName").toString()));
            }catch (Exception ee){}
            try {
                lastNameEdt.setText(Utils.firstLetterCapital(obj.get("lastName").toString()));
            }catch (Exception ee){}
            try {
                surNameEdt.setText(Utils.firstLetterCapital(obj.get("surName").toString()));
            }catch (Exception ee){}
        }

        //Setting up Address
        countryEdt.setText("Australia");
        try {
            if(!obj.get("address").equals("") || !obj.get("address").equals("null")) {
                addressEdt.setText(Utils.firstLetterCapital(obj.get("address").toString()));
            }
            if(!obj.get("country").equals("") || !obj.get("country").equals("null")) {
                countryEdt.setText(Utils.firstLetterCapital(obj.get("country").toString()));
            }
        }catch (Exception e){}

        try{
            if(obj.get("suburb") != null && !obj.get("suburb").toString().equals("")){
                subburbEdt.setText(Utils.firstLetterCapital(obj.get("suburb").toString()));
            }
        }catch (Exception ee){}

        try{
            if(obj.get("postcode") != null && !obj.get("postcode").toString().equals("")){
                postCodeEdt.setText(obj.get("postcode").toString());
            }
        }catch (Exception ee){}

        try{
            if(obj.get("preferredName") != null && !obj.get("preferredName").toString().equals("")){
                preferredName.setText(Utils.firstLetterCapital(obj.get("preferredName").toString()));
            }
        }catch (Exception ee){}

        try{
            if(obj.get("state") != null && !obj.get("state").toString().equals("") && !obj.get("state").toString().equals("null")){
                stateEdt.setText(Utils.firstLetterCapital(obj.get("state").toString()));
            }
        }catch (Exception ee){}

        try{
            if(obj.get("license_number") != null && !obj.get("license_number").toString().equals("") && !obj.get("license_number").toString().equals("null")){
                licenseNumberEdt.setText(obj.get("license_number").toString());
            }
        }catch (Exception ee){}

        try{
            if(obj.get("expiry") != null && !obj.get("expiry").toString().equals("") && !obj.get("expiry").toString().equals("null")){
                expiryEdt.setText(obj.get("expiry").toString());
            }
        }catch (Exception ee){}

        try{
            if(obj.get("dob") != null && !obj.get("dob").toString().equals("") && !obj.get("dob").toString().equals("null")){
                dateOfBirthEdt.setText(obj.get("dob").toString());
            }
        }catch (Exception ee){}

        try{
            if(obj.get("suburb") != null){
                subburbEdt.setText(Utils.firstLetterCapital(obj.get("suburb").toString()));
            }
            if(obj.get("postcode") != null){
                postCodeEdt.setText(Utils.firstLetterCapital(obj.get("postcode").toString()));
            }
            if(obj.get("state") != null){
                stateEdt.setText(Utils.firstLetterCapital(obj.get("state").toString()));
            }
        }catch (Exception ee){}

        try {
            if(obj.get("dob").toString().equals("null")){
                dateOfBirthEdt.setText(obj.get("dob").toString());
            }
            if(obj.get("license_number").toString().equals("null")){
                licenseNumberEdt.setText(obj.get("license_number").toString());
            }
            if(obj.get("expiry").toString().equals("null")){
                expiryEdt.setText(obj.get("expiry").toString());
            }
        }catch (Exception e){}
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
        mStepsView.go(2, true);
        mStepsView.setOnStepClickListener(new StepView.OnStepClickListener() {
            @Override
            public void onStepClick(int step) {
                if(step != 2){
                    if(step == 1){
                        startActivity(new Intent(PersonalInfoActivity.this, TakePhotoActivity.class));
                        finishAffinity();
                    }else if(step == 0){
                        Utils.checkDataOnFirstPage(PersonalInfoActivity.this, PersonalInfoActivity.this);
                    }
                    else if(step == 3){
                        startActivity(new Intent(PersonalInfoActivity.this, ContactInfoActivity.class));
                        finishAffinity();
                    }
                    else if(step == 4) {
                        startActivity(new Intent(PersonalInfoActivity.this, ESignatureActivity.class));
                        finishAffinity();
                    }
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    void changeThemeColor(int color){
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

        imageView.setBorderColor(color);

        firstNameEdt.setTextColor(color);
        lastNameEdt.setTextColor(color);
        surNameEdt.setTextColor(color);
        addressEdt.setTextColor(color);
        subburbEdt.setTextColor(color);
        postCodeEdt.setTextColor(color);
        stateEdt.setTextColor(color);
        countryEdt.setTextColor(color);
        expiryEdt.setTextColor(color);
        dateOfBirthEdt.setTextColor(color);
        licenseNumberEdt.setTextColor(color);
        preferredName.setTextColor(color);


    }
}