package com.appsnipp.e4solutions.Steps;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.appsnipp.e4solutions.Models.Login;
import com.appsnipp.e4solutions.R;
import com.appsnipp.e4solutions.SplashActivity;
import com.appsnipp.e4solutions.Utils.DialogView;
import com.appsnipp.e4solutions.Utils.SessionUtil;
import com.appsnipp.e4solutions.Utils.Utils;
import com.github.gcacace.signaturepad.views.SignaturePad;
import com.google.gson.Gson;
import com.shuhart.stepview.StepView;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ESignatureActivity extends AppCompatActivity {

    StepView mStepsView;
    SignaturePad mSignaturePad;
    TextView signature_text;
    JSONObject jsonObject;
    Button submitBtn;
    String signatureTxt = "";
    boolean signed = false;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //for changing status bar icon colors
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        setContentView(R.layout.activity_sign);
        setupMultiSteps();
        signature_text = findViewById(R.id.signature_text);

        signature_text.setText("- By signing below, I declare that I am at least 18 years of age or older and that all representations made by me on this application are true and correct.\n" +
                "- I have received a copy of the Mindil Beach Casino & Resort membership program terms and conditions ans understant that the are available online at www.mindilbeachcasinoresort.com.au\n" +
                "- By signing up to the Mindil Beach Casino & Resort Membership Program, I agree to be contacted by Minidil Beach Casino & Resort using the information on this application form.\n" +
                "- I am aware that gambling at Mindil Beach Casino & Resort is a form of fun and entertainment, not a strategy for financial success and that free information and advice is available from Amity Community Services www.amity.org.au");

        mSignaturePad = (SignaturePad) findViewById(R.id.signature_pad);
        submitBtn = findViewById(R.id.submitBtn);
        mSignaturePad.setOnSignedListener(new SignaturePad.OnSignedListener() {

            @Override
            public void onStartSigning() {
                //Event triggered when the pad is touched
            }

            @Override
            public void onSigned() {
                signed = true;
            }

            @Override
            public void onClear() {
                signed = false;
            }
        });
        try{
            jsonObject = SessionUtil.getData(ESignatureActivity.this);
        }catch (Exception e){
            e.toString();
        }

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!signed){
                    Toast.makeText(ESignatureActivity.this, "Please Sign the before submit", Toast.LENGTH_SHORT).show();
                }else{
                    ProgressDialog progressBar = new ProgressDialog(ESignatureActivity.this);
                    try{
                        if(jsonObject == null){
                            jsonObject = new JSONObject();
                        }
                        progressBar.setMessage("Submitting Member Info...");
                        progressBar.show();
                        Bitmap bitmap = mSignaturePad.getSignatureBitmap();
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                        byte[] byteArray = byteArrayOutputStream .toByteArray();
                        String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
                        jsonObject.put("signature", encoded);
                        submitMember(progressBar);

                    }catch (Exception e){
                        progressBar.dismiss();
                        Toast.makeText(ESignatureActivity.this, "There is some issue in signature", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });

        findViewById(R.id.logoutImage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SessionUtil.setSignOut(ESignatureActivity.this);
                startActivity(new Intent(ESignatureActivity.this, SplashActivity.class));
                finishAffinity();
            }
        });

//        Login login = SessionUtil.getLoginData(ESignatureActivity.this);
//        if(login.data.tokenAuth.user.organization.theme != null){
//            String color = login.data.tokenAuth.user.organization.theme.primaryColor;
//            changeThemeColor(color);
//        }
        String color = "#00524c";
        changeThemeColor(color);

    }

