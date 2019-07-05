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
	public static final String m_configdir = "/acaia_firmware_tool";

	public static String get_root_dir(){
		return Environment.getExternalStorageDirectory().getAbsolutePath()+m_configdir;
	}
	public RawFileHelper(Context context) {
		m_context = context;
		m_sdcard = Environment.getExternalStorageDirectory().getAbsolutePath();
		File cfgdir = new File(m_sdcard + m_configdir);
		if (!cfgdir.exists()) {
			cfgdir.mkdirs();
		}
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

	public void copy_to_external(int resID) {
		copyResources(resID);
	}

    public static String getFilePath(int resId,Context m_context){
        File file=new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                + m_configdir, m_context.getResources().getResourceEntryName(resId));
        return file.getAbsolutePath();
    }
    public File getCopiedFilePath(int resId){
        String filename = m_context.getResources().getResourceEntryName(resId);
       return  new File(m_sdcard
                + m_configdir, filename);

    }
	public void copyResources(int resId) {
		Log.i("Test", "Setup::copyResources");
		InputStream in = m_context.getResources().openRawResource(resId);
		String filename = m_context.getResources().getResourceEntryName(resId);

		File f = new File(filename);

		if (!f.exists()) {
			try {
				OutputStream out = new FileOutputStream(new File(m_sdcard
						+ m_configdir, filename));
				byte[] buffer = new byte[1024];
				int len;
				while ((len = in.read(buffer, 0, buffer.length)) != -1) {
					out.write(buffer, 0, len);
				}
				in.close();
				out.close();
			} catch (FileNotFoundException e) {
				Log.i("Test", "Setup::copyResources - " + e.getMessage());
			} catch (IOException e) {
				Log.i("Test", "Setup::copyResources - " + e.getMessage());
			}
		}
	}
}
