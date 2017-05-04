package tv.sportssidekick.sportssidekick.fragment.instance;


import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import com.wang.avi.AVLoadingIndicatorView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

import tv.sportssidekick.sportssidekick.R;
import tv.sportssidekick.sportssidekick.fragment.BaseFragment;
import tv.sportssidekick.sportssidekick.model.AlertDialogManager;
import tv.sportssidekick.sportssidekick.model.Model;
import tv.sportssidekick.sportssidekick.model.wall.WallBase;
import tv.sportssidekick.sportssidekick.model.wall.WallModel;
import tv.sportssidekick.sportssidekick.model.wall.WallStoreItem;

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
    Button closeButton;
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
                if (view.getUrl().equals(getResources().getString(R.string.store_url)))
                {
                    Log.d("WEB VIEW", "Home page!");
                    closeButton.setVisibility(View.GONE);
                    shareToWallButton.setVisibility(View.GONE);
                }
                else
                {
                    Log.d("WEB VIEW", "Product page!");
                    closeButton.setVisibility(View.VISIBLE);
                    item = new WallStoreItem();
                    item.setType(WallBase.PostType.wallStoreItem);
                    item.setPoster(Model.getInstance().getUserInfo());

                    if (android.os.Build.VERSION.SDK_INT > 9) {
                        StrictMode.ThreadPolicy policy =
                                new StrictMode.ThreadPolicy.Builder().permitAll().build();
                        StrictMode.setThreadPolicy(policy);
                    }

                    Elements imageDiv;
                    Elements priceDiv;
                    try {
                        doc = Jsoup.connect(url).get();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    imageDiv = doc.getElementsByClass("guideSize");
                    Element imageElement;
                    String absoluteUrl="";
                    if (imageDiv!=null)
                    {
                        imageElement = imageDiv.select("img").first();
                        if (imageElement!=null)
                        {
                            absoluteUrl = imageElement.absUrl("src");
                        }
                    }

                    priceDiv = doc.getElementsByClass("price");
                    Element priceElement;
                    String price="";

                    if (priceDiv!=null)
                    {
                        priceElement = priceDiv.first();
                        if (priceElement!=null)
                        {
                            price = priceElement.text();
                        }
                    }

                    item.setTitle(view.getTitle());
                    item.setSubTitle(price);
                    item.setUrl(url);
                    item.setCoverImageUrl(absoluteUrl);
                    item.setTimestamp((double) System.currentTimeMillis());

                    shareToWallButton.setVisibility(View.VISIBLE);
                }
                Log.d("WEB VIEW", "Loaded successfully!");
            }
        });

        webView.loadUrl(url);

        progressBar = (AVLoadingIndicatorView) view.findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);

        refreshButton = (Button) view.findViewById(R.id.refresh_button);
        backButton = (Button) view.findViewById(R.id.back_button);
        forwardButton = (Button) view.findViewById(R.id.forward_button);
        shareToWallButton = (Button) view.findViewById(R.id.share_to_wall_button);
        closeButton = (Button) view.findViewById(R.id.close_button);

        refreshButton.setOnClickListener(refreshClickListener);
        backButton.setOnClickListener(goBackClickListener);
        forwardButton.setOnClickListener(goForwardClickListener);
        shareToWallButton.setOnClickListener(shareToWallOnClickListener);
        closeButton.setOnClickListener(closeButtonOnClickListener);

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
            if (item !=null )
            {
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

    View.OnClickListener closeButtonOnClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            webView.loadUrl(url);
        }
    };
}
