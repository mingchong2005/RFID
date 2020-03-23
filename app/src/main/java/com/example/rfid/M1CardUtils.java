package com.example.rfid;

import android.app.Activity;
import android.app.PendingIntent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.nfc.tech.MifareClassic;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;

/**
 * @author kuan
 * Created on 2019/2/26.
 * @description MifareClassic卡片读写工具类
 */
public class M1CardUtils {

    private static byte myKeyA[] = { (byte) 0x00, (byte) 0x79, (byte) 0x96, (byte) 0x81,
		(byte) 0x55, (byte) 0x35 };
    private static byte myKeyB[] = { (byte) 0x00, (byte) 0x79, (byte) 0x96, (byte) 0x81,
		(byte) 0x55, (byte) 0x35 };
//   08778F69
    private static byte myPermission[] = { (byte) 0x080, (byte) 0x77, (byte) 0x8f, (byte) 0x69};

    private static byte myPermission_1[] = { 
											(byte) 0x00, (byte) 0x79, (byte) 0x96, (byte) 0x81, (byte) 0x55, (byte) 0x35, //KeyA
											(byte) 0x08, (byte) 0x77, (byte) 0x8f, (byte) 0x69, //Premission
											(byte) 0x00, (byte) 0x79, (byte) 0x96, (byte) 0x81,	(byte) 0x55, (byte) 0x35  //KeyB
										 };

    private static PendingIntent pendingIntent;
    public static PendingIntent getPendingIntent(){
        return pendingIntent;
    }

    public static void setPendingIntent(PendingIntent pendingIntent){
        M1CardUtils.pendingIntent = pendingIntent;
    }

    /**
     * 判断是否支持NFC
     * @return
     */
    public static NfcAdapter isNfcAble(Activity mContext){
        NfcAdapter mNfcAdapter = NfcAdapter.getDefaultAdapter(mContext);
        if (mNfcAdapter == null) {
            Toast.makeText(mContext, "设备不支持NFC！", Toast.LENGTH_LONG).show();
        }else {
            if (!mNfcAdapter.isEnabled()) {
                Toast.makeText(mContext, "请在系统设置中先启用NFC功能！", Toast.LENGTH_LONG).show();
            }
        }
        return mNfcAdapter;
    }

    /**
     * 监测是否支持cardType类型卡
     * @param tag
     * @param activity
     * @param cardType
     * @return
     */
    public static boolean hasCardType(Tag tag,Activity activity,String cardType){

        if (tag == null){
            //Toast.makeText(activity,"请贴卡",Toast.LENGTH_LONG).show();
            return false;
        }

        String[] techList = tag.getTechList();

        boolean hasCardType = false;
        for (String tech : techList) {
            Log.e("TagTech",tech);
            if (tech.contains(cardType)) {
                hasCardType = true;
                break;
            }
        }

        if (!hasCardType) {
            Toast.makeText(activity, "不支持"+cardType+"卡", Toast.LENGTH_LONG).show();
        }

        return hasCardType;
    }

    /**
     * CPU卡信息读取
     * @param tag
     * @return
     * @throws IOException
     */
    public static String readIsoCard(Tag tag) throws IOException {
        IsoDep isoDep = IsoDep.get(tag);
        if (!isoDep.isConnected()){
            isoDep.connect();
        }

        String result = StringUtil.bytesToHexString(isoDep.transceive(StringUtil.hex2Bytes("00A40400023F00")));
        Log.e("readIsoCard",result);
        result = StringUtil.bytesToHexString(isoDep.transceive(StringUtil.hex2Bytes("00B0950030")));
        Log.e("readIsoCard",result);
        isoDep.close();
        return result;
    }

