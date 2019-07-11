package co.acaia.acaiaupdater.rawfile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

public class RawFileHelper {
	/*
	 * Temp copy to external
	 */

	private Context m_context;
	private String m_sdcard;
	public static final String m_configdir = "/acaia_updater_tool";

	public RawFileHelper(Context context) {
		m_context = context;
	}

    public String saveByteToFile(byte [] data, String filename){
        try {
			FileOutputStream fos = m_context.openFileOutput(filename,Context.MODE_PRIVATE);
            fos.write(data);
            fos.close();
            return filename;
        }catch (Exception e){
            e.printStackTrace();
        }
        return "";
    }


    public static String getFilePath(int resId,Context m_context){
        File file=new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                + m_configdir, m_context.getResources().getResourceEntryName(resId));
        return file.getAbsolutePath();
    }

}
