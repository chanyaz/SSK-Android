package base.app.ui.fragment.popup;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.pixplicity.easyprefs.library.Prefs;

import org.greenrobot.eventbus.EventBus;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import base.app.R;
import base.app.data.AlertDialogManager;
import base.app.data.Model;
import base.app.data.user.LoginStateReceiver;
import base.app.data.user.UserInfo;
import base.app.data.wall.WallModel;
import base.app.ui.adapter.profile.UserStatsAdapter;
import base.app.ui.fragment.base.BaseFragment;
import base.app.ui.fragment.base.FragmentEvent;
import base.app.ui.fragment.content.WallFragment;
import base.app.util.commons.Utility;
import base.app.util.events.post.AutoTranslateEvent;
import base.app.util.ui.ImageLoader;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;

import static base.app.ui.adapter.menu.LanguageAdapter.languageIcons;
import static base.app.ui.adapter.menu.LanguageAdapter.languageShortName;
import static base.app.util.commons.Utility.AUTO_TRANSLATE;
import static base.app.util.commons.Utility.CHOSEN_LANGUAGE;
import static base.app.util.commons.Utility.NEWS_NOTIFICATIOINS;
import static base.app.util.commons.Utility.RUMOURS_NOTIFICATIONS;
import static base.app.util.commons.Utility.SOCIAL_NOTIFICATIONS;
import static base.app.util.commons.Utility.WALL_NOTIFICATIONS;
import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by Filip on 1/16/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */
public class ProfileFragment extends BaseFragment implements LoginStateReceiver.LoginStateListener {

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

    @BindView(R.id.autoTranslateToggle)
    CompoundButton autoTranslateToggle;
    @BindView(R.id.wallNotificationsToggle)
    CompoundButton wallNotificationsToggle;
    @BindView(R.id.newsNotificationsToggle)
    CompoundButton newsNotificationsToggle;
    @BindView(R.id.rumoursNotificationsToggle)
    CompoundButton rumoursNotificationsToggle;
    @BindView(R.id.socialNotificationsToggle)
    CompoundButton socialNotificationsToggle;

    UserStatsAdapter adapter;

    private LoginStateReceiver loginStateReceiver;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        ButterKnife.bind(this, view);
        autoTranslateToggle.setChecked(isAutoTranslateEnabled());
        wallNotificationsToggle.setChecked(isWallNotificationsEnabled());
        newsNotificationsToggle.setChecked(isNewsNotificationsEnabled());
        rumoursNotificationsToggle.setChecked(isRumoursNotificationsEnabled());
        socialNotificationsToggle.setChecked(isSocialNotificationsEnabled());
        setClickListeners();

        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        statsRecyclerView.setLayoutManager(layoutManager);

        adapter = new UserStatsAdapter();
        statsRecyclerView.setAdapter(adapter);

        setupFragment();
        loginStateReceiver = new LoginStateReceiver(this);

        showLanguageIcon();

