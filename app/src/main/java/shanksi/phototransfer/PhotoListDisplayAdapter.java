package shanksi.phototransfer;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
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
        FileInfoExtractor fileUri = values.get(position);

        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.rowlayout, parent, false);

            holder.textView = (TextView) convertView.findViewById(R.id.label);
            holder.spinner = (ProgressBar) convertView.findViewById(R.id.progressSpinner);
            holder.tick = (ImageView) convertView.findViewById(R.id.tick);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.textView.setText(fileUri.getDisplayText());
        holder.spinner.setVisibility(fileUri.getStatus() == FileInfoExtractor.FileStatus.COPYING || fileUri.getStatus() == FileInfoExtractor.FileStatus.COPIED ? View.VISIBLE : View.INVISIBLE);
        if (fileUri.getStatus() == FileInfoExtractor.FileStatus.COPIED)
            holder.spinner.setProgress(holder.spinner.getMax());
        holder.tick.setVisibility(fileUri.getStatus() == FileInfoExtractor.FileStatus.COPIED ? View.VISIBLE : View.INVISIBLE);

        return convertView;
    }

    static class ViewHolder {
        TextView textView;
        ProgressBar spinner;
        ImageView tick;
    }
}