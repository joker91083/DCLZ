package com.otitan.dclz.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.google.gson.Gson;
import com.lling.photopicker.PhotoPickerActivity;
import com.lling.photopicker.utils.OtherUtils;
import com.otitan.dclz.R;
import com.otitan.dclz.adapter.SelectPictureAdapter;
import com.otitan.dclz.bean.Audio;
import com.otitan.dclz.bean.EventReport;
import com.otitan.dclz.bean.Image;
import com.otitan.dclz.bean.Video;
import com.otitan.dclz.common.EventReportModel;
import com.otitan.dclz.net.RetrofitHelper;
import com.otitan.dclz.permission.PermissionsChecker;
import com.otitan.dclz.util.Constant;
import com.otitan.dclz.util.MobileUtil;
import com.titan.baselibrary.util.ConverterUtils;
import com.titan.baselibrary.util.ProgressDialogUtil;
import com.titan.baselibrary.util.ToastUtil;
import com.titan.baselibrary.util.Util;
import com.titan.medialibrary.activity.AudioRecorderActivity;
import com.titan.medialibrary.activity.VideoRecorderActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 事件上报
 */
public class MonitorDetailActivity extends AppCompatActivity {

    @BindView(R.id.iv_back)
    ImageView mIv_back;
    @BindView(R.id.et_name)
    EditText mEt_name;
    @BindView(R.id.et_description)
    EditText mEt_description;
    @BindView(R.id.et_longitude)
    EditText mEt_longitude;
    @BindView(R.id.et_latitude)
    EditText mEt_latitude;
    @BindView(R.id.et_address)
    EditText mEt_address;
    @BindView(R.id.rv_picture)
    RecyclerView mRv_picture;
    @BindView(R.id.tv_temporary)
    TextView mTv_temporary;
    @BindView(R.id.tv_report)
    TextView mTv_report;

    @BindView(R.id.tv_picture)
    TextView mTv_picture;
    @BindView(R.id.iv_video)
    VideoView ivVideo;
    @BindView(R.id.iv_audio)
    TextView ivAudio;
    @BindView(R.id.view_audio)
    TextView viewAudio;
    @BindView(R.id.view_video)
    TextView viewVideo;

    private Context mContext;
    private List<String> picList = new ArrayList<>();

    private String audioPath = "";
    private String videoPath = "";
    private String lon;
    private String lat;
    private String address;

    private ArrayList<Image> images = new ArrayList<>();
    private ArrayList<Video> videos = new ArrayList<>();
    private ArrayList<Audio> audios = new ArrayList<>();

    private EventReportModel reportModel = null;

    private EventReport eventReport = null;

