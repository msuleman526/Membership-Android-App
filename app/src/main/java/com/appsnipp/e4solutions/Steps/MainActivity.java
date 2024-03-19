package com.appsnipp.e4solutions.Steps;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleOwner;

import com.appsnipp.e4solutions.Models.Login;
import com.appsnipp.e4solutions.R;
import com.appsnipp.e4solutions.SplashActivity;
import com.appsnipp.e4solutions.Utils.AddressParser;
import com.appsnipp.e4solutions.Utils.DialogView;
import com.appsnipp.e4solutions.Utils.SessionUtil;
import com.appsnipp.e4solutions.Utils.Utils;
import com.otaliastudios.cameraview.BitmapCallback;
import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.PictureResult;
import com.otaliastudios.cameraview.VideoResult;
import com.shuhart.stepview.StepView;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements LifecycleOwner{

    StepView mStepsView;
    CameraView camera;
    ImageView cameraButton;
    Button submitBtn;

    JSONObject licenseJSONResponse;
    ProgressDialog pd;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //for changing status bar icon color
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        setContentView(R.layout.activity_main);
        setupMultiSteps();

        cameraButton = findViewById(R.id.cameraButton);
        camera = findViewById(R.id.camera);
        camera.setLifecycleOwner(this);

        camera.addCameraListener(new CameraListener() {
            @Override
            public void onPictureTaken(PictureResult result) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    result.toBitmap(2520, 1720, new BitmapCallback() {
                        @Override
                        public void onBitmapReady(@Nullable Bitmap bitmap) {
                            CropImage.activity(getImageUri(MainActivity.this, bitmap))
                                    .setGuidelines(CropImageView.Guidelines.ON)
                                    .start(MainActivity.this);
                            //String encoded = encodeImage(bitmap);
                            //licenseCapture(encoded);
                        }
                    });
                } else {
                    Toast.makeText(MainActivity.this, "Mobile not support for this feature", Toast.LENGTH_SHORT).show();
                }
            }
        });
        camera.open();
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                camera.takePicture();
            }
        });
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    // This method will use URL Image Encoding
    private String encodeImage(Bitmap bm)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG,80,baos);
        byte[] b = baos.toByteArray();
        String encImage = Base64.encodeToString(b, Base64.DEFAULT);
        return encImage;
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    void setupMultiSteps (){
        List<String> stepLabels = new ArrayList<>();
        stepLabels.add("License");
        stepLabels.add("Photo");
        stepLabels.add("Profile");
        stepLabels.add("Contact");
        stepLabels.add("E-Sign");

        mStepsView = findViewById(R.id.stepsView);
        mStepsView.setSteps(stepLabels);
        mStepsView.setOnStepClickListener(new StepView.OnStepClickListener() {
            @Override
            public void onStepClick(int step) {
                if(step != 0){
                    if(step == 1){
                        startActivity(new Intent(MainActivity.this, TakePhotoActivity.class));
                        finishAffinity();
                    }else if(step == 2){
                        startActivity(new Intent(MainActivity.this, PersonalInfoActivity.class));
                        finishAffinity();
                    }
                    else if(step == 3){
                        startActivity(new Intent(MainActivity.this, ContactInfoActivity.class));
                        finishAffinity();
                    }
                    else if(step == 4) {
                        startActivity(new Intent(MainActivity.this, ESignatureActivity.class));
                        finishAffinity();
                    }
                }
            }
        });

        findViewById(R.id.logoutImage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SessionUtil.setSignOut(MainActivity.this);
                startActivity(new Intent(MainActivity.this, SplashActivity.class));
                finishAffinity();
            }
        });

        //Login login = SessionUtil.getLoginData(MainActivity.this);

