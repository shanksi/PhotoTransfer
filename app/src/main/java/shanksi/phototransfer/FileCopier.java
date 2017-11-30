package shanksi.phototransfer;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;

import java.util.ArrayList;

import jcifs.smb.NtlmPasswordAuthentication;

public class FileCopier {
    private final Context mContext;

    public FileCopier(Context context) {
        super();
        this.mContext = context;
    }

    protected ArrayList<FileInfoExtractor> CopyFiles(ArrayList<FileInfoExtractor> uriArrayList, String directoryTitle) {
        try {
            jcifs.Config.registerSmbURLHandler();
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);

            String user = prefs.getString("user_name", "");
            String pass = prefs.getString("password", "");
            String pathRoot = prefs.getString("root", "default");
            String pathFormat = prefs.getString("path", "default");

            NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication("", user, pass);

            for (int i = 0; i < uriArrayList.size(); ++i) {

                try {
                    FileInfoExtractor fileUri = uriArrayList.get(i);
                    fileUri.setStatus(FileInfoExtractor.FileStatus.COPYING);
                    new CopyFileOperation(auth, pathRoot, pathFormat, directoryTitle).execute(fileUri);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return uriArrayList;
    }
}
