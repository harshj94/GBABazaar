package gbabazaar.gbabazaar;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class ItemsAdapter extends ArrayAdapter<Item> {
    private final Context context;
    private final ArrayList<Item> itemsArrayList;
    Bitmap bmp;
    int pos;
    ImageView imageView;
    private byte[] blob;

    public ItemsAdapter(Context context, ArrayList<Item> itemsArrayList) {

        super(context, R.layout.ad_row, itemsArrayList);

        this.context = context;
        this.itemsArrayList = itemsArrayList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        pos = position;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.ad_row, parent, false);

        imageView = (ImageView) rowView.findViewById(R.id.image);
        TextView title = (TextView) rowView.findViewById(R.id.title);
        TextView category = (TextView) rowView.findViewById(R.id.category);

        Glide
                .with(context)
                .load(itemsArrayList.get(position).gettURL())
                .centerCrop()
                .placeholder(R.drawable.placeholderr)
                .crossFade()
                .into(imageView);
        title.setText(itemsArrayList.get(position).gettTitle());
        category.setText(itemsArrayList.get(position).gettCategory());

        return rowView;
    }
}