//        if(login.data.tokenAuth.user.organization.theme != null){
//            String color = login.data.tokenAuth.user.organization.theme.primaryColor;
//            changeThemeColor(color);
//        }

        String color = "#00524c";
        changeThemeColor(color);

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, TakePhotoActivity.class));
                finishAffinity();
            }
        });
    }

    void licenseCapture(String base64Img){
        if(Utils.isInternetConnected(this)) {
            pd = new ProgressDialog(MainActivity.this);
            pd.setMessage("Getting License Info");
            pd.show();
            try {
                camera.close();
                OkHttpClient.Builder builder = new OkHttpClient.Builder();
                builder.connectTimeout(600, TimeUnit.SECONDS);
                builder.readTimeout(600, TimeUnit.SECONDS);
                builder.writeTimeout(600, TimeUnit.SECONDS);
                OkHttpClient client = builder.build();

                MediaType mediaType = MediaType.parse("text/plain");
                RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                        .addFormDataPart("file_base64", base64Img)
                        .build();

                Request request = new Request.Builder()
                        .url("https://api.recokgnize.com/licensebeta")
                        .method("POST", body)
                        .addHeader("x-api-key", "12345abcd")
                        .build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        final String yourResponse = response.body().string();

                        MainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                pd.cancel();
                                pd.dismiss();
                                Log.d("Response", yourResponse);
                                verifyInfoAndGoToNext(yourResponse, base64Img);
                            }
                        });

                    }
                });
            } catch (Exception e) {
                Log.d("ERROR", e.toString());
            }
        }else{
            DialogView.showAleryDialog(SweetAlertDialog.ERROR_TYPE, this, "No Internet", "Please connect internet to proceed.");
        }
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
            submitBtn = findViewById(R.id.submitBtn);
            submitBtn.setBackgroundTintList(ColorStateList.valueOf(color));
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    void verifyInfoAndGoToNext(String response, String base64Img){
        try {
            licenseJSONResponse = new JSONObject(response);
            if (!licenseJSONResponse.get("name").equals("") && !licenseJSONResponse.get("name").equals("null") && !licenseJSONResponse.get("photo").equals("null")) {
                Intent intent = new Intent(MainActivity.this, TakePhotoActivity.class);
                licenseJSONResponse.put("license", base64Img);
                licenseJSONResponse.put("homephone", "");
                licenseJSONResponse.put("mobilephone", "");
                licenseJSONResponse.put("occupation", "");
                licenseJSONResponse.put("language", "");
                licenseJSONResponse.put("email", "");
                licenseJSONResponse.put("interests", "");

                licenseJSONResponse.put("suburb", "");
                licenseJSONResponse.put("state", "");
                licenseJSONResponse.put("postcode", "");
                licenseJSONResponse.put("country", "");

                String capturedAddress = licenseJSONResponse.get("address").toString();

                try {
                    String address = capturedAddress;
                    String[] addressData = address.split(",");
                    address = addressData[0];
                    address = address.replace("address ", "");
                    address = address.replace("address", "");
                    licenseJSONResponse.put("address", address);
                    if (addressData.length > 1) {
                        String[] otherData = addressData[1].split(" ");
                        licenseJSONResponse.put("postcode", otherData[otherData.length - 1]);
                        licenseJSONResponse.put("state", otherData[otherData.length - 2]);
                        String suburb = "";
                        if (otherData[otherData.length - 3] != null) {
                            suburb = otherData[otherData.length - 3];
                        }
                        if (otherData[otherData.length - 4] != null) {
                            suburb = otherData[otherData.length - 4] + " " + suburb;
                        }
                        licenseJSONResponse.put("suburb", suburb);
                    }

                } catch (Exception e) {}

                capturedAddress = capturedAddress.replace("address ", "");
                capturedAddress = capturedAddress.replace("adress ", "");
                capturedAddress = capturedAddress.replace("addres ", "");
                capturedAddress = capturedAddress.replace("addrss ", "");
                capturedAddress = capturedAddress.replace("address", "");
                capturedAddress = capturedAddress.replace("adress", "");
                capturedAddress = capturedAddress.replace("addres", "");
                capturedAddress = capturedAddress.replace("addrss", "");

                if (capturedAddress.equals("") || capturedAddress.equals(" ") || capturedAddress.equals("null")) {
                    SessionUtil.saveData(MainActivity.this, licenseJSONResponse);
                    startActivity(intent);
                } else {
                    GetAddressInformation getAddressInformation = new GetAddressInformation();
                    getAddressInformation.execute(capturedAddress);
                }
            } else {
                camera.open();
                Toast.makeText(MainActivity.this, "Invalid License / Blur License. Try Again", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            camera.open();
            Toast.makeText(MainActivity.this, "Invalid License / Blur License. Try Again", Toast.LENGTH_SHORT).show();
        }
    }

    //This method is used for Getting Proper Address By Google.
    class GetAddressInformation extends AsyncTask<String, Void, List<Address>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.setMessage("Verifying address please wait....");
            pd.show();
        }

        @Override
        protected List<Address> doInBackground(String... address) {

            List<Address> addresses = new ArrayList<>();
            try {
                Geocoder geocoder;
                geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                try {
                    String adrs = address[0] + " Australia";
                    Log.d("ADDRESS", adrs);
                    addresses = geocoder.getFromLocationName(adrs, 1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return addresses;
        }

        @Override
        protected void onPostExecute(List<Address> results) {
            super.onPostExecute(results);
            pd.dismiss();
            if (results.size() < 1) {
                Log.d("Address Details", "No Address Found - 0");
            } else {
                try {
                    String addres= results.get(0).getAddressLine(0);
                    String[] addressParts = addres.split(","); // Split the address by comma
                    if (addressParts.length > 0) {
                        addres =  addressParts[0].trim(); // Get the first part, which should be the street address
                    }
                    String city = results.get(0).getLocality();
                    String statee = results.get(0).getAdminArea();
                    String zipCode = results.get(0).getPostalCode();
                    String country = results.get(0).getCountryName();

                    Log.d("STREEET", results.get(0).toString());

                    licenseJSONResponse.put("address", addres);
                    licenseJSONResponse.put("suburb", city);
                    licenseJSONResponse.put("state", statee);
                    licenseJSONResponse.put("postcode", zipCode);
                    licenseJSONResponse.put("country", country);

                    SessionUtil.saveData(MainActivity.this, licenseJSONResponse);
                    Intent intent = new Intent(MainActivity.this, TakePhotoActivity.class);
                    startActivity(intent);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

}