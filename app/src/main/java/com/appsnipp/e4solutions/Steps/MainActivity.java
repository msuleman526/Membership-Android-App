package com.appsnipp.e4solutions.Steps;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
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

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.AspectRatio;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.util.Consumer;
import androidx.lifecycle.LifecycleOwner;

import com.appsnipp.e4solutions.Models.Login;
import com.appsnipp.e4solutions.R;
import com.appsnipp.e4solutions.SplashActivity;
import com.appsnipp.e4solutions.Utils.AddressParser;
import com.appsnipp.e4solutions.Utils.DialogView;
import com.appsnipp.e4solutions.Utils.SessionUtil;
import com.appsnipp.e4solutions.Utils.Utils;
import com.google.common.util.concurrent.ListenableFuture;
import com.shuhart.stepview.StepView;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;
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

public class MainActivity extends AppCompatActivity implements LifecycleOwner {

    StepView mStepsView;
    private PreviewView previewView;
    int cameraFacing = CameraSelector.LENS_FACING_BACK;
    ImageView cameraButton;
    Button submitBtn;

    JSONObject licenseJSONResponse;
    ProgressDialog pd;

    private final ActivityResultLauncher<String> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
        @Override
        public void onActivityResult(Boolean result) {
            if (result) {
                startCamera(cameraFacing);
            }
        }
    });

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

        previewView = findViewById(R.id.cameraPreview);
        cameraButton = findViewById(R.id.cameraButton);
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            activityResultLauncher.launch(Manifest.permission.CAMERA);
        } else {
            startCamera(cameraFacing);
        }
    }

    public void startCamera(int cameraFacing) {
        int aspectRatio = aspectRatio(previewView.getWidth(), previewView.getHeight());
        ListenableFuture<ProcessCameraProvider> listenableFuture = ProcessCameraProvider.getInstance(this);

        listenableFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = (ProcessCameraProvider) listenableFuture.get();

                Preview preview = new Preview.Builder().setTargetAspectRatio(aspectRatio).build();

                ImageCapture imageCapture = new ImageCapture.Builder().setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                        .setTargetRotation(getWindowManager().getDefaultDisplay().getRotation()).build();

                CameraSelector cameraSelector = new CameraSelector.Builder()
                        .requireLensFacing(cameraFacing).build();

                cameraProvider.unbindAll();

                Camera camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture);

                cameraButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            activityResultLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                        }
                        takePicture(imageCapture);
                    }
                });
                preview.setSurfaceProvider(previewView.getSurfaceProvider());
            } catch (Exception e) {
                Log.d("ERRROR", e.toString());
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    public void takePicture(ImageCapture imageCapture) {
        final File file = new File(getExternalFilesDir(null), System.currentTimeMillis() + ".jpg");
        ImageCapture.OutputFileOptions outputFileOptions = new ImageCapture.OutputFileOptions.Builder(file).build();
        imageCapture.takePicture(outputFileOptions, Executors.newCachedThreadPool(), new ImageCapture.OnImageSavedCallback() {
            @Override
            public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CropImage.activity(outputFileResults.getSavedUri())
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .start(MainActivity.this);
                    }
                });
                startCamera(cameraFacing);
            }

            @Override
            public void onError(@NonNull ImageCaptureException exception) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "Failed to capture: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
                startCamera(cameraFacing);
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
    private String encodeImage(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 80, baos);
        byte[] b = baos.toByteArray();
        String encImage = Base64.encodeToString(b, Base64.DEFAULT);
        return encImage;
    }

    private int aspectRatio(int width, int height) {
        double previewRatio = (double) Math.max(width, height) / Math.min(width, height);
        if (Math.abs(previewRatio - 4.0 / 3.0) <= Math.abs(previewRatio - 16.0 / 9.0)) {
            return AspectRatio.RATIO_4_3;
        }
        return AspectRatio.RATIO_16_9;
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    void setupMultiSteps() {
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
                if (step != 0) {
                    if (step == 1) {
                        startActivity(new Intent(MainActivity.this, TakePhotoActivity.class));
                        finishAffinity();
                    } else if (step == 2) {
                        startActivity(new Intent(MainActivity.this, PersonalInfoActivity.class));
                        finishAffinity();
                    } else if (step == 3) {
                        startActivity(new Intent(MainActivity.this, ContactInfoActivity.class));
                        finishAffinity();
                    } else if (step == 4) {
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

    void licenseCapture(File file, String bitmapBase64) {
        if (Utils.isInternetConnected(this)) {
            pd = new ProgressDialog(MainActivity.this);
            pd.setMessage("Getting License Info");
            pd.show();
            try {
                OkHttpClient.Builder builder = new OkHttpClient.Builder();
                builder.connectTimeout(600, TimeUnit.SECONDS);
                builder.readTimeout(600, TimeUnit.SECONDS);
                builder.writeTimeout(600, TimeUnit.SECONDS);
                OkHttpClient client = builder.build();

                MediaType mediaType = MediaType.parse("text/plain");
                RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                        .addFormDataPart("file", file.getName(),
                                RequestBody.create(MediaType.parse("application/octet-stream"),
                                        file))
                        .build();
                Request request = new Request.Builder()
                        .url("https://newapi.recokgnize.com/")
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
                                Log.d("Responseeeee", yourResponse);
                                verifyInfoAndGoToNext(yourResponse, bitmapBase64);
                            }
                        });

                    }
                });
            } catch (Exception e) {
                Log.d("ERROR", e.toString());
            }
        } else {
            DialogView.showAleryDialog(SweetAlertDialog.ERROR_TYPE, this, "No Internet", "Please connect internet to proceed.");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        //camera.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //camera.stop();
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
            if (!licenseJSONResponse.get("name").equals("") && !licenseJSONResponse.get("name").equals("null") && !licenseJSONResponse.get("face").equals("null")) {
                Intent intent = new Intent(MainActivity.this, TakePhotoActivity.class);
                licenseJSONResponse.put("license_img", base64Img);
                licenseJSONResponse.put("homephone", "");
                licenseJSONResponse.put("mobilephone", "");
                licenseJSONResponse.put("occupation", "");
                licenseJSONResponse.put("language", "");
                licenseJSONResponse.put("email", "");
                licenseJSONResponse.put("photo", licenseJSONResponse.get("face").toString());
                licenseJSONResponse.put("interests", "");

                licenseJSONResponse.put("suburb", "");
                licenseJSONResponse.put("state", "");
                licenseJSONResponse.put("postcode", "");
                licenseJSONResponse.put("country", "");

                String capturedAddress = "";

                try {
                    capturedAddress = licenseJSONResponse.get("address").toString();
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
                Toast.makeText(MainActivity.this, "Invalid License / Blur License. Try Again", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            Log.d("ERRRORR", e.toString());
            Toast.makeText(MainActivity.this, "Invalid License / Blur License. Try Again/", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                File encoded = new File(result.getUri().getPath());
                String encod = "";
                licenseCapture(encoded, encod);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(this, "There is some issue in cropping.", Toast.LENGTH_SHORT).show();
            }
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