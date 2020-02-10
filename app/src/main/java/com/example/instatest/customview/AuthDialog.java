package com.example.instatest.customview;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.instatest.Constants;
import com.example.instatest.R;
import com.example.instatest.interfaces.AuthenticationListener;

public class AuthDialog extends Dialog {

    private AuthenticationListener listener;
    private Context context;
    private WebView webView;

    private final String url ="https://api.instagram.com/oauth/authorize?app_id="+Constants.INSTAGRMA_ID+"&redirect_uri="+ Constants.REDIRECT_URI +"/&scope=user_profile,user_media&response_type=code";

    public AuthDialog(@NonNull Context context, AuthenticationListener listener) {
        super(context);

        this.context= context;
        this.listener= listener;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.auth_dialog);
        initializeWebView();
    }

    private void initializeWebView() {
        webView =(WebView) findViewById(R.id.webView);
        webView.loadUrl(url);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);


        webView.setWebViewClient(new WebViewClient(){

            String access_token;
            boolean authcomplete;


            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }


            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                String webUrl = webView.getUrl();
                Log.e("raw material",webUrl);
                if (webUrl.contains("code") && !authcomplete){
                    Uri uri = Uri.parse(webUrl);
                    access_token= uri.getEncodedSchemeSpecificPart();
                    access_token = access_token.substring();
                    Log.e("access_token_before",access_token);
                    access_token = access_token.substring(access_token.indexOf("D")+1);
                    Toast.makeText(context,access_token, Toast.LENGTH_LONG).show();
                    Log.e("access_token_after",access_token);
                    authcomplete=true;
                    listener.onCodeReceived(access_token);
                    dismiss();
                }else if(url.contains("?error")){
                    Log.e("access token", "error in the url");
                    dismiss();
                }
                else{
                    Log.e("access token","some type of error again");
                    Toast.makeText(context,"omasio sam sve", Toast.LENGTH_LONG).show();
                }


            }

        });


    }
}
