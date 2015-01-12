package pl.pokerquiz.pokerquiz.gui.fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import pl.pokerquiz.pokerquiz.PokerQuizApplication;
import pl.pokerquiz.pokerquiz.R;
import pl.pokerquiz.pokerquiz.database.DatabaseHelper;
import pl.pokerquiz.pokerquiz.datamodel.rest.Category;
import pl.pokerquiz.pokerquiz.gui.adapters.CategoriesAdapter;
import pl.pokerquiz.pokerquiz.rest.RESTManager;

public class CategoriesListFragment extends Fragment {
    private static final String BUNDLE_KEY_SELECTED_IDS = "selected_ids";
    private View mRootView;
    private ListView mLvCategories;
    private LinearLayout mLlDescriptionHolder;
    private ImageView mImgvBigImage;
    private TextView mTxtvDescription;
    private ProgressBar mPbDownloading;

    private List<CategoriesAdapter.AdapterObject> mItems;
    private CategoriesAdapter mAdapter;

    public static CategoriesListFragment newInstance(CroupierMenuFragment targetFragment, List<Category> selectedCategories) {
        CategoriesListFragment fragment = new CategoriesListFragment();
        Bundle args = new Bundle();
        HashSet<Long> selectedCategoriesSet = new HashSet<>();
        for (Category category : selectedCategories) {
           selectedCategoriesSet.add(category.getId());
        }
        args.putSerializable(BUNDLE_KEY_SELECTED_IDS, selectedCategoriesSet);
        fragment.setArguments(args);
        fragment.setTargetFragment(targetFragment, 1);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Fragment needs layoutInflater from Activity because of Lollipop broken inflation
        mRootView = getActivity().getLayoutInflater().inflate(R.layout.fragment_categories_list, container, false);

        findViews();
        refreshCategories();

        return mRootView;
    }

    private void findViews() {
        mLvCategories = (ListView) mRootView.findViewById(R.id.lvCategories);
        mLlDescriptionHolder = (LinearLayout) mRootView.findViewById(R.id.llDescriptionHolder);
        mImgvBigImage = (ImageView) mRootView.findViewById(R.id.imgvBigImage);
        mTxtvDescription = (TextView) mRootView.findViewById(R.id.txtvDescription);
        mPbDownloading = (ProgressBar) mRootView.findViewById(R.id.pbDownloading);
    }

    private void refreshCategories() {
        DatabaseHelper databaseHelper = PokerQuizApplication.getDatabaseHelper();

        mItems = new ArrayList<>();
        HashSet<Long> selectedCategories = null;
        if (getArguments() != null && getArguments().containsKey(BUNDLE_KEY_SELECTED_IDS)) {
            selectedCategories = (HashSet<Long>) getArguments().getSerializable(BUNDLE_KEY_SELECTED_IDS);
        }
        final HashSet<Long> finalSelectedCategories = selectedCategories;

        mLvCategories.setOnItemClickListener((adapterView, view, i, l) -> {
            mLlDescriptionHolder.setVisibility(View.VISIBLE);
            Category selectedCategory = mItems.get(i).getCategory();
            ImageLoader.getInstance().displayImage(selectedCategory.getBigImageUrl(), mImgvBigImage);
            mTxtvDescription.setText(selectedCategory.getDescription());
        });

        List<Category> storedCategories = databaseHelper.getAll(Category.class);
        for (Category storedCategory : storedCategories) {
            CategoriesAdapter.AdapterObject item = new CategoriesAdapter.AdapterObject(storedCategory);
            item.setDownloaded(true);
            if (finalSelectedCategories != null && finalSelectedCategories.contains(storedCategory.getId())) {
                item.setSelected(true);
            }
            mItems.add(item);
        }

        RESTManager restManager = new RESTManager(getActivity());
        restManager.getCategories((success, categories) -> {
            mPbDownloading.setVisibility(View.INVISIBLE);
            if (getActivity() != null) {
                if (success) {
                    for (Category category : categories) {
                        if (databaseHelper.getById(Category.class, category.getId()) == null) {
                            CategoriesAdapter.AdapterObject item = new CategoriesAdapter.AdapterObject(category);
                            mItems.add(item);
                            if (finalSelectedCategories != null && finalSelectedCategories.contains(category.getId())) {
                                item.setSelected(true);
                            }
                        }
                    }
                } else {
                    new AlertDialog.Builder(getActivity())
                            .setTitle(R.string.error)
                            .setMessage(R.string.internet_problem)
                            .setPositiveButton(R.string.ok, (dialogInterface, i) -> {
                                dialogInterface.dismiss();
                            })
                            .show();
                }

                mAdapter = new CategoriesAdapter(getActivity(), mItems, finalSelectedCategories != null, position -> {
                    new RESTManager(getActivity()).getQuestions(mItems.get(position).getCategory().getId(),
                            (success1, categories1, questions) -> {
                                if (success1) {
                                    databaseHelper.insertIfNotExistsOrUpdate(categories1, () -> {
                                        databaseHelper.insertIfNotExistsOrUpdate(questions, () -> {
                                            refreshCategories();
                                        });
                                    });
                                }
                            });
                }, (position, checked) -> {
                    if (finalSelectedCategories != null) {
                        mItems.get(position).setSelected(checked);
                        List<Category> selected = new ArrayList<Category>();
                        for (CategoriesAdapter.AdapterObject item : mItems) {
                            if (item.isSelected()) {
                                selected.add(item.getCategory());
                            }
                        }
                        ((CroupierMenuFragment) getTargetFragment()).setCategories(selected);
                    }
                });
                mLvCategories.setAdapter(mAdapter);
            }
        });

    }
}