    /**
     * 动态检测权限
     */
    private static final int REQUEST_CODE = 10000; // 权限请求码
    private PermissionsChecker permissionsChecker;
    private String[] permissions = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor_detail);

        ButterKnife.bind(this);
        this.mContext = this;

        eventReport = (EventReport) getIntent().getSerializableExtra("eventReport");

        permissionsChecker = new PermissionsChecker(this);
        // 缺少权限时, 进入权限配置页面
        if (permissionsChecker.lacksPermissions(permissions)) {
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE);
        }

        initView();

        reportModel = new EventReportModel();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initView() {
        mIv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        if (eventReport != null) {
            mEt_name.setText(eventReport.getXJ_SJMC());
            mEt_description.setText(eventReport.getXJ_MSXX());
            mEt_longitude.setText(eventReport.getXJ_JD());
            mEt_latitude.setText(eventReport.getXJ_WD());
            mEt_address.setText(eventReport.getXJ_XXDZ());
            mRv_picture.setVisibility(View.GONE);
            mTv_temporary.setVisibility(View.GONE);
            mTv_picture.setVisibility(View.GONE);
            ivVideo.setVisibility(View.GONE);
            ivAudio.setVisibility(View.GONE);
            viewAudio.setVisibility(View.GONE);
            viewVideo.setVisibility(View.GONE);

        } else {
            String x = getIntent().getStringExtra("X");
            lon = Constant.sixFormat.format(ConverterUtils.toDouble(x));
            mEt_longitude.setText(lon);
            String y = getIntent().getStringExtra("Y");
            lat = Constant.sixFormat.format(ConverterUtils.toDouble(y));
            mEt_latitude.setText(lat);
            address = getIntent().getStringExtra("address");
            mEt_address.setText(address);


            mEt_name.setFocusable(true);
            mEt_name.setFocusableInTouchMode(true);
            mEt_name.requestFocus();
            mEt_name.requestFocusFromTouch();

            initAdapter();

            int screenWidth = OtherUtils.getWidthInPx(getApplicationContext());
            int mColumnWidth = (screenWidth - OtherUtils.dip2px(getApplicationContext(), 4)) / 4;

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(mColumnWidth, mColumnWidth);
            params.setMargins(15, 10, 10, 10);
            ivAudio.setLayoutParams(params);
            ivVideo.setLayoutParams(params);

            ivVideo.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    startVideo();
                    return false;
                }
            });
        }
    }


    /**
     * 跳转到选择图片界面
     */
    public void toSelectPic() {
        if (picList.size() != 0 && picList.size() != 9) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("重新选择会覆盖之前的图片");
            builder.setMessage("是否重新选择");
            builder.setCancelable(true);
            builder.setPositiveButton("重新选择", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    Intent intent = new Intent(MonitorDetailActivity.this, PhotoPickerActivity.class);
                    intent.putExtra(PhotoPickerActivity.EXTRA_SHOW_CAMERA, true); // 是否显示相机
                    intent.putExtra(PhotoPickerActivity.EXTRA_SELECT_MODE, PhotoPickerActivity.MODE_MULTI); // 选择模式（默认多选模式）
                    intent.putExtra(PhotoPickerActivity.EXTRA_MAX_MUN, PhotoPickerActivity.DEFAULT_NUM); // 最大照片张数
                    startActivityForResult(intent, Constant.PICK_PHOTO);
                }
            });
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextSize(16);
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextSize(16);
        }
        if (picList.size() == 0) {
            Intent intent = new Intent(MonitorDetailActivity.this, PhotoPickerActivity.class);
            intent.putExtra(PhotoPickerActivity.EXTRA_SHOW_CAMERA, true); // 是否显示相机
            intent.putExtra(PhotoPickerActivity.EXTRA_SELECT_MODE, PhotoPickerActivity.MODE_MULTI); // 选择模式（默认多选模式）
            intent.putExtra(PhotoPickerActivity.EXTRA_MAX_MUN, PhotoPickerActivity.DEFAULT_NUM); // 最大照片张数
            startActivityForResult(intent, Constant.PICK_PHOTO);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Constant.PICK_PHOTO:
                    // 图片选择成功
                    picList.clear();
                    picList.addAll(data.getStringArrayListExtra(PhotoPickerActivity.KEY_RESULT));

                    initAdapter();
                    break;
                case Constant.PICK_AUDIO:
                    if (data != null) {
                        audioPath = data.getStringExtra(AudioRecorderActivity.KEY_RESULT);
                        ivAudio.setText(new File(audioPath).getName());
                    }
                    break;

                case Constant.PICK_VIDEO:
                    if (data != null) {
                        videoPath = data.getStringExtra(VideoRecorderActivity.KEY_RESULT);
                        ivVideo.setVideoPath(videoPath);
                        ivVideo.start();
                    }
                    break;
            }
        }
    }

    /*初始化图片*/
    private void initAdapter() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 4);
        mRv_picture.setLayoutManager(gridLayoutManager);

        int screenWidth = OtherUtils.getWidthInPx(getApplicationContext());
        int mColumnWidth = (screenWidth - OtherUtils.dip2px(getApplicationContext(), 4)) / 4;

        SelectPictureAdapter spAdapter = new SelectPictureAdapter(this, picList, mColumnWidth);
        mRv_picture.setAdapter(spAdapter);

        spAdapter.setItemClickListener(new SelectPictureAdapter.MyItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                toSelectPic();
            }
        });
    }

    @OnClick({R.id.iv_audio, R.id.tv_report, R.id.tv_temporary})
    public void setonClick(View view) {
        switch (view.getId()) {
            case R.id.iv_audio:
                startAudio();
                break;
            case R.id.tv_report:
                addOnline();
                break;
            case R.id.tv_temporary:
                addLocalReport();
                break;
        }
    }

    /*录制音频*/
    private void startAudio() {
        Intent intent = new Intent(MonitorDetailActivity.this, AudioRecorderActivity.class);
        startActivityForResult(intent, Constant.PICK_AUDIO);
    }

    /*录制视频*/
    private void startVideo() {
        Intent intent = new Intent(MonitorDetailActivity.this, VideoRecorderActivity.class);
        startActivityForResult(intent, Constant.PICK_VIDEO);
    }

    /*在线上报*/
    private void addOnline() {

        if(eventReport != null){
            String json = new Gson().toJson(eventReport);
            if (RetrofitHelper.getInstance(mContext).networkMonitor.isConnected()) {
                ProgressDialogUtil.startProgressDialog(this, "上传中...");
                reportModel.senInofToServer(json, "", this);
            } else {
                ToastUtil.setToast(mContext, "网络未连接");
            }
            return;
        }

        if(mEt_name.getText().toString().trim().equals("")){
            ToastUtil.setToast(mContext,"事件名称未填写");
            return;
        }

        if(mEt_description.getText().toString().trim().equals("")){
            ToastUtil.setToast(mContext,"事件描述未填写");
            return;
        }

        //网络连接后上报成功
        EventReport report = new EventReport();
        report.setXJ_JD(lon);
        report.setXJ_WD(lat);
        report.setXJ_MSXX(mEt_description.getText().toString().trim());
        report.setXJ_SJMC(mEt_name.getText().toString().trim());
        report.setREMARK("");
        report.setXJ_XXDZ(address);
        String macAddress = MobileUtil.getInstance().getMacAdress(mContext);
        report.setXJ_SBBH(macAddress);

        String pictxt = "";
        if (picList.size() == 1) {
            pictxt = picList.get(0);
        } else if (picList.size() > 1) {
            pictxt = picList.get(0);
            for (int i = 1; i < picList.size(); i++) {
                pictxt = pictxt + "," + picList.get(i);
            }
        }

        if (!RetrofitHelper.getInstance(mContext).networkMonitor.isConnected()) {
            report.setXJ_ZPDZ(pictxt);
            ToastUtil.setToast(mContext, "网络未连接,数据保存到本地");
            //保存数据
            boolean state = reportModel.addLocalResport(mContext, report);
            if (state) {
                ToastUtil.setToast(mContext, "保存成功");
                finish();
            } else {
                ToastUtil.setToast(mContext, "保存失败");
            }
            return;
        }

        for (String pic : picList) {
            String base = Util.picToString(pic);
            Image image = new Image();
            image.setBase(base);
            image.setName(new File(pic).getName());
            images.add(image);
        }

        Gson gson = new Gson();
        if (images.size() > 0) {
            String jsonimage = gson.toJson(images);
            report.setXJ_ZPDZ(jsonimage);
        } else {
            report.setXJ_ZPDZ("[]");
        }


        if (!audioPath.equals("")) {
            File audioFile = new File(audioPath);
            if (audioFile.exists()) {
                Audio audio = new Audio();
                String audiobase = Util.fileToString(audioPath);
                audio.setName(audioFile.getName());
                audio.setBase(audiobase);
                audios.add(audio);
            }

            String audiojson = gson.toJson(audios);
            report.setXJ_YPDZ(audiojson);
        } else {
            report.setXJ_YPDZ("[]");
        }

        if (!videoPath.equals("")) {
            File videoFile = new File(videoPath);
            if (videoFile.exists()) {
                Video video = new Video();
                String videobase = Util.fileToString(videoPath);
                video.setName(videoFile.getName());
                video.setBase(videobase);
                videos.add(video);
            }
            String videojson = gson.toJson(videos);
            report.setXJ_SPDZ(videojson);
        } else {
            report.setXJ_SPDZ("[]");
        }

        String json = new Gson().toJson(report);
        if (RetrofitHelper.getInstance(mContext).networkMonitor.isConnected()) {
            ProgressDialogUtil.startProgressDialog(this, "上传中...");
            reportModel.senInofToServer(json, "", this);
        } else {
            ToastUtil.setToast(mContext, "网络未连接");
        }
    }

    /*本地保存数据*/
    private void addLocalReport() {

        if(mEt_name.getText().toString().trim().equals("")){
            ToastUtil.setToast(mContext,"事件名称未填写");
            return;
        }

        if(mEt_description.getText().toString().trim().equals("")){
            ToastUtil.setToast(mContext,"事件描述未填写");
            return;
        }
        //网络连接后上报成功
        EventReport report = new EventReport();
        report.setXJ_JD(lon);
        report.setXJ_WD(lat);
        report.setXJ_MSXX(mEt_description.getText().toString().trim());
        report.setXJ_SJMC(mEt_name.getText().toString().trim());
        report.setREMARK("");
        report.setXJ_XXDZ(address);
        String macAddress = MobileUtil.getInstance().getMacAdress(mContext);
        report.setXJ_SBBH(macAddress);

        String pictxt = "";
        if (picList.size() == 1) {
            pictxt = picList.get(0);
        } else if (picList.size() > 1) {
            pictxt = picList.get(0);
            for (int i = 1; i < picList.size(); i++) {
                pictxt = pictxt + "," + picList.get(i);
            }
        }

        report.setXJ_ZPDZ(pictxt);

        if (!audioPath.equals("")) {
            report.setXJ_YPDZ(audioPath);
        }

        if (!videoPath.equals("")) {
            report.setXJ_SPDZ(videoPath);
        }

        //保存数据
        boolean state = reportModel.addLocalResport(mContext, report);
        if (state) {
            ToastUtil.setToast(mContext, "保存成功");
            finish();
        } else {
            ToastUtil.setToast(mContext, "保存失败");
        }

    }
}
