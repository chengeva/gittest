package co.acaia.firmwaretool.filehelper;

import android.content.Context;

import java.io.File;

/**
 * Created by hanjord on 15/5/5.
 */
public class LocalFileRetriever implements  FileRetriever {
    @Override
    public void retrieve_firmware_files(Context context) {

    }

    @Override
    public boolean validate_file(File firmwareFile) {
        return false;
    }
}
