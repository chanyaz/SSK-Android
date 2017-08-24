package base.app.fragment.popup;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import base.app.BuildConfig;
import base.app.R;
import base.app.activity.LoungeActivity;
import base.app.adapter.AddFriendsAdapter;
import base.app.adapter.SelectableFriendsAdapter;
import base.app.fragment.BaseFragment;
import base.app.fragment.FragmentEvent;
import base.app.model.Model;
import base.app.model.friendship.FriendsManager;
import base.app.model.im.ChatInfo;
import base.app.model.im.ImsManager;
import base.app.model.user.AddFriendsEvent;
import base.app.model.user.UserInfo;
import base.app.util.Utility;
import base.app.util.ui.AutofitDecoration;
import base.app.util.ui.AutofitRecyclerView;
import base.app.util.ui.LinearItemSpacing;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

import static base.app.Constant.REQUEST_CODE_CHAT_EDIT_IMAGE_CAPTURE;
import static base.app.Constant.REQUEST_CODE_CHAT_EDIT_IMAGE_PICK;
import static base.app.fragment.popup.FriendsFragment.GRID_PERCENT_CELL_WIDTH;



/**
 * Created by Filip on 12/26/2016.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

@RuntimePermissions
public class EditChatFragment extends BaseFragment {
    public static final double GRID_PERCENT_CELL_WIDTH_PHONE = 0.24;
    @BindView(R.id.friends_recycler_view)
    AutofitRecyclerView friendsRecyclerView;
    @BindView(R.id.confirm_button)
    Button confirmButton;
    @BindView(R.id.chat_name_edit_text)
    EditText chatNameEditText;
    @BindView(R.id.join_a_chat)
    TextView joinChatTextView;
    @BindView(R.id.edit_caption_label)
    TextView addFriendsInChatLabel;
    @BindView(R.id.search_edit_text)
    EditText searchEditText;
    @BindView(R.id.chat_image_view)
    ImageView chatImageView;

    SelectableFriendsAdapter chatFriendsAdapter;

    @BindView(R.id.members_recycler_view)
    RecyclerView membersRecyclerView;

    @BindView(R.id.friends_in_chat_headline)
    TextView headlineFriendsInChat;

    List<UserInfo> userInfoList;
    AddFriendsAdapter addFriendsAdapter;

    ChatInfo chatInfo;
    String uploadedImageUrl;
    String currentPath;

    public EditChatFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.popup_edit_chat, container, false);
        ButterKnife.bind(this, view);

        String chatId = getPrimaryArgument();
        chatInfo = ImsManager.getInstance().getChatInfoById(chatId);

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitChanges();
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
         if(Utility.isTablet(getActivity()))
        {
            friendsRecyclerView.setCellWidth((int) (screenWidth * GRID_PERCENT_CELL_WIDTH));
        }else {
             friendsRecyclerView.setCellWidth((int) (screenWidth * GRID_PERCENT_CELL_WIDTH_PHONE));
         }

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

                        List<UserInfo> chatMembers = Model.getInstance().getCachedUserInfoById(chatInfo.getUsersIds());
                        chatFriendsAdapter.setSelectedUsers(chatMembers);

                        friendsRecyclerView.setAdapter(chatFriendsAdapter);

                        addFriendsAdapter.add(chatMembers);
                        addFriendsAdapter.notifyDataSetChanged();
                        updateFirendsCountLabel();

                    }
                });

        searchEditText.addTextChangedListener(textWatcher);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        membersRecyclerView.setLayoutManager(linearLayoutManager);
        addFriendsAdapter = new AddFriendsAdapter(getActivity());
        int space = getResources().getDimensionPixelOffset(R.dimen.margin_15);
        membersRecyclerView.addItemDecoration(new LinearItemSpacing(space, true, true));
        membersRecyclerView.setAdapter(addFriendsAdapter);

        chatNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                String value = getString(R.string.unnamed_chat);
                if(!TextUtils.isEmpty(s)){
                    value = s.toString();
                }
                addFriendsInChatLabel.setText(String.format(getResources().getString(R.string.manage_public_chat_caption), "'" + value +"'"));
            }
        });

        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        chatNameEditText.setText(chatInfo.getChatTitle());
        String captionText = String.format(getResources().getString(R.string.manage_public_chat_caption), "'" + chatInfo.getChatTitle() +"'");
        if(chatInfo.getChatAvatarUrl()!=null){
            ImageLoader.getInstance().displayImage(chatInfo.getChatAvatarUrl(), chatImageView, Utility.imageOptionsImageLoader());
            chatImageView.setVisibility(View.VISIBLE);
        }
        addFriendsInChatLabel.setText(captionText);
    }

    @Subscribe
    public void updateAddFriendsAdapter(AddFriendsEvent event) {
        if (event.isRemove()) {
            addFriendsAdapter.remove(event.getUserInfo());
        } else {
            addFriendsAdapter.add(event.getUserInfo());
        }
        updateFirendsCountLabel();
    }

    private void updateFirendsCountLabel(){
        int friendCount = addFriendsAdapter.getItemCount();
        String friendsInChat = getContext().getResources().getString(R.string.chat_frends_in_chat);
        if (friendCount == 0) {
            headlineFriendsInChat.setText(friendsInChat);
        } else if (friendCount == 1) {
            headlineFriendsInChat.setText(getContext().getResources().getString(R.string.chat_frend_in_chat));
        } else {
            String friendsTotal = friendCount + " " + friendsInChat;
            headlineFriendsInChat.setText(friendsTotal);
        }
    }

    @OnClick(R.id.delete)
    public void deleteChat(){
        chatInfo.deleteChat();
    }

    @OnClick(R.id.popup_image_button)
    public void pickImage() {
        AlertDialog.Builder chooseDialog = new AlertDialog.Builder(getActivity(), R.style.AlertDialog);
        chooseDialog.setTitle(getContext().getResources().getString(R.string.chat_choose_option));
        chooseDialog.setNegativeButton(getContext().getResources().getString(R.string.chat_choose_from_library), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               EditChatFragmentPermissionsDispatcher.invokeImageSelectionWithCheck(EditChatFragment.this);

            }
        });
        chooseDialog.setPositiveButton(getContext().getResources().getString(R.string.chat_use_camera), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditChatFragmentPermissionsDispatcher.invokeCameraCaptureWithCheck(EditChatFragment.this);
            }
        });
        chooseDialog.show();
    }

    @OnClick(R.id.close)
    public void closeFragment() {
        if ((getActivity() instanceof LoungeActivity)) {
            ((LoungeActivity) getActivity()).hideSlidePopupFragmentContainer();
        } else {
            getActivity().onBackPressed();
        }
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
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

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

    private void submitChanges(){
        List<UserInfo> chatMembers = Model.getInstance().getCachedUserInfoById(chatInfo.getUsersIds());
        List<UserInfo> selectedValues = chatFriendsAdapter.getSelectedValues();

        boolean shouldUpdate = false;
         // Check users
        if(chatMembers.size() == selectedValues.size()){
            for(UserInfo user : chatMembers){
                if(!selectedValues.contains(user)){
                    shouldUpdate = true;
                    break;
                }
            }
        } else { // There are different counts of users
            shouldUpdate = true;
        }

        if(shouldUpdate){
            ArrayList<String> newMembersIds = new ArrayList<>();
            for(UserInfo userInfo : selectedValues){
                newMembersIds.add(userInfo.getUserId());
            }
            chatInfo.setUsersIds(newMembersIds);
        }

        // Check chat name
        String newChatName = chatNameEditText.getText().toString();
        if(!TextUtils.isEmpty(newChatName)){ // chat name is changed
            if(!newChatName.equals(chatInfo.getName())){
                chatInfo.setName(newChatName);
                shouldUpdate = true;
            }
        }

        // Check chat image
        if(uploadedImageUrl!=null){
            chatInfo.setAvatarUrl(uploadedImageUrl);
            shouldUpdate = true;
        }

        if(shouldUpdate){
            chatInfo.updateChatInfo();
        } else {
            Toast.makeText(getContext(),getContext().getResources().getString(R.string.chat_no_changes), Toast.LENGTH_SHORT).show();
        }
        closeFragment();
    }

    /**
     * * ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** **
     * CAMERA, IMAGES...
     * * ** ** ** ** ** ** ** ** ** ** ** ** ** ** ** **
     **/


    @NeedsPermission({Manifest.permission.WRITE_EXTERNAL_STORAGE})
    public void invokeImageSelection() {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto, REQUEST_CODE_CHAT_EDIT_IMAGE_PICK);//one can be replaced with any action code
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
                if(Utility.isKitKat()){
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                }
                if(Utility.isLollipopAndUp()){
                    Uri photoURI = FileProvider.getUriForFile(getActivity(), BuildConfig.APPLICATION_ID + ".fileprovider", photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                }
            }
            startActivityForResult(takePictureIntent, REQUEST_CODE_CHAT_EDIT_IMAGE_CAPTURE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
       EditChatFragmentPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_CHAT_EDIT_IMAGE_CAPTURE:
                    uploadImage(currentPath);
                    ImageLoader.getInstance().displayImage(currentPath,chatImageView);
                    break;
                case REQUEST_CODE_CHAT_EDIT_IMAGE_PICK:
                    Uri selectedImageURI = intent.getData();
                    String realPath = Model.getRealPathFromURI(getContext(), selectedImageURI);
                    uploadImage(realPath);
                    ImageLoader.getInstance().displayImage(realPath,chatImageView);
                    break;
            }
        }
    }

    private void uploadImage(String path){
        final TaskCompletionSource<String> source = new TaskCompletionSource<>();
        Model.getInstance().uploadImageForProfile(path,getActivity().getFilesDir(),source);
        source.getTask().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (task.isSuccessful()) {
                    uploadedImageUrl = task.getResult();
                    if(uploadedImageUrl!=null){
                        chatImageView.setVisibility(View.VISIBLE);
                        ImageLoader.getInstance().displayImage(uploadedImageUrl, chatImageView, Utility.imageOptionsImageLoader());
                    }
                } else {
                    // TODO @Filip Handle error!
                }
            }
        });
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
        new AlertDialog.Builder(getContext(), R.style.AlertDialog)
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
}
