package shanksi.phototransfer;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileOutputStream;


public class CopyFileOperation extends AsyncTask<FileInfoExtractor, Void, FileInfoExtractor> {
    private final NtlmPasswordAuthentication mAuth;
    private final String mPathRoot;
    private final String mPathFormat;
    private final String mDirectoryTitle;

    public CopyFileOperation(NtlmPasswordAuthentication auth, String pathRoot, String pathFormat, String directoryTitle) {
        super();
        this.mAuth = auth;
        this.mPathRoot = pathRoot;
        this.mPathFormat = pathFormat;
        this.mDirectoryTitle = directoryTitle;
    }

    @Override
    protected FileInfoExtractor doInBackground(FileInfoExtractor... params) {
        try {
            jcifs.Config.registerSmbURLHandler();

            FileInfoExtractor info = params[0];

            String newPath = new SimpleDateFormat(mPathFormat, Locale.ENGLISH).format(info.getDate());

            String fullPath = "smb:" + mPathRoot + "/" + newPath;
            if (mDirectoryTitle.length() > 0) {
                fullPath += " " + mDirectoryTitle;
            }
            fullPath += "/";

            SmbFile dir = new SmbFile(fullPath, mAuth);
            if (!dir.exists()) {
                Log.w("Not Exists", fullPath + " does not exist");
                dir.mkdirs();
            }

            File fileSource = info.getFile();
            String filePath = fullPath + fileSource.getName();

            SmbFile smbFileTarget = new SmbFile(filePath, mAuth);

            // input and output stream
            // writing data
            try (FileInputStream fis = new FileInputStream(fileSource); SmbFileOutputStream smbfos = new SmbFileOutputStream(smbFileTarget)) {
                // 16 kb
                final byte[] b = new byte[16 * 1024];
                int read = 0;
                while ((read = fis.read(b, 0, b.length)) > 0) {
                    smbfos.write(b, 0, read);
                }
            }
            smbFileTarget.setLastModified(info.getDate().getTime());
            // fileSource.delete();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (SmbException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return params[0];
    }

    @Override
    protected void onPostExecute(FileInfoExtractor uri) {
        // Update Ui here
        //
        //
        uri.setStatus(FileInfoExtractor.FileStatus.COPIED);
    }
}