    /**
     * M1读取卡片信息
     * @return
     */
    public static String[][] readCard(Tag tag)  throws IOException{
        MifareClassic mifareClassic = MifareClassic.get(tag);
        try {
            mifareClassic.connect();
            String[][] metaInfo = new String[16][4];
            // 获取TAG中包含的扇区数
            int sectorCount = mifareClassic.getSectorCount();
            for (int j = 0; j < sectorCount; j++) {
                int bCount;//当前扇区的块数
                int bIndex;//当前扇区第一块
                if (m1Auth(mifareClassic,j)) {
                    bCount = mifareClassic.getBlockCountInSector(j);
                    bIndex = mifareClassic.sectorToBlock(j);
                    for (int i = 0; i < bCount; i++) {
                        byte[] data = mifareClassic.readBlock(bIndex);
                        String dataString = bytesToHexString(data);
                        metaInfo[j][i] = dataString;
                        //Log.e("获取到信息",dataString);
                        bIndex++;
                    }
                } else {
                    Log.e("readCard","密码校验失败");
					return null;
                }
            }
            return metaInfo;
        } catch (IOException e){
            throw new IOException(e);
        } finally {
            try {
                mifareClassic.close();
            }catch (IOException e){
                throw new IOException(e);
            }
        }
    }

    public static String readCardOneBlock(Tag tag, int block)  throws IOException{
        MifareClassic mifareClassic = MifareClassic.get(tag);
        try {
            mifareClassic.connect();
			String metaInfo = "";
            // 获取TAG中包含的扇区数
            int sectorCount = mifareClassic.getSectorCount();
            //for (int j = 0; j < sectorCount; j++) {
                //int bCount;//当前扇区的块数
                //int bIndex;//当前扇区第一块
                if (m1Auth(mifareClassic,block/4)) {
                    //bCount = mifareClassic.getBlockCountInSector(sectorIndex);
                    //bIndex = mifareClassic.sectorToBlock(sectorIndex);
                    //for (int i = 0; i < bCount; i++) {
                        byte[] data = mifareClassic.readBlock(block);
                        String dataString = bytesToHexString(data);
                        metaInfo += dataString;
                        //Log.e("获取到信息",dataString);
                        //bIndex++;
                    //}
                } else {
                    Log.e("readCardOneBlock","密码校验失败");
					return null;
                }
            //}
            return metaInfo;
        } catch (IOException e){
            throw new IOException(e);
        } finally {
            try {
                mifareClassic.close();
            }catch (IOException e){
                throw new IOException(e);
            }
        }
    }

    public static String readCardOneSector(Tag tag, int sectorIndex)  throws IOException{
        MifareClassic mifareClassic = MifareClassic.get(tag);
        try {
            mifareClassic.connect();
			String metaInfo = "";
            // 获取TAG中包含的扇区数
            int sectorCount = mifareClassic.getSectorCount();
            //for (int j = 0; j < sectorCount; j++) {
                int bCount;//当前扇区的块数
                int bIndex;//当前扇区第一块
                if (m1Auth(mifareClassic,sectorIndex)) {
                    bCount = mifareClassic.getBlockCountInSector(sectorIndex);
                    bIndex = mifareClassic.sectorToBlock(sectorIndex);
                    for (int i = 0; i < bCount; i++) {
                        byte[] data = mifareClassic.readBlock(bIndex);
                        String dataString = bytesToHexString(data);
                        metaInfo += dataString;
                        //Log.e("获取到信息",dataString);
                        bIndex++;
                    }
                } else {
                    Log.e("readCardOneBlock","密码校验失败");
					return null;
                }
            //}
            return metaInfo;
        } catch (IOException e){
            throw new IOException(e);
        } finally {
            try {
                mifareClassic.close();
            }catch (IOException e){
                throw new IOException(e);
            }
        }
    }

    /**
     * 改写数据
     * @param block
     * @param blockbyte
     */
    public static boolean writeBlock(Tag tag, int block, byte[] blockbyte) throws IOException {
        MifareClassic mifareClassic = MifareClassic.get(tag);
        try {
            mifareClassic.connect();
			int sectorIndex = block/4;
			//Log.e("writeBlock","sectorIndex" + sectorIndex);
            if ((m1Auth(mifareClassic,block/4))&&(m1AuthKeyB(mifareClassic,block/4))) {
                mifareClassic.writeBlock(block, blockbyte);
                mifareClassic.writeBlock(sectorIndex * 4 + 3, myPermission_1);
                //Log.e("writeBlock","permission block" + (sectorIndex * 4 + 3));
            } else {
                Log.e("密码是", "没有找到密码");
                return false;
            }
        } catch (IOException e){
            throw new IOException(e);
        } finally {
            try {
                mifareClassic.close();
            }catch (IOException e){
                throw new IOException(e);
            }
        }
        return true;

    }

