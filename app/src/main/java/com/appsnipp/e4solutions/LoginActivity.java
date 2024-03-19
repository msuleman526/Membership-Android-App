package com.appsnipp.e4solutions;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.appsnipp.e4solutions.Models.Login;
import com.appsnipp.e4solutions.Models.OTPResponse;
import com.appsnipp.e4solutions.Steps.MainActivity;
import com.appsnipp.e4solutions.Utils.Constants;
import com.appsnipp.e4solutions.Utils.SessionUtil;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class LoginActivity extends AppCompatActivity {

    EditText emailEdt, passwordEdt;
    RelativeLayout loginView, otpView;
    Button loginBtn;
    Login loginResponse = null;
    Button submitOTP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //for changing status bar icon colors
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M){
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        setContentView(R.layout.activity_login);
        emailEdt = findViewById(R.id.editText);
        passwordEdt  = findViewById(R.id.editText2);
        loginView = findViewById(R.id.loginView);
        otpView = findViewById(R.id.otpView);
        loginBtn = findViewById(R.id.button);
        submitOTP = findViewById(R.id.submitOTP);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onLoginClick();
            }
        });

        submitOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSubmitOTP();
            }
        });

    }

    public void onLoginClick(){
        String email = emailEdt.getText().toString();
        String password = passwordEdt.getText().toString();
        if(email.equals("") || password.equals("")){
            Toast.makeText(this, "Please fill the form", Toast.LENGTH_SHORT).show();
        }else if(!Constants.isValidEmailAddress(email)){
            Toast.makeText(this, "Invalid Email Address", Toast.LENGTH_SHORT).show();
        }else{
            callLoginApi(email, password);
        }

    }

    public void onSubmitOTP (){
        EditText otpEdt = findViewById(R.id.otpEdt);
        String otp = otpEdt.getText().toString();
        if(otp.equals("") || otp.length() < 4){
            Toast.makeText(this, "Invalid OTP/Minimum Length is 4", Toast.LENGTH_SHORT).show();
        }else{
            callOTPApi(otp);
        }
    }

    public void callOTPApi (String otp) {
        ProgressDialog pd = new ProgressDialog(LoginActivity.this);
        pd.setMessage("Verifying OTP Please wait...");
        pd.show();
        try {
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.connectTimeout(600, TimeUnit.SECONDS);
            builder.readTimeout(600, TimeUnit.SECONDS);
            builder.writeTimeout(600, TimeUnit.SECONDS);
            OkHttpClient client = builder.build();

            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, "{\"operationName\":\"VerifyMfa\",\"variables\":{\"mfa\":" + otp + ",\"MFAHash\":\"T7UIBL3UZ4EOSAIXYX2Y3JSMIGCK3LOU\"},\"query\":\"mutation VerifyMfa($mfa: String!, $MFAHash: String!) {\\n  verifyMfa(mfa: $mfa, MFAHash: $MFAHash) {\\n    mfa\\n    __typename\\n  }\\n}\\n\"}");
            Request request = new Request.Builder()
                    .url("https://ops.e4solutions.io/graphql")
                    .method("POST", body)
                    .build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    final String yourResponse = response.body().string();

                    LoginActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pd.cancel();
                            pd.dismiss();
                            try {
                                Log.d("Response", yourResponse);
                                Gson gson1 = new Gson();
                                OTPResponse otpResponse = gson1.fromJson(yourResponse, OTPResponse.class);
                                if (otpResponse.data.verifyMfa.mfa.equals("true") || otpResponse.data.verifyMfa.mfa.equals("True")) {
                                    confirmLogin(loginResponse);
                                } else {
                                    Toast.makeText(LoginActivity.this, "Invalid OTP", Toast.LENGTH_SHORT).show();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(LoginActivity.this, "Invalid OTP", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
            });
        } catch (Exception e) {
            pd.cancel();
            pd.dismiss();
            e.printStackTrace();
            Toast.makeText(LoginActivity.this, "Invalid OTP", Toast.LENGTH_SHORT).show();
        }
    }

    private void callLoginApi(String email, String password) {
        ProgressDialog pd = new ProgressDialog(LoginActivity.this);
        pd.setMessage("Sign in Please wait...");
        pd.show();
        try {
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.connectTimeout(600, TimeUnit.SECONDS);
            builder.readTimeout(600, TimeUnit.SECONDS);
            builder.writeTimeout(600, TimeUnit.SECONDS);
            OkHttpClient client = builder.build();

            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, "{\"operationName\":\"AuthToken\",\"variables\":{\"username\":\""+email+"\",\"password\":\""+password+"\"},\"query\":\"fragment VendorType on VendorType {\\n  name\\n  slug\\n  __typename\\n}\\n\\nfragment OrganizationTheme on OrganizationTheme {\\n  primaryColor\\n  secondaryColor\\n  __typename\\n}\\n\\nfragment TierBrandingType on TierBrandingType {\\n  name\\n  code\\n  value\\n  primaryColor\\n  secondaryColor\\n  __typename\\n}\\n\\nfragment AppType on AppType {\\n  name\\n  slug\\n  __typename\\n}\\n\\nfragment Organization on OrganizationType {\\n  id\\n  name\\n  logo\\n  slug\\n  modified\\n  offsiteApi\\n  onsiteApi\\n  phone\\n  website\\n  vaccinationTheme\\n membershipType\\n theme {\\n    ...OrganizationTheme\\n    __typename\\n  }\\n  tiers {\\n    ...TierBrandingType\\n    __typename\\n  }\\n  apps {\\n    ...AppType\\n    __typename\\n  }\\n  vendor {\\n    ...VendorType\\n    __typename\\n  }\\n  __typename\\n}\\n\\nfragment User on UserType {\\n  id\\n  firstName\\n  lastName\\n  email\\n  phone\\n  birthday\\n  address\\n  username\\n  isActive\\n  isAdmin\\n  isMFA\\n  MFAHash\\n  isOffsite\\n  lastLogin\\n  dateJoined\\n  organization {\\n    ...Organization\\n    __typename\\n  }\\n  __typename\\n}\\n\\nmutation AuthToken($username: String!, $password: String!) {\\n  tokenAuth(username: $username, password: $password) {\\n    token\\n    user {\\n      ...User\\n      __typename\\n    }\\n    __typename\\n  }\\n}\\n\"}");
            //RequestBody body = RequestBody.create(mediaType, "{\"operationName\":\"AuthToken\",\"variables\":{\"username\":\""+email+"\",\"password\":\""+password+"\"},\"query\":\"fragment VendorType on VendorType {\\n  name\\n  slug\\n  __typename\\n}\\n\\nfragment OrganizationTheme on OrganizationTheme {\\n  primaryColor\\n  secondaryColor\\n  __typename\\n}\\n\\nfragment TierBrandingType on TierBrandingType {\\n  name\\n  code\\n  value\\n  primaryColor\\n  secondaryColor\\n  __typename\\n}\\n\\nfragment AppType on AppType {\\n  name\\n  slug\\n  __typename\\n}\\n\\nfragment Organization on OrganizationType {\\n  id\\n  name\\n  logo\\n  slug\\n  modified\\n  offsiteApi\\n  onsiteApi\\n  phone\\n  website\\n  vaccinationTheme\\n  theme {\\n    ...OrganizationTheme\\n    __typename\\n  }\\n  tiers {\\n    ...TierBrandingType\\n    __typename\\n  }\\n  apps {\\n    ...AppType\\n    __typename\\n  }\\n  vendor {\\n    ...VendorType\\n    __typename\\n  }\\n  __typename\\n}\\n\\nfragment User on UserType {\\n  id\\n  firstName\\n  lastName\\n  email\\n  phone\\n  birthday\\n  address\\n  username\\n  isActive\\n  isAdmin\\n  isOffsite\\n  lastLogin\\n  dateJoined\\n  organization {\\n    ...Organization\\n    __typename\\n  }\\n  __typename\\n}\\n\\nmutation AuthToken($username: String!, $password: String!) {\\n  tokenAuth(username: $username, password: $password) {\\n    token\\n    user {\\n      ...User\\n      __typename\\n    }\\n    __typename\\n  }\\n}\\n\"}");

            Request request = new Request.Builder()
                    .url("https://ops.e4solutions.io/graphql")
                    .method("POST", body)
                    .build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    final String yourResponse = response.body().string();

                    LoginActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pd.cancel();
                            pd.dismiss();
                            try {
                                Log.d("Response", yourResponse);
                                Gson gson1 = new Gson();
                                loginResponse = gson1.fromJson(yourResponse, Login.class);
                                if(loginResponse.data.tokenAuth.user.isMFA.equals("true") || loginResponse.data.tokenAuth.user.isMFA.equals("True")){
                                    loginView.setVisibility(View.GONE);
                                    otpView.setVisibility(View.VISIBLE);
                                }else {
                                    confirmLogin(loginResponse);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(LoginActivity.this, "Invalid Login Credentials", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
            });
        }catch (Exception e){
            pd.cancel();
            pd.dismiss();
            e.printStackTrace();
            Toast.makeText(LoginActivity.this, "Invalid Login Credentials", Toast.LENGTH_SHORT).show();
        }
    }

    public void confirmLogin(Login loginResponse){
        if (loginResponse.errors != null && loginResponse.errors.size() > 0) {
            Toast.makeText(LoginActivity.this, loginResponse.errors.get(0).message, Toast.LENGTH_SHORT).show();
        } else {
            SessionUtil.setSession(LoginActivity.this, loginResponse);
            Intent openstartingpoint = new Intent(LoginActivity.this, SplashActivity.class);
            startActivity(openstartingpoint);
            finishAffinity();
        }
    }
}
