package shanksi.phototransfer;

import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
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
        FileInfoExtractor info = params[0];

        try {
            // jcifs.Config.registerSmbURLHandler();

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
                int read;
                while ((read = fis.read(b, 0, b.length)) > 0) {
                    smbfos.write(b, 0, read);
                }
            }
            smbFileTarget.setLastModified(info.getDate().getTime());
            info.setStatus(FileInfoExtractor.FileStatus.COPIED);
            // fileSource.delete();
        } catch (IOException e) {
            e.printStackTrace();
            info.setStatus(FileInfoExtractor.FileStatus.ERROR);
            info.setMessage(e.getMessage());
        }
        return params[0];
    }

    @Override
    protected void onPostExecute(FileInfoExtractor uri) {
        // Update Ui here
        uri.informListeners();
    }
}