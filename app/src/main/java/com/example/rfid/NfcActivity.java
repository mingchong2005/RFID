
package com.example.rfid;

import android.Manifest;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import android.app.AlertDialog; 
import android.app.AlertDialog.Builder; 
import android.content.DialogInterface; 
import android.content.DialogInterface.OnClickListener;

import java.io.IOException;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import cn.edu.gdmec.android.androidstudiodemo.utils.MD5Utils;

public class NfcActivity extends AppCompatActivity{
    private TextView tv_main_title;//\u6807\u9898
    private TextView tv_back,tv_register,tv_find_psw;//\u8fd4\u56de\u952e,\u663e\u793a\u7684\u6ce8\u518c\uff0c\u627e\u56de\u5bc6\u7801
    private Button btn_login;//\u767b\u5f55\u6309\u94ae
    private String userName,psw,spPsw;//\u83b7\u53d6\u7684\u7528\u6237\u540d\uff0c\u5bc6\u7801\uff0c\u52a0\u5bc6\u5bc6\u7801
    private EditText et_user_name,et_psw;//\u7f16\u8f91\u6846
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //\u8bbe\u7f6e\u6b64\u754c\u9762\u4e3a\u7ad6\u5c4f
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        init();
    }
    //\u83b7\u53d6\u754c\u9762\u63a7\u4ef6
    private void init() {
        //\u4ecemain_title_bar\u4e2d\u83b7\u53d6\u7684id
        tv_main_title=findViewById(R.id.tv_main_title);
        tv_main_title.setText("\u767b\u5f55");
        tv_back=findViewById(R.id.tv_back);
        //\u4eceactivity_login.xml\u4e2d\u83b7\u53d6\u7684
        tv_register=findViewById(R.id.tv_register);
        tv_find_psw=findViewById(R.id.tv_find_psw);
        btn_login=findViewById(R.id.btn_login);
        et_user_name=findViewById(R.id.et_user_name);
        et_psw=findViewById(R.id.et_psw);
        //\u8fd4\u56de\u952e\u7684\u70b9\u51fb\u4e8b\u4ef6
        tv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //\u767b\u5f55\u754c\u9762\u9500\u6bc1
                LoginActivity.this.finish();
            }
        });
        //\u7acb\u5373\u6ce8\u518c\u63a7\u4ef6\u7684\u70b9\u51fb\u4e8b\u4ef6
        tv_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //\u4e3a\u4e86\u8df3\u8f6c\u5230\u6ce8\u518c\u754c\u9762\uff0c\u5e76\u5b9e\u73b0\u6ce8\u518c\u529f\u80fd
                Intent intent=new Intent(LoginActivity.this,RegisterActivity.class);
                startActivityForResult(intent, 1);
            }
        });
        //\u627e\u56de\u5bc6\u7801\u63a7\u4ef6\u7684\u70b9\u51fb\u4e8b\u4ef6
        tv_find_psw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //\u8df3\u8f6c\u5230\u627e\u56de\u5bc6\u7801\u754c\u9762\uff08\u6b64\u9875\u9762\u6682\u672a\u521b\u5efa\uff09
            }
        });
        //\u767b\u5f55\u6309\u94ae\u7684\u70b9\u51fb\u4e8b\u4ef6
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //\u5f00\u59cb\u767b\u5f55\uff0c\u83b7\u53d6\u7528\u6237\u540d\u548c\u5bc6\u7801 getText().toString().trim();
                userName=et_user_name.getText().toString().trim();
                psw=et_psw.getText().toString().trim();
                //\u5bf9\u5f53\u524d\u7528\u6237\u8f93\u5165\u7684\u5bc6\u7801\u8fdb\u884cMD5\u52a0\u5bc6\u518d\u8fdb\u884c\u6bd4\u5bf9\u5224\u65ad, MD5Utils.md5( ); psw \u8fdb\u884c\u52a0\u5bc6\u5224\u65ad\u662f\u5426\u4e00\u81f4
                String md5Psw= MD5Utils.md5(psw);
                // md5Psw ; spPsw \u4e3a \u6839\u636e\u4eceSharedPreferences\u4e2d\u7528\u6237\u540d\u8bfb\u53d6\u5bc6\u7801
                // \u5b9a\u4e49\u65b9\u6cd5 readPsw\u4e3a\u4e86\u8bfb\u53d6\u7528\u6237\u540d\uff0c\u5f97\u5230\u5bc6\u7801
                spPsw=readPsw(userName);
                // TextUtils.isEmpty
                if(TextUtils.isEmpty(userName)){
                    Toast.makeText(LoginActivity.this, "\u8bf7\u8f93\u5165\u7528\u6237\u540d", Toast.LENGTH_SHORT).show();
                    return;
                }else if(TextUtils.isEmpty(psw)){
                    Toast.makeText(LoginActivity.this, "\u8bf7\u8f93\u5165\u5bc6\u7801", Toast.LENGTH_SHORT).show();
                    return;
                    // md5Psw.equals(); \u5224\u65ad\uff0c\u8f93\u5165\u7684\u5bc6\u7801\u52a0\u5bc6\u540e\uff0c\u662f\u5426\u4e0e\u4fdd\u5b58\u5728SharedPreferences\u4e2d\u4e00\u81f4
                }else if(md5Psw.equals(spPsw)){
                    //\u4e00\u81f4\u767b\u5f55\u6210\u529f
                    Toast.makeText(LoginActivity.this, "\u767b\u5f55\u6210\u529f", Toast.LENGTH_SHORT).show();
                    //\u4fdd\u5b58\u767b\u5f55\u72b6\u6001\uff0c\u5728\u754c\u9762\u4fdd\u5b58\u767b\u5f55\u7684\u7528\u6237\u540d \u5b9a\u4e49\u4e2a\u65b9\u6cd5 saveLoginStatus boolean \u72b6\u6001 , userName \u7528\u6237\u540d;
                    saveLoginStatus(true, userName);
                    //\u767b\u5f55\u6210\u529f\u540e\u5173\u95ed\u6b64\u9875\u9762\u8fdb\u5165\u4e3b\u9875
                    Intent data=new Intent();
                    //datad.putExtra( ); name , value ;
                    data.putExtra("isLogin",true);
                    //RESULT_OK\u4e3aActivity\u7cfb\u7edf\u5e38\u91cf\uff0c\u72b6\u6001\u7801\u4e3a-1
                    // \u8868\u793a\u6b64\u9875\u9762\u4e0b\u7684\u5185\u5bb9\u64cd\u4f5c\u6210\u529f\u5c06data\u8fd4\u56de\u5230\u4e0a\u4e00\u9875\u9762\uff0c\u5982\u679c\u662f\u7528back\u8fd4\u56de\u8fc7\u53bb\u7684\u5219\u4e0d\u5b58\u5728\u7528setResult\u4f20\u9012data\u503c
                    setResult(RESULT_OK,data);
                    //\u9500\u6bc1\u767b\u5f55\u754c\u9762
                    LoginActivity.this.finish();
                    //\u8df3\u8f6c\u5230\u4e3b\u754c\u9762\uff0c\u767b\u5f55\u6210\u529f\u7684\u72b6\u6001\u4f20\u9012\u5230 MainActivity \u4e2d
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    return;
                }else if((spPsw!=null&&!TextUtils.isEmpty(spPsw)&&!md5Psw.equals(spPsw))){
                    Toast.makeText(LoginActivity.this, "\u8f93\u5165\u7684\u7528\u6237\u540d\u548c\u5bc6\u7801\u4e0d\u4e00\u81f4", Toast.LENGTH_SHORT).show();
                    return;
                }else{
                    Toast.makeText(LoginActivity.this, "\u6b64\u7528\u6237\u540d\u4e0d\u5b58\u5728", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    /**
     *\u4eceSharedPreferences\u4e2d\u6839\u636e\u7528\u6237\u540d\u8bfb\u53d6\u5bc6\u7801
     */
    private String readPsw(String userName){
        //getSharedPreferences("loginInfo",MODE_PRIVATE);
        //"loginInfo",mode_private; MODE_PRIVATE\u8868\u793a\u53ef\u4ee5\u7ee7\u7eed\u5199\u5165
        SharedPreferences sp=getSharedPreferences("loginInfo", MODE_PRIVATE);
        //sp.getString() userName, "";
        return sp.getString(userName , "");
    }
    /**
     *\u4fdd\u5b58\u767b\u5f55\u72b6\u6001\u548c\u767b\u5f55\u7528\u6237\u540d\u5230SharedPreferences\u4e2d
     */
    private void saveLoginStatus(boolean status,String userName){
        //saveLoginStatus(true, userName);
        //loginInfo\u8868\u793a\u6587\u4ef6\u540d  SharedPreferences sp=getSharedPreferences("loginInfo", MODE_PRIVATE);
        SharedPreferences sp=getSharedPreferences("loginInfo", MODE_PRIVATE);
        //\u83b7\u53d6\u7f16\u8f91\u5668
        SharedPreferences.Editor editor=sp.edit();
        //\u5b58\u5165boolean\u7c7b\u578b\u7684\u767b\u5f55\u72b6\u6001
        editor.putBoolean("isLogin", status);
        //\u5b58\u5165\u767b\u5f55\u72b6\u6001\u65f6\u7684\u7528\u6237\u540d
        editor.putString("loginUserName", userName);
        //\u63d0\u4ea4\u4fee\u6539
        editor.commit();
    }
    /**
     * \u6ce8\u518c\u6210\u529f\u7684\u6570\u636e\u8fd4\u56de\u81f3\u6b64
     * @param requestCode \u8bf7\u6c42\u7801
     * @param resultCode \u7ed3\u679c\u7801
     * @param data \u6570\u636e
     */
    @Override
    //\u663e\u793a\u6570\u636e\uff0c onActivityResult
    //startActivityForResult(intent, 1); \u4ece\u6ce8\u518c\u754c\u9762\u4e2d\u83b7\u53d6\u6570\u636e
    //int requestCode , int resultCode , Intent data
    // LoginActivity -> startActivityForResult -> onActivityResult();
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        if(data!=null){
            //\u662f\u83b7\u53d6\u6ce8\u518c\u754c\u9762\u56de\u4f20\u8fc7\u6765\u7684\u7528\u6237\u540d
            // getExtra().getString("***");
            String userName=data.getStringExtra("userName");
            if(!TextUtils.isEmpty(userName)){
                //\u8bbe\u7f6e\u7528\u6237\u540d\u5230 et_user_name \u63a7\u4ef6
                et_user_name.setText(userName);
                //et_user_name\u63a7\u4ef6\u7684setSelection()\u65b9\u6cd5\u6765\u8bbe\u7f6e\u5149\u6807\u4f4d\u7f6e
                et_user_name.setSelection(userName.length());
            }
        }
    }
}

