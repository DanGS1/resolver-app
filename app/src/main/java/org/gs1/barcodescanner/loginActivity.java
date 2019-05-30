package org.gs1.barcodescanner;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class loginActivity extends AppCompatActivity {
    public TextView res;
    public EditText uname;
    public EditText pass;

    private CheckBox logCheck;

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String TEXT = "text";
    public static final String TEXT2 = "text2";
    public static final String SWITCH1 = "switch1";

    private String text;
    private String text2;
    private boolean switchOnOff;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        logCheck = (CheckBox)findViewById(R.id.logCheck);
        uname = (EditText)findViewById(R.id.username);
        pass = (EditText)findViewById(R.id.password);
        res = (TextView)findViewById(R.id.result);
        Button btn = (Button)findViewById(R.id.login);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                OkHttpClient client = new OkHttpClient();
                MediaType JSON = MediaType.parse("application/json charset=utf-8");
                JSONObject body = new JSONObject();
                try {
                    body.put("command", "get_session");
                    body.put("email", uname.getText().toString());
                    body.put("password", pass.getText().toString());
                } catch (JSONException e) {
                    Log.d("OKHTTP3", "JSON Exception");
                    e.printStackTrace();
                }
                String url = "https://data.gs1.org/api/api.php";
                RequestBody req_body = RequestBody.create(JSON, body.toString());
                Request request = new Request.Builder()
                        .url(url)
                        .post(req_body)
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.isSuccessful()){
                            final String myResponse = response.body().string();


                            loginActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    res.setText(myResponse);
                                }
                            });
                        }
                    }
                });
            }
        });
//////////////////////////////////////////////////////////////////////////////////////////////////
      logCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (logCheck.isChecked())
                    saveData();
                else
                    clearData();
            }
        });

        loadData();
        updateView();


//////////////////////////////////////////////////////////////////////////////////////////////////
    }
//////////////////////////////////////////////////////////////////////////////////////////////////
   private void saveData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(TEXT, uname.getText().toString());
       editor.putString(TEXT2, pass.getText().toString());
        editor.putBoolean(SWITCH1, logCheck.isChecked());

        editor.apply();

        //Toast.makeText(this,"Data saved", Toast.LENGTH_SHORT).show();
    }

    private  void clearData(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(TEXT, "");
        editor.putString(TEXT2, "");
        editor.putBoolean(SWITCH1, false);

        editor.apply();
    }

    public void loadData(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        text = sharedPreferences.getString(TEXT, "");
        text2 = sharedPreferences.getString(TEXT2, "");
        switchOnOff = sharedPreferences.getBoolean(SWITCH1,false);
    }

    public void updateView(){
        uname.setText(text);
        pass.setText(text2);
        logCheck.setChecked(switchOnOff);
    }
//////////////////////////////////////////////////////////////////////////////////////////////////

}
