package pl.pokerquiz.pokerquiz.gui.fragments;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.renderscript.RenderScript;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import pl.pokerquiz.pokerquiz.R;
import pl.pokerquiz.pokerquiz.datamodel.rest.Category;
import pl.pokerquiz.pokerquiz.gui.adapters.CategoriesAdapter;
import pl.pokerquiz.pokerquiz.rest.RESTManager;

public class CategoriesListFragment extends Fragment {
    private View mRootView;
    private ListView mLvCategories;
    private LinearLayout mLlDescriptionHolder;
    private ImageView mImgvBigImage;
    private TextView mTxtvDescription;
    private ProgressBar mPbDownloading;

    private List<Category> mCategories;
    private CategoriesAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Fragment needs layoutInflater from Activity because of Lollipop broken inflation
        mRootView = getActivity().getLayoutInflater().inflate(R.layout.fragment_categories_list, container, false);

        findViews();
        downloadCategories();

        return mRootView;
    }

    private void findViews() {
        mLvCategories = (ListView) mRootView.findViewById(R.id.lvCategories);
        mLlDescriptionHolder = (LinearLayout) mRootView.findViewById(R.id.llDescriptionHolder);
        mImgvBigImage = (ImageView) mRootView.findViewById(R.id.imgvBigImage);
        mTxtvDescription = (TextView) mRootView.findViewById(R.id.txtvDescription);
        mPbDownloading = (ProgressBar) mRootView.findViewById(R.id.pbDownloading);
    }

    private void downloadCategories() {
        RESTManager restManager = new RESTManager(getActivity());
        restManager.getCategories((success, categories) -> {
            mPbDownloading.setVisibility(View.INVISIBLE);
            if (getActivity() != null) {
                if (success) {
                    mCategories = categories;
                    mAdapter = new CategoriesAdapter(getActivity(), mCategories);
                    mLvCategories.setAdapter(mAdapter);

                    mLvCategories.setOnItemClickListener((adapterView, view, i, l) -> {
                        mLlDescriptionHolder.setVisibility(View.VISIBLE);
                        Category selectedCategory = mCategories.get(i);
                        ImageLoader.getInstance().displayImage(selectedCategory.getBigImageUrl(), mImgvBigImage);
                        mTxtvDescription.setText(selectedCategory.getDescription());
                    });
                } else {
                    new AlertDialog.Builder(getActivity())
                            .setTitle(R.string.error)
                            .setMessage(R.string.internet_problem)
                            .setPositiveButton(R.string.ok, (dialogInterface, i) -> {
                                dialogInterface.dismiss();
                            })
                            .show();
                }
            }
        });
    }
}
