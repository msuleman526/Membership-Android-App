package com.appsnipp.e4solutions.Visitors;

import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.appsnipp.e4solutions.Models.Login;
import com.appsnipp.e4solutions.Models.Responses.MessageResponse;
import com.appsnipp.e4solutions.Models.VisitorProfile;
import com.appsnipp.e4solutions.R;
import com.appsnipp.e4solutions.Utils.Constants;
import com.appsnipp.e4solutions.Utils.SessionUtil;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class VisitorProfileActivity extends AppCompatActivity {

    View tab1View;
    LinearLayout tab1;
    CardView tab1Card;

    TextView fullNameTxt, addressTxt, emailTxt, phoneTxt;
    TextView genderTxt, visitorIDTxt, visitorTypeTxt, contractorTxt, staffTxt, lastVisitTxt, joinDateTxt, lastUpdatedTxt, distanceTxt, bannedTxt, lastTempTxt;
    ImageView profileImage;
    Switch banSwitch, contSwitch, notiSwitch;

    Login loginData;
    VisitorProfile profileResponse = null;

    ArrayList<String> templateMessages = new ArrayList<>();
    CheckBox customMessageCheckbox;
    Spinner messageSpinner;
    Button sendMessageBtn;
    EditText customMessageEdt;
    CardView msgCardView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //for changing status bar icon colors
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M){
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        setContentView(R.layout.activity_visitor_profile);
        tab1View = findViewById(R.id.tab1View);
        tab1 = findViewById(R.id.tab1);

        loginData = SessionUtil.getLoginData(VisitorProfileActivity.this);

        fullNameTxt = findViewById(R.id.fullNameTxt);
        profileImage = findViewById(R.id.profileImage);
        addressTxt = findViewById(R.id.addressTxt);
        emailTxt = findViewById(R.id.emailTxt);
        phoneTxt = findViewById(R.id.phoneTxt);
        banSwitch = findViewById(R.id.banSwitch);
        contSwitch = findViewById(R.id.contractorSwitch);
        notiSwitch = findViewById(R.id.notifySwift);

        genderTxt = findViewById(R.id.genderTxt);
        visitorIDTxt = findViewById(R.id.visitorIDTxt);
        visitorTypeTxt = findViewById(R.id.visitorTypeTxt);
        contractorTxt = findViewById(R.id.contractorTxt);
        staffTxt = findViewById(R.id.staffTxt);
        lastVisitTxt = findViewById(R.id.lastVisitTxt);
        joinDateTxt = findViewById(R.id.joinDateTxt);
        lastUpdatedTxt = findViewById(R.id.lastUpdatedTxt);
        distanceTxt = findViewById(R.id.distanceTxt);
        bannedTxt = findViewById(R.id.bannedTxt);
        lastTempTxt = findViewById(R.id.lastTmpTxt);

        tab1Card = findViewById(R.id.tab1Card);

        setUpMessageView();
        findViewById(R.id.sendMessageButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                msgCardView.setVisibility(View.VISIBLE);
                customMessageEdt.setText("");
                customMessageEdt.setVisibility(View.GONE);
                messageSpinner.setSelection(0);
                customMessageCheckbox.setChecked(false);
            }
        });

        getProfile(getIntent().getStringExtra("playerId"));

    }

    void setUpMessageView(){
        msgCardView = findViewById(R.id.msgCard);
        messageSpinner = findViewById(R.id.messageDropDown);
        customMessageCheckbox = findViewById(R.id.customMessageCheckBox);
        customMessageEdt = findViewById(R.id.msgEdit);
        sendMessageBtn = findViewById(R.id.sendSMSButton);

        msgCardView.setVisibility(View.GONE);
        customMessageCheckbox.setChecked(false);
        messageSpinner.setEnabled(true);
        customMessageEdt.setVisibility(View.GONE);

        templateMessages.add("Enjoy a meal on us at Harry's Cafe today.");
        templateMessages.add("Congratulations a 500 point Bonus has been  applied to your profile.");
        templateMessages.add("Take a break! Have a Coffee on us at Harry's Cafe.");

        ArrayAdapter adapter = new ArrayAdapter<>(VisitorProfileActivity.this,
                android.R.layout.simple_spinner_item, templateMessages);
        adapter.setDropDownViewResource(android.R.layout
                .simple_spinner_dropdown_item);
        messageSpinner.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        findViewById(R.id.cancelSMSBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                msgCardView.setVisibility(View.GONE);
            }
        });

        sendMessageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = "";
                if(customMessageCheckbox.isChecked()){
                    message = customMessageEdt.getText().toString();
                }else{
                    message = templateMessages.get(messageSpinner.getSelectedItemPosition());
                }

                if(message.equals("")){
                    Toast.makeText(VisitorProfileActivity.this, "Please write a message or choose from template", Toast.LENGTH_SHORT).show();
                }else{
                    if(profileResponse.data != null) {
                        if(profileResponse.data.visitor.primaryphone != null && !profileResponse.data.visitor.primaryphone.equals("")) {
                            sendMessage(message);
                        }
                        else{
                            Toast.makeText(VisitorProfileActivity.this, "Phone Number not found", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(VisitorProfileActivity.this, "No Profile or Phone Number not found", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        customMessageCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    messageSpinner.setEnabled(false);
                    customMessageEdt.setVisibility(View.VISIBLE);
                }else{
                    messageSpinner.setEnabled(true);
                    customMessageEdt.setVisibility(View.GONE);
                }
            }
        });

    }

    private void sendMessage(String message) {

        ProgressDialog pd = new ProgressDialog(VisitorProfileActivity.this);
        pd.setCancelable(false);
        pd.setMessage("Sending Message...");
        pd.show();
        try {
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.connectTimeout(600, TimeUnit.SECONDS);
            builder.readTimeout(600, TimeUnit.SECONDS);
            builder.writeTimeout(600, TimeUnit.SECONDS);
            OkHttpClient client = builder.build();

            MediaType mediaType = MediaType.parse("application/json");
            String name = profileResponse.data.visitor.firstname + " " + profileResponse.data.visitor.surname;
            String phoneNo = profileResponse.data.visitor.primaryphone;
            if(phoneNo.charAt(0) != '+'){
                if(phoneNo.charAt(0) == '0'){
                    phoneNo = phoneNo.replaceFirst("0", "+61");
                }
            }
            phoneNo = phoneNo.replace(" ", "");
            RequestBody body = RequestBody.create(mediaType, "{\r\n    \"operationName\": \"postMessage\",\r\n    \"variables\": {\r\n        \"input\": {\r\n            \"Phone_number\": \""+phoneNo+"\",\r\n            \"Name\": \""+name+"\",\r\n            \"message\": \""+message+"\"\r\n        }\r\n    },\r\n    \"query\": \"mutation postMessage($input: postBroadcastInput) {postMessage(input: $input) {ResponseMetadata}} \\n\"\r\n}");
            Request request = new Request.Builder()
                    .url(Constants.MESSAGE_API_LINK)
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

                    VisitorProfileActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pd.cancel();
                            pd.dismiss();
                            Log.d("RESPONSE", yourResponse);
                            try {
                                Gson gson1 = new Gson();
                                MessageResponse messageResponse = gson1.fromJson(yourResponse, MessageResponse.class);
                                if(messageResponse.data.postMessage.ResponseMetadata.equals("success")){
                                    Toast.makeText(VisitorProfileActivity.this, "Message Sent Successfully", Toast.LENGTH_SHORT).show();
                                    msgCardView.setVisibility(View.GONE);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(VisitorProfileActivity.this, "No Profile Data", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }

            });
        }catch (Exception e){
            pd.cancel();
            pd.dismiss();
            e.printStackTrace();
            Toast.makeText(VisitorProfileActivity.this, "No Profile Data", Toast.LENGTH_SHORT).show();
        }
    }

    void getProfile(String playerId) {
        if(playerId != null) {
            //playerId = "\""+playerId+"\"";
        }
        ProgressDialog pd = new ProgressDialog(VisitorProfileActivity.this);
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
            RequestBody body = RequestBody.create(mediaType, "{\"operationName\":\"Visitor\",\"variables\":{\"id\":\""+playerId+"\"},\"query\":\"fragment Visitor on Visitor {\\n  id\\n  uuid\\n  visitorid\\n  surname\\n  firstname\\n  primaryphone\\n  is_staff\\n  is_contractor\\n  is_notify_on_visit\\n  banned\\n  ban_reason\\n  suburb\\n  state\\n  idimage_id\\n  face_uuid\\n  vaccination\\n  company_id\\n  postcode\\n  address_1\\n  address_2\\n  created_at\\n  updated_at\\n  distance\\n  __typename\\n}\\n\\nfragment Company on Company {\\n  id\\n  name\\n  email\\n  address\\n  contactphone\\n  contactperson\\n  __typename\\n}\\n\\nfragment VisitorLog on VisitorLog {\\n  id\\n  maskflag\\n  detectid\\n  deviceid\\n  surname\\n  firstname\\n  primaryphone\\n  vaccination\\n  profileid\\n  kiosk_group_id\\n  idtype\\n  visitorid\\n  visittype\\n  is_processed\\n  temperature\\n  contractor\\n  guestmember\\n  visitdatetime\\n  leavedatetime\\n  error_code\\n  failure_reason\\n  created_at\\n  is_checkedintosnsw\\n  __typename\\n}\\n\\nquery Visitor($id: ID!) {\\n  visitor(id: $id) {\\n    ...Visitor\\n    company {\\n      ...Company\\n      __typename\\n    }\\n    visitorLog {\\n      ...VisitorLog\\n      __typename\\n    }\\n    __typename\\n  }\\n}\\n\"}");
            Request request = new Request.Builder()
                    .url(SessionUtil.getUrl(VisitorProfileActivity.this)+"/graphql")
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

                    VisitorProfileActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pd.cancel();
                            pd.dismiss();
                            Log.d("RESPONSE", yourResponse);
                            try {
                                Gson gson1 = new Gson();
                                profileResponse = gson1.fromJson(yourResponse, VisitorProfile.class);
                                setUpProfile(profileResponse);
                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(VisitorProfileActivity.this, "No Profile Data", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }

            });
        }catch (Exception e){
            pd.cancel();
            pd.dismiss();
            e.printStackTrace();
            Toast.makeText(VisitorProfileActivity.this, "No Profile Data", Toast.LENGTH_SHORT).show();
        }
    }

    private void setUpProfile(VisitorProfile profile) {
        VisitorProfile.Data.Visitor member = profile.data.visitor;
        fullNameTxt.setText(member.firstname + " " + member.surname);
        String addressLine2 = "";
        if(member.address_2 != null){
            addressLine2 = member.address_2;
        }
        String addres = member.address_1 +", "+member.suburb+", "+member.state+", "+addressLine2+", "+member.postcode;
        addressTxt.setText(addres);
        phoneTxt.setText(member.primaryphone);
        emailTxt.setText("-");

        Picasso.get().load(SessionUtil.getUrl(VisitorProfileActivity.this)+"/media?type=m&id="+member.id+"&field=undefined").placeholder(getResources().getDrawable(R.drawable.profile)).into(profileImage);
        try {
            if (member.banned == false) {
                banSwitch.setChecked(false);
            } else {
                banSwitch.setChecked(true);
            }
        }catch (Exception e){}

        try{contSwitch.setChecked(member.is_contractor); }catch (Exception e){}
        try{notiSwitch.setChecked(member.is_notify_on_visit); }catch (Exception e){}

        String staff = "No";

        try{
            if(member.is_staff){
                staff = "Yes";
            }
        }catch (Exception e){}

        String contt = "No";
        try{
            if(member.is_contractor){
                contt = "Yes";
            }
        }catch (Exception e){}

        try {
            genderTxt.setText("-");
            visitorIDTxt.setText(member.visitorid);
            try {
                visitorTypeTxt.setText(member.__typename);
            }catch (Exception e){
                visitorTypeTxt.setText("-");
            }
            lastVisitTxt.setText(getModifiedDate(member.visitorLog.visitdatetime));
            joinDateTxt.setText(getModifiedDate(member.created_at));
            lastUpdatedTxt.setText(getModifiedDate(member.updated_at));
            try{
                String distance = "0KM";
                if(member.distance != null){
                    distance = member.distance + "KM";
                }
                distanceTxt.setText(distance);
            }catch (Exception e){
                distanceTxt.setText("0KM");
            }
            try{
                staffTxt.setText(staff);
            }catch (Exception e){
                staffTxt.setText("-");
            }

            try{
                contractorTxt.setText(contt);
            }catch (Exception e){
                contractorTxt.setText("-");
            }

            try{
                if(member.banned){
                    bannedTxt.setText("Yes");
                }
                bannedTxt.setText("No");
            }catch (Exception e){
                bannedTxt.setText("No");
            }

            lastTempTxt.setText("-");
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public String getModifiedDate (String normalDate) {
        try{
            String pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX";
            DateFormat df = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                df = new SimpleDateFormat(pattern);
                Date myDate = df.parse(normalDate);

                pattern = "dd-MM-yyyy";
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
                String date = simpleDateFormat.format(myDate);
                return date;
            }

        }catch (Exception e) {
            e.printStackTrace();
            return "-";
        }
        return normalDate;
    }

}
