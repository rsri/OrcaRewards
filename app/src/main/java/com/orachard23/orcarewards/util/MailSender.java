package com.orachard23.orcarewards.util;

import android.content.Context;

import com.creativityapps.gmailbackgroundlibrary.BackgroundMail;

/**
 * Created by srikaram on 08-Dec-17.
 */

public class MailSender {

    public static void sendRedeemRequestMail(Context context, Mail mailInfo, String mailMsg, final OnMailSendListener listener) {
        BackgroundMail.newBuilder(context)
                .withUsername(mailInfo.getFrom())
                .withPassword(mailInfo.getPwd())
                .withMailto(mailInfo.getTo())
                .withType(BackgroundMail.TYPE_PLAIN)
                .withSubject("Orca Rewards - Redemption request")
                .withBody(mailMsg)
                .withOnSuccessCallback(new BackgroundMail.OnSuccessCallback() {
                    @Override
                    public void onSuccess() {
                        if (listener != null) {
                            listener.onMailSendSucceeded();
                        }
                    }
                })
                .withOnFailCallback(new BackgroundMail.OnFailCallback() {
                    @Override
                    public void onFail() {
                        if (listener != null) {
                            listener.onMailSendFailed();
                        }
                    }
                }).send();
    }

    public static class Mail {
        private String from;
        private String to;
        private String pwd;

        private String getFrom() {
            return from;
        }

        public void setFrom(String from) {
            this.from = from;
        }

        private String getTo() {
            return to;
        }

        public void setTo(String to) {
            this.to = to;
        }

        private String getPwd() {
            return pwd;
        }

        public void setPwd(String pwd) {
            this.pwd = pwd;
        }

    }

    public interface OnMailSendListener {
        void onMailSendSucceeded();

        void onMailSendFailed();
    }
}
