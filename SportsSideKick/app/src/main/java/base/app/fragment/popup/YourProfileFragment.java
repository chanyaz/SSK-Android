package base.app.fragment.popup;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import base.app.util.ui.ImageLoader;
import com.pixplicity.easyprefs.library.Prefs;

import org.greenrobot.eventbus.EventBus;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import base.app.R;
import base.app.adapter.profile.UserStatsAdapter;
import base.app.fragment.BaseFragment;
import base.app.fragment.FragmentEvent;
import base.app.model.AlertDialogManager;
import base.app.model.Model;
import base.app.model.user.LoginStateReceiver;
import base.app.model.user.UserInfo;
import base.app.util.Utility;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;

import static base.app.util.Utility.CHOSEN_LANGUAGE;

/**
 * Created by Filip on 1/16/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class YourProfileFragment extends BaseFragment implements LoginStateReceiver.LoginStateListener {

    @BindView(R.id.profile_stats_recycler_view)
    RecyclerView statsRecyclerView;

    @BindView(R.id.chosen_language_value)
    TextView languageValueTextView;

    @BindView(R.id.language_icon)
    ImageView languageIcon;

    @BindView(R.id.your_profile_image)
    ImageView profileImage;

    @BindView(R.id.your_profile_name)
    TextView profileName;

    @BindView(R.id.contact_info_email)
    TextView profileEmail;

    @BindView(R.id.contact_info_phone)
    TextView profilePhone;

    @BindView(R.id.location_value)
    TextView location;

    @BindView(R.id.subscribed_since_value)
    TextView subscribedSince;


    @BindView(R.id.progressBar)
    ProgressBar progressBarCircle;

    @BindView(R.id.caps_progress_bar)
    ProgressBar progressBarCaps;

    @BindView(R.id.profile_image_level)
    TextView profileImageLevel;

    @BindView(R.id.caps_value)
    TextView capsValue;

    @BindView(R.id.next_caps_value)
    TextView nextCapsValue;

    public YourProfileFragment() {
        // Required empty public constructor
    }

    UserStatsAdapter adapter;

    private LoginStateReceiver loginStateReceiver;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.popup_your_profile, container, false);
        ButterKnife.bind(this, view);

        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        statsRecyclerView.setLayoutManager(layoutManager);

        adapter = new UserStatsAdapter();
        statsRecyclerView.setAdapter(adapter);

        setupFragment();
        this.loginStateReceiver = new LoginStateReceiver(this);

        return view;
    }


    @OnClick(R.id.logout_button)
    public void logoutOnClick() {
        AlertDialogManager.getInstance().showAlertDialog(getContext().getResources().getString(R.string.are_you_sure), getContext().getResources().getString(R.string.logout_from_app),
                new View.OnClickListener() {// Cancel
                    @Override
                    public void onClick(View v) {
                        getActivity().onBackPressed();
                        //EventBus.getDefault().post(new FragmentEvent(YourProfileFragment.class));
                    }
                }, new View.OnClickListener() { // Confirm
                    @Override
                    public void onClick(View v) {
                        Model.getInstance().logout();
                        if(Utility.isTablet(getActivity())){
                            getActivity().onBackPressed();
                        }else {
                            getActivity().onBackPressed();
                        }
                    }
                });
    }

    @Optional
    @OnClick(R.id.confirm_button)
    public void confirmOnClick() {
        getActivity().onBackPressed();
    }

    @Optional
    @OnClick(R.id.edit_button)
    public void editOnClick() {
        getActivity().onBackPressed(); // Prevent Your profile fragment not to trigger in invisible state...
        EventBus.getDefault().post(new FragmentEvent(EditProfileFragment.class));
    }

    @Optional
    @OnClick(R.id.your_wallet_button)
    public void walletOnClick() {
        EventBus.getDefault().post(new FragmentEvent(WalletFragment.class));
    }

    @Optional
    @OnClick(R.id.your_stash_button)
    public void stashOnClick() {
        EventBus.getDefault().post(new FragmentEvent(StashFragment.class));
    }

    @Optional
    @OnClick(R.id.your_profile_button)
    public void profileOnClick() {
        EventBus.getDefault().post(new FragmentEvent(YourProfileFragment.class));
    }

    @Optional
    @OnClick(R.id.chosen_language_value)
    public void languageOnClick() {
        EventBus.getDefault().post(new FragmentEvent(LanguageFragment.class));
    }

    @Optional
    @OnClick(R.id.light_theme_button)
    public void resetOnClick() {
        AlertDialogManager.getInstance().showAlertDialog(getContext().getResources().getString(R.string.are_you_sure), getContext().getResources().getString(R.string.reset_app),
                new View.OnClickListener() {// Cancel
                    @Override
                    public void onClick(View v) {
                        getActivity().onBackPressed();
                        EventBus.getDefault().post(new FragmentEvent(YourProfileFragment.class));
                    }
                }, new View.OnClickListener() { // Confirm
                    @Override
                    public void onClick(View v) {
                        getActivity().onBackPressed();
                    }
                });
    }

    @Optional
    @OnClick(R.id.close_dialog_button)
    public void back() {
        getActivity().onBackPressed();
    }

    private void setupLanguageSelection(){
        String selectedLanguage = Prefs.getString(CHOSEN_LANGUAGE,"en");
        switch (selectedLanguage) {
            case "en":
                languageValueTextView.setText(R.string.english);
                languageIcon.setImageResource(R.drawable.english);
                break;
            case "pt":
                languageValueTextView.setText(R.string.portuguese);
                languageIcon.setImageResource(R.drawable.portuguese);
                break;
            case "zh":
                languageValueTextView.setText(R.string.chinese);
                languageIcon.setImageResource(R.drawable.chinese);
                break;
        }
    }

    private void setupFragment() {
        setupLanguageSelection();
        UserInfo user = Model.getInstance().getUserInfo();
        if (user != null && Model.getInstance().isRealUser()) {
            double subscribedAsDouble = user.getSubscribedDate();
            String daysUsingSSK = String.valueOf((int)((Utility.getCurrentTime()-subscribedAsDouble)/(1000*60*60*24)));
            ArrayList<Pair<String, String>> values = new ArrayList<>();
            values.add(new Pair<>(getContext().getResources().getString(R.string.caps_level), String.valueOf((int)user.getProgress())));
            values.add(new Pair<>(getContext().getResources().getString(R.string.days_using), daysUsingSSK));
            values.add(new Pair<>(getContext().getResources().getString(R.string.friends), String.valueOf(user.getFriendsCount())));
            values.add(new Pair<>(getContext().getResources().getString(R.string.following), String.valueOf(user.getFollowingCount())));
            values.add(new Pair<>(getContext().getResources().getString(R.string.followers), String.valueOf(user.getFollowersCount())));
            values.add(new Pair<>(getContext().getResources().getString(R.string.wall_posts), String.valueOf(user.getWallPosts())));
            values.add(new Pair<>(getContext().getResources().getString(R.string.videos_watched), String.valueOf(user.getVideosWatched())));
            values.add(new Pair<>(getContext().getResources().getString(R.string.chats), String.valueOf(user.getChats())));
            values.add(new Pair<>(getContext().getResources().getString(R.string.video_chats), String.valueOf(user.getVideoChats())));
            values.add(new Pair<>(getContext().getResources().getString(R.string.public_chats), String.valueOf(user.getPublicChats())));
            values.add(new Pair<>(getContext().getResources().getString(R.string.matches_attended_home), String.valueOf(user.getMatchesHome())));
            values.add(new Pair<>(getContext().getResources().getString(R.string.matches_attended_away), String.valueOf(user.getMatchesAway())));
            values.add(new Pair<>(getContext().getResources().getString(R.string.likes_received), String.valueOf(user.getLikes())));
            values.add(new Pair<>(getContext().getResources().getString(R.string.comments_made), String.valueOf(user.getComments())));
            adapter.getValues().addAll(values);

            ImageLoader.displayImage(user.getCircularAvatarUrl(), profileImage);
            profileName.setText(user.getFirstName() + " " + user.getLastName());
            profileEmail.setText(user.getEmail());
            profilePhone.setText(user.getPhone());
            StringBuilder locationToDisplay = new StringBuilder("");
            if(user.getLocation()!=null){
                if(!TextUtils.isEmpty(user.getLocation().getCity())){
                    locationToDisplay.append(user.getLocation().getCity());
                    locationToDisplay.append(", ");
                }
                if(!TextUtils.isEmpty(user.getLocation().getCountry())){
                    locationToDisplay.append(user.getLocation().getCountry());
                }
            }
            location.setText(locationToDisplay.toString());
            DateFormat df = new SimpleDateFormat("dd MMM yyyy");
            subscribedSince.setText(df.format(subscribedAsDouble));

            progressBarCircle.setProgress((int) (user.getProgress() * progressBarCircle.getMax()));
            progressBarCaps.setProgress((int) (user.getProgress() * progressBarCircle.getMax()));
            profileImageLevel.setText(String.valueOf((int)user.getProgress()));
            capsValue.setText(String.valueOf((int)user.getProgress()));
            nextCapsValue.setText(String.valueOf((int)user.getProgress() + 1));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(loginStateReceiver);
    }

    @Override
    public void onLogout() {
        if (Model.getInstance().isRealUser()) {
            setupFragment();
        }
    }

    @Override
    public void onLoginAnonymously() {
    }

    @Override
    public void onLogin(UserInfo user) {

    }

    @Override
    public void onLoginError(Error error) {
    }
}