package com.example.rfid;

import android.Manifest;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import android.app.AlertDialog; 
import android.app.AlertDialog.Builder; 
import android.content.DialogInterface; 
import android.content.DialogInterface.OnClickListener;

import java.io.IOException;

/**
 * @author kuan
 * Created on 2019/2/25.
 * @description
 */
public class NfcActivity extends AppCompatActivity {
    public static  NfcAdapter mNfcAdapter;
    public static  Tag mTag;
	private static boolean validCard = false;
	private static boolean blankCard = false;
    private AlertDialog mValidCardDialog = null;
    private AlertDialog mBlankCardDialog = null;
    TextView textView;

    private AlertDialog mReadDialog = null;
    private AlertDialog mWriteDialog = null;
    private AlertDialog mNoMiDialog = null;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mNfcAdapter = M1CardUtils.isNfcAble(this);
        M1CardUtils.setPendingIntent(PendingIntent.getActivity(this, 0, new Intent(this,
                getClass()), 0));
        mTag = getIntent().getParcelableExtra(NfcAdapter.EXTRA_TAG);

        textView = findViewById(R.id.card_content);

        noMifarealertDialog();
        readMifarealertDialog();
        writeMifarealertDialog();
		judgeValidCardDialog();
		judgeBlankCardDialog();
		
