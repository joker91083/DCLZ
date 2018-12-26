package com.otitan.dclz.activity;

import android.graphics.Canvas;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lidong.pdf.PDFView;
import com.lidong.pdf.listener.OnDrawListener;
import com.lidong.pdf.listener.OnLoadCompleteListener;
import com.lidong.pdf.listener.OnPageChangeListener;
import com.otitan.dclz.R;
import com.titan.baselibrary.util.ProgressDialogUtil;
import com.titan.baselibrary.util.ToastUtil;

import retrofit2.http.Url;

public class PdfActivity extends AppCompatActivity implements OnPageChangeListener
        ,OnLoadCompleteListener, OnDrawListener {

    private PDFView pdfView;
    private ImageView back;
    private String pdfurl= "";
    private String pdfname = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf);

        pdfView = findViewById( R.id.pdfView );
        pdfurl = getIntent().getStringExtra("url");
        pdfname = getIntent().getStringExtra("name");

        displayFromFile1(pdfurl,pdfname);
        //displayFromFile1("http://file.chmsp.com.cn/colligate/file/00100000224821.pdf", "00100000224821.pdf");

        back = findViewById(R.id.pdf_close);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PdfActivity.this.finish();
            }
        });
    }


    /**
     * 获取打开网络的pdf文件
     * @param fileUrl
     * @param fileName
     */
    private void displayFromFile1( String fileUrl ,String fileName) {
        showProgress();
        pdfView.fileFromLocalStorage(this,this,this,fileUrl,fileName);   //设置pdf文件地址

    }

    /**
     * 翻页回调
     * @param page
     * @param pageCount
     */
    @Override
    public void onPageChanged(int page, int pageCount) {
        //ToastUtil.setToast( PdfActivity.this , "page= " + page + " pageCount= " + pageCount);
    }

    /**
     * 加载完成回调
     * @param nbPages  总共的页数
     */
    @Override
    public void loadComplete(int nbPages) {
        //ToastUtil.setToast( PdfActivity.this ,  "加载完成" + nbPages);
        hideProgress();
    }

    /**
     * 显示对话框
     */
    private void showProgress(){
        ProgressDialogUtil.startProgressDialog(this,"报告加载中,请等待。。。");
    }

    /**
     * 关闭等待框
     */
    private void hideProgress(){
        ProgressDialogUtil.stopProgressDialog(this);
    }

    @Override
    public void onLayerDrawn(Canvas canvas, float pageWidth, float pageHeight, int displayedPage) {

    }
}
