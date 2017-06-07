package base.app.fragment.instance;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.Locale;

import base.app.R;
import base.app.activity.PhoneLoungeActivity;
import base.app.fragment.BaseFragment;
import base.app.model.Model;
import base.app.model.user.UserInfo;

/**
 * A simple {@link BaseFragment} subclass.
 */
public class FantasyFragment extends BaseFragment {


    public FantasyFragment() {
        // Required empty public constructor
    }


    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if(getActivity() instanceof PhoneLoungeActivity)
            ((PhoneLoungeActivity) getActivity()).setMarginTop(true);
        View view = inflater.inflate(R.layout.fragment_fantasy, container, false);
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
        String url = getResources().getString(R.string.fantasy_url);
        UserInfo user;
        user = Model.getInstance().getUserInfo();
        String fullname = "";
        String userId = "";
        if(user != null)
        {
            if (user.getFirstName()!=null && user.getFirstName()!=null)
            {
                fullname = user.getFirstName()+" "+user.getLastName();
            }
            userId = user.getUserId();
        }
        Model.getInstance().getUserInfo().getLanguage();
        fullname = fullname.replaceAll(" ", "%20");
        url = url + "d481e9be-7b90-4147-b108-a5aaea127d05?username=" + fullname + "---" + userId +"&language="+ Locale.getDefault().getLanguage().toUpperCase();
        Log.e("Fantasy URL:", url);

        webView.loadUrl(url);
        return view;

    }

}
