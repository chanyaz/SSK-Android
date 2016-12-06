package tv.sportssidekick.sportssidekick.fragment.instance;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import com.wang.avi.AVLoadingIndicatorView;

import tv.sportssidekick.sportssidekick.R;
import tv.sportssidekick.sportssidekick.fragment.BaseFragment;

/**
 * Created by Filip on 12/5/2016.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 *
 * A simple {@link BaseFragment} subclass.
 */

public class StoreFragment extends BaseFragment {

    String url;
    WebView webView;

    Button backButton;
    Button forwardButton;
    Button refreshButton;
    Button shareToWallButton;
    View view;
    AVLoadingIndicatorView progressBar;
    View webContainer;
    boolean withNavigation;

    public StoreFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_with_web_view, container, false);

        webContainer = view.findViewById(R.id.navigation_web_container);

        webView = (WebView) view.findViewById(R.id.web_view);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(true);

        setupFragment();

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                webContainer.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                Log.d("WEB VIEW", "Loaded successfully!");
            }
        });

        webView.loadUrl(url);

        progressBar = (AVLoadingIndicatorView) view.findViewById(R.id.login_login_progress_bar);
        progressBar.setVisibility(View.VISIBLE);

        refreshButton = (Button) view.findViewById(R.id.refresh_button);
        backButton = (Button) view.findViewById(R.id.back_button);
        forwardButton = (Button) view.findViewById(R.id.forward_button);
        shareToWallButton = (Button) view.findViewById(R.id.share_to_wall_button);

        refreshButton.setOnClickListener(refreshClickListener);
        backButton.setOnClickListener(goBackClickListener);
        forwardButton.setOnClickListener(goForwardClickListener);
        forwardButton.setOnClickListener(shareToWallOnClickListener);

        return view;
    }

    protected void setupFragment(){
        url = getResources().getString(R.string.store_url);
        withNavigation = true;
    }

    View.OnClickListener refreshClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            webView.loadUrl(url);
        }
    };
    View.OnClickListener goBackClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            webView.goBack();
        }
    };
    View.OnClickListener goForwardClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            webView.goForward();
        }
    };
    View.OnClickListener shareToWallOnClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            // TODO
        }
    };
}
