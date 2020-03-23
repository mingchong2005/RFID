package com.example.rfid;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Toast;
import java.nio.charset.Charset; 

import java.io.IOException;

public class M1CardWrite extends AppCompatActivity {
	private static final String TAG = "M1CardWrite";	

    private NfcAdapter mNfcAdapterWrite;
    private Tag mTagWrite;
	static EditText mInputText;
	private AlertDialog mWriteInDialog = null;
	String TestString = "12345678";
	private TextView WritetextView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);
        //mNfcAdapterWrite = M1CardUtils.isNfcAble(this);
        //M1CardUtils.setPendingIntent(PendingIntent.getActivity(this, 0, new Intent(this,getClass()), 0));
        //mTagWrite = getIntent().getParcelableExtra(NfcAdapter.EXTRA_TAG);
		mTagWrite = NfcActivity.GetTag();

		WritetextView = findViewById(R.id.write_content);
		WritetextView.setMovementMethod(ScrollingMovementMethod.getInstance());

		mInputText = (EditText) findViewById(R.id.EditTextInput);
		mInputText.requestFocus();
		writeMifarealertDialog();
/*
        findViewById(R.id.btn_write_m1).setOnClickListener(v -> {
            if (M1CardUtils.hasCardType(mTagWrite, this, "MifareClassic")) {
                try {
					String ConvertString = null;
					CharSequence editText = mInputText.getText();	
		
					if(editText == null || editText.length() == 0 || editText.length() > 16 ){
						Toast.makeText(getApplicationContext(), 
										"\u5199\u5165\u7684\u6570\u636e\u4e0d\u80fd\u4e3a\u7a7a",Toast.LENGTH_LONG).show();
						WritetextView.setText("写入失败");
					}
					else{
						ConvertString = editText.toString();
						//Log.e(TAG, "ConvertString = " + ConvertString); 

						//Log.e(TAG, "ConvertString=" + ConvertString.length()); 
						ConvertString = addZeroForNum(ConvertString, 16);
						
						byte[] data = ConvertString.getBytes();
						boolean writeflag = M1CardUtils.writeBlock(mTagWrite, 4, ConvertString.getBytes());
						writeflag = M1CardUtils.writeBlock(mTagWrite, 5, ConvertString.getBytes());

						if(writeflag){
							Toast.makeText(getApplicationContext(), 
											"\u5199\u5165\u6210\u529f",Toast.LENGTH_LONG).show();
							WritetextView.setText("写入成功");
						}
						//String HexString = bytesToHexString(data);
						//Log.e(TAG, "HexString.getBytes() = " + HexString);
						//Log.e(TAG, "HexString length =" + HexString.length()); 

						//String NormalString = hexStringToString(HexString);
						//Log.e(TAG, "NormalString length =" + NormalString.length()); 
						//Log.e(TAG, "NormalString = " + NormalString); 
					}
					
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        //back 
        findViewById(R.id.btn_write_back).setOnClickListener(v->{
			finish();
        });
*/
    }

	private void  writeMifarealertDialog() {
		if(mWriteInDialog == null){
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setIcon(null);
			//builder.setTitle("ZhaoKaifeng.com");
			builder.setMessage("确定写入铅封数据吗？");
			builder.setPositiveButton("确定", new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface arg0, int arg1){
					//Toast.makeText(M1CardRead.this,"\u786e\u5b9a\u6210\u529f\uff01",Toast.LENGTH_SHORT).show();
					writeMiFareClassic();
				}
			});

			builder.setNegativeButton("取消", new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface arg0,int arg1){
					//Toast.makeText(M1CardRead.this, "\u53d6\u6d88\u6210\u529f\uff01", Toast.LENGTH_SHORT).show();
				}
			});
			mWriteInDialog = builder.create();
		}
	}

	private void writeMiFareClassic() {
		if (M1CardUtils.hasCardType(mTagWrite, this, "MifareClassic")) {
			try {
				String ConvertString = null;
				CharSequence editText = mInputText.getText();

				if(editText == null || editText.length() == 0 || editText.length() > 17 ){
					Toast.makeText(getApplicationContext(),
							"\u5199\u5165\u7684\u6570\u636e\u4e0d\u80fd\u4e3a\u7a7a",Toast.LENGTH_LONG).show();
					WritetextView.setText("写入失败");
				}
				else{
					ConvertString = editText.toString();
					Log.e(TAG, "ConvertString = " + ConvertString);

					Log.e(TAG, "ConvertString=" + ConvertString.length());
					ConvertString = addZeroForNum(ConvertString, 32);

					byte[] data = ConvertString.substring(0,16).getBytes();
					boolean writeflag = M1CardUtils.writeBlock(mTagWrite, 4, data);

					data = ConvertString.substring(16,32).getBytes();
					writeflag = M1CardUtils.writeBlock(mTagWrite, 5, data);

					if(writeflag){
						Toast.makeText(getApplicationContext(),
								"\u5199\u5165\u6210\u529f",Toast.LENGTH_LONG).show();
						WritetextView.setText("写入成功");
					}
					//String HexString = bytesToHexString(data);
					//Log.e(TAG, "HexString.getBytes() = " + HexString);
					//Log.e(TAG, "HexString length =" + HexString.length());

					//String NormalString = hexStringToString(HexString);
					//Log.e(TAG, "NormalString length =" + NormalString.length());
					//Log.e(TAG, "NormalString = " + NormalString);
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void onButtonClick(View view) {
		switch (view.getId()) {
			case R.id.btn_write_m1:
				mWriteInDialog.show();
				break;
			case R.id.btn_write_back:
				finish();
				break;
			default:
				break;
		}
	}
/*
    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        mNfcAdapterWrite = M1CardUtils.isNfcAble(this);
        M1CardUtils.setPendingIntent(PendingIntent.getActivity(this, 0, new Intent(this,
                getClass()), 0));
        Log.e("onNewIntent","onNewIntent");
        mTagWrite = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
    }
*/

	public static String hexStringToString(String hexStr) {
		String str = "0123456789ABCDEF";
		char[] hexs = hexStr.toCharArray();
		byte[] bytes = new byte[hexStr.length() / 2];
		int n;
		for (int i = 0; i < bytes.length; i++) {
			n = str.indexOf(hexs[2 * i]) * 16;
			n += str.indexOf(hexs[2 * i + 1]);
			bytes[i] = (byte) (n & 0xff);
		}
		return new String(bytes);
	}

    private static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        char[] buffer = new char[2];
        for (int i = 0; i < src.length; i++) {
            buffer[0] = Character.forDigit((src[i] >>> 4) & 0x0F, 16);
            buffer[1] = Character.forDigit(src[i] & 0x0F, 16);
            System.out.println(buffer);
            stringBuilder.append(buffer);
        }
        return stringBuilder.toString();
    }

	public static String addZeroForNum(String str, int strLength) {
		int strLen = str.length();
		if (strLen < strLength) {
			while (strLen < strLength) {
			StringBuffer sb = new StringBuffer();
			//sb.append("0").append(str);//\u5de6\u88650
			sb.append(str).append("0");//\u53f3\u88650
			str = sb.toString();
			strLen = str.length();
			}
		}
		return str;
	}

    @Override
    public void onPause() {
        super.onPause();
        if (mNfcAdapterWrite != null) {
            mNfcAdapterWrite.disableForegroundDispatch(this);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mNfcAdapterWrite != null) {
            mNfcAdapterWrite.enableForegroundDispatch(this, M1CardUtils.getPendingIntent(),
                    null, null);
        }
    }

}