        verifyStoragePermissions(this);
        //writeMifarealertDialog();
/*
        //read
        findViewById(R.id.btn_read_data).setOnClickListener(v -> {
			startActivity(new Intent(NfcActivity.this, M1CardRead.class));
        });
        //write
        findViewById(R.id.btn_write_data).setOnClickListener(v->{
			startActivity(new Intent(NfcActivity.this, M1CardWrite.class));
        });
		//Back
		findViewById(R.id.btn_user_management).setOnClickListener(v->{
			//startActivity(new Intent(NfcActivity.this, M1CardRead.class));
		});
 */

    }

    public static void verifyStoragePermissions(AppCompatActivity activity) {
        if (Build.VERSION.SDK_INT >= 23) {
            int REQUEST_CODE_CONTACT = 101;
            String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
            for (String str : permissions) {
                if (activity.checkSelfPermission(str) != PackageManager.PERMISSION_GRANTED) {
                    activity.requestPermissions(permissions, REQUEST_CODE_CONTACT);
                    return;
                } else {
                }
            }
        }

    }

    public void onButtonClick(View view) {
	    switch (view.getId()) {
	        case R.id.btn_read_data:
	            if (M1CardUtils.hasCardType(mTag, this, "MifareClassic")) {
					if(blankCard){
						mBlankCardDialog.show();
					}else{
						if(validCard){
							mReadDialog.show();
						}else{
							mValidCardDialog.show();
						}
					}
	            }else{
	                mNoMiDialog.show();
	            }
	            break;
	        case R.id.btn_write_data:
	            if (M1CardUtils.hasCardType(mTag, this, "MifareClassic")) {
					if(blankCard){
						mWriteDialog.setMessage(blankCard ? "空白卡, 确定写入铅封数据吗？" : "确定写入铅封数据吗？");
						mWriteDialog.show();
					}else{
						if(validCard){
							mWriteDialog.show();
						}else{
							mValidCardDialog.show();
						}
					}
				}else{
					mNoMiDialog.show();
				}
	            break;
	        case R.id.btn_user_management:
	            //finish();
	            break;
	        case R.id.btn_quit:
	            finish();
	            break;
	        default:
	            break;
	    }
    }

    private void  noMifarealertDialog() {
        if(mNoMiDialog == null){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setIcon(null);
            //builder.setTitle("ZhaoKaifeng.com");
            builder.setMessage("请帖RFID卡？");
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface arg0, int arg1){
                    //Toast.makeText(M1CardRead.this,"\u786e\u5b9a\u6210\u529f\uff01",Toast.LENGTH_SHORT).show();
                    //startActivity(new Intent(NfcActivity.this, M1CardRead.class));
                }
            });

            builder.setNegativeButton("取消", new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface arg0,int arg1){
                    //Toast.makeText(M1CardRead.this, "\u53d6\u6d88\u6210\u529f\uff01", Toast.LENGTH_SHORT).show();
                }
            });
            mNoMiDialog = builder.create();
        }
    }

    private void  readMifarealertDialog() {
        if(mReadDialog == null){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setIcon(null);
            //builder.setTitle("ZhaoKaifeng.com");
            builder.setMessage("确定读取铅封数据吗？");
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface arg0, int arg1){
                    //Toast.makeText(M1CardRead.this,"\u786e\u5b9a\u6210\u529f\uff01",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(NfcActivity.this, M1CardRead.class));
                }
            });

            builder.setNegativeButton("取消", new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface arg0,int arg1){
                    //Toast.makeText(M1CardRead.this, "\u53d6\u6d88\u6210\u529f\uff01", Toast.LENGTH_SHORT).show();
                }
            });
            mReadDialog = builder.create();
        }
    }

    private void  writeMifarealertDialog() {
        if(mWriteDialog == null){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setIcon(null);
            //builder.setTitle("ZhaoKaifeng.com");
            builder.setMessage("确定写入铅封数据吗？");
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface arg0, int arg1){
                    //Toast.makeText(M1CardRead.this,"\u786e\u5b9a\u6210\u529f\uff01",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(NfcActivity.this, M1CardWrite.class));
                }
            });

            builder.setNegativeButton("取消", new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface arg0,int arg1){
                    //Toast.makeText(M1CardRead.this, "\u53d6\u6d88\u6210\u529f\uff01", Toast.LENGTH_SHORT).show();
                }
            });
            mWriteDialog = builder.create();
        }
    }

    public static NfcAdapter GetNfcAdapter() {
        return mNfcAdapter;
    }

    public static Tag GetTag() {
        return mTag;
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        mNfcAdapter = M1CardUtils.isNfcAble(this);
        M1CardUtils.setPendingIntent(PendingIntent.getActivity(this, 0, new Intent(this,
                getClass()), 0));
        Log.e("onNewIntent","onNewIntent");
        mTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

		try {
			StringBuilder stringBuilder = new StringBuilder();

			String m1Content = M1CardUtils.readCardInfo(mTag);

			blankCard = M1CardUtils.JudgeBlankCard(mTag);

			if(blankCard){
				mBlankCardDialog.show();//\u663e\u793a\u5bf9\u8bdd\u6846
			}else{
				validCard = M1CardUtils.JudgeIllegalCard(mTag);
				if(!validCard){
					mValidCardDialog.show();//\u663e\u793a\u5bf9\u8bdd\u6846
				}
			}
			/*
			for (int i = 0; i < m1Content.length; i++) {
				for (int j = 0; j < m1Content[i].length; j++) {
					stringBuilder.append(m1Content[i][j]+"\n");
				}
			}*/
			//textView.setText(m1Content);
			//writeFileData(fileName,stringBuilder.toString());
			//writeTxtInOutDisk(this,stringBuilder.toString()); 
			
		} catch (IOException e) {
			e.printStackTrace();
		}
    }


	private void  judgeValidCardDialog() {
        if(mValidCardDialog == null){
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setIcon(null);
			//builder.setTitle("ZhaoKaifeng.com");
			builder.setMessage("\u975e\u6cd5\u5361");
			builder.setPositiveButton("确定", new OnClickListener(){
				@Override
				public void onClick(DialogInterface arg0, int arg1){
					//Toast.makeText(M1CardRead.this,"\u786e\u5b9a\u6210\u529f\uff01",Toast.LENGTH_SHORT).show();
					//writeTxtInOutDisk(M1CardRead.this,m1Content, true,true);
				}
			});
			mValidCardDialog = builder.create();
        	}
		}
	
		private void  judgeBlankCardDialog() {
			if(mBlankCardDialog == null){
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setIcon(null);
				//builder.setTitle("ZhaoKaifeng.com");
				builder.setMessage("\u7a7a\u767d\u5361");
				builder.setPositiveButton("确定", new OnClickListener(){
					@Override
					public void onClick(DialogInterface arg0, int arg1){
						//Toast.makeText(M1CardRead.this,"\u786e\u5b9a\u6210\u529f\uff01",Toast.LENGTH_SHORT).show();
						//writeTxtInOutDisk(M1CardRead.this,m1Content, true,true);
					}
				});
				mBlankCardDialog = builder.create();
				}
			}

    @Override
    public void onPause() {
        super.onPause();
        if (mNfcAdapter != null) {
            mNfcAdapter.disableForegroundDispatch(this);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mNfcAdapter != null) {
            mNfcAdapter.enableForegroundDispatch(this, M1CardUtils.getPendingIntent(),
                    null, null);
        }
    }

}
