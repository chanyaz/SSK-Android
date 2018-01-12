package base.app.ui.adapter.stream;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import base.app.R;
import base.app.ui.fragment.base.FragmentEvent;
import base.app.ui.fragment.popup.FriendFragment;
import base.app.data.user.UserInfo;
import base.app.util.commons.Utility;
import base.app.util.ui.ImageLoader;
import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by Aleksandar Marinkovic on 25-May-17.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class FindOfficialAdapter extends RecyclerView.Adapter<FindOfficialAdapter.ViewHolder> {

    private float halfWidthScreen = 0.5f;
    private Context context;
    private int imageSize;
    List<UserInfo> userInfoList;


    public FindOfficialAdapter(Context context, Class initiatorFragment) {
        this.context = context;

        final int screenWidth = Utility.getDisplayWidth(context);
        imageSize = (int) (screenWidth * halfWidthScreen);


        this.initiatorFragment = initiatorFragment;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.profile_image)
        ImageView profileImage;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    private Class initiatorFragment;

    @Override
    public FindOfficialAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Create a new View
        View v = LayoutInflater.from(context).inflate(R.layout.row_image_offical_account, parent, false);
        v.getLayoutParams().height = imageSize;
        v.getLayoutParams().width = imageSize;
        final ViewHolder viewHolder = new ViewHolder(v);
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentEvent fragmentEvent = new FragmentEvent(FriendFragment.class);
                fragmentEvent.setInitiatorFragment(initiatorFragment);
                int position = viewHolder.getLayoutPosition();
                fragmentEvent.setId(userInfoList.get(position).getUserId());
                EventBus.getDefault().post(fragmentEvent);
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        UserInfo userInfo = userInfoList.get(position);
        String avatarUrl = userInfo.getAvatarUrl();
        if (avatarUrl != null) {
            ImageLoader.displayImage(avatarUrl, holder.profileImage, null);
        }


    }

    @Override
    public int getItemCount() {
        if (userInfoList == null)
            return 0;
        else return userInfoList.size();
    }



    public void setValues(List<UserInfo> values)
    {
      this.userInfoList=values;
      notifyDataSetChanged();
  }


}