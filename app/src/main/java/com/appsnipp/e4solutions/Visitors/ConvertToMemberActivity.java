package com.appsnipp.e4solutions.Visitors;

import android.app.ProgressDialog;
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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.appsnipp.e4solutions.Models.Login;
import com.appsnipp.e4solutions.Models.Responses.UpdateVisitorResponse;
import com.appsnipp.e4solutions.Models.VisitorProfile;
import com.appsnipp.e4solutions.R;
import com.appsnipp.e4solutions.SplashActivity;
import com.appsnipp.e4solutions.Steps.ESignatureActivity;
import com.appsnipp.e4solutions.TestPrinterActivity;
import com.appsnipp.e4solutions.Utils.ImageUtils;
import com.appsnipp.e4solutions.Utils.SessionUtil;
import com.appsnipp.e4solutions.Utils.Utils;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.internal.Util;

public class ConvertToMemberActivity extends AppCompatActivity {

    EditText firstNameEdt, surNameEdt, empoloyementTypeTxt, address1Edt, address2Edt, cityEdt, stateEdt, postEdt, dobEdt, visitorIDEdt, phoneEdt;
    Spinner membershipType;
    Login login = null;
    Button convertToMemberBtn;
    CircleImageView profileImage;
    VisitorProfile profileResponse = null;
    CheckBox staffBox, contractorBox;
    TextView messageTxt;
    JSONObject obj = null;

    ImageView profileImage2;
    TextView nameTxt, expiryTxt;
    CardView cardDiv;

    String playerID = "94179";
    String licensePictureBase64 = "";
    String profilePictureBase64 = "";
    String[] types = null;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //for changing status bar icon colors
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        setContentView(R.layout.convert_to_member);

        membershipType = findViewById(R.id.membershipType);
        firstNameEdt = findViewById(R.id.firstEdt);
        surNameEdt = findViewById(R.id.surEdt);
        empoloyementTypeTxt = findViewById(R.id.empTypeEdt);
        address2Edt = findViewById(R.id.address2);
        cityEdt = findViewById(R.id.cityEdt);
        phoneEdt = findViewById(R.id.phoneEdt);
        postEdt = findViewById(R.id.post);
        stateEdt = findViewById(R.id.state);
        dobEdt = findViewById(R.id.dobEdt);
        visitorIDEdt = findViewById(R.id.visitorIdEdt);
        address1Edt = findViewById(R.id.address);
        convertToMemberBtn = findViewById(R.id.convertBtn);
        profileImage = findViewById(R.id.profileImage);
        staffBox = findViewById(R.id.staffCheckBox);
        contractorBox = findViewById(R.id.contractorCheckBox);
        messageTxt = findViewById(R.id.messageTxt);
        profileImage2 = findViewById(R.id.profileImage1);
        nameTxt = findViewById(R.id.nameTxt);
        expiryTxt = findViewById(R.id.expiryTxt);
        cardDiv = findViewById(R.id.cardDiv);
        cardDiv.setVisibility(View.GONE);

