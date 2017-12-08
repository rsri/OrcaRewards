package com.orachard23.orcarewards.fragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.orachard23.orcarewards.R;
import com.orachard23.orcarewards.RewardsApp;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChangePasswordFragment extends Fragment implements View.OnClickListener {

    public static final String TAG = ChangePasswordFragment.class.getName();

    public ChangePasswordFragment() {
        // Required empty public constructor
    }

    private EditText mPasswordView;
    private EditText mConfirmPasswordView;

    private ProgressDialog mProgressDialog;
    private FirebaseUser mUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_change_pwd, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mUser = RewardsApp.getApp(context).getUser();

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.change_password);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        EditText emailView = view.findViewById(R.id.email);
        mPasswordView = view.findViewById(R.id.password);
        mConfirmPasswordView = view.findViewById(R.id.password_confirm);
        mConfirmPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptPasswordChange();
                    return true;
                }
                return false;
            }
        });
        view.findViewById(R.id.pwd_change_button).setOnClickListener(this);
        emailView.setText(mUser.getEmail());

        mProgressDialog = new ProgressDialog(getContext());
        mProgressDialog.setMessage(getString(R.string.loading));
        mProgressDialog.setCancelable(false);
    }

    private void attemptPasswordChange() {
        mPasswordView.setError(null);
        mConfirmPasswordView.setError(null);

        String password = mPasswordView.getText().toString();
        String confirmPassword = mConfirmPasswordView.getText().toString();
        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!isValidPassword(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        if (!isValidPassword(confirmPassword)) {
            mConfirmPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mConfirmPasswordView;
            cancel = true;
        }

        if (!doPasswordsMatch(password, confirmPassword)) {
            mPasswordView.setError(getString(R.string.passwords_dont_match));
            mConfirmPasswordView.setError(getString(R.string.passwords_dont_match));
            focusView = mPasswordView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            showProgress(true);
            doPasswordChange(password);
        }
    }

    private void showProgress(boolean show) {
        if (show && !mProgressDialog.isShowing()) {
            mProgressDialog.show();
        } else {
            mProgressDialog.dismiss();
        }
    }

    private void doPasswordChange(String password) {
        Task<Void> updatePasswordTask = mUser.updatePassword(password);
        showProgress(true);
        updatePasswordTask.addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                showProgress(false);
                String msg;
                if (task.isSuccessful()) {
                    msg = "Update successful.";
                } else {
                    msg = "Update failed.";
                }
                Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        attemptPasswordChange();
    }

    private boolean isValidPassword(String password) {
        return !TextUtils.isEmpty(password) && password.length() > 6;
    }

    private boolean doPasswordsMatch(String password, String confirmPassword) {
        return password.equals(confirmPassword);
    }
}
