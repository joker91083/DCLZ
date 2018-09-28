package com.otitan.dclz.net;

import android.content.Context;

import com.google.gson.GsonBuilder;
import com.otitan.dclz.R;
import com.titan.baselibrary.util.ToastUtil;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

/**
 * Created by whs on 2017/2/17
 * Retrofit 初始化
 */

public class RetrofitHelper {
    private Context mCntext;

    private static NetworkMonitor networkMonitor;
    GsonConverterFactory factory = GsonConverterFactory.create(new GsonBuilder().create());
    private static RetrofitHelper instance = null;
    private Retrofit mRetrofit = null;
    public static RetrofitHelper getInstance(Context context) {
        if (instance == null) {
            instance = new RetrofitHelper(context);
            networkMonitor = new LiveNetworkMonitor(context);
        }
        return instance;
    }

    private RetrofitHelper(Context mContext) {
        mCntext = mContext;
        init();
    }

    private void init() {
        resetApp();
    }

    private void resetApp() {
        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();
        okHttpClientBuilder.addNetworkInterceptor(new MyNetworkInterceptor());
        okHttpClientBuilder.connectTimeout(5, TimeUnit.SECONDS);
        okHttpClientBuilder.readTimeout(20, TimeUnit.SECONDS);

        mRetrofit = new Retrofit.Builder()
                .baseUrl(mCntext.getResources().getString(R.string.serverhost))
                .client(okHttpClientBuilder.build())
                .addConverterFactory(SimpleXmlConverterFactory.create())
                //.addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
    }

    public RetrofitService getServer() {
        return mRetrofit.create(RetrofitService.class);
    }

    private class MyNetworkInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            if (networkMonitor.isConnected()) {
                return chain.proceed(chain.request());
            } else {
                //throw new NoNetworkException();
                ToastUtil.setToast(mCntext, "无网络连接，请检查网络");
            }
            return null;
        }
    }
}
