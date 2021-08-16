package com.tamrd.spearsdkexample.helper;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import com.thinkamove.spearnative.helper.LogHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class AssetsHelper {
    private static final String TAG = AssetsHelper.class.getSimpleName();
    public static final String SPEAR_ASR_DOMAIN = "com.think-a-move.spear";

    public static synchronized File InitLMPackage(Context context ) throws IOException {

        String destination = context.getFilesDir().getAbsolutePath() + "/" + SPEAR_ASR_DOMAIN ;
        File destination_dir= new File(destination);
        if (destination_dir.exists()) {
            deleteRecursive(destination_dir);
        }

        return copyAssets(context, SPEAR_ASR_DOMAIN , destination);
    }

    /**
     * Recursively copies asset files stored in .apk to the provided destination path.
     *
     * @param context Application context.
     * @param assetsPath  Relative path to asset file or directory.
     * @param destinationPath Destination path of the directory or a file where assets files should
     *                       be copied.
     **/
    public static File copyAssets(Context context, String assetsPath, String destinationPath) throws IOException {
        File destinationFile = new File(destinationPath);
        AssetManager assets = context.getAssets();
        String[] content = assets.list(assetsPath);

        if (content != null && content.length > 0) {
            for (String item : content) {
                copyAssets(context, new File(assetsPath, item).getPath(), new File(destinationPath, item).getPath());
            }
        } else {
            Log.i(TAG, "copy " + assetsPath + " to " + destinationPath);
            destinationFile.getParentFile().mkdirs();
            copyStream(assets.open(assetsPath), new FileOutputStream(destinationFile));
        }

        return destinationFile;
    }

    private static void deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                deleteRecursive(child);

        fileOrDirectory.delete();
    }

    private static void copyStream(InputStream source, OutputStream dest) throws IOException {
        byte[] buffer = new byte[1024];
        int nread;

        try {
            while ((nread = source.read(buffer)) != -1) {
                if (nread == 0) {
                    nread = source.read();
                    if (nread < 0)
                        break;

                    dest.write(nread);
                    continue;
                }

                dest.write(buffer, 0, nread);
            }
        } finally {
            if (dest != null) {
                dest.close();
            }

            if (source != null) {
                source.close();
            }
        }
    }

}
