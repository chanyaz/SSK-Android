package tv.sportssidekick.sportssidekick.fragment.popup;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;
import tv.sportssidekick.sportssidekick.R;
import tv.sportssidekick.sportssidekick.activity.LoungeActivity;
import tv.sportssidekick.sportssidekick.adapter.AddFriendsAdapter;
import tv.sportssidekick.sportssidekick.adapter.SelectableFriendsAdapter;
import tv.sportssidekick.sportssidekick.fragment.BaseFragment;
import tv.sportssidekick.sportssidekick.fragment.FragmentEvent;
import tv.sportssidekick.sportssidekick.model.Model;
import tv.sportssidekick.sportssidekick.model.friendship.FriendsManager;
import tv.sportssidekick.sportssidekick.model.im.ChatInfo;
import tv.sportssidekick.sportssidekick.model.im.ImsManager;
import tv.sportssidekick.sportssidekick.model.user.AddFriendsEvent;
import tv.sportssidekick.sportssidekick.model.user.UserInfo;
import tv.sportssidekick.sportssidekick.events.GameSparksEvent;
import tv.sportssidekick.sportssidekick.util.Utility;
import tv.sportssidekick.sportssidekick.util.ui.AutofitDecoration;
import tv.sportssidekick.sportssidekick.util.ui.AutofitRecyclerView;
import tv.sportssidekick.sportssidekick.util.ui.LinearItemSpacing;

import static tv.sportssidekick.sportssidekick.Constant.REQUEST_CODE_CHAT_CREATE_IMAGE_CAPTURE;
import static tv.sportssidekick.sportssidekick.Constant.REQUEST_CODE_CHAT_CREATE_IMAGE_PICK;
import static tv.sportssidekick.sportssidekick.fragment.popup.FriendsFragment.GRID_PERCENT_CELL_WIDTH;

/**
 * Created by Filip on 12/26/2016.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */
@RuntimePermissions
public class CreateChatFragment extends BaseFragment {

    @BindView(R.id.friends_recycler_view)
    AutofitRecyclerView friendsRecyclerView;
    @BindView(R.id.confirm_button)
    Button confirmButton;
    @BindView(R.id.chat_name_edit_text)
    EditText chatNameEditText;
    @BindView(R.id.join_a_chat)
    TextView joinChatTextView;
    @BindView(R.id.search_edit_text)
    EditText searchEditText;
    @BindView(R.id.private_chat_switch)
    Switch privateChatSwitch;
    SelectableFriendsAdapter chatFriendsAdapter;
    @BindView(R.id.private_chat_label)
    TextView privateChatTextView;
    @BindView(R.id.chat_image_view)
    ImageView chatImageView;
    @BindView(R.id.chat_friends_in_chat_recycler_view)
    RecyclerView addFriendsRecyclerView;
    @BindView(R.id.chat_friends_in_chat_headline)
    TextView headlineFriendsInChat;
    List<UserInfo> userInfoList;
    AddFriendsAdapter addFriendsAdapter;
    String currentPath;

