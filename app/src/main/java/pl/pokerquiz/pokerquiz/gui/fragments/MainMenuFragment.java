package pl.pokerquiz.pokerquiz.gui.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedVignetteBitmapDisplayer;

import java.io.File;
import java.io.IOException;

import pl.pokerquiz.pokerquiz.AppPrefs;
import pl.pokerquiz.pokerquiz.BuildConfig;
import pl.pokerquiz.pokerquiz.PokerQuizApplication;
import pl.pokerquiz.pokerquiz.R;
import pl.pokerquiz.pokerquiz.networking.NetworkingManager;
import pl.pokerquiz.pokerquiz.utils.BitmapCompressor;
import pl.pokerquiz.pokerquiz.utils.ImageCaptureHelper;

public class MainMenuFragment extends Fragment{
    private static final int REQUEST_CODE_TAKE_PHOTO = 0;
    private static final int REQUEST_CODE_PICK_IMAGE = 1;

    private View mRootView;
    private ImageView mImgvAvatar;
    private TextView mTxtvNickname;

    private NetworkingManager mNetworkingManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNetworkingManager = ((PokerQuizApplication) getActivity().getApplication()).getNetworkingManager();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Fragment needs layoutInflater from Activity because of Lollipop broken inflation
        mRootView = getActivity().getLayoutInflater().inflate(R.layout.fragment_main_menu, container, false);

        findViews();
        setViews();
        setListeners();

        return mRootView;
    }

    private void findViews() {
        mImgvAvatar = (ImageView) mRootView.findViewById(R.id.imgvAvatar);
        mTxtvNickname = (TextView) mRootView.findViewById(R.id.txtvNickname);
    }

    private void setViews() {
        AppPrefs prefs = ((PokerQuizApplication) getActivity().getApplication()).getAppPrefs();

        mTxtvNickname.setText(prefs.getNickname());

        DisplayImageOptions imgOptions = new DisplayImageOptions.Builder()
                .displayer(new RoundedBitmapDisplayer(1000, 0))
                .resetViewBeforeLoading(true)
                .cacheInMemory(false)
                .cacheOnDisk(false)
                .bitmapConfig(Bitmap.Config.RGB_565).build();

        ImageLoader.getInstance().displayImage(prefs.getAvatarBase64(), mImgvAvatar, imgOptions);
    }

    private void setListeners() {
        mImgvAvatar.setOnClickListener(view -> editProfilePicture());
    }

    private void editProfilePicture() {
        ArrayAdapter adapter = new ArrayAdapter<String>(getActivity(), R.layout.lvitem_dialog, new String[]{
                getResources().getString(R.string.take_photo),
                getResources().getString(R.string.pick_photo),
                getResources().getString(R.string.cancel)}) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                return super.getView(position, convertView, parent);
            }

        };
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                .setAdapter(adapter, (dialog, which) -> {
                    switch (which) {
                        case REQUEST_CODE_TAKE_PHOTO:
                            startTakePhotoIntent();
                            break;
                        case REQUEST_CODE_PICK_IMAGE:
                            startPickPhotoIntent();
                            break;
                    }
                    dialog.dismiss();
                })
                .create();
        alertDialog.show();
    }

    private void startPickPhotoIntent() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(intent, getString(R.string.select_picture)), REQUEST_CODE_PICK_IMAGE);
    }

    private void startTakePhotoIntent() {
        if (getActivity() != null) {
            try {
                ImageCaptureHelper.launchCameraApp(REQUEST_CODE_TAKE_PHOTO, MainMenuFragment.this, null);
            } catch (Exception e) {
                if (BuildConfig.DEBUG) {
                    e.printStackTrace();
                }
            }
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_PICK_IMAGE:
                if (resultCode == Activity.RESULT_OK && getActivity() != null && data != null && data.getData() != null) {

                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};

                    Cursor cursor = getActivity().getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String photoPath = cursor.getString(columnIndex);
                    cursor.close();

                    try {
                        String imageBase64 = BitmapCompressor.compressImageToBase64(photoPath);
                        ((PokerQuizApplication) getActivity().getApplication()).getAppPrefs().setAvatarBase64(imageBase64);
                        setViews();
                    } catch (IOException ioe) {
                        Toast.makeText(getActivity(), R.string.photo_processing_problem, Toast.LENGTH_LONG).show();
                        if (BuildConfig.DEBUG) {
                            ioe.printStackTrace();
                        }
                    }

                }
                break;
            case REQUEST_CODE_TAKE_PHOTO:
                if (resultCode == Activity.RESULT_OK && getActivity() != null) {

                    File tempCamPhotoFile = ImageCaptureHelper.retrievePhotoResult(getActivity(), null);
                    String photoPath = tempCamPhotoFile.getPath();

                    try {
                        String imageBase64 = BitmapCompressor.compressImageToBase64(photoPath);
                        ((PokerQuizApplication) getActivity().getApplication()).getAppPrefs().setAvatarBase64(imageBase64);
                        setViews();
                    } catch (IOException ioe) {
                        Toast.makeText(getActivity(), R.string.photo_processing_problem, Toast.LENGTH_LONG).show();
                        if (BuildConfig.DEBUG) {
                            ioe.printStackTrace();
                        }
                    }
                }
                break;
        }
    }
}
