package tv.sportssidekick.sportssidekick.fragment.instance;


import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.UnsupportedEncodingException;

import tv.sportssidekick.sportssidekick.R;
import tv.sportssidekick.sportssidekick.fragment.BaseFragment;

/**
 * Created by Filip on 12/6/2016.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 *
 * A simple {@link BaseFragment} subclass.
 */

public class QuizFragment extends BaseFragment {


    public QuizFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quiz, container, false);
        WebView webView = (WebView) view.findViewById(R.id.web_view);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.setVisibility(View.VISIBLE);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                // page loaded successfully!
            }
        });

        // TODO Fill with proper data when User data is ready
        String userId = "123456789";
        String firstName = "123456789";
        String lastName = "123456789";
        String nick = "123456789";

        try {
            String base64userId = Base64.encodeToString(userId.getBytes("UTF-8"), Base64.DEFAULT);
            String base64firstName = Base64.encodeToString(userId.getBytes("UTF-8"), Base64.DEFAULT);
            String base64lastName = Base64.encodeToString(userId.getBytes("UTF-8"), Base64.DEFAULT);
            String base64nick = Base64.encodeToString(userId.getBytes("UTF-8"), Base64.DEFAULT);

            String url = getResources().getString(R.string.quiz_url);

            StringBuilder urlBuilder = new StringBuilder(url);
            urlBuilder
                    .append("?userid=")
                    .append(base64userId)
                    .append("&fname=")
                    .append(base64firstName)
                    .append("&lname=")
                    .append(base64lastName)
                    .append("&nick=")
                    .append(base64nick);

            webView.loadUrl(urlBuilder.toString());


        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return view;
    }

}
