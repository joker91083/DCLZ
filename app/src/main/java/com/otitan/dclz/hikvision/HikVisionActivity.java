package com.otitan.dclz.hikvision;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;

import com.hikvision.netsdk.HCNetSDK;
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
public class HikVisionActivity extends AppCompatActivity implements SurfaceHolder.Callback {

    @BindView(R.id.sv_surveillance)
    SurfaceView mSv_surveillance;

    public String IP = "192.168.100.251"; // IP
    public int PORT = 8000; // 端口
    public String USER = "admin"; // 账号
    public String PSD = "hik12345"; // 密码

    @BindView(R.id.sure)
    TextView sure;

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


    private NET_DVR_DEVICEINFO_V30 m_oNetDvrDeviceInfoV30 = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hik_vision);

        ButterKnife.bind(this);

        initView();

    }

    private void initView() {
        /*IP = "222.85.147.92";
        PORT = 8000;
        USER = "admin";
        PSD = "sfb12345";*/

        initeSdk();
        initHikVision();
    }

    /**
     * @return true - success;false - fail
     * @fn initeSdk
     * @brief SDK init
     */
    private boolean initeSdk() {
        // init net sdk
        if (!HCNetSDK.getInstance().NET_DVR_Init()) {
            Log.e(TAG, "HCNetSDK init is failed!");
            return false;
        }
        HCNetSDK.getInstance().NET_DVR_SetLogToFile(3, "/mnt/sdcard/sdklog/", true);

        mSv_surveillance.getHolder().addCallback(this);
        return true;
    }

    private void initHikVision() {

        // 登录设备
        //NET_DVR_DEVICEINFO_V30 m_oNetDvrDeviceInfoV30 = new NET_DVR_DEVICEINFO_V30();
        //iLogID = HCNetSDK.getInstance().NET_DVR_Login_V30(ADDRESS, PORT, USER, PSD, m_oNetDvrDeviceInfoV30);
        iLogID = loginDevice();

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
        //thread.start();

        sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSinglePreview();//预览
            }
        });
    }

    private void startSinglePreview() {
        NET_DVR_PREVIEWINFO previewInfo = new NET_DVR_PREVIEWINFO();
        previewInfo.lChannel = m_iStartChan+13; // 通道号
        previewInfo.dwStreamType = 0; // 码流类型
        previewInfo.bBlocked = 1;

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

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }


    /**
     * @return login ID
     * @fn loginDevice
     * @author zhangqing
     */
    private int loginDevice() {
        int iLogID = -1;

        iLogID = loginNormalDevice();

        // iLogID = JNATest.TEST_EzvizLogin();
        // iLogID = loginEzvizDevice();

        return iLogID;
    }

    /**
     * @return login ID
     * @fn loginNormalDevice
     * @author zhuzhenlei
     * @brief login on device
     * [out]
     */
    private int loginNormalDevice() {
        // get instance
        m_oNetDvrDeviceInfoV30 = new NET_DVR_DEVICEINFO_V30();
        if (null == m_oNetDvrDeviceInfoV30) {
            Log.e(TAG, "HKNetDvrDeviceInfoV30 new is failed!");
            return -1;
        }
        // call NET_DVR_Login_v30 to login on, port 8000 as default
        int iLogID = HCNetSDK.getInstance().NET_DVR_Login_V30(IP, PORT,USER, PSD, m_oNetDvrDeviceInfoV30);
        if (iLogID < 0) {
            Log.e(TAG, "NET_DVR_Login is failed!Err:"
                    + HCNetSDK.getInstance().NET_DVR_GetLastError());
            return -1;
        }
        if (m_oNetDvrDeviceInfoV30.byChanNum > 0) {
            m_iStartChan = m_oNetDvrDeviceInfoV30.byStartChan;
            m_iChanNum = m_oNetDvrDeviceInfoV30.byChanNum;
        } else if (m_oNetDvrDeviceInfoV30.byIPChanNum > 0) {
            m_iStartChan = m_oNetDvrDeviceInfoV30.byStartDChan;
            m_iChanNum = m_oNetDvrDeviceInfoV30.byIPChanNum
                    + m_oNetDvrDeviceInfoV30.byHighDChanNum * 256;
        }
        Log.i(TAG, "NET_DVR_Login is Successful!");

        return iLogID;
    }
}
