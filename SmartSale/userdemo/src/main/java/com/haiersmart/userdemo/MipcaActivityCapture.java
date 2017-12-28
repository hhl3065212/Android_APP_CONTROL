package com.haiersmart.userdemo;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.haiersmart.library.Utils.Bind;
import com.haiersmart.library.Utils.ViewBinder;
import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

public class MipcaActivityCapture extends AppCompatActivity implements View.OnClickListener ,DecoratedBarcodeView.TorchListener{

    @Bind(id = R.id.btn_return,click = true)
    private Button btnReturn;
    @Bind(id = R.id.barcodeView)
    private DecoratedBarcodeView barcodeView;

    /**
     * 截图管理
     */
    private CaptureManager captureManager;
    /**
     * 当前闪光灯是否打开了
     */
    private boolean isTorchOn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mipca_capture);
        ViewBinder.bind(this);

        captureManager = new CaptureManager(this, barcodeView);
        captureManager.initializeFromIntent(getIntent(),savedInstanceState);
        captureManager.decode();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_return:
                finish();
                break;
        }
    }
    /**
     * 这里需要将captureManager也考虑进去
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if(captureManager!=null){
            captureManager.onSaveInstanceState(savedInstanceState);
        }
    }
    @Override
    public void onTorchOn() {
        isTorchOn = true;
    }

    @Override
    public void onTorchOff() {
        isTorchOn = false;
    }

    /**
     * 这里需要将captureManager也考虑进去
     */
    @Override
    protected void onPause() {
        super.onPause();
        if(captureManager!=null){
            captureManager.onPause();
        }
    }

    /**
     * 这里需要将captureManager也考虑进去
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(captureManager!=null){
            captureManager.onDestroy();
        }
    }

    /**
     * 这里需要将captureManager也考虑进去
     */
    @Override
    protected void onResume() {
        super.onResume();
        if(captureManager!=null){
            captureManager.onResume();
        }
    }

    /**
     * 切换闪光灯
     * @param view
     */
    public void openOrCloseTorch(View view) {
        if(isTorchOn){
            if(hasTorch())
                barcodeView.setTorchOff();
        }else{
            if(hasTorch())
                barcodeView.setTorchOn();
        }
    }

    /**
     * 判断是否有闪光灯
     * @return
     */
    public boolean hasTorch(){
        return getApplication().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
    }

}