    public static String readCardInfo(Tag tag)  throws IOException{
	{
	        MifareClassic mifareClassicInfo = MifareClassic.get(tag);
	        try {
	            mifareClassicInfo.connect();
				String CardId =ByteArrayToHexString(tag.getId());
				String CardInfo = "";
				CardInfo+="卡片ID:"+CardId;
				for (String tech : tag.getTechList()) {
					System.out.println(tech);
				}
	            // 获取TAG中包含的扇区数
				int type = mifareClassicInfo.getType();//获取TAG的类型
				int sectorCount = mifareClassicInfo.getSectorCount();//获取TAG中包含的扇区数
				String typeS = "";
				switch (type) {
					case MifareClassic.TYPE_CLASSIC:
						typeS = "TYPE_CLASSIC";
						break;
					case MifareClassic.TYPE_PLUS:
						typeS = "TYPE_PLUS";
						break;
					case MifareClassic.TYPE_PRO:
						typeS = "TYPE_PRO";
						break;
					case MifareClassic.TYPE_UNKNOWN:
						typeS = "TYPE_UNKNOWN";
						break;
				}

				CardInfo += "\n卡片类型：" + typeS + "\n共" + sectorCount + "个扇区\n共"
						+ mifareClassicInfo.getBlockCount() + "个块\n存储空间: " + mifareClassicInfo.getSize() + "B\n";
			
				return CardInfo;

	        } catch (IOException e){
	            throw new IOException(e);
	        } finally {
	            try {
	                mifareClassicInfo.close();
	            }catch (IOException e){
	                throw new IOException(e);
	            }
	        }
	    }
    }


    public static boolean JudgeIllegalCard(Tag tag)  throws IOException{
	{
	        MifareClassic mifareClassicInfo = MifareClassic.get(tag);
	        try {
	            mifareClassicInfo.connect();
				
                if (m1AuthKeyDefault(mifareClassicInfo, 1)) {
					return false;
                }else if(m1AuthKeyForum(mifareClassicInfo, 1)){
					return false;
                }else if(m1AuthKeyA(mifareClassicInfo, 1)){
					return true;
                } else {
                    Log.e("JudgeIllegalCard","密码校验失败");
					return false;
                }

	        } catch (IOException e){
	            throw new IOException(e);
	        } finally {
	            try {
	                mifareClassicInfo.close();
	            }catch (IOException e){
	                throw new IOException(e);
	            }
	        }
	    }
    }	

    public static boolean JudgeBlankCard(Tag tag)  throws IOException{
	{
	        MifareClassic mifareClassicInfo = MifareClassic.get(tag);
	        try {
	            mifareClassicInfo.connect();
				
                if (m1AuthKeyDefault(mifareClassicInfo, 1)) {
					return true;
                }else if(m1AuthKeyForum(mifareClassicInfo, 1)){
					return true;
                }else if(m1AuthKeyA(mifareClassicInfo, 1)){
					return false;
                } else {
                    Log.e("JudgeBlankCard","密码校验失败");
					return false;
                }

	        } catch (IOException e){
	            throw new IOException(e);
	        } finally {
	            try {
	                mifareClassicInfo.close();
	            }catch (IOException e){
	                throw new IOException(e);
	            }
	        }
	    }
    }	

	/**
	 * 密码校验
	 * @param mTag
	 * @param position
	 * @return
	 * @throws IOException
	 */
	public static boolean m1AuthKeyDefault(MifareClassic mTag,int sectorIndex) throws IOException {
		boolean auth = false;  
		try {
			auth = mTag.authenticateSectorWithKeyA(sectorIndex,  
					MifareClassic.KEY_DEFAULT);
		} catch (IOException e) {
			Log.e("m1AuthKeyDefault", "IOException while authenticateSectorWithKey MifareClassic...", e);
		}
		return auth;
	}

