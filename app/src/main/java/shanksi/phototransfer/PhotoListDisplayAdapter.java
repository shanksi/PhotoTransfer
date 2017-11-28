package shanksi.phototransfer;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class PhotoListDisplayAdapter extends ArrayAdapter<Uri> {
    private final Context context;
    private final ArrayList<Uri> values;

    public PhotoListDisplayAdapter(Context context, ArrayList<Uri> values) {
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

        // Displaying a textview
        TextView textView = (TextView) rowView.findViewById(R.id.label);
        Uri fileUri = values.get(position);
        textView.setText(fileUri.getPath());
        try {
            FileInfoExtractor info = new FileInfoExtractor(fileUri, context);
            textView.setText(info.getFile().getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rowView;
    }
}