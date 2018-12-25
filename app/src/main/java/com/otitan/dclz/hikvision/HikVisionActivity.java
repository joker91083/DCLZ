package com.otitan.dclz.hikvision;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceView;

import com.hikvision.netsdk.HCNetSDK;
import com.hikvision.netsdk.NET_DVR_CLIENTINFO;
import com.hikvision.netsdk.NET_DVR_DEVICEINFO_V30;
import com.hikvision.netsdk.NET_DVR_PREVIEWINFO;
import com.hikvision.netsdk.RealPlayCallBack;
import com.otitan.dclz.R;

import org.MediaPlayer.PlayM4.Player;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 视频监控
 */
public class HikVisionActivity extends AppCompatActivity {

    @BindView(R.id.sv_surveillance)
    SurfaceView mSv_surveillance;

    public String ADDRESS = "192.168.100.251"; // IP
    public int PORT = 8000; // 端口
    public String USER = "admin"; // 账号
    public String PSD = "hik12345"; // 密码

    private int iLogID;
    private int m_iStartChan = 0;
    private int m_iChanNum = 0;
    private boolean m_bNeedDecode = true;
    private boolean m_bStopPlayback = false;
    private int m_iPort = -1; // play port
    private int m_iPlaybackID = -1; // return by NET_DVR_PlayBackByTime

    private final String TAG = "TAG";
    private final String TAG_ERROR = "TAG_ERROR";

    private boolean isShow = true;

    private Thread thread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hik_vision);

        ButterKnife.bind(this);

        initView();
    }

    private void initView() {
        ADDRESS = "222.85.147.92";
        PORT = 8000;
        USER = "admin";
        PSD = "sfb12345";

        initHikVision();
    }

    private void initHikVision() {
        HCNetSDK.getInstance().NET_DVR_Init(); // 初始化SDK

        // 登录设备
        NET_DVR_DEVICEINFO_V30 m_oNetDvrDeviceInfoV30 = new NET_DVR_DEVICEINFO_V30();
        iLogID = HCNetSDK.getInstance().NET_DVR_Login_V30(ADDRESS, PORT, USER, PSD, m_oNetDvrDeviceInfoV30);
        if (m_oNetDvrDeviceInfoV30.byChanNum > 0) {
            m_iStartChan = m_oNetDvrDeviceInfoV30.byStartChan;
            m_iChanNum = m_oNetDvrDeviceInfoV30.byChanNum;
        } else if (m_oNetDvrDeviceInfoV30.byIPChanNum > 0) {
            m_iStartChan = m_oNetDvrDeviceInfoV30.byStartDChan;
            m_iChanNum = m_oNetDvrDeviceInfoV30.byIPChanNum + m_oNetDvrDeviceInfoV30.byHighDChanNum * 256;
        }

        //设置默认点
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!Thread.currentThread().isInterrupted()) {
                    SystemClock.sleep(1000);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (isShow)
                                startSinglePreview();//预览
                        }
                    });
                }
            }
        });
        thread.start();
    }

    private void startSinglePreview() {
        NET_DVR_PREVIEWINFO previewInfo = new NET_DVR_PREVIEWINFO();
        previewInfo.lChannel = m_iStartChan; // 通道号
        previewInfo.dwStreamType = 0; // 码流类型
        previewInfo.bBlocked = 0;

        RealPlayCallBack fRealDataCallBack = getRealPlayerCbf();

        int m_iPlayID = HCNetSDK.getInstance().NET_DVR_RealPlay_V40(iLogID, previewInfo, fRealDataCallBack);
        Log.v("-----------", m_iPlayID + " / 错误码：" + HCNetSDK.getInstance().NET_DVR_GetLastError());
        isShow = false;
        thread.interrupt();
    }

    private RealPlayCallBack getRealPlayerCbf() {
        RealPlayCallBack cbf = new RealPlayCallBack() {
            public void fRealDataCallBack(int iRealHandle, int iDataType, byte[] pDataBuffer, int iDataSize) {
                processRealData(iDataType, pDataBuffer, iDataSize, Player.STREAM_REALTIME);
            }
        };
        return cbf;
    }

    private void processRealData(int iDataType, byte[] pDataBuffer, int iDataSize, int iStreamMode) {
        if (m_bNeedDecode) {
            if (HCNetSDK.NET_DVR_SYSHEAD == iDataType) {
                m_iPort = Player.getInstance().getPort();
                if (m_iPort == -1) {
                    Log.e(TAG_ERROR, "getPort is failed with: " + Player.getInstance().getLastError(m_iPort));
                    return;
                }
                Log.i(TAG, "getPort success with: " + m_iPort);
                if (iDataSize > 0) {
                    if (!Player.getInstance().setStreamOpenMode(m_iPort, iStreamMode))  //set stream mode
                    {
                        Log.e(TAG_ERROR, "setStreamOpenMode failed");
                        return;
                    }
                    if (!Player.getInstance().openStream(m_iPort, pDataBuffer, iDataSize, 2 * 1024 * 1024)) //open stream
                    {
                        Log.e(TAG_ERROR, "openStream failed");
                        return;
                    }
                    if (!Player.getInstance().play(m_iPort, mSv_surveillance.getHolder())) {
                        Log.e(TAG_ERROR, "play failed");
                        return;
                    }
                    if (!Player.getInstance().playSound(m_iPort)) {
                        Log.e(TAG_ERROR, "playSound failed with error code:" + Player.getInstance().getLastError(m_iPort));
                        return;
                    }
                }
            } else {
                if (!Player.getInstance().inputData(m_iPort, pDataBuffer, iDataSize)) {
                    for (int i = 0; i < 4000 && m_iPlaybackID >= 0 && !m_bStopPlayback; i++) {
                        if (Player.getInstance().inputData(m_iPort, pDataBuffer, iDataSize)) {
                            break;
                        }
                        if (i % 100 == 0) {
                            Log.e(TAG_ERROR, "inputData failed with: " + Player.getInstance().getLastError(m_iPort) + ", i:" + i);
                        }
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }
}
