package action.svse.com.ewmtest;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.activity.CaptureActivity;
import com.google.zxing.common.BitmapUtils;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText mContent;
    private Button mCreate, mScan;
    private ImageView mImage, img1;
    private final static int REQ_CODE = 1028;
    private TextView mResult;

    private TextView tv;

    /**
     * 使用Handler+Timer实现刷新
     */
    final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    update();
                    break;
            }
        }
    };

    private void update() {
        String content = mContent.getText().toString().trim();
        initData(content);

    }

    Timer t = new Timer();
    TimerTask ts = new TimerTask() {
        @Override
        public void run() {
            Message message = new Message();
            message.what = 1;
            handler.sendMessage(message);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = mContent.getText().toString().trim();
                initData(content);
            }
        });

        t.schedule(ts, 30000, 30000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (t != null) {//停止timer
            t.cancel();
            t = null;
        }
        super.onDestroy();
    }

    private void initView() {
        mContent = (EditText) findViewById(R.id.edt_content);
        mCreate = (Button) findViewById(R.id.btn_create);
        mScan = (Button) findViewById(R.id.btn_scan);

        mImage = (ImageView) findViewById(R.id.image);
        mResult = (TextView) findViewById(R.id.tv_result);
        img1 = (ImageView) findViewById(R.id.image1);

        tv = (TextView) findViewById(R.id.tv);

        mCreate.setOnClickListener(this);
        mScan.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_create:
                String content = mContent.getText().toString().trim();
                initData(content);
                break;

            case R.id.btn_scan:
                //扫码
                Intent intent = new Intent(MainActivity.this, CaptureActivity.class);
                startActivityForResult(intent, REQ_CODE);
                break;
            default:
                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE) {
            mImage.setVisibility(View.VISIBLE);
            mContent.setVisibility(View.GONE);

            String result = data.getStringExtra(CaptureActivity.SCAN_QRCODE_RESULT);
            Bitmap bitmap = data.getParcelableExtra(CaptureActivity.SCAN_QRCODE_BITMAP);

            mResult.setText("扫描结果为：" + result);
            showToast("扫码结果：" + result);
            if (bitmap != null) {
                mImage.setImageBitmap(bitmap);
            }

        }

    }

    private void showToast(String msg) {
        Toast.makeText(MainActivity.this, "" + msg, Toast.LENGTH_SHORT).show();
    }


    private void initData(String content) {
        try {
            // 条形码
            int len = content.length();
            Bitmap tiaoCode;
            if (len > 8) {
                tiaoCode = EncodingHandler.createBarCode(content.substring(len - 8, len), 681, 190);
            } else {
                tiaoCode = EncodingHandler.createBarCode(content, 681, 190);
            }
            img1.setVisibility(View.VISIBLE);
            img1.setImageBitmap(tiaoCode);

            //生成二维码
            content = mContent.getText().toString().trim();
            Bitmap bitmap = null;
            bitmap = BitmapUtils.create2DCode(content);
            mImage.setVisibility(View.VISIBLE);
            mImage.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