        return view;
    }

    private void showLanguageIcon() {
        String currentLanguage = Prefs.getString(CHOSEN_LANGUAGE, "en");

        int languageIndex = languageShortName.indexOf(currentLanguage);

        Glide.with(this).load(languageIcons.get(languageIndex)).into(languageIcon);
    }

    private void setClickListeners() {
        autoTranslateToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton toggle, boolean isEnabled) {
                saveGlobalAutoTranslate(isEnabled);
                EventBus.getDefault().post(new AutoTranslateEvent(isEnabled));
            }
        });
        wallNotificationsToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton toggle, boolean isMuted) {
                boolean isEnabled = !isMuted;

                saveWallNotificationsEnabled(isEnabled);

                WallModel.getInstance().wallSetMuteValue(isEnabled ? "true" : "false");
            }
        });
        CompoundButton.OnCheckedChangeListener listener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton toggle, boolean isChecked) {
                String type = toggle.getTag().toString();
                if (isChecked) {
                    WallModel.getInstance().muteNotifications(type);
                } else {
                    WallModel.getInstance().unmuteNotifications(type);
                }
                switch (type) {
                    case "News":
                        saveNewsNotificationsEnabled(!isChecked);
                        break;
                    case "Rumours":
                        saveRumoursNotificationsEnabled(!isChecked);
                        break;
                    case "Social":
                        saveSocialNotificationsEnabled(!isChecked);
                        break;
                }
            }
        };
        newsNotificationsToggle.setOnCheckedChangeListener(listener);
        rumoursNotificationsToggle.setOnCheckedChangeListener(listener);
        socialNotificationsToggle.setOnCheckedChangeListener(listener);
    }

    private void saveGlobalAutoTranslate(boolean isEnabled) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        prefs.edit().putBoolean(AUTO_TRANSLATE, isEnabled).apply();
    }

    private void saveWallNotificationsEnabled(boolean isEnabled) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        prefs.edit().putBoolean(WALL_NOTIFICATIONS, isEnabled).apply();
    }

    private void saveNewsNotificationsEnabled(boolean isEnabled) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        prefs.edit().putBoolean(NEWS_NOTIFICATIOINS, isEnabled).apply();
    }

    private void saveRumoursNotificationsEnabled(boolean isEnabled) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        prefs.edit().putBoolean(RUMOURS_NOTIFICATIONS, isEnabled).apply();
    }

    private void saveSocialNotificationsEnabled(boolean isEnabled) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        prefs.edit().putBoolean(SOCIAL_NOTIFICATIONS, isEnabled).apply();
    }

    public static boolean isAutoTranslateEnabled() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        return prefs.getBoolean(AUTO_TRANSLATE, false);
    }

    public static boolean isWallNotificationsEnabled() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        return prefs.getBoolean(WALL_NOTIFICATIONS, true);
    }

    public static boolean isNewsNotificationsEnabled() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        return prefs.getBoolean(NEWS_NOTIFICATIOINS, true);
    }

    public static boolean isRumoursNotificationsEnabled() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        return prefs.getBoolean(RUMOURS_NOTIFICATIONS, true);
    }

    public static boolean isSocialNotificationsEnabled() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        return prefs.getBoolean(SOCIAL_NOTIFICATIONS, true);
    }

    @OnClick(R.id.logout_button)
    public void logoutOnClick() {
        AlertDialogManager.getInstance().showAlertDialog(
                getContext().getResources().getString(R.string.are_you_sure),
                getContext().getResources().getString(R.string.logout_from_app),
                new View.OnClickListener() { // Cancel
                    @Override
                    public void onClick(View v) {
                    }
                }, new View.OnClickListener() { // Confirm
                    @Override
                    public void onClick(View v) {
                        Model.getInstance().logout();
                        getActivity().onBackPressed();
                        EventBus.getDefault().post(new FragmentEvent(WallFragment.class));
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
        // TODO: Refresh displayed profile info on activity result
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
        EventBus.getDefault().post(new FragmentEvent(ProfileFragment.class));
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
                        EventBus.getDefault().post(new FragmentEvent(ProfileFragment.class));
                    }
                }, new View.OnClickListener() { // Confirm
                    @Override
                    public void onClick(View v) {
                        getActivity().onBackPressed();
                    }
                });
    }

    @Optional
    @OnClick(R.id.backButton)
    public void back() {
        getActivity().onBackPressed();
    }

    private void setupFragment() {
        UserInfo user = Model.getInstance().getUserInfo();
        if (user != null && Model.getInstance().isRealUser()) {
            double subscribedAsDouble = user.getSubscribedDate();
            String daysUsingSSK = String.valueOf((int) ((Utility.getCurrentTime() - subscribedAsDouble) / (1000 * 60 * 60 * 24)));
            ArrayList<Pair<String, String>> values = new ArrayList<>();
            values.add(new Pair<>(getContext().getResources().getString(R.string.caps_level), String.valueOf((int) user.getProgress())));
            values.add(new Pair<>(getContext().getResources().getString(R.string.days_using), daysUsingSSK));
            values.add(new Pair<>(getContext().getResources().getString(R.string.friends), String.valueOf(user.getFriendsCount())));
            values.add(new Pair<>(getContext().getResources().getString(R.string.following), String.valueOf(user.getFollowingCount())));
            values.add(new Pair<>(getContext().getResources().getString(R.string.followers), String.valueOf(user.getFollowersCount())));
            values.add(new Pair<>(getContext().getResources().getString(R.string.wall_posts), String.valueOf(user.getWallPosts())));
            values.add(new Pair<>(getContext().getResources().getString(R.string.videos_watched), String.valueOf(user.getVideosWatched())));
            values.add(new Pair<>(getContext().getResources().getString(R.string.chats), String.valueOf(user.getChats())));
            values.add(new Pair<>(getContext().getResources().getString(R.string.video_chats), String.valueOf(user.getVideoChats())));
            values.add(new Pair<>(getContext().getResources().getString(R.string.chats_public), String.valueOf(user.getPublicChats())));
            values.add(new Pair<>(getContext().getResources().getString(R.string.matches_home), String.valueOf(user.getMatchesHome())));
            values.add(new Pair<>(getContext().getResources().getString(R.string.matches_away), String.valueOf(user.getMatchesAway())));
            values.add(new Pair<>(getContext().getResources().getString(R.string.likes_received), String.valueOf(user.getLikes())));
            values.add(new Pair<>(getContext().getResources().getString(R.string.comments_made), String.valueOf(user.getComments())));
            adapter.getValues().addAll(values);

            // TODO: Refresh profile image if updated on server (currently requires app restart)
            ImageLoader.displayRoundImage(user.getCircularAvatarUrl(), profileImage);
            profileName.setText(user.getFirstName() + " " + user.getLastName());
            profileEmail.setText(user.getEmail());
            profilePhone.setText(user.getPhone());
            StringBuilder locationToDisplay = new StringBuilder("");
            if (user.getLocation() != null) {
                if (!TextUtils.isEmpty(user.getLocation().getCity())) {
                    locationToDisplay.append(user.getLocation().getCity());
                    locationToDisplay.append(", ");
                }
                if (!TextUtils.isEmpty(user.getLocation().getCountry())) {
                    locationToDisplay.append(user.getLocation().getCountry());
                }
            }
            location.setText(locationToDisplay.toString());
            DateFormat df = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
            subscribedSince.setText(df.format(subscribedAsDouble));

            progressBarCircle.setProgress((int) (user.getProgress() * progressBarCircle.getMax()));
            progressBarCaps.setProgress((int) (user.getProgress() * progressBarCircle.getMax()));
            profileImageLevel.setText(String.valueOf((int) user.getProgress()));
            capsValue.setText(String.valueOf((int) user.getProgress()));
            nextCapsValue.setText(String.valueOf((int) user.getProgress() + 1));
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