        findViewById(R.id.logoutImage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SessionUtil.setSignOut(ConvertToMemberActivity.this);
                startActivity(new Intent(ConvertToMemberActivity.this, SplashActivity.class));
                finishAffinity();
            }
        });

        convertToMemberBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String state = stateEdt.getText().toString();
                String membershipData = membershipType.getSelectedItem().toString();
                String firstName = firstNameEdt.getText().toString();
                String surName = surNameEdt.getText().toString();
                String address1 = address1Edt.getText().toString();
                String suburb = cityEdt.getText().toString();
                String postCode = postEdt.getText().toString();
                String dateOfBirth = dobEdt.getText().toString();
                String phone = phoneEdt.getText().toString();

                if(membershipType.getSelectedItemPosition() == 0 || state.equals("") || firstName.equals("") ||  surName.equals("") ||  address1.equals("") ||  suburb.equals("") ||
                    postCode.equals("") || dateOfBirth.equals("")){
                    new SweetAlertDialog(ConvertToMemberActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Data Required")
                            .setContentText("Please fill the form properly")
                            .show();
                }else{
                    String data[] = membershipData.split("-");
                    String newData[] = data[0].split("/");
                    String membershipType = newData[0].replace(" ", "");
                    String membershipTerm = newData[1].replace(" ", "");
                    convertToMemberClick(state, dateOfBirth, visitorIDEdt.getText().toString(), membershipType, membershipTerm, suburb, firstName, surName, address1, postCode, phone);
                }

            }
        });

        findViewById(R.id.printMainBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //cardDiv.setVisibility(View.VISIBLE);
                Intent in = new Intent(ConvertToMemberActivity.this, TestPrinterActivity.class);
                in.putExtra("name", firstNameEdt.getText().toString() + " " + surNameEdt.getText().toString());
                in.putExtra("photo", profilePictureBase64);
                startActivity(in);
            }
        });

        login = SessionUtil.getLoginData(ConvertToMemberActivity.this);
        if(login.data.tokenAuth.user.organization.theme != null){
            String hexColor = login.data.tokenAuth.user.organization.theme.primaryColor;
            if(hexColor.charAt(0) != '#'){
                hexColor = "#" + hexColor;
            }
            try {
                changeThemeColor(Color.parseColor(hexColor));
                setUpMembershipType(Color.parseColor(hexColor));
            }catch (Exception e){
                e.printStackTrace();
            }
        }else{
            setUpMembershipType(getResources().getColor(R.color.primaryTextColor));
        }
        convertToMemberBtn.setEnabled(true);
        playerID = getIntent().getStringExtra("playerId");
        getProfile(playerID);

    }

    void getProfile(String playerId) {
        ProgressDialog pd = new ProgressDialog(ConvertToMemberActivity.this);
        pd.setCancelable(false);
        pd.setMessage("Getting ToDo List...");
        pd.show();
        try {
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.connectTimeout(600, TimeUnit.SECONDS);
            builder.readTimeout(600, TimeUnit.SECONDS);
            builder.writeTimeout(600, TimeUnit.SECONDS);
            OkHttpClient client = builder.build();

            MediaType mediaType = MediaType.parse("application/json");
            String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            RequestBody body = RequestBody.create(mediaType, "{\"operationName\":\"Visitor\",\"variables\":{\"id\":\""+playerId+"\"},\"query\":\"fragment Visitor on Visitor {\\n  id\\n  uuid\\n  visitorid\\n  surname\\n  firstname\\n  primaryphone\\n  is_staff\\n  is_contractor\\n  is_notify_on_visit\\n  banned\\n  ban_reason\\n  suburb\\n  state\\n  idimage_id\\n  face_uuid\\n  vaccination\\n  company_id\\n  postcode\\n  address_1\\n  address_2\\n  created_at\\n  updated_at\\n dateofbirth\\n  distance\\n  __typename\\n}\\n\\nfragment Company on Company {\\n  id\\n  name\\n  email\\n  address\\n  contactphone\\n  contactperson\\n  __typename\\n}\\n\\nfragment VisitorLog on VisitorLog {\\n  id\\n  maskflag\\n  detectid\\n  deviceid\\n  surname\\n  firstname\\n  primaryphone\\n  vaccination\\n  profileid\\n  kiosk_group_id\\n  idtype\\n  visitorid\\n  visittype\\n  is_processed\\n  temperature\\n  contractor\\n  guestmember\\n  visitdatetime\\n  leavedatetime\\n  error_code\\n  failure_reason\\n  created_at\\n  is_checkedintosnsw\\n  __typename\\n}\\n\\nquery Visitor($id: ID!) {\\n  visitor(id: $id) {\\n    ...Visitor\\n    company {\\n      ...Company\\n      __typename\\n    }\\n    visitorLog {\\n      ...VisitorLog\\n      __typename\\n    }\\n    __typename\\n  }\\n}\\n\"}");
            Request request = new Request.Builder()
                    .url(SessionUtil.getUrl(ConvertToMemberActivity.this)+"/graphql")
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

                    ConvertToMemberActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pd.cancel();
                            pd.dismiss();
                            Log.d("RESPONSE", yourResponse);
                            try {
                                Gson gson1 = new Gson();
                                profileResponse = gson1.fromJson(yourResponse, VisitorProfile.class);
                                licenseCapture("https://api.venues.e4solutions.io/ffrsl/media?type=v&id="+profileResponse.data.visitor.id+"&field=idimage");
                                setUpProfile(profileResponse.data.visitor);
                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(ConvertToMemberActivity.this, "No Profile Data", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }

            });
        }catch (Exception e){
            pd.cancel();
            pd.dismiss();
            e.printStackTrace();
            Toast.makeText(ConvertToMemberActivity.this, "No Profile Data", Toast.LENGTH_SHORT).show();
        }
    }

    void convertToMemberClick(String state, String dob, String visitorID, String membershipType, String membershipTerm, String suburb, String firstName, String surName, String address1, String postcode, String primaryNumbera){
        String phone = null;
        if(primaryNumbera != null && !primaryNumbera.equals("")) {
            phone = "\""+primaryNumbera+"\"";
        }
        ProgressDialog pd = new ProgressDialog(ConvertToMemberActivity.this);
        pd.setCancelable(false);
        pd.setMessage("Creating Member...");
        pd.show();
        try {
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.connectTimeout(600, TimeUnit.SECONDS);
            builder.readTimeout(600, TimeUnit.SECONDS);
            builder.writeTimeout(600, TimeUnit.SECONDS);
            OkHttpClient client = builder.build();

            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, "{\"operationName\":\"CreateMember\",\"variables\":{\"input\":{\"state\":\""+state+"\",\"visitorid\":\""+visitorID+"\",\"dateofbirth\":\""+dob+"\",\"membershipTypes\":\""+membershipType+"\",\"membershipTerms\":\""+membershipTerm+"\",\"suburb\":\""+suburb+"\",\"firstname\":\""+firstName+"\",\"surname\":\""+surName+"\",\"address_1\":\""+address1+"\",\"postcode\":\""+postcode+"\",\"primaryphone\":"+phone+"}},\"query\":\"mutation CreateMember($input: MemberInput!) {\\n  CreateMember(input: $input) {\\n    status\\n    __typename\\n  }\\n}\\n\"}");

            Request request = new Request.Builder()
                    .url(SessionUtil.getUrl(ConvertToMemberActivity.this)+"/graphql")
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
                    Log.d("Response", yourResponse);
                    ConvertToMemberActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pd.cancel();
                            pd.dismiss();
                            try {
                                if(yourResponse.contains("CreateMemberOutput")){
                                    Toast.makeText(ConvertToMemberActivity.this, "Create Member successfully", Toast.LENGTH_SHORT).show();
                                    updatingVisitor(state, dob, visitorIDEdt.getText().toString(), membershipType, membershipTerm, suburb, firstName, surName, address1, postcode, primaryNumbera);
                                }else{
                                    Toast.makeText(ConvertToMemberActivity.this, "Unable to convert to member, Please check information", Toast.LENGTH_SHORT).show();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(ConvertToMemberActivity.this, "Unable to convert to member", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }

            });
        }catch (Exception e){
            pd.cancel();
            pd.dismiss();
            e.printStackTrace();
            Toast.makeText(ConvertToMemberActivity.this, "Unable to convert to member", Toast.LENGTH_SHORT).show();
        }
    }

    void updatingVisitor(String state, String dob, String visitorID, String membershipType, String membershipTerm, String suburb, String firstName, String surName, String address1, String postcode, String primaryNumbera){
        String phone = null;
        if(primaryNumbera != null && !primaryNumbera.equals("")) {
            phone = "\""+primaryNumbera+"\"";
        }
        ProgressDialog pd = new ProgressDialog(ConvertToMemberActivity.this);
        pd.setCancelable(false);
        pd.setMessage("Updating Visitor...");
        pd.show();
        try {
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.connectTimeout(600, TimeUnit.SECONDS);
            builder.readTimeout(600, TimeUnit.SECONDS);
            builder.writeTimeout(600, TimeUnit.SECONDS);
            OkHttpClient client = builder.build();

            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, "{\"operationName\":\"VisitorUpdate\",\"variables\":{\"id\":\""+playerID+"\",\"input\":{\"state\":\""+state+"\",\"visitorid\":\""+visitorID+"\",\"suburb\":\""+suburb+"\",\"firstname\":\""+firstName+"\",\"surname\":\""+surName+"\",\"address_1\":\""+address1+"\",\"postcode\":\""+postcode+"\",\"primaryphone\":"+phone+",\"avatar\":null}},\"query\":\"fragment Visitor on Visitor {\\n  id\\n  uuid\\n  visitorid\\n  surname\\n  firstname\\n  dateofbirth\\n  primaryphone\\n  is_staff\\n  is_contractor\\n  is_notify_on_visit\\n  banned\\n  ban_reason\\n  suburb\\n  state\\n  idimage_id\\n  face_uuid\\n  vaccination\\n  company_id\\n  postcode\\n  address_1\\n  address_2\\n  created_at\\n  updated_at\\n  distance\\n  __typename\\n}\\n\\nfragment Company on Company {\\n  id\\n  name\\n  email\\n  address\\n  contactphone\\n  contactperson\\n  __typename\\n}\\n\\nmutation VisitorUpdate($id: ID!, $input: VisitorInput!) {\\n  visitorUpdate(id: $id, input: $input) {\\n    visitor {\\n      ...Visitor\\n      company {\\n        ...Company\\n        __typename\\n      }\\n      __typename\\n    }\\n    __typename\\n  }\\n}\\n\"}");
            Request request = new Request.Builder()
                    .url(SessionUtil.getUrl(ConvertToMemberActivity.this)+"/graphql")
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
                    Log.d("Response", yourResponse);
                    ConvertToMemberActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pd.cancel();
                            pd.dismiss();
                            try {
                                Gson gson1 = new Gson();
                                UpdateVisitorResponse res = gson1.fromJson(yourResponse, UpdateVisitorResponse.class);
                                setUpProfile(res.data.visitorUpdate.visitor);
                                Toast.makeText(ConvertToMemberActivity.this, "Information updated successfully", Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(ConvertToMemberActivity.this, "Unable to update member", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }

            });
        }catch (Exception e){
            pd.cancel();
            pd.dismiss();
            e.printStackTrace();
            Toast.makeText(ConvertToMemberActivity.this, "Unable to update member", Toast.LENGTH_SHORT).show();
        }
    }

    private void setUpProfile(VisitorProfile.Data.Visitor visitor) {
        messageTxt.setText("");
        firstNameEdt.setText(visitor.firstname);
        surNameEdt.setText(visitor.surname);
        phoneEdt.setText(visitor.primaryphone);
        dobEdt.setText(visitor.dateofbirth);
        address1Edt.setText(visitor.address_1);
        address2Edt.setText(visitor.address_2);
        cityEdt.setText(visitor.suburb);
        stateEdt.setText(visitor.state);
        postEdt.setText(visitor.postcode);
        visitorIDEdt.setText(visitor.visitorid);

        staffBox.setChecked(visitor.is_staff);
        contractorBox.setChecked(visitor.is_contractor);
    }

    private void setUpMembershipType(int color) {
        List<String> membershipTypes = new ArrayList<String>();
        membershipTypes.add("Select Membership Type");

        if(login != null && login.data.tokenAuth.user.organization.membershipType != null && !login.data.tokenAuth.user.organization.membershipType.equals("") && !login.data.tokenAuth.user.organization.membershipType.equals(" ")){
            types = login.data.tokenAuth.user.organization.membershipType.split(",");
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
    }

    void licenseCapture(String url){
        convertToMemberBtn.setEnabled(false);
        messageTxt.setText("Getting visitor license image Please wait...");
        ImageUtils.loadImageAsBase64(url, new ImageUtils.OnImageLoadedListener() {
            @Override
            public void onImageLoaded(String base64EncodedImage) {
                if(base64EncodedImage != null) {
                    messageTxt.setText("Getting License Info Please wait....");
                    licensePictureBase64 = base64EncodedImage;
                    try {
                        OkHttpClient.Builder builder = new OkHttpClient.Builder();
                        builder.connectTimeout(600, TimeUnit.SECONDS);
                        builder.readTimeout(600, TimeUnit.SECONDS);
                        builder.writeTimeout(600, TimeUnit.SECONDS);
                        OkHttpClient client = builder.build();

                        RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                                .addFormDataPart("file_base64", base64EncodedImage)
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
                                ConvertToMemberActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            convertToMemberBtn.setEnabled(true);
                                            obj = new JSONObject(yourResponse);
                                            if (!obj.get("name").equals("") && obj.get("photo") != null && !obj.get("name").equals("null") && !obj.get("photo").equals("null")) {
                                                messageTxt.setText("");
                                                convertToMemberBtn.setEnabled(true);

                                                final byte[] decodedBytes = Base64.decode(obj.get("photo").toString(), Base64.DEFAULT);
                                                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                                                profileImage.setImageBitmap(decodedByte);
                                                profileImage2.setImageBitmap(decodedByte);
                                                nameTxt.setText(Utils.toTitleCase(obj.get("name").toString()));

                                            } else {
                                                convertToMemberBtn.setEnabled(false);
                                                messageTxt.setText("Invalid License Image/Please check license image and try again.");
                                            }
                                        } catch (JSONException e) {
                                            convertToMemberBtn.setEnabled(false);
                                            messageTxt.setText("Invalid License Image/Please check license image and try again");
                                        }
                                    }
                                });

                            }
                        });
                    } catch (Exception e) {
                        convertToMemberBtn.setEnabled(false);
                        messageTxt.setText("Invalid License Image/Please check license image and try again");
                    }
                }else{
                    convertToMemberBtn.setEnabled(false);
                    messageTxt.setText("Invalid License Image/Please check license image and try again..");
                }
            }

            @Override
            public void onError(Exception e) {
                convertToMemberBtn.setEnabled(false);
                messageTxt.setText("Invalid License Image/Please check license image and try again...");
            }
        });
    }

    private boolean checkAllFieldsFilled() {
        Boolean check = true;
        if(firstNameEdt.getText().toString().equals("")){
            check = false;
        }
        return check;
    }


    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    void changeThemeColor(int color){

        Window window = this.getWindow();
        window.setStatusBarColor(color);
        convertToMemberBtn.setBackgroundTintList(ColorStateList.valueOf(color));
        profileImage.setBorderColor(color);
        firstNameEdt.setTextColor(color);
        surNameEdt.setTextColor(color);
        empoloyementTypeTxt.setTextColor(color);
        address2Edt.setTextColor(color);
        cityEdt.setTextColor(color);
        phoneEdt.setTextColor(color);
        stateEdt.setTextColor(color);
        postEdt.setTextColor(color);
        dobEdt.setTextColor(color);
        visitorIDEdt.setTextColor(color);
        address1Edt.setTextColor(color);


    }
}