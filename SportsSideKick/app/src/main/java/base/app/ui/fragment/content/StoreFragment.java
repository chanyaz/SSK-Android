package base.app.ui.fragment.content;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.Toast;

import com.wang.avi.AVLoadingIndicatorView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

import base.app.R;
import base.app.data.AlertDialogManager;
import base.app.data.Model;
import base.app.data.wall.StoreOffer;
import base.app.data.wall.WallModel;
import base.app.ui.fragment.base.BaseFragment;
import base.app.util.commons.Utility;

/**
 * Created by Filip on 12/5/2016.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */


public class StoreFragment extends BaseFragment {

    String url;
    WebView webView;

    ImageView backButton;
    ImageView forwardButton;
    ImageView shareToWallButton;
    ImageView homeButton;
    View view;
    AVLoadingIndicatorView progressBar;
    View webContainer;
    boolean withNavigation;
    StoreOffer item;
    Document doc;

    public StoreFragment() {
        // Required empty public constructor
    }

    @Override
    public void onPause() {
        super.onPause();
        webView.setWebViewClient(null);
    }

    @Override
    public void onResume() {
        super.onResume();
        webView.setWebViewClient(webViewClient);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_with_web_view, container, false);

        webContainer = view.findViewById(R.id.navigation_web_container);
        webView = view.findViewById(R.id.web_view);
        progressBar = view.findViewById(R.id.progressBar);
        backButton = view.findViewById(R.id.backButton);
        forwardButton = view.findViewById(R.id.forward_button);
        shareToWallButton = view.findViewById(R.id.share_to_wall_button);
        homeButton =  view.findViewById(R.id.home_button);
        backButton.setOnClickListener(goBackClickListener);
        forwardButton.setOnClickListener(goForwardClickListener);
        shareToWallButton.setOnClickListener(shareToWallOnClickListener);
        homeButton.setOnClickListener(closeButtonOnClickListener);

        setupFragment();

        return view;
    }

    private void setViewEnabled(boolean isEnabled, View view){
        if(isEnabled){
            view.setAlpha(1.0f);
            view.setEnabled(true);
        } else {
            view.setAlpha(0.35f);
            view.setEnabled(false);
        }
    }

    WebViewClient webViewClient = new WebViewClient(){

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            progressBar.setVisibility(View.VISIBLE);
            setViewEnabled(false, shareToWallButton);
        }

        @Override
        public void onPageFinished(WebView webView, String url) {
            webContainer.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);

            setViewEnabled(webView.canGoBack(), backButton);
            setViewEnabled(webView.canGoForward(), forwardButton);

            if (webView.getUrl().equals(getResources().getString(R.string.store_url))) {
                Log.d("WEB VIEW", "Home page!");
                setViewEnabled(false, homeButton);
                setViewEnabled(false, shareToWallButton);
            } else {
                Log.d("WEB VIEW", "Product page!");
                setViewEnabled(true, homeButton);
                if(
                    url.toLowerCase().contains(getResources().getString(R.string.store_url_item).toLowerCase())
                    || url.toLowerCase().contains(getResources().getString(R.string.store_url_item2).toLowerCase())
                    || url.toLowerCase().contains(getResources().getString(R.string.store_url_item3).toLowerCase())
                    || url.toLowerCase().contains(getResources().getString(R.string.store_url_item4).toLowerCase())
                    || url.toLowerCase().contains(getResources().getString(R.string.store_url_item5).toLowerCase())
                    || url.toLowerCase().contains(getResources().getString(R.string.store_url_item6).toLowerCase())
                    || url.toLowerCase().contains(getResources().getString(R.string.store_url_item7).toLowerCase())
                    || url.toLowerCase().contains(getResources().getString(R.string.store_url_item8).toLowerCase())
                    || url.toLowerCase().contains(getResources().getString(R.string.store_url_item9).toLowerCase())
                ){
                    setViewEnabled(true, shareToWallButton);
                } else {
                    setViewEnabled(false, shareToWallButton);
                }
                extractDataForWallItem(webView);
            }
            Log.d("WEB VIEW", "Loaded successfully!");
        }
    };

    private void extractDataForWallItem(WebView webView){
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Elements imageDiv;
        Elements priceDiv;
        try {
            doc = Jsoup.connect(url).get();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (doc != null) {
            imageDiv = doc.getElementsByClass("guideSize");
            Element imageElement;
            String absoluteUrl = "";
            if (imageDiv != null) {
                imageElement = imageDiv.select("img").first();
                if (imageElement != null) {
                    absoluteUrl = imageElement.absUrl("src");
                }
            }

            priceDiv = doc.getElementsByClass("price");
            Element priceElement;
            String price = "";

            if (priceDiv != null) {
                priceElement = priceDiv.first();
                if (priceElement != null) {
                    price = priceElement.text();
                }
            }
            item = new StoreOffer();
            item.setPoster(Model.getInstance().getUserInfo());
            item.setTitle(webView.getTitle());
            item.setUrl(url);
            item.setCoverImageUrl(absoluteUrl);
            item.setTimestamp((double) Utility.getCurrentTime());
        }
    }

    protected void setupFragment() {
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(true);
        url = getResources().getString(R.string.store_url);
        withNavigation = true;
        webView.loadUrl(url);
    }

    View.OnClickListener goBackClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            webView.goBack();
        }
    };
    View.OnClickListener goForwardClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            webView.goForward();
        }
    };

    View.OnClickListener shareToWallOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(!Model.getInstance().isRealUser()) {
                Toast.makeText(getContext(),"You have to be logged in in order to pin to wall",Toast.LENGTH_SHORT).show();
                return;
            }
            if (item != null) {
                AlertDialogManager.getInstance().showAlertDialog(getContext().getResources().getString(R.string.pin_title), getContext().getResources().getString(R.string.pin_confirm),
                        new View.OnClickListener() {// Cancel
                            @Override
                            public void onClick(View v) {
                                getActivity().onBackPressed();
                            }
                        }, new View.OnClickListener() { // Confirm
                            @Override
                            public void onClick(View v) {
                                WallModel.getInstance().createPost(item);
                                getActivity().onBackPressed();
                            }
                        });
            }
        }
    };

    View.OnClickListener closeButtonOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            webView.loadUrl(url);
        }
    };
}
