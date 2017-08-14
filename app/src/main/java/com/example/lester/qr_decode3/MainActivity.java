package com.example.lester.qr_decode3;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.CaptureActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private IntentIntegrator integrator;
    private TextView textView;
    private String id=null;
    private FirebaseDatabase database;

    @Override   //@Override 是當複寫生命週期等等的東西時,用來自動檢查程式碼的,格式有錯或拼錯就會提出來
    protected void onCreate(Bundle savedInstanceState) {    //app啟動
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        database = FirebaseDatabase.getInstance();

        //建立物件integrator後掃描初始化
        integrator = new IntentIntegrator(this);
        initiateScanning();
    }

    //CaptureActivityAnyOrientation是我自己新增的,
    // 用來繼承CaptureActivity然後覆寫他的權限,
    // 為了讓相機為"正"的,要不然一開始是橫的
    private void initiateScanning(){    //初始化 這裡不太需要管他
        integrator.setCaptureActivity(CaptureActivityAnyOrientation.class); //初始化會將東西丟到CaptureActivity
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);   //然後再從CaptureActivity丟回到onActivityResult
        integrator.setPrompt("請掃描");
        integrator.setCameraId(0);  // Use a specific camera of the device
        integrator.setOrientationLocked(false);
        integrator.setBeepEnabled(false);
        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanResult =
                IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        //利用Result的getText方法获取String对象
        if(scanResult.getContents() != null){   //QR-code掃下去若不為null則繼續執行
            //scanResult.getContents().toString()是將scanResult.getContents()轉成String
            Toast.makeText(this, scanResult.getContents().toString(), Toast.LENGTH_LONG).show();
            try {   //正常情況下執行try裡的東西
                //以下是JSONObject的基本解析
                JSONObject jsonObject = new JSONObject(scanResult.getContents());   //獲取json物件
                /**00000000000000000000000000   已知問題  判斷null的if沒啥用      0000000000  */
                /**00000000000000000000000000   這裡還沒寫好                     0000000   */
                /**00000000000000000000000000   isNull好像是把Value設成null而已   000000  */
                if(!jsonObject.isNull("shop")){     //接收的shop裡不為null則執行
                    //我把QRCODE生成 成{shop:{"條碼","金額"}}
                    id = jsonObject.getJSONObject("shop").names().toString();   //只擷取條碼，不收value
                    textView = (TextView)findViewById(R.id.textView);   //利用textview顯示收到的資料
                    textView.setText(id);
                    Log.i("Test", "id: " + id );

                    DatabaseReference myRef = database.getReference("customer/id/password");    //指定好路徑
                    myRef.child("flag").setValue("waiting");    //flag設為waiting

                    /**
                     * 下面的for是要把客戶端的資料丟到firebase的
                     * 然後從資料庫抓金額下來在一起丟上去
                     */
                    for(int i=0;i<jsonObject.getJSONObject("shop").names().length();i++){   //利用收到的條碼數量做for
                        Log.i("Test",jsonObject.getJSONObject("shop").names().get(i).toString() );  //個別將資料比對並上傳
                    }
                }else {
                    Log.i("Test","null");
                }
            } catch (JSONException e) { //發生意外則印出Log
                Log.i("Test", e.toString());
            }
        } else
        {   //AlertDialog對話視窗
            new AlertDialog.Builder(this)
                    .setMessage("Do you want to try scanning again?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // start rescanning again
                            initiateScanning();
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }

        super.onActivityResult(requestCode, resultCode, intent); //
    }

    public void click (View view){  //按鈕opencamera點擊觸發initiateScanning()，再次打開相機
        //View是用來獲得焦點的(focus)
        initiateScanning();
    }
    /**
     *      3個button點擊操作
     */
    public void set0(View view){        //設定flag清除
        DatabaseReference myRef = database.getReference("customer/id/password");
        myRef.child("flag").setValue("clear");
        Toast.makeText(this, "clear", Toast.LENGTH_SHORT).show();
    }
    /*public void set1(View view){
        DatabaseReference myRef = database.getReference("customer/id/password");
        myRef.child("flag").setValue(true);
    }*/
    public void checkout1(View view){   //現金結帳
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("結帳成功")
                //.setMessage("")
                .setNegativeButton("Cancel", null)
                .show();
        DatabaseReference myRef = database.getReference("customer/id/password");
        myRef.child("flag").setValue("complete");   //結帳成功則送出flag=complete
        new Handler().postDelayed(new Runnable(){   //計時2秒將flag清空為clear
            public void run(){
                //處理少量資訊或UI
                DatabaseReference myRef = database.getReference("customer/id/password");
                myRef.child("flag").setValue("clear");
            }
        }, 2000);
    }
    //以下相同
    public void checkout2(View view){
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("結帳成功")
                //.setMessage("")
                .setNegativeButton("Cancel", null)
                .show();
        DatabaseReference myRef = database.getReference("customer/id/password");
        myRef.child("flag").setValue("complete");
        new Handler().postDelayed(new Runnable(){
            public void run(){
                //處理少量資訊或UI
                DatabaseReference myRef = database.getReference("customer/id/password");
                myRef.child("flag").setValue("clear");
            }
        }, 2000);
    }
    public void checkout3(View view){
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("結帳成功")
                //.setMessage("")
                .setNegativeButton("Cancel", null)
                .show();
        DatabaseReference myRef = database.getReference("customer/id/password");
        myRef.child("flag").setValue("complete");
        new Handler().postDelayed(new Runnable(){
            public void run(){
                //處理少量資訊或UI
                DatabaseReference myRef = database.getReference("customer/id/password");
                myRef.child("flag").setValue("clear");
            }
        }, 2000);

    }
}