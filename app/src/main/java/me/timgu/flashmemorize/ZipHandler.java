package me.timgu.flashmemorize;

import android.content.Context;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipHandler {
    //this code is modified from https://stackoverflow.com/questions/9324103/download-and-extract-zip-file-in-android

    private InputStream _inputStream;
    private String _location;
    private Context _context;

    public ZipHandler(InputStream inputStream , String location, Context context) {
        _inputStream = inputStream;
        _location = location;
        _context = context;

        _dirChecker("");
    }

    public void unzip() {
        try  {
            InputStream fin = _inputStream;
            ZipInputStream zin = new ZipInputStream(fin);
            File root = new File(_context.getFilesDir(), _location);
            byte b[] = new byte[1024];

            ZipEntry ze;
            ze = null;
            while ((ze = zin.getNextEntry()) != null) {
                Log.v("ZipHandler", "Unzipping " + ze.getName());

                if(ze.isDirectory()) {
                    _dirChecker(ze.getName());
                } else {
                    File fze = new File(ze.getName());
                    File file = new File(_context.getCacheDir(),fze.getName());
                    FileOutputStream fout = new FileOutputStream(file);

                    BufferedInputStream in = new BufferedInputStream(zin);
                    BufferedOutputStream out = new BufferedOutputStream(fout);

                    int n;
                    while ((n = in.read(b,0,1024)) >= 0) {
                        out.write(b,0,n);
                    }

                    zin.closeEntry();
                    out.close();
                }

            }
            zin.close();
        } catch(Exception e) {
            Log.e("ZipHandler", "unzip", e);
        }

    }

    private void _dirChecker(String dir) {
        File f = new File(_location + dir);

        if(!f.isDirectory()) {
            f.mkdirs();
        }
    }}