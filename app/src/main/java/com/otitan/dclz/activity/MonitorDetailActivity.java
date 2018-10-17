package com.otitan.dclz.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lling.photopicker.PhotoPickerActivity;
import com.lling.photopicker.utils.OtherUtils;
import com.otitan.dclz.R;
import com.otitan.dclz.adapter.SelectPictureAdapter;
import com.otitan.dclz.permission.PermissionsChecker;
import com.titan.baselibrary.util.ToastUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 事件上报
 */
public class MonitorDetailActivity extends AppCompatActivity implements View.OnClickListener {

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
    @BindView(R.id.iv_audio)
    ImageView mIv_audio;
    @BindView(R.id.iv_video)
    ImageView mIv_video;
    @BindView(R.id.tv_temporary)
    TextView mTv_temporary;
    @BindView(R.id.tv_report)
    TextView mTv_report;
    /*@BindView(R.id.rv_audio)
    RecyclerView mRv_audio;
    @BindView(R.id.rv_video)
    RecyclerView mRv_video;*/

    @BindView(R.id.tv_picture)
    TextView mTv_picture;

    private List<String> picList = new ArrayList<>();

    /**动态检测权限*/
    private static final int REQUEST_CODE = 10000; // 权限请求码
    private PermissionsChecker permissionsChecker;
    private String[] permissions = new String[] {
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor_detail);

        ButterKnife.bind(this);

        permissionsChecker = new PermissionsChecker(this);
        // 缺少权限时, 进入权限配置页面
        if (permissionsChecker.lacksPermissions(permissions)) {
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE);
        }

        initView();
    }

    private void initView() {
        mIv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mEt_longitude.setText(getIntent().getStringExtra("X"));
        mEt_latitude.setText(getIntent().getStringExtra("Y"));

        int screenWidth = OtherUtils.getWidthInPx(getApplicationContext());
        int mColumnWidth = (screenWidth - OtherUtils.dip2px(getApplicationContext(), 4)) / 4;

        // 选择图片
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,4);
        mRv_picture.setLayoutManager(gridLayoutManager);
        SelectPictureAdapter spAdapter = new SelectPictureAdapter(this, picList, mColumnWidth);
        mRv_picture.setAdapter(spAdapter);
        spAdapter.setItemClickListener(new SelectPictureAdapter.MyItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                toSelectPic();
            }
        });

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(mColumnWidth, mColumnWidth);
        params.setMargins(15, 10,10, 10);
        mIv_audio.setLayoutParams(params);
        mIv_video.setLayoutParams(params);

        mIv_audio.setOnClickListener(this);
        mIv_video.setOnClickListener(this);
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
                    startActivityForResult(intent, 1);
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
            startActivityForResult(intent, 1);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            // 图片选择成功
            picList.clear();
            picList = data.getStringArrayListExtra(PhotoPickerActivity.KEY_RESULT);

            // 选择图片
            GridLayoutManager gridLayoutManager = new GridLayoutManager(this,4);
            mRv_picture.setLayoutManager(gridLayoutManager);

            int screenWidth = OtherUtils.getWidthInPx(getApplicationContext());
            int mColumnWidth = (screenWidth - OtherUtils.dip2px(getApplicationContext(), 4)) / 4;

            SelectPictureAdapter spAdapter = new SelectPictureAdapter(this, picList, mColumnWidth);
            mRv_picture.setAdapter(spAdapter);

            spAdapter.setItemClickListener(new SelectPictureAdapter.MyItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    if (position == 0) {
                        toSelectPic();
                    }
                }
            });
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_audio:

                MediaRecorder mediaRecorder = new MediaRecorder();
                // 第1步：设置音频来源（MIC表示麦克风）
                mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                // 第2步：设置音频输出格式（默认的输出格式）
                mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
                // 第3步：设置音频编码方式（默认的编码方式）
                mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
                // 创建一个临时的音频输出文件
                File audioFile = null;
                try {
                    audioFile = File.createTempFile("record_", ".amr");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                // 第4步：指定音频输出文件
                mediaRecorder.setOutputFile(audioFile.getAbsolutePath());
                // 第5步：调用prepare方法
                try {
                    mediaRecorder.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                // 第6步：调用start方法开始录音
                mediaRecorder.start();

                ToastUtil.setToast(this, "录音");

                break;
        }
    }
}
