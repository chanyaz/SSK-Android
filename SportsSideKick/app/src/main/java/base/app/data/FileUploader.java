package base.app.data;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.google.android.gms.tasks.TaskCompletionSource;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Random;

import base.app.util.commons.Utility;
import base.app.util.ui.ExifUtil;

/**
 * Created by Filip on 3/17/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class FileUploader {

    private static String EUWest_PoolId = "eu-west-1:8a0d240e-2ebb-4b62-b66b-2288bc88ce1f";
    private static String EUWest_BaseUrl = "https://s3-eu-west-1.amazonaws.com/sskirbucket/";
    private static String EUWest_Bucket = "sskirbucket";

    private static FileUploader instance;

    private String poolId;
    private String baseUrl;
    private String bucket;
    private TransferUtility transferUtility;

    public static FileUploader getInstance() {
        if (instance == null) {
            instance = new FileUploader();
        }
        return instance;
    }

    public void initialize(final Context context) {
        Regions regionType = Regions.EU_WEST_1;
        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                context,    /* get the context for the application */
                poolId,    /* Identity Pool ID */
                regionType /* Region for your identity pool--US_EAST_1 or EU_WEST_1*/
        );

        AmazonS3Client s3 = new AmazonS3Client(credentialsProvider);
        transferUtility = new TransferUtility(s3, context);
    }

    private FileUploader() {
        poolId = FileUploader.EUWest_PoolId;
        baseUrl = FileUploader.EUWest_BaseUrl;
        bucket = FileUploader.EUWest_Bucket;
    }

    void upload(final String filename, String filepath, final TaskCompletionSource<String> completion) {
        try {
            File file = new File(filepath);
            TransferObserver observer = transferUtility.upload(
                    bucket,     /* The bucket to upload to */
                    filename,    /* The key for the uploaded object */
                    file /* The file where the data to upload exists */
            );

            observer.setTransferListener(new TransferListener() {
                @Override
                public void onStateChanged(int id, TransferState state) {
                    if (TransferState.COMPLETED.equals(state)) {
                        if(completion!=null){
                            completion.setResult(baseUrl + filename);
                        }
                    }
                }

                @Override
                public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {}

                @Override
                public void onError(int id, Exception ex) {
                    // Notify about error
                    completion.setException(new Exception("Something went wrong, file not uploaded!"));
                }
            });
        } catch (Exception e) {
            completion.setException(new Exception("Something went wrong, file not uploaded!"));
        }
    }

    void upload(final File image, final String filename, final TaskCompletionSource<String> completion) {
        try {
            TransferObserver observer = transferUtility.upload(
                    bucket,     /* The bucket to upload to */
                    filename,    /* The key for the uploaded object */
                    image /* The file where the data to upload exists */
            );

            observer.setTransferListener(new TransferListener() {
                @Override
                public void onStateChanged(int id, TransferState state) {
                    if (TransferState.COMPLETED.equals(state)) {
                        if(completion!=null){
                            completion.setResult(baseUrl + filename);
                        }
                    }
                }

                @Override
                public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {}

                @Override
                public void onError(int id, Exception ex) {
                    // Notify about error
                    completion.setException(new Exception("Something went wrong, file not uploaded!"));
                }
            });
        } catch (Exception e) {
            completion.setException(new Exception("Something went wrong, file not uploaded!"));
        }
    }

    void uploadThumbnail(String filename, String filepath, File filesDir, final TaskCompletionSource<String> completion) {
        Bitmap bmThumbnail = ThumbnailUtils.createVideoThumbnail(filepath, MediaStore.Video.Thumbnails.FULL_SCREEN_KIND);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bmThumbnail.compress(Bitmap.CompressFormat.JPEG, 70, bos);  // TODO @Filip - Magic number
        try {
            File file = new File(filesDir, "temp_thumbnail_video.jpg");
            bos.writeTo(new BufferedOutputStream(new FileOutputStream(file)));
            upload(filename, file.getPath(), completion);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void uploadCompressedImage(String filename, String filepath, File filesDir, final TaskCompletionSource<String> completion){
        File image = new File(filepath);
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(), bmOptions);
        bitmap = ExifUtil.rotateBitmap(filepath, bitmap);
        final int maxSize = 640;
        int outWidth;
        int outHeight;
        int inWidth = bitmap.getWidth();
        int inHeight = bitmap.getHeight();
        if(inWidth > inHeight){
            outWidth = maxSize;
            outHeight = (inHeight * maxSize) / inWidth;
        } else {
            outHeight = maxSize;
            outWidth = (inWidth * maxSize) / inHeight;
        }

        Bitmap resizedBitmap  = Bitmap.createScaledBitmap(bitmap, outWidth, outHeight, true);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 70, bos);  // TODO @Filip - Magic number
        try {
            File file = new File(filesDir, filename);
            bos.writeTo(new BufferedOutputStream(new FileOutputStream(file)));
            upload(filename, file.getPath(), completion);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void uploadImage(File image, String filename, final TaskCompletionSource<String> completion){
        upload(image, filename, completion);
    }

    public void uploadCircularProfileImage(String filename, String filepath, File filesDir, final TaskCompletionSource<String> completion) {
        File image = new File(filepath);
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(), bmOptions);
        bitmap = ExifUtil.rotateBitmap(filepath, bitmap);
        Bitmap outputBitmap;
        if (bitmap.getWidth() >= bitmap.getHeight()) {
            outputBitmap = Bitmap.createBitmap(bitmap, bitmap.getWidth() / 2 - bitmap.getHeight() / 2, 0, bitmap.getHeight(), bitmap.getHeight());
        } else {
            outputBitmap = Bitmap.createBitmap(bitmap, 0, bitmap.getHeight() / 2 - bitmap.getWidth() / 2, bitmap.getWidth(), bitmap.getWidth());
        }
        outputBitmap = Bitmap.createScaledBitmap(outputBitmap, 250, 250, true);  // TODO @Filip - Magic number
        outputBitmap = getCircleBitmap(outputBitmap);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        outputBitmap.compress(Bitmap.CompressFormat.PNG, 70, bos);  // TODO @Filip - Magic number
        try {
            File file = new File(filesDir, "temp_profile_circled.jpg");
            bos.writeTo(new BufferedOutputStream(new FileOutputStream(file)));
            upload(filename, file.getPath(), completion);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Bitmap getCircleBitmap(Bitmap bm) {
        int size = Math.min((bm.getWidth()), (bm.getHeight()));
        Bitmap bitmap = ThumbnailUtils.extractThumbnail(bm, size, size);
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xffff0000;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setFilterBitmap(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawOval(rectF, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        bitmap.recycle();
        return output;
    }

    public static String generateRandName(int length) {
        String letters = "1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        String randomString = "";
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            int rand = random.nextInt(letters.length());
            char nextChar = letters.charAt(rand);
            randomString += nextChar;
        }
        return randomString;
    }

    public static String generateMongoOID() {
        Long tsLong = Utility.getCurrentTime() / 1000L; // TODO @Filip - Magic number
        return (getFirst8(Long.toHexString(tsLong)) + generateRandom() + generateRandom());

    }

    private static String generateRandom() {
        BigInteger b = new BigInteger(256, new Random());  // TODO @Filip - Magic number
        long number = b.longValue();
        return getFirst8(Long.toHexString((number)));
    }

    private static String getFirst8(String str) {
        return str.substring(0, Math.min(str.length(), 8));  // TODO @Filip - Magic number
    }
}
