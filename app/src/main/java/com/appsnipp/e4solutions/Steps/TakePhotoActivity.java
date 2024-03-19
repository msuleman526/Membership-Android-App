package com.appsnipp.e4solutions.Steps;

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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.appsnipp.e4solutions.Models.Login;
import com.appsnipp.e4solutions.R;
import com.appsnipp.e4solutions.SplashActivity;
import com.appsnipp.e4solutions.Utils.SessionUtil;
import com.appsnipp.e4solutions.Utils.Utils;
import com.otaliastudios.cameraview.BitmapCallback;
import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.PictureResult;
import com.otaliastudios.cameraview.VideoResult;
import com.shuhart.stepview.StepView;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class TakePhotoActivity extends AppCompatActivity {

    StepView mStepsView;
    Button skipButton, takePhotoBtn;
    ImageView imageView;
    JSONObject object;
    CameraView camera;
    ImageView cameraButton;
    TextView title;

    RelativeLayout cameraDiv;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //for changing status bar icon colors
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        setContentView(R.layout.activity_take_photo);
        setupMultiSteps();
        skipButton = findViewById(R.id.skipButton);
        imageView = findViewById(R.id.profileImage);
        takePhotoBtn = findViewById(R.id.takePhotoBtn);
        cameraDiv = findViewById(R.id.cameraDiv);
        title = findViewById(R.id.title);

        object = SessionUtil.getData(TakePhotoActivity.this);

        try {
            String photo = object.get("photo").toString();
            final byte[] decodedBytes = Base64.decode(photo, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
            imageView.setImageBitmap(decodedByte);
            title.setText("Welcome "+ Utils.toTitleCase(object.get("name").toString()));
        }catch (Exception e){
            object = new JSONObject();
        }

        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if(object == null){
                        object = new JSONObject();
                    }
                    if (object.get("photo") != null && !object.get("photo").toString().equals("")) {
                        object.put("photo", object.get("photo").toString());
                    }
                    SessionUtil.saveData(TakePhotoActivity.this, object);
                }catch (Exception e){
                    e.printStackTrace();
                }
                Intent intent = new Intent(TakePhotoActivity.this, PersonalInfoActivity.class);
                startActivity(intent);
            }
        });

        takePhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    imageView.setVisibility(View.GONE);
                    setUpCamera();
                }catch (Exception e){
                    Toast.makeText(TakePhotoActivity.this, "Please Try Again.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        findViewById(R.id.logoutImage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SessionUtil.setSignOut(TakePhotoActivity.this);
                startActivity(new Intent(TakePhotoActivity.this, SplashActivity.class));
                finishAffinity();
            }
        });

        findViewById(R.id.rotateImage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                camera.toggleFacing();
            }
        });
//
//        Login login = SessionUtil.getLoginData(TakePhotoActivity.this);
//        if(login.data.tokenAuth.user.organization.theme != null){
//            String color = login.data.tokenAuth.user.organization.theme.primaryColor;
//            changeThemeColor(color);
//        }

        String color = "#00524c";
        changeThemeColor(color);


    }

    //This will encode Image.
    private String encodeImage(Bitmap bm)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG,80,baos);
        byte[] b = baos.toByteArray();
        String encImage = Base64.encodeToString(b, Base64.DEFAULT);
        return encImage;
    }

    //This will setup Camera View
    void setUpCamera () {
        camera = findViewById(R.id.camera);
        cameraButton = findViewById(R.id.cameraButton);
        cameraButton.setVisibility(View.VISIBLE);
        cameraDiv.setVisibility(View.VISIBLE);
        camera.setVisibility(View.VISIBLE);
        camera.toggleFacing();
        camera.setLifecycleOwner(this);
        camera.addCameraListener(new CameraListener() {
            @Override
            public void onPictureTaken(PictureResult result) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    result.toBitmap(1920, 1020, new BitmapCallback() {
                        @Override
                        public void onBitmapReady(@Nullable Bitmap bitmap) {
                            try {
                                String photo = encodeImage(bitmap);
                                Intent intent = new Intent(TakePhotoActivity.this, PersonalInfoActivity.class);
                                object.put("photo", photo);
                                SessionUtil.saveData(TakePhotoActivity.this, object);
                                startActivity(intent);
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    });
                }else{
                    Toast.makeText(TakePhotoActivity.this, "Mobile not support for this feature", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onVideoTaken(VideoResult result) {
                // A Video was taken!
            }

            // And much more
        });
        camera.open();

        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                camera.takePicture();
            }
        });
    }

    //This will Setup MultiStep View
    void setupMultiSteps (){
        List<String> stepLabels = new ArrayList<>();
        stepLabels.add("License");
        stepLabels.add("Photo");
        stepLabels.add("Profile");
        stepLabels.add("Contact");
        stepLabels.add("E-Sign");

        mStepsView = findViewById(R.id.stepsView);
        mStepsView.setSteps(stepLabels);
        mStepsView.go(1, true);
        mStepsView.setOnStepClickListener(new StepView.OnStepClickListener() {
            @Override
            public void onStepClick(int step) {
                if(step != 1){
                    if(step == 2){
                        startActivity(new Intent(TakePhotoActivity.this, PersonalInfoActivity.class));
                        finishAffinity();
                    }else if(step == 0){
                        Utils.checkDataOnFirstPage(TakePhotoActivity.this, TakePhotoActivity.this);
                    }
                    else if(step == 3){
                        startActivity(new Intent(TakePhotoActivity.this, ContactInfoActivity.class));
                        finishAffinity();
                    }
                    else if(step == 4) {
                        startActivity(new Intent(TakePhotoActivity.this, ESignatureActivity.class));
                        finishAffinity();
                    }
                }
            }
        });
    }

    //This will change Theme of Screen.
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

            title.setTextColor(color);

            Window window = this.getWindow();
            window.setStatusBarColor(color);
            skipButton.setBackgroundTintList(ColorStateList.valueOf(color));
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}