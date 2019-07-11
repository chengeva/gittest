package co.acaia.acaiaupdater.ndkhelper;

import co.acaia.acaiaupdater.rawfile.RawFileHelper;

public class acaiaFirmwareNDKHelper {

	public static void test_hello() {
		testhelloisp();
		
	}
	
	

	private native static void testfileread(String file_path);
	private native static void testhelloisp();
	
	static {
		System.loadLibrary("scalecomm-isp");
	}
}
