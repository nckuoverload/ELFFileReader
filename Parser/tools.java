package Parser;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Map.Entry;
/**
 * 此類別主要用來寫一些小工具。<br>
 * */
public class tools {
	/**
	 * 將ascii轉成十六進制用的陣列。
	 * */
	private final static char[] hexArray = "0123456789ABCDEF".toCharArray();
	
	/**
	 * @param bytes 輸入為一10進位的byte陣列，因java讀進byte後會存成10進位。
	 * @return 輸出該陣列代表的16進位字串。
	 * */
	public static String bytesToHex(byte[] bytes) {
	    char[] hexChars = new char[bytes.length * 2];
	    for ( int j = 0; j < bytes.length; j++ ) {
	        int v = bytes[j] & 0xFF;
	        hexChars[j * 2] = hexArray[v >>> 4];
	        hexChars[j * 2 + 1] = hexArray[v & 0x0F];
	    }
	    return new String(hexChars);
	}
	
	/**
	 * 用來讀檔案，將檔案以byte的形式讀近來再針對溢位做處理，最後用Integer陣列來儲存。
	 * @param file 要處理之檔案。
	 * @return 返回該檔案之資料流。
	 * @throws IOException 讀檔案可能會產生之I/O例外。
	 * */
    public static int[] readFile(File file) throws IOException {
        // Open file
        RandomAccessFile f = new RandomAccessFile(file, "r");
        try {
            // Get and check length
            long longlength = f.length();
            int length = (int) longlength;
            if (length != longlength)
                throw new IOException("File size >= 2 GB");
            // Read file and return data
            int[] res = new int[length];
            for (int i = 0 ; i < length ; i ++ ) {
            	int ttt = f.readByte();
            	if ( ttt < 0 ) {
            		ttt += 0x100;
            	}
            	res[i] = ttt;
            	//res[i] = Integer.valueOf(String.valueOf(ttt), 16);
            }
            return res;
        } finally {
            f.close();
        }
    }
    
    
    /**
     * 主要是用來處理little endian的部分。
     * @param arr 此陣列為int[]形式，為資料流中的一部份，會將該部分轉為十進制。
     * @return 回傳該資料流所代表的十進制意義。
     * */
    public static int hexToInt(int[] arr) {
    	int len = arr.length;
    	int ans = 0;
    	for(int i = 0 ; i < len ; i ++) {
    		ans += arr[i]*(Math.pow(256,i));
    	}
    	return ans;
    }
       
    /**
     * 將十進至轉成可識讀的ASCII字串。
     * @param arr 要轉換的資料流。
     * @return 將每個陣列的內容轉為ASCII後回傳。
     * */
    public static String[] IntToASCII(int[] arr) {
    	String[] res = new String[arr.length];
    	for(int i = 0 ; i < arr.length ; i ++) {
    		res[i] = (char) arr[i] +"";
    	}
    	return res;
    }
    
    /**
     * 將整份資料流{@link Parser.ElfParser#dataArray}從讀進來後會以十進位的形式儲存。<br>
     * 此方法會將整份資料流改以十六進位的方式以字串陣列儲存，主要是用來處理GUI介面中左半面板部分。
     * @param elf 要處理之elf物件。
     * @return 回傳該物件的資料流，以十六進位的方式。
     * */
    public static String[] IntToHex(ElfParser elf) {
    	String[] res = new String[elf.dataArray.length];
		for(int i = 0 ; i < elf.dataArray.length; i++) {
			String temp = Integer.toHexString(elf.dataArray[i]);
			if (temp.length() < 2) {
				temp = "0"+temp;
			}
			res[i] = temp;
		}
		return res;
    }
    
    /**
     * 用來處理table的部分。
     * @param eLFHeaderInfo 輸入hashtable
     * @return 返回一個二元陣列。
     * */
    public static<T,Q> String[][] toArray (HashMap<String, String> eLFHeaderInfo) {
        if(eLFHeaderInfo == null) {
            return null;
        }
        String[][] result = new String[eLFHeaderInfo.size()][];
        int index = 0;
        for(Entry<String, String> e : eLFHeaderInfo.entrySet()) {
            result[index++] = new String[] {e.getKey(),e.getValue()};
        }
        return result;
    }
    
    /**
     * 用來實踐GUI中save按鍵
     * @param path 要儲存的路徑
     * @param ELF 要儲存的ELF物件
     * */
    public static void saveAsByte(String path,ElfParser ELF) {
    	FileOutputStream fos = null;
    	try {
			fos = new FileOutputStream(path);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
    	byte[] output = new byte[ELF.dataArray.length];
    	for( int i = 0 ; i < ELF.dataArray.length ; i ++) {
    		output[i] = (byte)(ELF.dataArray[i]);
    	}
    	try {
			fos.write(output);
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    /**
     * 型別轉換
     * 將Integer轉至byte陣列。
     * @param value 要轉換的Integer
     * @return 回傳對應的byte陣列。
     * */
    public static final byte[] intToByteArray(int value) {
        return new byte[] {
                (byte)(value >>> 24),
                (byte)(value >>> 16),
                (byte)(value >>> 8),
                (byte)value};
    }
    
    
    /**
     * 在資料流中尋找字串。
     * @param taget 要尋找的字串。
     * @param data 被尋找的資料流。
     * @return 回傳在整份資料流中的offset位置。
     * */
    public static final int findString(String taget,int[] data) {
    	int[] tagetIntArr = StringToASCII(taget);
    	int res = -1;
    	for (int i = 0 ; i < data.length ; i ++) {
    		if(data[i] == tagetIntArr[0]) {
    			int[] temp = Arrays.copyOfRange(data,i,i+tagetIntArr.length);
    			if(Arrays.equals(temp, tagetIntArr)) {
    				res = i ;
    				break;
    			}
    		}
    	}
    	return res;
    }
    
    /**
     * 將字串轉乘ASCII編碼。
     * @param t 要轉換的字串
     * @return 回傳int[]為轉換結果。
     * */
    public static final int[] StringToASCII(String t) {
    	char[] tagetArr = t.toCharArray();
    	int[] tagetIntArr = new int[tagetArr.length];
    	for(int i = 0 ; i < tagetArr.length ; i ++) {
    		tagetIntArr[i] = (int)tagetArr[i];
    	}
    	return tagetIntArr;
    }
    
    /**
     * 用來實踐找多個字串的情境，可以與{@link tools#findString(String, int[])}合併。
     * @param taget 要尋找的字串。
     * @param data 被尋找的資料流。
     * @return 搜尋結果可能不只一個。
     * */
    public static final ArrayList<Integer> findMultiString(String taget,int[] data) {
    	int[] tagetIntArr = StringToASCII(taget);
    	ArrayList<Integer> res = new ArrayList<Integer>();
    	for (int i = 0 ; i < data.length ; i ++) {
    		if(data[i] == tagetIntArr[0]) {
    			int[] temp = Arrays.copyOfRange(data,i,i+tagetIntArr.length);
    			if(Arrays.equals(temp, tagetIntArr)) {
    				res.add(i);
    			}
    		}
    	}
    	return res;
    }



}
