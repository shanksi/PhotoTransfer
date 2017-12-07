package shanksi.phototransfer;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class PhotoListDisplayAdapter extends ArrayAdapter<FileInfoExtractor> {
    // private final Context context;
    // private final ArrayList<FileInfoExtractor> values;

    public PhotoListDisplayAdapter(Context context, ArrayList<FileInfoExtractor> values) {
        super(context, R.layout.rowlayout, values);
        // this.context = context;
        // this.values = values;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        FileInfoExtractor fileUri = getItem(position); //  values.get(position);

        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.rowlayout, parent, false);

            holder.textView = (TextView) convertView.findViewById(R.id.label);
            holder.status = (TextView) convertView.findViewById(R.id.statusMessage);
            holder.tick = (ImageView) convertView.findViewById(R.id.tick);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.textView.setText(fileUri.getDisplayText());
        holder.status.setText(fileUri.getMessage());

        Resources resources = getContext().getResources();
        switch(fileUri.getStatus()) {
            case SELECTED:
                holder.tick.clearColorFilter();
                break;
            case COPYING:
                holder.tick.setImageResource(R.drawable.ic_file_upload_black_24dp);
                holder.tick.setColorFilter(resources.getColor(R.color.colorPrimaryDark));
                break;
            case COPIED:
                holder.tick.setImageResource(R.drawable.ic_done_black_24dp);
                holder.tick.setColorFilter(resources.getColor(R.color.colorSuccess));
                break;
            case ERROR:
                holder.tick.setImageResource(R.drawable.ic_error_outline_black_24dp);
                holder.tick.setColorFilter(resources.getColor(R.color.colorFail));
                break;
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
        TextView status;
        ImageView tick;
    }
}