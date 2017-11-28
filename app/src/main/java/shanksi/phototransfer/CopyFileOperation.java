package shanksi.phototransfer;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
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


public class CopyFileOperation extends AsyncTask<Uri, Void, Uri> {
    private final Context mContext;
    private final NtlmPasswordAuthentication mAuth;
    private final String mPathRoot;
    private final String mDirectoryTitle;

    public CopyFileOperation(Context context, NtlmPasswordAuthentication auth, String pathRoot, String directoryTitle) {
        super();
        this.mContext = context;
        this.mAuth = auth;
        this.mPathRoot = pathRoot;
        this.mDirectoryTitle = directoryTitle;
    }

    @Override
    protected Uri doInBackground(Uri... params) {
        try {
            jcifs.Config.registerSmbURLHandler();

            Uri fileUri = params[0];
            FileInfoExtractor info = new FileInfoExtractor(fileUri, mContext);


            // local source file and target smb file
            //String[] filePathColumn = {MediaStore.MediaColumns.DATA};
            //Cursor cursor = mContext.getContentResolver().query(fileUri, filePathColumn, null, null, null);
            //cursor.moveToFirst();

            //int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            //Date dt = new Date();
            //File fileSource = new File(cursor.getString(columnIndex));
            //ExifInterface exif = null;
            //try {
            //    exif = new ExifInterface(cursor.getString(columnIndex));
            //    String dtString = exif.getAttribute(ExifInterface.TAG_DATETIME);
            //    dt = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss", Locale.ENGLISH).parse(dtString);
            //} catch (ParseException e) {
            //    e.printStackTrace();
            //}
            //cursor.close();

            File fileSource = info.getFile();

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
            String pathFormat = prefs.getString("path", "default");
            String newPath = new SimpleDateFormat(pathFormat, Locale.ENGLISH).format(info.getDate());

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

            String filePath = fullPath + fileSource.getName();

            SmbFile smbFileTarget = new SmbFile(filePath, mAuth);
            // input and output stream
            FileInputStream fis = new FileInputStream(fileSource);
            SmbFileOutputStream smbfos = new SmbFileOutputStream(smbFileTarget);
            // writing data
            try {
                // 16 kb
                final byte[] b = new byte[16 * 1024];
                int read = 0;
                while ((read = fis.read(b, 0, b.length)) > 0) {
                    smbfos.write(b, 0, read);
                }
            } finally {
                fis.close();
                smbfos.close();
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
    protected void onPostExecute(Uri uri) {
        // Update Ui here
        //
        //
    }
}