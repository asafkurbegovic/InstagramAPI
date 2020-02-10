package com.example.instatest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.instatest.customview.AuthDialog;
import com.example.instatest.interfaces.AuthenticationListener;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import okhttp3.OkHttpClient;

import static com.example.instatest.Constants.INSTAGRAM_SECRET;
import static com.example.instatest.Constants.REDIRECT_URI;

public class MainActivity extends AppCompatActivity implements AuthenticationListener {


    SharedPreferences prefs = null;
    private AuthDialog auth_dialog;
    Button button = null;
    TextView tv_id;
    String token = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button= findViewById(R.id.button);
        tv_id= (TextView) findViewById(R.id.id);
        prefs = getSharedPreferences(Constants.PREFS,MODE_PRIVATE);
        token = prefs.getString("token",null);
        if (token != null){
            button.setText("Logout");
            getUserInfoByToken(token);
        }else
            button.setText("Instagram Login");
    }


    @Override
    public void onCodeReceived(String auth_token) {
        if(auth_token == null)
            return;

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("token",auth_token);
        editor.apply();
        token = auth_token;
        button.setText("Logout");
    }

    private void getUserInfoByToken(String token) {
        new RequestInstagramAPI().execute();
    }




    public class RequestInstagramAPI extends AsyncTask<Void, String, String>{

        @Override
        protected String doInBackground(Void... voids) {
            HttpClient HttpClinet = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet("https://api.instagram.com/oauth/access_token&app_id="+ Constants.INSTAGRMA_ID +"&app_secret="+ Constants.INSTAGRAM_SECRET +"&grant_type=authorization_code&redirect_uri="+ Constants.REDIRECT_URI +"&code=" + token);

            try {
                HttpResponse response = HttpClinet.execute(httpGet);
                HttpEntity entity = response.getEntity();
                String json = EntityUtils.toString(entity);
                return json;
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            if(response != null){
                try {
                    JSONObject json = new JSONObject(response);
                    Log.e("response",json.toString());
                    String id = json.getString("user_id");
                    String acctoken = json.getString("access_token");
                    tv_id.setText(id);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }else
                Log.e("JSON MESSAGE", "O5 SI NESTO ZAJEBAO  ");
        }
    }

    public void afterClickLogin(View view){
        if (token != null){
            SharedPreferences.Editor editor = prefs.edit();
            editor.clear();
            editor.apply();
            button.setText("Login Instagram");
            token = null;
        }else{
            auth_dialog = new AuthDialog(this, this);
            auth_dialog.setCancelable(true);
            auth_dialog.show();
        }
    }

    public void auth(View view){
        auth_dialog = new AuthDialog(this, this);
        auth_dialog.setCancelable(true);
        auth_dialog.show();
        Log.i("jesam","THE BUTTON IS PRESSED");
    }


}
