package pl.pokerquiz.pokerquiz.utils;

import android.content.Context;
import android.net.Uri;
import android.util.Base64;

import com.nostra13.universalimageloader.core.download.BaseImageDownloader;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class Base64ImageLoader extends BaseImageDownloader {

    public Base64ImageLoader(Context context) {
        super(context);
    }

    @Override
    protected InputStream getStreamFromOtherSource(String base64, Object extra) throws IOException {
        return new ByteArrayInputStream(Base64.decode(base64, Base64.DEFAULT));
    }
}
