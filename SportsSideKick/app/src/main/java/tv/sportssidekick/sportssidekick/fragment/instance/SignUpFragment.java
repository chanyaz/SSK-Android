package tv.sportssidekick.sportssidekick.fragment.instance;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import tv.sportssidekick.sportssidekick.R;
import tv.sportssidekick.sportssidekick.activity.LoginActivity;
import tv.sportssidekick.sportssidekick.activity.LoungeActivity;
import tv.sportssidekick.sportssidekick.entity.UserInfo;
import tv.sportssidekick.sportssidekick.fragment.BaseFragment;


/**
 * Created by Djordje Krutil on 6.12.2016.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */
public class SignUpFragment extends BaseFragment {

    private static final String TAG = "Forgot password fragment";
    AlertDialog alertDialog;
    EditText firstName, lastName, userName, email, phone, password;
    Button signUpButton;
    Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = getActivity();
        return inflater.inflate(R.layout.fragment_sign_up, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        alertDialog = new AlertDialog.Builder(getActivity()).create();
        alertDialog.setTitle(getString(R.string.dialog_warning));
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.dialog_ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        firstName = (EditText) view.findViewById(R.id.sign_up_firstname);
        lastName = (EditText) view.findViewById(R.id.sign_up_lastname);
        userName = (EditText) view.findViewById(R.id.sign_up_username);
        email = (EditText) view.findViewById(R.id.sign_up_email);
        phone = (EditText) view.findViewById(R.id.sign_up_phone);
        password = (EditText) view.findViewById(R.id.sign_up_password);

        signUpButton = (Button) view.findViewById(R.id.sign_up_button);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isEmpty(firstName, getString(R.string.sign_up_firstname)))
                    return;
                if (isEmpty(lastName, getString(R.string.sign_up_lastname)))
                    return;
                if (isEmpty(userName, getString(R.string.sign_up_username)))
                    return;
                if (isEmpty(email, getString(R.string.sign_up_email)))
                    return;
                if (isEmpty(phone, getString(R.string.sign_up_phone)))
                    return;
                if (isEmpty(password, getString(R.string.sign_up_passwoed)))
                    return;

                final UserInfo userInfo = new UserInfo(
                        firstName.getText().toString(),
                        lastName.getText().toString(),
                        userName.getText().toString(),
                        phone.getText().toString());

                if (getmAuth() != null) {
                    getmAuth().createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                            .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                    if (user != null) {
                                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                                        if (userInfo!= null)
                                        {
                                            if (database != null)
                                            {
                                                DatabaseReference myRef = database.getReference("usersInfo").child(user.getUid());
                                                if (myRef!=null)
                                                {
                                                    myRef.setValue(userInfo);
                                                    Intent main = new Intent(getActivity(), LoungeActivity.class);
                                                    getActivity().startActivity(main);                                                }
                                            }
                                        }
                                    }
                                    if (!task.isSuccessful()) {
//                                        Toast.makeText(EmailPasswordActivity.this, "Authentication failed.",
//                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });
    }


    private boolean isEmpty(EditText text, String filedName) {
        if ("".compareTo(text.getText().toString()) == 0) {
            if (alertDialog != null) {
                alertDialog.setMessage(getString(R.string.dialog_message) + " " + filedName + "!");
                alertDialog.show();
            }
            return true;
        }
        return false;
    }
}
