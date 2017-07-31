package base.app.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.SparseArray;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import base.app.Constant;
import base.app.adapter.ClubTVPlaylistAdapter;
import base.app.fragment.instance.ChatFragment;
import base.app.fragment.instance.ClubRadioFragment;
import base.app.fragment.instance.ClubRadioStationFragment;
import base.app.fragment.instance.ClubTVFragment;
import base.app.fragment.instance.ClubTvPlaylistFragment;
import base.app.fragment.instance.FantasyFragment;
import base.app.fragment.instance.NewsFragment;
import base.app.fragment.instance.NewsItemFragment;
import base.app.fragment.instance.QuizFragment;
import base.app.fragment.instance.RumoursFragment;
import base.app.fragment.instance.StatisticsFragment;
import base.app.fragment.instance.StoreFragment;
import base.app.fragment.instance.VideoChatFragment;
import base.app.fragment.instance.WallFragment;
import base.app.fragment.instance.WallItemFragment;
import base.app.fragment.instance.YoutubePlayerFragment;
import base.app.fragment.popup.AccountCreatingFragment;
import base.app.fragment.popup.AddFriendFragment;
import base.app.fragment.popup.AlertDialogFragment;
import base.app.fragment.popup.CreateChatFragment;
import base.app.fragment.popup.EditChatFragment;
import base.app.fragment.popup.FollowersFragment;
import base.app.fragment.popup.FollowingFragment;
import base.app.fragment.popup.FriendRequestsFragment;
import base.app.fragment.popup.FriendsFragment;
import base.app.fragment.popup.InviteFriendFragment;
import base.app.fragment.popup.JoinChatFragment;
import base.app.fragment.popup.LanguageFragment;
import base.app.fragment.popup.LoginFragment;
import base.app.fragment.popup.MemberInfoFragment;
import base.app.fragment.popup.ModalFragment;
import base.app.fragment.popup.SignUpFragment;
import base.app.fragment.popup.SignUpLoginFragment;
import base.app.fragment.popup.SignUpLoginPopupRightFragment;
import base.app.fragment.popup.SignUpLoginVideoFragment;
import base.app.fragment.popup.StartingNewCallFragment;
import base.app.fragment.popup.StashFragment;
import base.app.fragment.popup.WalletFragment;
import base.app.fragment.popup.YourProfileFragment;
import base.app.fragment.popup.YourStatementFragment;
import base.app.model.friendship.FriendRequest;
import base.app.util.ui.NavigationDrawerItems;


