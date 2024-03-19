package com.appsnipp.e4solutions.Visitors;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.appsnipp.e4solutions.Adapters.TotalVisitorAdapter;
import com.appsnipp.e4solutions.Models.Login;
import com.appsnipp.e4solutions.Models.Responses.TotalVisitorResponse;
import com.appsnipp.e4solutions.Models.VisitorEdge;
import com.appsnipp.e4solutions.R;
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


public class VisitorListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TotalVisitorAdapter totalVisitorAdapter;
    ArrayList<VisitorEdge> visitorEdgeArrayList = new ArrayList<>();
    ArrayList<VisitorEdge> allVisitorEdgeArrayList = new ArrayList<>();
    boolean hasNext = false;
    String after = null;
    Button loadMoreBtn;

    Login loginData;

    TextView resultsText;
    ImageView addFilter;
    String searchTxt = "";
    Boolean isBanned = false;
    EditText searchEdt;
    Button searchButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //for changing status bar icon colors
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M){
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        setContentView(R.layout.activity_total_visitors);
        this.recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(VisitorListActivity.this));

        resultsText = findViewById(R.id.resultsText);
        addFilter = findViewById(R.id.addFilters);

        addFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openModal();
            }
        });


        loadMoreBtn = findViewById(R.id.loadMoreBtn);

        loadMoreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(hasNext && after != null){
                    getToDoList(after);
                }
            }
        });

        this.totalVisitorAdapter = new TotalVisitorAdapter(this);
        this.recyclerView.setAdapter((RecyclerView.Adapter) this.totalVisitorAdapter);
        this.totalVisitorAdapter.set(visitorEdgeArrayList);
        this.totalVisitorAdapter.notifyDataSetChanged();

        loginData = SessionUtil.getLoginData(VisitorListActivity.this);

        searchEdt = findViewById(R.id.searchEdt);
        searchButton = findViewById(R.id.searchBtn);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String val = searchEdt.getText().toString();
                searchTxt = val;
                visitorEdgeArrayList = new ArrayList<>();
                allVisitorEdgeArrayList = new ArrayList<>();
                getToDoList("");

            }
        });

        if(loginData.data.tokenAuth.user.organization.theme != null){
            String color = loginData.data.tokenAuth.user.organization.theme.primaryColor;
            changeThemeColor(color);
        }

    }

    void openModal(){
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.modal_visitor_search);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        final Spinner bannedSpinner = (Spinner) dialog.findViewById(R.id.bannedSpinner);
        TextView clearButton = dialog.findViewById(R.id.clearTxt);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isBanned = false;
                bannedSpinner.setSelection(0);
                visitorEdgeArrayList = new ArrayList<>();
                allVisitorEdgeArrayList = new ArrayList<>();
                getToDoList("");
                dialog.dismiss();
            }
        });
        bannedSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i == 0){
                    isBanned = false;
                }else{
                    isBanned = true;
                    visitorEdgeArrayList = new ArrayList<>();
                    allVisitorEdgeArrayList = new ArrayList<>();
                    getToDoList("");
                    dialog.dismiss();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        dialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        visitorEdgeArrayList = new ArrayList<>();
        after = null;
        hasNext = false;
        getToDoList(after);
    }

    void getToDoList(String after) {
        if(after != null) {
            after = "\""+after+"\"";
        }
        ProgressDialog pd = new ProgressDialog(VisitorListActivity.this);
        pd.setCancelable(false);
        pd.setMessage("Getting Visitors List...");
        pd.show();
        try {
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.connectTimeout(600, TimeUnit.SECONDS);
            builder.readTimeout(600, TimeUnit.SECONDS);
            builder.writeTimeout(600, TimeUnit.SECONDS);
            OkHttpClient client = builder.build();

            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = null;
            if(searchTxt.equals("")){
                if(!isBanned) {
                    body = RequestBody.create(mediaType, "{\"operationName\":\"Visitors\",\"variables\":{\"first\":10,\"after\":" + after + ",\"filter\":null},\"query\":\"fragment Visitor on Visitor {\\n  id\\n  uuid\\n  visitorid\\n  surname\\n  firstname\\n  primaryphone\\n  is_staff\\n  is_contractor\\n  is_notify_on_visit\\n  banned\\n  ban_reason\\n  suburb\\n  state\\n  idimage_id\\n  face_uuid\\n  vaccination\\n  company_id\\n  postcode\\n  address_1\\n  address_2\\n  created_at\\n  updated_at\\n  distance\\n  __typename\\n}\\n\\nquery Visitors($first: Int, $after: String, $filter: VisitorFilterInput) {\\n  visitors(first: $first, after: $after, filter: $filter) {\\n    edges {\\n      ...Visitor\\n      __typename\\n    }\\n    pageInfo {\\n      hasNextPage\\n      cursor\\n      __typename\\n    }\\n    totalCount\\n    __typename\\n  }\\n}\\n\"}");
                }else{
                    body = RequestBody.create(mediaType, "{\"operationName\":\"Visitors\",\"variables\":{\"first\":10,\"after\":" + after + ",\"filter\":{\"isBanned\": "+isBanned+"}},\"query\":\"fragment Visitor on Visitor {\\n  id\\n  uuid\\n  visitorid\\n  surname\\n  firstname\\n  primaryphone\\n  is_staff\\n  is_contractor\\n  is_notify_on_visit\\n  banned\\n  ban_reason\\n  suburb\\n  state\\n  idimage_id\\n  face_uuid\\n  vaccination\\n  company_id\\n  postcode\\n  address_1\\n  address_2\\n  created_at\\n  updated_at\\n  distance\\n  __typename\\n}\\n\\nquery Visitors($first: Int, $after: String, $filter: VisitorFilterInput) {\\n  visitors(first: $first, after: $after, filter: $filter) {\\n    edges {\\n      ...Visitor\\n      __typename\\n    }\\n    pageInfo {\\n      hasNextPage\\n      cursor\\n      __typename\\n    }\\n    totalCount\\n    __typename\\n  }\\n}\\n\"}");
                }
            }
            else {
                if(!isBanned) {
                    body = RequestBody.create(mediaType, "{\"operationName\":\"Visitors\",\"variables\":{\"first\":10,\"after\":" + after + ",\"filter\":{\"search\": \"" + searchTxt + "\"}},\"query\":\"fragment Visitor on Visitor {\\n  id\\n  uuid\\n  visitorid\\n  surname\\n  firstname\\n  primaryphone\\n  is_staff\\n  is_contractor\\n  is_notify_on_visit\\n  banned\\n  ban_reason\\n  suburb\\n  state\\n  idimage_id\\n  face_uuid\\n  vaccination\\n  company_id\\n  postcode\\n  address_1\\n  address_2\\n  created_at\\n  updated_at\\n  distance\\n  __typename\\n}\\n\\nquery Visitors($first: Int, $after: String, $filter: VisitorFilterInput) {\\n  visitors(first: $first, after: $after, filter: $filter) {\\n    edges {\\n      ...Visitor\\n      __typename\\n    }\\n    pageInfo {\\n      hasNextPage\\n      cursor\\n      __typename\\n    }\\n    totalCount\\n    __typename\\n  }\\n}\\n\"}");
                }else{
                    body = RequestBody.create(mediaType, "{\"operationName\":\"Visitors\",\"variables\":{\"first\":10,\"after\":" + after + ",\"filter\":{\"search\": \"" + searchTxt + "\", \"isBanned\": "+isBanned+"}},\"query\":\"fragment Visitor on Visitor {\\n  id\\n  uuid\\n  visitorid\\n  surname\\n  firstname\\n  primaryphone\\n  is_staff\\n  is_contractor\\n  is_notify_on_visit\\n  banned\\n  ban_reason\\n  suburb\\n  state\\n  idimage_id\\n  face_uuid\\n  vaccination\\n  company_id\\n  postcode\\n  address_1\\n  address_2\\n  created_at\\n  updated_at\\n  distance\\n  __typename\\n}\\n\\nquery Visitors($first: Int, $after: String, $filter: VisitorFilterInput) {\\n  visitors(first: $first, after: $after, filter: $filter) {\\n    edges {\\n      ...Visitor\\n      __typename\\n    }\\n    pageInfo {\\n      hasNextPage\\n      cursor\\n      __typename\\n    }\\n    totalCount\\n    __typename\\n  }\\n}\\n\"}");
                }
            }
            Log.d("URLLLL", loginData.data.tokenAuth.user.organization.offsiteApi);
            Request request = new Request.Builder()
                    .url(loginData.data.tokenAuth.user.organization.offsiteApi+"/graphql")
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

                    VisitorListActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pd.cancel();
                            pd.dismiss();
                            Log.d("RESPONSE", yourResponse);
                            try {
                                Gson gson1 = new Gson();
                                TotalVisitorResponse dashboard = gson1.fromJson(yourResponse, TotalVisitorResponse.class);
                                setUpVisitors(dashboard);
                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(VisitorListActivity.this, "No To Do List", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }

            });
        }catch (Exception e){
            pd.cancel();
            pd.dismiss();
            e.printStackTrace();
            Toast.makeText(VisitorListActivity.this, "No To Do List", Toast.LENGTH_SHORT).show();
        }
    }

    private void setUpVisitors(TotalVisitorResponse visitors) {

        visitorEdgeArrayList.addAll(visitors.data.visitors.edges);
        allVisitorEdgeArrayList.addAll(visitors.data.visitors.edges);
        resultsText.setText(""+visitors.data.visitors.totalCount+" Results");
        if(visitors.data.visitors.pageInfo.hasNextPage){
            hasNext = true;
            after = visitors.data.visitors.pageInfo.cursor;
            loadMoreBtn.setVisibility(View.VISIBLE);
        }else{
            hasNext = false;
            after = null;
            loadMoreBtn.setVisibility(View.GONE);
        }

        this.totalVisitorAdapter.set(visitorEdgeArrayList);
        this.totalVisitorAdapter.notifyDataSetChanged();

        this.totalVisitorAdapter.setOnEToDoItemClickListener(new TotalVisitorAdapter.OnToDoListItemClickListener() {
            @Override
            public void onTodoClick(VisitorEdge p0, int index) {
                Intent intent = new Intent(VisitorListActivity.this, VisitorProfileActivity.class);
                intent.putExtra("playerId", "" + p0.id);
                startActivity(intent);
            }
        });

        this.totalVisitorAdapter.setOnItemClickListener(new TotalVisitorAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(VisitorEdge p0, int index) {
                Intent in = new Intent(VisitorListActivity.this, ConvertToMemberActivity.class);
                in.putExtra("playerId", p0.id);
                startActivity(in);
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
            Window window = this.getWindow();
            window.setStatusBarColor(color);
            loadMoreBtn.setBackgroundTintList(ColorStateList.valueOf(color));
            searchEdt.setTextColor(color);
            TextView filterTxt = findViewById(R.id.filterText);
            filterTxt.setTextColor(color);
            searchButton.setBackgroundTintList(ColorStateList.valueOf(color));
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
