package com.orachard23.orcarewards.util;

import com.orachard23.orcarewards.BuildConfig;

/**
 * Created by srikaram on 30-Nov-17.
 */

public class Constants {

    private static final String ORIG_ADMOB_ID = "ca-app-pub-9784659225960777~8255090351";
    private static final String ORIG_ADMOB_UNIT_ID = "ca-app-pub-9784659225960777/6989840649";

    private static final String TEST_ADMOB_ID = "ca-app-pub-3940256099942544~3347511713";
    private static final String TEST_ADMOB_UNIT_ID = "ca-app-pub-3940256099942544/6300978111";
    public static final long GIF_TIMEOUT = 9500;
    public static final long AD_TIMEOUT = 3000;

    public static String getAdmobAdId() {
        return BuildConfig.USE_ORIGINAL_AD_ID ? ORIG_ADMOB_ID : TEST_ADMOB_ID;
    }

    public static String getAdmobAdUnitId() {
        return BuildConfig.USE_ORIGINAL_AD_ID ? ORIG_ADMOB_UNIT_ID : TEST_ADMOB_UNIT_ID;
    }

    public static final String URL_GIF = "http://orchard23.com/gifs/gif%s.gif";
    public static final String ERROR_RESPONSE = "Failure response - ";
}
