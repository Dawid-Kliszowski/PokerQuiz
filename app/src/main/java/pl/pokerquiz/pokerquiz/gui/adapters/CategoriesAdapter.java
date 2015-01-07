package pl.pokerquiz.pokerquiz.gui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import pl.pokerquiz.pokerquiz.R;
import pl.pokerquiz.pokerquiz.datamodel.rest.Category;

public class CategoriesAdapter extends ArrayAdapter<Category> {
    private LayoutInflater mInflater;
    private ImageLoader mImageLoader;

    public CategoriesAdapter(Context context, List<Category> categories) {
        super(context, R.layout.lvitem_categories, categories);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mImageLoader = ImageLoader.getInstance();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.lvitem_categories, parent, false);

            holder = new ViewHolder();
            holder.mImgvThumbnail= (ImageView) convertView.findViewById(R.id.imgvThumbnail);
            holder.mTxtvName = (TextView) convertView.findViewById(R.id.txtvName);
            convertView.setTag(holder);
        }

        holder = (ViewHolder) convertView.getTag();
        Category item = getItem(position);

        mImageLoader.displayImage(item.getSmallImageUrl(), holder.mImgvThumbnail);
        holder.mTxtvName.setText(item.getName());

        return convertView;
    }

    private static class ViewHolder {
        private ImageView mImgvThumbnail;
        private TextView mTxtvName;
    }
}
