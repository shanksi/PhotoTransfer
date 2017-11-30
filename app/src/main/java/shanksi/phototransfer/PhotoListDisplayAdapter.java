package shanksi.phototransfer;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

public class PhotoListDisplayAdapter extends ArrayAdapter<FileInfoExtractor> {
    private final Context context;
    private final ArrayList<FileInfoExtractor> values;

    public PhotoListDisplayAdapter(Context context, ArrayList<FileInfoExtractor> values) {
        super(context, R.layout.rowlayout, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.rowlayout, parent, false);

        FileInfoExtractor fileUri = values.get(position);

        TextView textView = (TextView) rowView.findViewById(R.id.label);
        textView.setText(fileUri.getDisplayText());

        ProgressBar spinner = (ProgressBar) rowView.findViewById(R.id.progressSpinner);
        spinner.setVisibility(fileUri.getStatus() == FileInfoExtractor.FileStatus.COPYING || fileUri.getStatus() == FileInfoExtractor.FileStatus.COPIED ? View.VISIBLE : View.INVISIBLE);
        if (fileUri.getStatus() == FileInfoExtractor.FileStatus.COPIED)
            spinner.setProgress(spinner.getMax());
        return rowView;
    }
}