	public static boolean m1AuthKeyForum(MifareClassic mTag,int sectorIndex) throws IOException {
		boolean auth = false;  
		try {
			auth = mTag.authenticateSectorWithKeyA(sectorIndex,  
					MifareClassic.KEY_NFC_FORUM); 
		} catch (IOException e) {
			Log.e("m1AuthKeyForum", "IOException while authenticateSectorWithKey MifareClassic...", e);
		}
		return auth;
	}

	public static boolean m1AuthKeyA(MifareClassic mTag,int sectorIndex) throws IOException {
		boolean auth = false;  
		try {
			auth = mTag.authenticateSectorWithKeyA(sectorIndex,  
					myKeyA); 
		} catch (IOException e) {
			Log.e("m1AuthKeyForum", "IOException while authenticateSectorWithKey MifareClassic...", e);
		}
		return auth;
	}
    /**
     * 密码校验
     * @param mTag
     * @param position
     * @return
     * @throws IOException
     */
    public static boolean m1Auth(MifareClassic mTag,int sectorIndex) throws IOException {
        boolean auth = false;  
        try {
            auth = mTag.authenticateSectorWithKeyA(sectorIndex,  
                    MifareClassic.KEY_DEFAULT);
            if(!auth){
                auth = mTag.authenticateSectorWithKeyA(sectorIndex,  
                        myKeyA); 
            }
            if(!auth){
                auth = mTag.authenticateSectorWithKeyA(sectorIndex,  
                        MifareClassic.KEY_NFC_FORUM); 
            }
        } catch (IOException e) {
            Log.e("m1Auth", "IOException while authenticateSectorWithKey MifareClassic...", e);
        }
        return auth;
/*
        if (mTag.authenticateSectorWithKeyA(position, MifareClassic.KEY_DEFAULT)) {
            return true;
        } else if (mTag.authenticateSectorWithKeyB(position, MifareClassic.KEY_DEFAULT)) {
            return true;
        } else if (mTag.authenticateSectorWithKeyB(position, M1CardUtils.KEY_A)) {
            return true;
        } else if (mTag.authenticateSectorWithKeyB(position, M1CardUtils.KEY_B)) {
            return true;
        }
        return false;
*/
    }

		public static boolean m1AuthKeyB(MifareClassic mTag,int sectorIndex) throws IOException {
			boolean auth = false;  
			try {
				auth = mTag.authenticateSectorWithKeyB(sectorIndex,  
						MifareClassic.KEY_DEFAULT);
				if(!auth){
					auth = mTag.authenticateSectorWithKeyB(sectorIndex,  
							myKeyB); 
				}
				if(!auth){
					auth = mTag.authenticateSectorWithKeyB(sectorIndex,  
							MifareClassic.KEY_NFC_FORUM); 
				}
			} catch (IOException e) {
				Log.e("m1Auth", "IOException while authenticateSectorWithKey MifareClassic...", e);
			}
			return auth;
	/*
			if (mTag.authenticateSectorWithKeyA(position, MifareClassic.KEY_DEFAULT)) {
				return true;
			} else if (mTag.authenticateSectorWithKeyB(position, MifareClassic.KEY_DEFAULT)) {
				return true;
			} else if (mTag.authenticateSectorWithKeyB(position, M1CardUtils.KEY_A)) {
				return true;
			} else if (mTag.authenticateSectorWithKeyB(position, M1CardUtils.KEY_B)) {
				return true;
			}
			return false;
	*/
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

	private static String ByteArrayToHexString(byte[] inarray) {
		    int i, j, in;
		    String[] hex = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A",
		        "B", "C", "D", "E", "F" };
		    String out = "";
		    for (j = 0; j < inarray.length; ++j) {
		      in = (int) inarray[j] & 0xff;
		      i = (in >> 4) & 0x0f;
		      out += hex[i];
		      i = in & 0x0f;
		      out += hex[i];
		    }
		    return out;
		  }

}
