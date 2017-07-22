package base.app.fragment.instance;


import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.wang.avi.AVLoadingIndicatorView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

import base.app.R;
import base.app.activity.PhoneLoungeActivity;
import base.app.fragment.BaseFragment;
import base.app.model.AlertDialogManager;
import base.app.model.Model;
import base.app.model.wall.WallBase;
import base.app.model.wall.WallModel;
import base.app.model.wall.WallStoreItem;

/**
 * Created by Filip on 12/5/2016.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 * <p>
 * A simple {@link BaseFragment} subclass.
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
    WallStoreItem item;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_with_web_view, container, false);
        setMarginTop(true);
        webContainer = view.findViewById(R.id.navigation_web_container);

        webView = (WebView) view.findViewById(R.id.web_view);

        progressBar = (AVLoadingIndicatorView) view.findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);

        backButton = (ImageView) view.findViewById(R.id.back_button);
        forwardButton = (ImageView) view.findViewById(R.id.forward_button);
        shareToWallButton = (ImageView) view.findViewById(R.id.share_to_wall_button);
        homeButton = (ImageView) view.findViewById(R.id.home_button);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(true);
        setupFragment();

       // webView.setWebViewClient(webViewClient);

        webView.loadUrl(url);


        backButton.setOnClickListener(goBackClickListener);
        forwardButton.setOnClickListener(goForwardClickListener);
        if (Model.getInstance().

                isRealUser())

        {
            shareToWallButton.setOnClickListener(shareToWallOnClickListener);
        }

        homeButton.setOnClickListener(closeButtonOnClickListener);

        return view;
    }

    WebViewClient webViewClient = new WebViewClient(){
        @Override
        public void onPageFinished(WebView view, String url) {
            webContainer.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            //TODO CRASH 87line - fragment not attached to activity - disable webview on PAUSE

            if (view.getUrl().equals(getResources().getString(R.string.store_url))) {
                Log.d("WEB VIEW", "Home page!");
                homeButton.setVisibility(View.GONE);
                shareToWallButton.setVisibility(View.GONE);
            } else {
                Log.d("WEB VIEW", "Product page!");
                homeButton.setVisibility(View.VISIBLE);
                item = new WallStoreItem();

                item.setType(WallBase.PostType.wallStoreItem);
                item.setPoster(Model.getInstance().getUserInfo());
                if (url.toLowerCase().contains(getResources().getString(R.string.store_url_item).toLowerCase())
                        || url.toLowerCase().contains(getResources().getString(R.string.store_url_item2).toLowerCase())
                        || url.toLowerCase().contains(getResources().getString(R.string.store_url_item3).toLowerCase())) {
                    shareToWallButton.setVisibility(View.VISIBLE);
                } else {
                    shareToWallButton.setVisibility(View.GONE);
                }

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

                    item.setTitle(view.getTitle());
                    item.setSubTitle(price);
                    item.setUrl(url);
                    item.setCoverImageUrl(absoluteUrl);
                    item.setTimestamp((double) System.currentTimeMillis());


                }
            }
            Log.d("WEB VIEW", "Loaded successfully!");
        }
    };

    protected void setupFragment() {
        url = getResources().getString(R.string.store_url);
        withNavigation = true;
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
            if (item != null) {
                AlertDialogManager.getInstance().showAlertDialog(getContext().getResources().getString(R.string.news_post_to_wall_title), getContext().getResources().getString(R.string.news_post_to_wall_message),
                        new View.OnClickListener() {// Cancel
                            @Override
                            public void onClick(View v) {
                                getActivity().onBackPressed();
                            }
                        }, new View.OnClickListener() { // Confirm
                            @Override
                            public void onClick(View v) {
                                WallModel.getInstance().mbPost(item);
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
