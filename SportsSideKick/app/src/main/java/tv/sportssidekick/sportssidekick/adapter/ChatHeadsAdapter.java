package tv.sportssidekick.sportssidekick.adapter;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import tv.sportssidekick.sportssidekick.R;
import tv.sportssidekick.sportssidekick.fragment.FragmentEvent;
import tv.sportssidekick.sportssidekick.fragment.popup.CreateChatFragment;
import tv.sportssidekick.sportssidekick.model.im.ChatInfo;
import tv.sportssidekick.sportssidekick.model.im.ImModel;
import tv.sportssidekick.sportssidekick.service.UIEvent;
import tv.sportssidekick.sportssidekick.util.Utility;

/**
 * Created by Filip on 12/14/2016.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 *
 * Chat List Adapter for use in chat fragment
 */


public class ChatHeadsAdapter extends RecyclerView.Adapter<ChatHeadsAdapter.ViewHolder> {
    private static final int VIEW_TYPE_FOOTER = 1;
    private static final int VIEW_TYPE_CELL = 2;
    private static final String TAG = "Chat Heads Adapter";

    // Start with first item selected
    private int focusedItem = 0;
    private List<ChatInfo> values;
    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public View view;
        @Nullable @BindView(R.id.profile_image_1) ImageView firstImage;
        @Nullable @BindView(R.id.profile_image_2) ImageView secondImage;
        @Nullable @BindView(R.id.profile_image_3) ImageView thirdImage;
        @Nullable @BindView(R.id.profile_image_4) ImageView fourthImage;
        @Nullable @BindView(R.id.second_container) View secondContainer;
        @Nullable @BindView(R.id.selected) View selectedRingView;

        @Nullable @BindView(R.id.caption) TextView chatCaption;
        @Nullable @BindView(R.id.people_count_value) TextView userCount;

        ViewHolder(View v) {
            super(v);
            view = v;
            ButterKnife.bind(this, view);
        }
    }

    public ChatHeadsAdapter() {
        values = ImModel.getInstance().getUserChatsList();
    }


    @Override
    public int getItemViewType(int position) {
        return (position == values.size()) ? VIEW_TYPE_FOOTER : VIEW_TYPE_CELL;
    }

    @Override
    public ChatHeadsAdapter.ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        ViewHolder viewHolder;
        if (viewType == VIEW_TYPE_CELL) {
            // create a new view
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_head_item, parent, false);
            viewHolder = new ViewHolder(view);
            view.setOnClickListener(v -> {
                notifyItemChanged(focusedItem);  // notify previous item!
                focusedItem = viewHolder.getLayoutPosition();
                notifyItemChanged(focusedItem); // notify new item
                EventBus.getDefault().post(new UIEvent(focusedItem));
            });
            return  viewHolder;
        }
        else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_chat_button, parent, false);
            viewHolder = new ViewHolder(view);
            view.setOnClickListener(
                    view1 -> EventBus.getDefault().post(new FragmentEvent(CreateChatFragment.class)));

            return viewHolder;
        }
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        if (position < values.size()) { // don't take the last element!
            final ChatInfo info = values.get(position);
            int size = -1;
            if(info.getUsersIds()!=null){
                size = info.getUsersIds().size();
                List<String> urls = info.getProfileImagesUrls();
                if(urls.size()==size-1){
                    DisplayImageOptions imageOptions = Utility.imageOptionsImageLoader();
                    switch (size){
                        case 0:
                        case 1:
                            // TODO This should never happen?
                            // since its only me, add avatar image
                            break;
                        case 2:
                            // its 1 on 1 chat, get profile image of other guy
                            ImageLoader.getInstance().displayImage(info.getAvatarUrl(),holder.firstImage,imageOptions);
                            holder.secondImage.setVisibility(View.GONE);
                            holder.secondContainer.setVisibility(View.GONE);
                            break;
                        case 3:
                            // its 3p chat, get profile image of other 2 guys
                            ImageLoader.getInstance().displayImage(urls.get(0),holder.firstImage,imageOptions);
                            ImageLoader.getInstance().displayImage(urls.get(1),holder.thirdImage,imageOptions);
                            holder.secondImage.setVisibility(View.GONE);
                            holder.fourthImage.setVisibility(View.GONE);
                            holder.secondContainer.setVisibility(View.VISIBLE);
                            break;
                        case 4:
                            // its 4p chat, get profile image of other  3 guys
                            ImageLoader.getInstance().displayImage(urls.get(0),holder.firstImage,imageOptions);
                            ImageLoader.getInstance().displayImage(urls.get(1),holder.secondImage,imageOptions);
                            ImageLoader.getInstance().displayImage(urls.get(2),holder.thirdImage,imageOptions);
                            holder.secondImage.setVisibility(View.VISIBLE);
                            holder.thirdImage.setVisibility(View.VISIBLE);
                            holder.secondContainer.setVisibility(View.VISIBLE);
                            break;
                        default:
                        case 5:
                            // its 4+ chat, get profile image of other  4 guys
                            ImageLoader.getInstance().displayImage(urls.get(0),holder.firstImage,imageOptions);
                            ImageLoader.getInstance().displayImage(urls.get(1),holder.secondImage,imageOptions);
                            ImageLoader.getInstance().displayImage(urls.get(2),holder.thirdImage,imageOptions);
                            ImageLoader.getInstance().displayImage(urls.get(3),holder.fourthImage,imageOptions);
                            holder.secondImage.setVisibility(View.VISIBLE);
                            holder.thirdImage.setVisibility(View.VISIBLE);
                            holder.fourthImage.setVisibility(View.VISIBLE);
                            holder.secondContainer.setVisibility(View.VISIBLE);
                            break;
                    }
                } else {
                    Log.d(TAG, "Urls size is " + urls.size() + ", while chat size is: " + size);
                }

            } else {
                Log.d(TAG, "Have no chatUsers yet!");
            }
            holder.userCount.setText(String.valueOf(size));
            holder.chatCaption.setText(info.getChatTitle());

            holder.view.setTag(position);

           if(focusedItem==position){
               holder.selectedRingView.setVisibility(View.VISIBLE);
           } else {
               holder.selectedRingView.setVisibility(View.GONE);
           }
        }
    }

    @Override
    public int getItemCount() {
        return values.size() + 1;
    }
}