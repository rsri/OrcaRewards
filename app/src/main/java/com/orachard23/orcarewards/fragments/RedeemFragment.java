package com.orachard23.orcarewards.fragments;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.orachard23.orcarewards.R;
import com.orachard23.orcarewards.RewardsApp;
import com.orachard23.orcarewards.controller.PointsUpdater;
import com.orachard23.orcarewards.util.MailSender;

import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class RedeemFragment extends Fragment implements View.OnClickListener, PointsUpdater.OnPointUpdatedListener {

    public static final String TAG = RedeemFragment.class.getName();
    private ProgressDialog mProgressDialog;

    public RedeemFragment() {
        // Required empty public constructor
    }

    private EditText mPointsToRedeemView;
    private TextView mRedeemPointsInfoView;

    private FirebaseUser mUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_redeem, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mUser = RewardsApp.getApp(getContext()).getUser();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.redeem);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRedeemPointsInfoView = view.findViewById(R.id.redeem_info_text);
        String val = getString(R.string.redeem_data_info,
                String.valueOf(RewardsApp.getApp(getContext()).getPointsUpdater().getPoints()));
        mRedeemPointsInfoView.setText(Html.fromHtml(val));
        mPointsToRedeemView = view.findViewById(R.id.redeem_points_et);
        mPointsToRedeemView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptRedeem();
                    return true;
                }
                return false;
            }
        });
        view.findViewById(R.id.redeem_button).setOnClickListener(this);

        mProgressDialog = new ProgressDialog(view.getContext());
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage(getString(R.string.loading));
    }

    @Override
    public void onClick(View v) {
        attemptRedeem();
    }

    private void showProgress(boolean show) {
        if (show) {
            mProgressDialog.show();
        } else {
            mProgressDialog.dismiss();
        }
    }

    private void attemptRedeem() {
        String redeemPointsStr = mPointsToRedeemView.getText().toString();
        if (redeemPointsStr.isEmpty()) {
            mPointsToRedeemView.setError(getString(R.string.error_field_required));
            return;
        }
        int redeemPoints = Integer.valueOf(redeemPointsStr);
        if (redeemPoints % 1000 != 0) {
            mPointsToRedeemView.setError("Should be multiples of 1000.");
            // TODO - Uncomment the return.
//            return;
        }
        int pointsWithUser = RewardsApp.getApp(getContext()).getPointsUpdater().getPoints();
        if (redeemPoints > pointsWithUser) {
            mPointsToRedeemView.setError("Not enough points available to redeem this.");
            return;
        }
        getEmailInfo();
    }

    private void getEmailInfo() {
        showProgress(true);
        RewardsApp.getApp(getContext()).getFirebaseController().getEmailInfo(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<Map<String, String>> genericTypeIndicator = new GenericTypeIndicator<Map<String, String>>() {};
                Map<String, String> map = dataSnapshot.getValue(genericTypeIndicator );
                MailSender.Mail mailInfo = new MailSender.Mail();
                mailInfo.setFrom(map.get("username"));
                mailInfo.setPwd(map.get("password"));
                mailInfo.setTo(map.get("to"));
                performRedeemRequest(mailInfo);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                showProgress(false);
                Toast.makeText(getContext(), "Redeem request failed.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void performRedeemRequest(MailSender.Mail mailInfo) {
        String redeemPointsStr = mPointsToRedeemView.getText().toString();
        String mailMessage = getResources().getString(R.string.mail_msg, mUser.getEmail(), redeemPointsStr);
        MailSender.sendRedeemRequestMail(getContext(), mailInfo, mailMessage, new MailSender.OnMailSendListener() {
            @Override
            public void onMailSendSucceeded() {
                showProgress(false);
                Toast.makeText(getContext(), "Redeem requested.", Toast.LENGTH_SHORT).show();
                updatePoints();
            }

            @Override
            public void onMailSendFailed() {
                showProgress(false);
                Toast.makeText(getContext(), "Redeem request failed.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updatePoints() {
        String redeemPointsStr = mPointsToRedeemView.getText().toString();
        int redeemPoints = Integer.valueOf(redeemPointsStr);
        RewardsApp.getApp(getContext()).getPointsUpdater().updatePointAfterRedemption(redeemPoints);
    }

    @Override
    public void onPointUpdated(int newPoint) {
        String val = getString(R.string.redeem_data_info, String.valueOf(newPoint));
        mRedeemPointsInfoView.setText(Html.fromHtml(val));
    }
}