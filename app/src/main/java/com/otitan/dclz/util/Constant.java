package com.otitan.dclz.util;

import android.annotation.SuppressLint;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

public class Constant {

    public static final int PICK_PHOTO = 0x000003;
    public static final int PICK_AUDIO = 0x000004;
    public static final int PICK_VIDEO = 0x000005;

    public static DecimalFormat disFormat = new DecimalFormat("0.00");
    public static DecimalFormat sixFormat = new DecimalFormat("0.000000");
    @SuppressLint("SimpleDateFormat")
    public static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

}
