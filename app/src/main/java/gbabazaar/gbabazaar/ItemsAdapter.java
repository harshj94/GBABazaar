package gbabazaar.gbabazaar;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class ItemsAdapter extends ArrayAdapter<Item> {
    private final Context context;
    private final ArrayList<Item> itemsArrayList;
    private byte[] blob;

    public ItemsAdapter(Context context, ArrayList<Item> itemsArrayList) {

        super(context, R.layout.ad_row, itemsArrayList);

        this.context = context;
        this.itemsArrayList = itemsArrayList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.ad_row, parent, false);

        ImageView imageView = (ImageView) rowView.findViewById(R.id.image);
        TextView title = (TextView) rowView.findViewById(R.id.title);
        TextView category = (TextView) rowView.findViewById(R.id.category);

        blob = itemsArrayList.get(position).gettImageBitmap();
        Bitmap bmp = BitmapFactory.decodeByteArray(blob, 0, blob.length);
        imageView.setImageBitmap(bmp);
        title.setText(itemsArrayList.get(position).gettTitle());
        category.setText(itemsArrayList.get(position).gettCategory());

        return rowView;
    }
}