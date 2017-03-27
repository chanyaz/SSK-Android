package tv.sportssidekick.sportssidekick.fragment.popup;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import org.greenrobot.eventbus.EventBus;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import tv.sportssidekick.sportssidekick.R;
import tv.sportssidekick.sportssidekick.adapter.UserStatsAdapter;
import tv.sportssidekick.sportssidekick.fragment.BaseFragment;
import tv.sportssidekick.sportssidekick.fragment.FragmentEvent;
import tv.sportssidekick.sportssidekick.model.Model;
import tv.sportssidekick.sportssidekick.model.user.UserInfo;
import tv.sportssidekick.sportssidekick.util.Utility;

/**
 * Created by Filip on 1/16/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class YourProfileFragment extends BaseFragment {

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

    @BindView(R.id.logout_button)
    ImageView logoutButton;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.popup_your_profile, container, false);
        ButterKnife.bind(this, view);

        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        statsRecyclerView.setLayoutManager(layoutManager);

        adapter = new UserStatsAdapter();
        statsRecyclerView.setAdapter(adapter);

        setupFragment();
        return view;
    }

    @OnClick(R.id.logout_button)
    public void setLogoutButton() {
        Model.getInstance().logout();
        getActivity().onBackPressed();
    }

    @OnClick(R.id.confirm_button)
    public void confirmOnClick() {
        getActivity().onBackPressed();
    }

    @OnClick(R.id.edit_button)
    public void editOnClick() {
        EventBus.getDefault().post(new FragmentEvent(EditProfileFragment.class));
    }

    @OnClick(R.id.your_wallet_button)
    public void walletOnClick() {
        EventBus.getDefault().post(new FragmentEvent(WalletFragment.class));
    }

    @OnClick(R.id.your_stash_button)
    public void stashOnClick() {
        EventBus.getDefault().post(new FragmentEvent(StashFragment.class));
    }

    @OnClick(R.id.your_profile_button)
    public void profileOnClick() {
        EventBus.getDefault().post(new FragmentEvent(YourProfileFragment.class));
    }

    @OnClick(R.id.chosen_language_value)
    public void languageOnClick() {
        EventBus.getDefault().post(new FragmentEvent(LanguageFragment.class));
    }

    private void setupFragment() {
        UserInfo user = Model.getInstance().getUserInfo();
        if (user != null) {
            Date subscribed = new Date(user.getSubscribedDate().longValue());

            ArrayList<Pair<String, String>> values = new ArrayList<>();
            values.add(new Pair<>("Caps level",String.valueOf(user.getLevel())));
            String daysUsingSSK =  new SimpleDateFormat("d").format(subscribed);
            values.add(new Pair<>("Days using SSK",daysUsingSSK));
            values.add(new Pair<>("Friends",String.valueOf(user.getFriendsCount())));
            values.add(new Pair<>("Following",String.valueOf(user.getFollowingCount())));
            values.add(new Pair<>("Followers",String.valueOf(user.getFollowersCount())));
            values.add(new Pair<>("Wall posts",String.valueOf(user.getWallPosts())));
            values.add(new Pair<>("Videos watched",String.valueOf(user.getVideosWatched())));
            values.add(new Pair<>("Chats",String.valueOf(user.getChats())));
            values.add(new Pair<>("Video chats",String.valueOf(user.getVideoChats())));
            values.add(new Pair<>("Public chats",String.valueOf(user.getPublicChats())));
            values.add(new Pair<>("Matches attended(home)",String.valueOf(user.getMatchesHome())));
            values.add(new Pair<>("Matches attended(away)",String.valueOf(user.getMatchesAway())));
            values.add(new Pair<>("Likes received",String.valueOf(user.getLikes())));
            values.add(new Pair<>("Comments made",String.valueOf(user.getComments())));
            adapter.getValues().addAll(values);


            ImageLoader.getInstance().displayImage(user.getCircularAvatarUrl(), profileImage, Utility.getImageOptionsForUsers());
            profileName.setText(user.getFirstName() + " " + user.getLastName());
            profileEmail.setText(user.getEmail());
            profilePhone.setText(user.getPhone());
            location.setText(user.getLocation().getCity() + ", " + user.getLocation().getCountry());
            DateFormat df = new SimpleDateFormat("dd MMM yyyy");
            subscribedSince.setText(df.format(subscribed));

            progressBarCircle.setProgress((int) (user.getProgress()*progressBarCircle.getMax()));
            progressBarCaps.setProgress((int) (user.getProgress()*progressBarCircle.getMax()));
            profileImageLevel.setText(String.valueOf(user.getLevel()));
            capsValue.setText(String.valueOf(user.getLevel()));
            nextCapsValue.setText(String.valueOf(user.getLevel() + 1));
        }
    }
}