//    if(jsonObject.get("title") == null ||
//            jsonObject.get("firstName").toString().equals("") ||
//            jsonObject.get("lastName") == null ||
//            jsonObject.get("surName").toString().equals("") ||
//            jsonObject.get("suburb") == null ||
//            jsonObject.get("dob").toString().equals("") ||
//            jsonObject.get("license_no").toString().equals("") ||
//            jsonObject.get("expiry") == null ||
//            jsonObject.get("state").toString().equals("") ||
//            jsonObject.get("postcode").toString().equals("") ||
//            jsonObject.get("country").toString().equals("") ||
//            jsonObject.get("addressVal").toString().equals("") ||
//            jsonObject.get("address").toString().equals(""))
//    {
//        pd.dismiss();
//        new SweetAlertDialog(ESignatureActivity.this, SweetAlertDialog.ERROR_TYPE)
//                .setTitleText("Data Required")
//                .setContentText("Please fill all personal information and save")
//                .show();
//        return;
//    }

    void submitMember(ProgressDialog pd) {
        try {
            try {
                if (jsonObject.get("firstName").toString().equals("") || jsonObject.get("lastName") == null || jsonObject.get("dob").toString().equals("")) {
                    pd.dismiss();
                    DialogView.showAleryDialog(SweetAlertDialog.ERROR_TYPE, this, "Data Required", "FirstName, LastName, DOB is required please fill.");
                    return;
                }
            } catch (Exception ee) {
                pd.dismiss();
                DialogView.showAleryDialog(SweetAlertDialog.ERROR_TYPE, this, "Data Required", "FirstName, LastName, DOB is required please fill.");
                return;
            }

            String dob = jsonObject.get("dob") != null && !jsonObject.get("dob").equals("") ? jsonObject.get("dob").toString() : "";
            String firstName = jsonObject.get("firstName") != null && !jsonObject.get("firstName").equals("") ? jsonObject.get("firstName").toString() : "";
            String surName = jsonObject.get("surName") != null && !jsonObject.get("surName").equals("") ? jsonObject.get("surName").toString() : "";
            String preferredName = jsonObject.get("preferredName") != null && !jsonObject.get("preferredName").equals("") ? jsonObject.get("preferredName").toString() : "";
            String lastName = jsonObject.get("lastName") != null && !jsonObject.get("lastName").equals("") ? jsonObject.get("lastName").toString() : "";

            String address1 = jsonObject.get("address") != null && !jsonObject.get("address").equals("") ? jsonObject.get("address").toString() : "";
            String city = jsonObject.get("suburb") != null && !jsonObject.get("suburb").equals("") ? jsonObject.get("suburb").toString() : "";
            String state = jsonObject.get("state") != null && !jsonObject.get("state").equals("") ? jsonObject.get("state").toString() : "";
            String postCode = jsonObject.get("postcode") != null && !jsonObject.get("postcode").equals("") ? jsonObject.get("postcode").toString() : "";

            String expiryDate = jsonObject.get("exp") != null && !jsonObject.get("exp").equals("") ? jsonObject.get("exp").toString() : "";
            String licenseNumber = jsonObject.get("license") != null && !jsonObject.get("license").equals("") ? jsonObject.get("license").toString() : "";

            String countryCode = "036";



            String mobilePhone = jsonObject.get("mobilephone") != null && !jsonObject.get("mobilephone").equals("") ? jsonObject.get("mobilephone").toString() : "";
            String emailAddress = jsonObject.get("email") != null && !jsonObject.get("email").equals("") ? jsonObject.get("email").toString() : "";
            String language = jsonObject.get("language") != null && !jsonObject.get("language").equals("") ? jsonObject.get("language").toString() : "";

            OkHttpClient okHttpClient = new OkHttpClient()
                    .newBuilder()
                    .connectTimeout(10,TimeUnit.SECONDS)
                    .writeTimeout(10,TimeUnit.SECONDS)
                    .readTimeout(10,TimeUnit.SECONDS)
                    .retryOnConnectionFailure(false)
                    .build();

            String rawString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n<CRMAcresMessage\r\n\txmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"C:\\XSD\\CRM.xsd\">\r\n\t<Header>\r\n\t\t<MessageID>123456791</MessageID>\r\n\t\t<TimeStamp>2009-01-02T01:00:00</TimeStamp>\r\n\t\t<Operation Data=\"PlayerProfile\" Operand=\"Add\"/>\r\n\t</Header>\r\n\t<Body>\r\n\t\t<PlayerProfile>\r\n\t\t\t<DateofBirth>"+dob+"</DateofBirth>\r\n\t\t\t<CreditAccount>false</CreditAccount>\r\n\t\t\t<Gender>M</Gender>\r\n<Languages>\r\n<Language>"+language+"</Language>\r\n</Languages>\r\n<WebEnabled>N</WebEnabled>\r\n\t\t\t<Name>\r\n\t\t\t\t<FirstName>"+firstName+"</FirstName>\r\n\t\t\t\t<PreferredName>"+preferredName+"</PreferredName>\r\n\t\t\t\t<MiddleName>"+surName+"</MiddleName>\r\n\t\t\t\t<LastName>"+lastName+"</LastName>\r\n\t\t\t</Name>\r\n\t\t\t<Addresses>\r\n\t\t\t\t<Address>\r\n\t\t\t\t\t<Address1>"+address1+"</Address1>\r\n\t\t\t\t\t<City>"+city+"</City>\r\n\t\t\t\t\t<StateProvince>"+state+"</StateProvince>\r\n\t\t\t\t\t<PostalCode>"+postCode+"</PostalCode>\r\n\t\t\t\t\t<Country>"+countryCode+"</Country>\r\n\t\t\t\t\t<Location>Work</Location>\r\n\t\t\t\t</Address>\r\n\t\t\t</Addresses>\r\n<PhoneNumbers>\r\n<PhoneNumber>\r\n<Number>"+mobilePhone+"</Number>\r\n<Location>Home</Location>\r\n</PhoneNumber>\r\n</PhoneNumbers>\r\n<Emails>\r\n<Email>\r\n<Address>"+emailAddress+"</Address>\r\n<Location>Personal Email</Location>\r\n</Email>\r\n</Emails>\r\n\t\t\t<Identifications>\r\n<Identification>\r\n<Type>Drivers License</Type>\r\n<IDNumber>"+licenseNumber+"</IDNumber>\r\n<ExpirationDate>"+expiryDate+"</ExpirationDate>\r\n</Identification>\r\n</Identifications>\r\n<EnrolledBy>\r\n\t\t\t\t<User>\r\n\t\t\t\t\t<UserID>1</UserID>\r\n\t\t\t\t</User>\r\n\t\t\t</EnrolledBy>\r\n\t\t\t<SiteParameters>\r\n\t\t\t\t<SiteInfo>\r\n\t\t\t\t\t<SiteID>1</SiteID>\r\n\t\t\t\t\t<Host>\r\n\t\t\t\t\t\t<UserID>1</UserID>\r\n\t\t\t\t\t</Host>\r\n\t\t\t\t</SiteInfo>\r\n\t\t\t</SiteParameters>\r\n\t\t</PlayerProfile>\r\n\t</Body>\r\n</CRMAcresMessage>";
            if(language.equals("")){
                rawString = rawString.replace("\r\n<Languages>\r\n<Language>"+language+"</Language>\r\n</Languages>", "");
            }
            if(mobilePhone.equals("")){
                rawString = rawString.replace("\r\n<PhoneNumbers>\r\n<PhoneNumber>\r\n<Number>"+mobilePhone+"</Number>\r\n<Location>Home</Location>\r\n</PhoneNumber>\r\n</PhoneNumbers>","");
            }
            if(emailAddress.equals("")){
                rawString = rawString.replace("<Emails>\r\n<Email>\r\n<Address>"+emailAddress+"</Address>\r\n<Location>Personal Email</Location>\r\n</Email>\r\n</Emails>", "");
            }
            if(licenseNumber.equals("") || expiryDate.equals("")){
                rawString = rawString.replace("<Identification>\r\n<Type>Drivers License</Type>\r\n<IDNumber>"+licenseNumber+"</IDNumber>\r\n<ExpirationDate>"+expiryDate+"</ExpirationDate>\r\n</Identification>\r\n</Identifications>\r\n", "");
            }

            MediaType mediaType = MediaType.parse("application/xml");
            RequestBody body = RequestBody.create(mediaType, rawString);

            Request request = new Request.Builder()
                    .url("http://192.168.8.76:8094/")
                    .method("POST", body)
                    .addHeader("Accept", "*/*")
                    .addHeader("Content-Type", "application/xml")
                    .build();

            okHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e){
                    ESignatureActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pd.cancel();
                            pd.dismiss();
                            DialogView.showAleryDialog(SweetAlertDialog.ERROR_TYPE, ESignatureActivity.this, "Api Failure", "Failed to connect Server. (192.168.8.76)");
                        }
                    });
                }
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    final String yourResponse = response.body().string();

                    ESignatureActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pd.cancel();
                            pd.dismiss();
                            if (response.code() == 200) {
                                handleResponse(yourResponse, firstName+" " + lastName);
                            } else {
                                DialogView.showAleryDialog(SweetAlertDialog.ERROR_TYPE, ESignatureActivity.this, "Api Error", "unable to connect to server/server response issue");
                            }

                        }
                    });

                }
            });
        } catch (Exception e) {
            pd.cancel();
            pd.dismiss();
            DialogView.showAleryDialog(SweetAlertDialog.ERROR_TYPE, ESignatureActivity.this, "Api Process Error", e.toString());
        }
    }

    void handleResponse(String xmlString, String name){
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new ByteArrayInputStream(xmlString.getBytes()));
            Element root = document.getDocumentElement();

            NodeList errorDescription = root.getElementsByTagName("ErrorDescription");
            if(errorDescription != null && errorDescription.getLength() > 0){
                DialogView.showAleryDialog(SweetAlertDialog.ERROR_TYPE, ESignatureActivity.this, "Api Error", errorDescription.item(0).getTextContent());
            }else{
                String playerID = root.getElementsByTagName("PlayerID").item(0).getTextContent();
                SweetAlertDialog sDialog = new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE);
                sDialog.setCancelable(false);
                sDialog.setTitleText("Thank You");
                sDialog.setConfirmButton("Done", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        Intent intent = new Intent(ESignatureActivity.this, MainActivity.class);
                        SessionUtil.saveData(ESignatureActivity.this, null);
                        startActivity(intent);
                        finishAffinity();
                    }
                });
                sDialog.setContentText("Congratulations, #"+playerID+" - " + name + ", created as member successfully.");
                sDialog.show();
            }
        } catch (Exception e) {
            DialogView.showAleryDialog(SweetAlertDialog.ERROR_TYPE, ESignatureActivity.this, "Api Error.", "unable to connect to server/server response issue");
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
        mStepsView.go(4, true);
        mStepsView.setOnStepClickListener(new StepView.OnStepClickListener() {
            @Override
            public void onStepClick(int step) {
                if(step != 4){
                    if(step == 1){
                        startActivity(new Intent(ESignatureActivity.this, TakePhotoActivity.class));
                        finishAffinity();
                    }else if(step == 2){
                        startActivity(new Intent(ESignatureActivity.this, PersonalInfoActivity.class));
                        finishAffinity();
                    }
                    else if(step == 3){
                        startActivity(new Intent(ESignatureActivity.this, ContactInfoActivity.class));
                        finishAffinity();
                    }
                    else if(step == 0) {
                        Utils.checkDataOnFirstPage(ESignatureActivity.this, ESignatureActivity.this);
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
            mSignaturePad.setPenColor(color);
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}