    public CreateChatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.popup_create_chat, container, false);
        ButterKnife.bind(this, view);

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createNewChat();
            }
        });

        joinChatTextView.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        EventBus.getDefault().post(new FragmentEvent(JoinChatFragment.class));
                    }
                });

        int screenWidth = Utility.getDisplayWidth(getActivity());

        friendsRecyclerView.setCellWidth((int) (screenWidth * GRID_PERCENT_CELL_WIDTH));
        friendsRecyclerView.addItemDecoration(new AutofitDecoration(getActivity()));
        friendsRecyclerView.setHasFixedSize(true);

        Task<List<UserInfo>> task = FriendsManager.getInstance().getFriends(0);
        task.addOnSuccessListener(
                new OnSuccessListener<List<UserInfo>>() {
                    @Override
                    public void onSuccess(List<UserInfo> userInfos) {
                        chatFriendsAdapter = new SelectableFriendsAdapter(getContext());
                        chatFriendsAdapter.add(userInfos);
                        userInfoList = userInfos;
                        friendsRecyclerView.setAdapter(chatFriendsAdapter);
                    }
                });

        searchEditText.addTextChangedListener(textWatcher);

        final Resources res = getResources();
        privateChatSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String switchText;
                if (isChecked) {
                    switchText = res.getString(R.string.this_chat_is_private);
                } else {
                    switchText = res.getString(R.string.this_chat_is_public);
                }
                privateChatTextView.setText(switchText);
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        addFriendsRecyclerView.setLayoutManager(linearLayoutManager);
        addFriendsAdapter = new AddFriendsAdapter(getActivity());
        int space = getResources().getDimensionPixelOffset(R.dimen.margin_15);
        addFriendsRecyclerView.addItemDecoration(new LinearItemSpacing(space, true, true));
        addFriendsRecyclerView.setAdapter(addFriendsAdapter);

        return view;
    }

    @Subscribe
    public void updateAddFriendsAdapter(AddFriendsEvent event) {
        if (event.isRemove()) {
            addFriendsAdapter.remove(event.getUserInfo());
        } else {
            addFriendsAdapter.add(event.getUserInfo());
        }
        int friendCount = addFriendsAdapter.getItemCount();
        String friendsInchat = " "+ getContext().getResources().getString(R.string.chat_frends_in_chat);
        if (friendCount == 0) {
            headlineFriendsInChat.setText(friendsInchat);
        } else if (friendCount == 1) {
            headlineFriendsInChat.setText(getContext().getResources().getString(R.string.chat_frend_in_chat));
        } else {
            String friendsTotal = friendCount + friendsInchat;
            headlineFriendsInChat.setText(friendsTotal);
        }
    }

    @OnClick(R.id.chat_popup_image_button)
    public void pickImage() {
        AlertDialog.Builder chooseDialog = new AlertDialog.Builder(getActivity());
        chooseDialog.setTitle(getContext().getResources().getString(R.string.chat_choose_option));
        chooseDialog.setNegativeButton(getContext().getResources().getString(R.string.chat_choose_from_library), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                CreateChatFragmentPermissionsDispatcher.invokeImageSelectionWithCheck(CreateChatFragment.this);
            }
        });
        chooseDialog.setPositiveButton(getContext().getResources().getString(R.string.chat_use_camera), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                CreateChatFragmentPermissionsDispatcher.invokeCameraCaptureWithCheck(CreateChatFragment.this);
            }
        });
        chooseDialog.show();
    }

    @OnClick(R.id.chat_headline_close_fragment)
    public void closeFragment() {
        ((LoungeActivity) getActivity()).hideSlidePopupFragmentContainer();
    }

    public void performSearch() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (chatFriendsAdapter != null) {
                    final List<UserInfo> filteredModelList = filter(userInfoList, searchEditText.getText().toString());
                    chatFriendsAdapter.replaceAll(filteredModelList);
                    friendsRecyclerView.scrollToPosition(0);
                }
            }
        });
    }


    TextWatcher textWatcher = new TextWatcher() {
        private static final long DELAY = 500; // milliseconds
        private Timer timer = new Timer();

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void afterTextChanged(final Editable s) {
            timer.cancel();
            timer = new Timer();
            timer.schedule(
                    new TimerTask() {
                        @Override
                        public void run() {
                            performSearch();
                        }
                    },
                    DELAY
            );
        }
    };

    private static List<UserInfo> filter(List<UserInfo> models, String query) {
        final String lowerCaseQuery = query.toLowerCase();
        final List<UserInfo> filteredModelList = new ArrayList<>();
        if (models != null) {
            for (UserInfo model : models) {
                final String text = (model.getFirstName() + model.getLastName() + model.getNicName()).toLowerCase();
                if (text.contains(lowerCaseQuery)) {
                    filteredModelList.add(model);
                }
            }
        }
        return filteredModelList;
    }

    public void createNewChat() {
        if (chatFriendsAdapter != null) {
            List<UserInfo> selectedUsers = chatFriendsAdapter.getSelectedValues();
            String chatName = chatNameEditText.getText().toString();
            boolean isPrivate = privateChatSwitch.isChecked();

            ChatInfo newChatInfo = new ChatInfo();
            newChatInfo.setOwner(Model.getInstance().getUserInfo().getUserId());
            newChatInfo.setIsPublic(!isPrivate);
            newChatInfo.setName(chatName);
            ArrayList<String> userIds = new ArrayList<>();
            for (UserInfo info : selectedUsers) {
                userIds.add(info.getUserId());
            }
            userIds.add(newChatInfo.getOwner());
            newChatInfo.setUsersIds(userIds);
            if(uploadedImageUrl!=null){
                newChatInfo.setAvatarUrl(uploadedImageUrl);
            }
            ImsManager.getInstance().createNewChat(newChatInfo);
            getActivity().onBackPressed();
        } else {
            // TODO @Filip - Display error - no users to be selected!
        }
    }

    /**
     * * ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** **
     * CAMERA, IMAGES...
     * * ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** **
     **/


    @NeedsPermission({Manifest.permission.WRITE_EXTERNAL_STORAGE})
    public void invokeImageSelection() {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto, REQUEST_CODE_CHAT_CREATE_IMAGE_PICK);//one can be replaced with any action code
    }

    @NeedsPermission({Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    public void invokeCameraCapture() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = Model.createImageFile(getContext());
                currentPath = photoFile.getAbsolutePath();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getActivity(), "tv.sportssidekick.sportssidekick.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            }
            startActivityForResult(takePictureIntent, REQUEST_CODE_CHAT_CREATE_IMAGE_CAPTURE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        CreateChatFragmentPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_CHAT_CREATE_IMAGE_CAPTURE:
                    Model.getInstance().uploadImageForCreateChat(currentPath);
                    ImageLoader.getInstance().displayImage(currentPath,chatImageView);
                    break;
                case REQUEST_CODE_CHAT_CREATE_IMAGE_PICK:
                    Uri selectedImageURI = intent.getData();
                    String realPath = Model.getRealPathFromURI(getContext(), selectedImageURI);
                    Model.getInstance().uploadImageForCreateChat(realPath);
                    ImageLoader.getInstance().displayImage(realPath,chatImageView);
                    break;
            }
        }
    }

    @OnPermissionDenied({Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void showDeniedForCamera() {
        Toast.makeText(getContext(), R.string.permission_camera_denied, Toast.LENGTH_SHORT).show();
    }

    @OnNeverAskAgain({Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void showNeverAskForCamera() {
        Toast.makeText(getContext(), R.string.permission_camera_neverask, Toast.LENGTH_SHORT).show();
    }

    @OnShowRationale({Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void showRationaleForCamera(final PermissionRequest request) {
        new AlertDialog.Builder(getContext())
                .setMessage(R.string.permission_camera_rationale)
                .setPositiveButton(R.string.button_allow, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        request.proceed();
                    }
                })
                .setNegativeButton(R.string.button_deny, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        request.cancel();
                    }
                })
                .show();
    }

    String uploadedImageUrl;

    @Subscribe
    @SuppressWarnings("Unchecked cast")
    public void onEventDetected(GameSparksEvent event){
        switch (event.getEventType()) {
            case CREATE_CHAT_IMAGE_FILE_UPLOADED:
                if(event.getData()!=null){
                    uploadedImageUrl = (String)event.getData();
                    chatImageView.setVisibility(View.VISIBLE);
                    ImageLoader.getInstance().displayImage(uploadedImageUrl, chatImageView, Utility.imageOptionsImageLoader());
                }
        }
    }
}
