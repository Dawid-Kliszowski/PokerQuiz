package pl.pokerquiz.pokerquiz.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class BitmapCompressor {
    private static final int MAX_LONGER_EDGE = 800;
    private static final int MAX_SHORTER_EDGE = 500;

        public static String compressImageToBase64(String bitmapFilePath) throws IOException {
        ExifInterface exif = new ExifInterface(bitmapFilePath);
        int originalImageWidth = exif.getAttributeInt(ExifInterface.TAG_IMAGE_WIDTH, 0);
        int originalImageHeight = exif.getAttributeInt(ExifInterface.TAG_IMAGE_LENGTH, 0);

        int sampleSize = 1;
        while (originalImageWidth > MAX_LONGER_EDGE * 2 || originalImageHeight > MAX_LONGER_EDGE * 2) {
            sampleSize *= 2;
            originalImageWidth /= 2;
            originalImageHeight /= 2;
        }

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = sampleSize;

        Bitmap bitmap = BitmapFactory.decodeFile(bitmapFilePath, options);

        if (bitmap != null) {

            bitmap = createSmallerBitmap(bitmap, MAX_LONGER_EDGE, MAX_SHORTER_EDGE);
            int rotation = exifToDegrees(exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL));

            Matrix matrix = new Matrix();
            if (rotation != 0f) {
                matrix.preRotate(rotation);
            }

            Bitmap extraBitmapReference = bitmap;
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            if (extraBitmapReference != bitmap && !extraBitmapReference.isRecycled()) {
                extraBitmapReference.recycle();
            }

            extraBitmapReference = bitmap;
            bitmap = centerCroppToSquare(bitmap);
            if (extraBitmapReference != bitmap && !extraBitmapReference.isRecycled()) {
                extraBitmapReference.recycle();
            }

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream);
            byte[] bytesImage = byteArrayOutputStream .toByteArray();
            byteArrayOutputStream.close();

            if (!bitmap.isRecycled()) {
                bitmap.recycle();
            }

            return Base64.encodeToString(bytesImage, Base64.DEFAULT);
        } else {
            throw new IOException();
        }
    }

    private static Bitmap createSmallerBitmap(Bitmap bitmap, int maxLongerEdge, int maxShorterEdge) {
        Bitmap extraBitmapReference;

        float aspectRatio = ((float) bitmap.getWidth()) / ((float) bitmap.getHeight());
        if (aspectRatio > 1) {
            if (bitmap.getWidth() > maxLongerEdge || bitmap.getHeight() > maxShorterEdge) {
                float longerBorderAdjustingDifference = ((float) bitmap.getWidth()) / ((float) maxLongerEdge);
                float shorterBorderAdjustingDifference = ((float) bitmap.getHeight()) / ((float) maxShorterEdge);

                extraBitmapReference = bitmap;
                if (longerBorderAdjustingDifference > shorterBorderAdjustingDifference) {
                    bitmap = Bitmap.createScaledBitmap(bitmap, maxLongerEdge, (int) (((float) maxLongerEdge) / aspectRatio), false);
                } else {
                    bitmap = Bitmap.createScaledBitmap(bitmap, (int) (((float) maxShorterEdge) * aspectRatio), maxShorterEdge, false);
                }
                if (!extraBitmapReference.isRecycled()) {
                    extraBitmapReference.recycle();
                }
            }
        } else {
            if (bitmap.getHeight() > maxLongerEdge || bitmap.getWidth() > maxShorterEdge) {
                float longerBorderAdjustingDifference = ((float) bitmap.getHeight()) / ((float) maxLongerEdge);
                float shorterBorderAdjustingDifference = ((float) bitmap.getWidth()) / ((float) maxShorterEdge);

                extraBitmapReference = bitmap;
                if (longerBorderAdjustingDifference > shorterBorderAdjustingDifference) {
                    bitmap = Bitmap.createScaledBitmap(bitmap, (int) (((float) maxLongerEdge) * aspectRatio), maxLongerEdge, false);
                } else {
                    bitmap = Bitmap.createScaledBitmap(bitmap, maxShorterEdge, (int) (((float) maxShorterEdge) / aspectRatio), false);
                }
                if (!extraBitmapReference.isRecycled()) {
                    extraBitmapReference.recycle();
                }
            }
        }

        return bitmap;
    }

    private static Bitmap centerCroppToSquare(Bitmap bitmap) {
        if (bitmap.getWidth() >= bitmap.getHeight()){
            return  Bitmap.createBitmap(
                    bitmap,
                    bitmap.getWidth()/2 - bitmap.getHeight()/2,
                    0,
                    bitmap.getHeight(),
                    bitmap.getHeight()
            );
        }else{
            return Bitmap.createBitmap(
                    bitmap,
                    0,
                    bitmap.getHeight()/2 - bitmap.getWidth()/2,
                    bitmap.getWidth(),
                    bitmap.getWidth()
            );
        }
    }

    private static int exifToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }
}
