package shanksi.phototransfer;

import android.content.Context;
import android.database.Cursor;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FileInfoExtractor {
    private Uri _uri;
    private String _path;
    private File _file;
    private Date _date;
    private static final String[] _filePathColumn = {MediaStore.MediaColumns.DATA};

    public FileInfoExtractor(Uri fileUri, Context context) {
        _uri = fileUri;
        _path = fileUri.getPath();
        extractFileInfo(fileUri, context);
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
        try {
            _date = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss", Locale.ENGLISH).parse(dtString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        cursor.close();
    }

    public String getPath() {
        return _path;
    }

    public File getFile() {
        return _file;
    }

    public Date getDate() {
        return _date;
    }

}
