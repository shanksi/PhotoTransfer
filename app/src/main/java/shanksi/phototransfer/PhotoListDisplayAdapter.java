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
            holder.tick = (ImageView) convertView.findViewById(R.id.tick);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.textView.setText(fileUri.getDisplayText());

        switch(fileUri.getStatus()) {
            case COPYING:
                holder.tick.setImageResource(R.drawable.ic_file_upload_black_24dp);
                holder.tick.setColorFilter(getContext().getResources().getColor(R.color.colorPrimaryDark));
            case COPIED:
                holder.tick.setImageResource(R.drawable.ic_done_black_24dp);
                holder.tick.setColorFilter(getContext().getResources().getColor(R.color.colorSuccess));
            case ERROR:
                holder.tick.setImageResource(R.drawable.ic_error_outline_black_24dp);
                holder.tick.setColorFilter(getContext().getResources().getColor(R.color.colorFail));
        }
        //if (fileUri.getStatus() == FileInfoExtractor.FileStatus.COPIED)
        //    holder.spinner.setProgress(holder.spinner.getMax());
        //holder.tick.setVisibility(View.VISIBLE ); // fileUri.getStatus() == FileInfoExtractor.FileStatus.COPIED ? View.VISIBLE : View.INVISIBLE);
        //holder.tick.setImageResource(R.drawable.ic_done_black_24dp);
        //holder.tick.setColorFilter(getContext().getResources().getColor(R.color.colorPrimary));
        return convertView;
    }

    static class ViewHolder {
        TextView textView;
        ImageView tick;
    }
}