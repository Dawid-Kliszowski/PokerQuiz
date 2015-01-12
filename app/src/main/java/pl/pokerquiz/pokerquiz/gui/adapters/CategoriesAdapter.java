package pl.pokerquiz.pokerquiz.gui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import pl.pokerquiz.pokerquiz.R;
import pl.pokerquiz.pokerquiz.datamodel.rest.Category;

public class CategoriesAdapter extends ArrayAdapter<CategoriesAdapter.AdapterObject> {
    private LayoutInflater mInflater;
    private ImageLoader mImageLoader;
    private OnDownloadIconClickListener mDownloadClickListener;
    private OnCheckboxClickListener mCheckboxListener;
    private boolean mShowSelected;

    public CategoriesAdapter(Context context, List<AdapterObject> items,
                             boolean showSelected, OnDownloadIconClickListener onDownloadClickListener,
                             OnCheckboxClickListener checkboxListener) {
        super(context, R.layout.lvitem_categories, items);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mImageLoader = ImageLoader.getInstance();
        mDownloadClickListener = onDownloadClickListener;
        mCheckboxListener = checkboxListener;
        mShowSelected = showSelected;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.lvitem_categories, parent, false);

            ViewHolder holder;
            holder = new ViewHolder();
            holder.mCheckSelected = (CheckBox) convertView.findViewById(R.id.checkSelected);
            holder.mImgvThumbnail= (ImageView) convertView.findViewById(R.id.imgvThumbnail);
            holder.mTxtvName = (TextView) convertView.findViewById(R.id.txtvName);
            holder.mImgvDownload = (ImageView) convertView.findViewById(R.id.imgvDownload);
            holder.mPbDownload = (ProgressBar) convertView.findViewById(R.id.pbDownload);

            if (mShowSelected) {
                holder.mCheckSelected.setVisibility(View.VISIBLE);
            }

            convertView.setTag(holder);
        }

        ViewHolder holder = (ViewHolder) convertView.getTag();
        AdapterObject item = getItem(position);

        mImageLoader.displayImage(item.getCategory().getSmallImageUrl(), holder.mImgvThumbnail);
        holder.mTxtvName.setText(item.getCategory().getName());
        holder.mPbDownload.setVisibility(View.INVISIBLE);
        holder.mImgvDownload.setVisibility(View.VISIBLE);
        if (item.isDownloaded()) {
            holder.mImgvDownload.setImageResource(R.drawable.tick);
            holder.mImgvDownload.setOnClickListener(null);
        } else {
            holder.mImgvDownload.setImageResource(R.drawable.ic_download);
            holder.mImgvDownload.setOnClickListener(view -> {
                holder.mImgvDownload.setVisibility(View.GONE);
                holder.mPbDownload.setVisibility(View.VISIBLE);
                mDownloadClickListener.onDownloadIconClick(position);
            });
        }

        if (mShowSelected) {
            holder.mCheckSelected.setOnClickListener(view -> {
                mCheckboxListener.onCheckboxClick(position, holder.mCheckSelected.isChecked());
            });
            holder.mCheckSelected.setEnabled(item.isDownloaded());
            holder.mCheckSelected.setChecked(item.isSelected());
        }

        return convertView;
    }

    private static class ViewHolder {
        private CheckBox mCheckSelected;
        private ImageView mImgvThumbnail;
        private TextView mTxtvName;
        private ImageView mImgvDownload;
        private ProgressBar mPbDownload;
    }

    public static interface OnDownloadIconClickListener {
        public void onDownloadIconClick(int position);
    }

    public static interface OnCheckboxClickListener {
        public void onCheckboxClick(int position, boolean checked);
    }

    public static class AdapterObject {
        private Category mCategory;
        private boolean mDownloaded;
        private boolean mSelected;

        public AdapterObject(Category category) {
            mCategory = category;
        }

        public Category getCategory() {
            return mCategory;
        }

        public void setDownloaded(boolean downloaded) {
            mDownloaded = downloaded;
        }

        public boolean isDownloaded() {
            return mDownloaded;
        }

        public void setSelected(boolean selected) {
            mSelected = selected;
        }

        public boolean isSelected() {
            return mSelected;
        }
    }
}
