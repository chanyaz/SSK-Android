package tv.sportssidekick.sportssidekick.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import tv.sportssidekick.sportssidekick.R;
import tv.sportssidekick.sportssidekick.model.im.ChatInfo;
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
    private List<ChatInfo> dataset;
    private Context context;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public View view;
        @Nullable @BindView(R.id.profile_image_1) public ImageView firstImage;
        @Nullable @BindView(R.id.profile_image_2) public ImageView secondImage;
        @Nullable @BindView(R.id.profile_image_3) public ImageView thirdImage;
        @Nullable @BindView(R.id.profile_image_4)  public ImageView fourthImage;
        @Nullable @BindView(R.id.second_container)  public View secondContainer;

        @Nullable @BindView(R.id.caption) public TextView chatCaption;
        @Nullable @BindView(R.id.people_count_value) public TextView userCount;

        public ViewHolder(View v) {
            super(v);
            view = v;
            ButterKnife.bind(this, view);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ChatHeadsAdapter(List<ChatInfo> dataset, Context context) {
        this.context = context;
        this.dataset = dataset;
    }

    @Override
    public int getItemViewType(int position) {
        return (position == dataset.size()) ? VIEW_TYPE_FOOTER : VIEW_TYPE_CELL;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ChatHeadsAdapter.ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_CELL) {
            // create a new view
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_head_item, parent, false);
            return  new ViewHolder(view);
        }
        else {
            //Create view holder for your footer view
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_chat_button, parent, false);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(parent.getContext(), "To be implemented (create chat).", Toast.LENGTH_SHORT).show();
                }
            });
            return new ViewHolder(view);
        }
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        if (position < dataset.size()) { // don't take the last element!
            final ChatInfo info = dataset.get(position);
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

            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    EventBus.getDefault().post(new UIEvent(info.getId()));
                }
            });
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return dataset.size() + 1;
    }
}