/**
 * Created by Filip on 12/5/2016.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class FragmentOrganizer extends AbstractFragmentOrganizer {

    private static final String TAG = "FRAGMENT ORGANIZER";
    private SparseArray<List<Class>> containersMap;

    private Class initialFragment;

    public FragmentOrganizer(FragmentManager fragmentManager, Class fragment) {
        super(fragmentManager);
        containersMap = new SparseArray<>();
        this.initialFragment = fragment;
    }

    /**
     * Fragment factory method
     */
    @Subscribe
    @Override
    public void onEvent(FragmentEvent event) {
        Bundle arguments = new Bundle();
        arguments.putString(BaseFragment.PRIMARY_ARG_TAG, event.getId());

        if (event.getInitiatorFragment() != null) {
            arguments.putString(BaseFragment.INITIATOR, event.getInitiatorFragment().getName());
        }
        if (event.getStringArrayList() != null) {
            arguments.putStringArrayList(BaseFragment.STRING_ARRAY_ARG_TAG, event.getStringArrayList());
        }
        openFragment(createFragment(event.getType()), arguments, getFragmentContainer(event.getType()));
    }

    @Override
    public boolean handleBackNavigation() {
        Fragment fragment = getOpenFragment();
        if (fragment.getClass().isAnnotationPresent(IgnoreBackHandling.class)) {
            return true;
        }
        if (fragment.getClass().equals(initialFragment)) {
            return false;
        } else {
            fragmentManager.popBackStack();
            return true;
        }

    }

    public boolean handleNavigationFragment() {
        if (getOpenFragment().getClass().equals(initialFragment))
            return false;
        fragmentManager.popBackStack();
        Fragment fragment = getBackFragment();
        for (int i = 0; i < Constant.CLASS_LIST.size(); i++)
            if (fragment.getClass().equals(Constant.CLASS_LIST.get(i))) {
                NavigationDrawerItems.getInstance().setByPosition(i);
                return true;
            }
      //TODO  ACA
//region Use it later
//        if (fragment.getClass().equals(ChatFragment.class)) {
//            return true;
//        } else if (fragment.getClass().equals(ClubRadioFragment.class)) {
//            return true;
//        } else if (fragment.getClass().equals(ClubRadioStationFragment.class)) {
//            return true;
//        } else if (fragment.getClass().equals(ClubTVFragment.class)) {
//            return true;
//        } else if (fragment.getClass().equals(ClubTvPlaylistFragment.class)) {
//            return true;
//        } else if (fragment.getClass().equals(FantasyFragment.class)) {
//            return true;
//        } else if (fragment.getClass().equals(NewsFragment.class)) {
//            return true;
//        } else if (fragment.getClass().equals(NewsItemFragment.class)) {
//            return true;
//        } else if (fragment.getClass().equals(QuizFragment.class)) {
//            return true;
//        } else if (fragment.getClass().equals(RumoursFragment.class)) {
//            return true;
//        } else if (fragment.getClass().equals(StatisticsFragment.class)) {
//            return true;
//        } else if (fragment.getClass().equals(StoreFragment.class)) {
//            return true;
//        } else if (fragment.getClass().equals(VideoChatFragment.class)) {
//            return true;
//        } else if (fragment.getClass().equals(WallFragment.class)) {
//            return true;
//        } else if (fragment.getClass().equals(WallItemFragment.class)) {
//            return true;
//        } else if (fragment.getClass().equals(YoutubePlayerFragment.class)) {
//            return true;
//        }
//
//        //Popup
//        else if (fragment.getClass().equals(AccountCreatingFragment.class)) {
//            return true;
//        } else if (fragment.getClass().equals(AddFriendFragment.class)) {
//            return true;
//        } else if (fragment.getClass().equals(AlertDialogFragment.class)) {
//            return true;
//        } else if (fragment.getClass().equals(CreateChatFragment.class)) {
//            return true;
//        } else if (fragment.getClass().equals(EditChatFragment.class)) {
//            return true;
//        } else if (fragment.getClass().equals(FollowersFragment.class)) {
//            return true;
//        } else if (fragment.getClass().equals(FollowingFragment.class)) {
//            return true;
//        } else if (fragment.getClass().equals(FriendRequestsFragment.class)) {
//            return true;
//        } else if (fragment.getClass().equals(FriendsFragment.class)) {
//            return true;
//        } else if (fragment.getClass().equals(InviteFriendFragment.class)) {
//            return true;
//        } else if (fragment.getClass().equals(InviteFriendFragment.class)) {
//            return true;
//        } else if (fragment.getClass().equals(JoinChatFragment.class)) {
//            return true;
//        } else if (fragment.getClass().equals(LanguageFragment.class)) {
//            return true;
//        } else if (fragment.getClass().equals(LoginFragment.class)) {
//            return true;
//        } else if (fragment.getClass().equals(MemberInfoFragment.class)) {
//            return true;
//        } else if (fragment.getClass().equals(ModalFragment.class)) {
//            return true;
//        } else if (fragment.getClass().equals(SignUpFragment.class)) {
//            return true;
//        } else if (fragment.getClass().equals(SignUpLoginFragment.class)) {
//            return true;
//        } else if (fragment.getClass().equals(SignUpLoginPopupRightFragment.class)) {
//            return true;
//        } else if (fragment.getClass().equals(SignUpLoginVideoFragment.class)) {
//            return true;
//        } else if (fragment.getClass().equals(StartingNewCallFragment.class)) {
//            return true;
//        } else if (fragment.getClass().equals(StashFragment.class)) {
//            return true;
//        } else if (fragment.getClass().equals(WalletFragment.class)) {
//            return true;
//        } else if (fragment.getClass().equals(YourProfileFragment.class)) {
//            return true;
//        } else if (fragment.getClass().equals(YourStatementFragment.class)) {
//            return true;
//        }
//endregion
        return true;

    }


    protected int getFragmentContainer(Class fragment) {
        for (int i = 0; i < containersMap.size(); i++) {
            int key = containersMap.keyAt(i);
            List<Class> fragments = containersMap.get(key);
            for (Class f : fragments) {
                if (f.equals(fragment)) {
                    return key;
                }
            }
        }
        return -1;
    }

    public void setUpContainer(int containerResourceId, ArrayList<Class> containerFragments) {
        setUpContainer(containerResourceId, containerFragments, false);
    }

    public void setUpContainer(int containerResourceId, ArrayList<Class> containerFragments, boolean withoutBackStack) {
        containersMap.put(containerResourceId, containerFragments);
        if (withoutBackStack) {
            containersWithoutBackStack.add(containerResourceId);
        }
    }
}
