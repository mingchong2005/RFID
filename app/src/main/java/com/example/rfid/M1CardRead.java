package com.example.rfid;

import android.Manifest;
import android.os.Bundle;
import android.os.Build;
import android.os.Environment;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;

import android.view.View;
//import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;

import android.app.AlertDialog; 
import android.app.AlertDialog.Builder; 
import android.content.DialogInterface; 
import android.content.DialogInterface.OnClickListener;

import android.util.Log;
import java.io.IOException;

public class M1CardRead extends AppCompatActivity {
	private static final String TAG = "M1CardRead";	
    private NfcAdapter mNfcAdapterRead;
    private Tag mTagRead;
    private TextView mCardtextView;
    private String m1Content;
    private AlertDialog mSaveDialog = null;
	private String validData;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read);
        //mNfcAdapterRead = M1CardUtils.isNfcAble(this);
        //M1CardUtils.setPendingIntent(PendingIntent.getActivity(this, 0, new Intent(this,getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0));
        //mTag = getIntent().getParcelableExtra(NfcAdapter.EXTRA_TAG);
		mTagRead = NfcActivity.GetTag();
		
        mCardtextView = findViewById(R.id.tv_content);
        mCardtextView.setMovementMethod(ScrollingMovementMethod.getInstance());


		saveMifarealertDialog();
		readMiFareClassic();
/*
        //final Button ButtonM1CardRead = (Button)findViewById(R.id.btn_read_m1);
        //ButtonM1CardRead.setOnClickListener(M1CardReadBtnListener);

		if (M1CardUtils.hasCardType(mTagRead, this, "MifareClassic")) {
			try {
				StringBuilder stringBuilder = new StringBuilder();
				String m1Content = M1CardUtils.readCardOneSector(mTagRead, 4);

				//Log.e(TAG, "NormalString length =" + validData.length()); 
				//Log.e(TAG, "NormalString = " + validData); 
				if (m1Content != null){
					String validData = hexStringToString(m1Content,10);
					mCardtextView.setText(validData);
					writeTxtInOutDisk(this,validData,true, true);
				}else{
					Toast.makeText(getApplicationContext(),"\u5bc6\u7801\u9519\u8bef",Toast.LENGTH_LONG).show();
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
		}

        //M1卡类型
        findViewById(R.id.btn_read_m1).setOnClickListener(v -> {
            if (M1CardUtils.hasCardType(mTagRead, this, "MifareClassic")) {
                try {
				StringBuilder stringBuilder = new StringBuilder();
				String m1Content = M1CardUtils.readCardOneSector(mTagRead, 1);

				//Log.e(TAG, "NormalString length =" + validData.length()); 
				//Log.e(TAG, "NormalString = " + validData); 

				if (m1Content != null){
					String validData = hexStringToString(m1Content,10);
					mCardtextView.setText(validData);
					writeTxtInOutDisk(this,validData,true, true);
				}else{
					Toast.makeText(getApplicationContext(),"\u5bc6\u7801\u9519\u8bef",Toast.LENGTH_LONG).show();
				}	
				
			} catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        //back 
        findViewById(R.id.btn_read_back).setOnClickListener(v->{
			//textView.setText(" ");
			finish();
        });
*/
    }

    public void onButtonClick(View view) {
        switch (view.getId()) {
            case R.id.btn_read_m1:
				readMiFareClassic();
				break;
            case R.id.btn_read_back:
				finish();
                break;
            default:
                break;
        }
    }
	
	private void readMiFareClassic() {
		if (M1CardUtils.hasCardType(mTagRead, this, "MifareClassic")) {
			try {
				StringBuilder stringBuilder = new StringBuilder();
				String m1Content = M1CardUtils.readCardOneSector(mTagRead, 1);

				//Log.e(TAG, "NormalString length =" + validData.length()); 
				//Log.e(TAG, "NormalString = " + validData); 
				if (m1Content != null){
					validData = hexStringToString(m1Content,17);
					mCardtextView.setText(validData);
				}else{
					mCardtextView.setText("\u5bc6\u7801\u9519\u8bef");
					Toast.makeText(getApplicationContext(),"\u5bc6\u7801\u9519\u8bef",Toast.LENGTH_LONG).show();
				}	
			} catch (IOException e) {
				e.printStackTrace();
			}
			mSaveDialog.show();//\u663e\u793a\u5bf9\u8bdd\u6846
		}
	}
	
	private void  saveMifarealertDialog() {
        if(mSaveDialog == null){
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setIcon(null);
			//builder.setTitle("ZhaoKaifeng.com");
			builder.setMessage("确定需要保存吗？");
			builder.setPositiveButton("确定", new OnClickListener(){
				@Override
				public void onClick(DialogInterface arg0, int arg1){
					//Toast.makeText(M1CardRead.this,"\u786e\u5b9a\u6210\u529f\uff01",Toast.LENGTH_SHORT).show();
					writeTxtInOutDisk(M1CardRead.this, validData, true,true);
				}
			});
			
			builder.setNegativeButton("取消", new OnClickListener(){
				@Override
				public void onClick(DialogInterface arg0,int arg1){
					//Toast.makeText(M1CardRead.this, "\u53d6\u6d88\u6210\u529f\uff01", Toast.LENGTH_SHORT).show();
				}
			});
			mSaveDialog = builder.create();
        	}
		}

	public static String hexStringToString(String hexStr, int length) {
		String str = "0123456789ABCDEF";
		char[] hexs = hexStr.toCharArray();
		byte[] bytes = new byte[length];
		int n;
		for (int i = 0; i < bytes.length; i++) {
			n = str.indexOf(hexs[2 * i]) * 16;
			n += str.indexOf(hexs[2 * i + 1]);
			bytes[i] = (byte) (n & 0xff);
		}
		return new String(bytes);
	}

