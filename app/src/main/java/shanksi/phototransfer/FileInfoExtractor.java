package shanksi.phototransfer;

import android.content.Context;
import android.database.Cursor;
import android.support.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FileInfoExtractor {
    public enum FileStatus {
        SELECTED,
        COPYING,
        COPIED,
        ERROR
    }

    private final Uri _uri;
    private final String _path;
    private FileStatus _status;
    private File _file;
    private Date _date;
    private String _message;
    private static final String[] _filePathColumn = {MediaStore.MediaColumns.DATA};

    // add a private listener variable
    private FileStatusListener _listener = null;

    // provide a way for another class to set the listener
    public void setMyClassListener(FileStatusListener listener) {
        this._listener = listener;
    }

    public FileInfoExtractor(Uri fileUri, FileStatusListener listener, Context context) {
        _uri = fileUri;
        _path = fileUri.getPath();
        _message = "ok";

        extractFileInfo(fileUri, context);

        _status = FileStatus.SELECTED;
        _listener = listener;
    }

    private void extractFileInfo(Uri fileUri, Context context) {
        Cursor cursor = context.getContentResolver().query(fileUri, _filePathColumn, null, null, null);
        cursor.moveToFirst();

        int columnIndex = cursor.getColumnIndex(_filePathColumn[0]);
        _file = new File(cursor.getString(columnIndex));
        String dtString = "";
        try {
            ExifInterface exif = new ExifInterface(cursor.getString(columnIndex));
            dtString = exif.getAttribute(ExifInterface.TAG_DATETIME);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (dtString != null) {
            try {
                _date = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss", Locale.ENGLISH).parse(dtString);
            } catch (ParseException e) {
                _message = e.getMessage();
                e.printStackTrace();
            }
        }
        cursor.close();
    }

    public String getDisplayText() {
        if (_file != null) {
            return _file.getName();
        }
        return _path;
    }

    public Uri getUri() {
        return _uri;
    }

    public String getPath() {
        return _path;
    }

    public File getFile() {
        return _file;
    }

    public Date getDate() {
        return _date != null ? _date : new Date();
    }

    public FileStatus getStatus() {
        return _status;
    }

    public void setStatus(FileStatus value) {
        _status = value;
    }

    public String getMessage() {
        return _message;
    }

    public void setMessage(String value) {
        _message = value;
    }

    public void informListeners() {
        if (_listener != null)
            _listener.onStatusChanged(this);
    }

    public interface FileStatusListener {
        // add whatever methods you need here
        void onStatusChanged(FileInfoExtractor fileInfoExtractor);
    }
}
