package co.acaia.acaiaupdater.filehelper;

import android.content.Context;

import java.io.File;

/**
 * Created by hanjord on 15/5/5.
 */
public interface FileRetriever {
    public void retrieve_firmware_files(Context context);
    public boolean validate_file(File firmwareFile);
}