/*
    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        mNfcAdapterRead = M1CardUtils.isNfcAble(this);
        M1CardUtils.setPendingIntent(PendingIntent.getActivity(this, 0, new Intent(this,getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0));
        Log.e("onNewIntent","onNewIntent");
        mTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
    }
*/
    @Override
    public void onPause() {
        super.onPause();
        if (mNfcAdapterRead != null) {
            mNfcAdapterRead.disableForegroundDispatch(this);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mNfcAdapterRead != null) {
            mNfcAdapterRead.enableForegroundDispatch(this, M1CardUtils.getPendingIntent(),
                    null, null);
        }
    }

	public void writeFileData(String fileName,String message){ 
		try{ 
			FileOutputStream fout =openFileOutput(fileName, MODE_PRIVATE);
			byte [] bytes = message.getBytes(); 
			fout.write(bytes); 
			fout.close(); 
		} 
			catch(Exception e){ 
			e.printStackTrace(); 
		} 
	}

	private void writeTxtInOutDisk(Context context,String message, final boolean append, final boolean autoLine) {
		String TAG = "writeTxtInOutDisk";
		Log.d(TAG, "writeTxtInOutDisk: ");
		String root =  Environment.getExternalStorageDirectory().getAbsolutePath();
		String filePath = String.format("%s/RFID.txt", root);
		File file = new File(filePath);
		if(file.exists()){
			Log.d(TAG, "writeTxtInOutDisk: file exits!");
			Log.d(TAG, "writeTxtInOutDisk: file.length" + file.length());
			//file.delete();
	}
/*
	if (Build.VERSION.SDK_INT >= 23) {
		int REQUEST_CODE_CONTACT = 101;
		String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
		for (String str : permissions) {
			if (this.checkSelfPermission(str) != PackageManager.PERMISSION_GRANTED) {
				this.requestPermissions(permissions, REQUEST_CODE_CONTACT);
				return;
			} else {
			}
		}
	}
*/
		Log.d(TAG, "writeTxtInOutDisk: create RFID.txt!");
		FileOutputStream fileOutputStream = null;
        RandomAccessFile raf = null;
        FileOutputStream out = null;
		try {
			if (append) {
	            raf = new RandomAccessFile(file, "rw");
	            raf.seek(file.length());
	            raf.write(message.getBytes());
	            if (autoLine) {
	                raf.write("\n".getBytes());
	            }
	        } else {
	            out = new FileOutputStream(file);
	            out.write(message.getBytes());
	            out.flush();
	        }
			//fileOutputStream = new FileOutputStream(file);
			//fileOutputStream.write(message.getBytes());
			Log.d(TAG, "writeTxtInOutDisk: wirte success ");
			Toast.makeText(M1CardRead.this,"保存成功",Toast.LENGTH_SHORT).show();
		} catch (FileNotFoundException e) {
			Log.e(TAG, "writeTxtInOutDisk: " + e);
			e.printStackTrace();
		} catch (IOException e) {
			Log.e(TAG, "writeTxtInOutDisk: " + e);
			e.printStackTrace();
		} finally {
			if(fileOutputStream == null) return;
			try {
				fileOutputStream.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

}

