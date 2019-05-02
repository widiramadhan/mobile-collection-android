package sfi.mobile.collection.util;
import android.graphics.Bitmap;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class FileUtilSS {
    private static FileUtilSS mInstance;
    private FileUtilSS() {
    }

    public static FileUtilSS getInstance() {
        if (mInstance == null) {
            synchronized (FileUtilSS.class) {
                if (mInstance == null) {
                    mInstance = new FileUtilSS();
                }
            }
        }
        return mInstance;
    }

}
