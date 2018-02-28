package base.app.data;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import java.util.concurrent.Callable;

import base.app.util.commons.Utility;
import base.app.util.ui.ExifUtil;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Filip on 3/17/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class FileUploader {

    public static final int COMPRESSED_IMAGE_QUALITY = 70;
    public static final long MILLIS_IN_SECOND = 1000L;
    public static final int NUM_BITS = 256;
    public static final int BEGIN_INDEX = 0;
    public static final int END_INDEX = 8;
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

    public void upload(final String filename, String filepath, final TaskCompletionSource<String> completion) {
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
                        if (completion != null) {
                            completion.setResult(baseUrl + filename);
                        }
                    }
                }

                @Override
                public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                }

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
                        if (completion != null) {
                            completion.setResult(baseUrl + filename);
                        }
                    }
                }

                @Override
                public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                }

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

    void uploadThumbnail(String videofilepath, File filesDir, final TaskCompletionSource<String> completion) {
        Bitmap bmThumbnail = ThumbnailUtils.createVideoThumbnail(videofilepath, MediaStore.Video.Thumbnails.FULL_SCREEN_KIND);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bmThumbnail.compress(Bitmap.CompressFormat.JPEG, COMPRESSED_IMAGE_QUALITY, bos);
        try {
            String filename = "temp_thumbnail_video_" + FileUploader.generateRandName(10) + ".jpg";
            File file = new File(filesDir, filename);
            bos.writeTo(new BufferedOutputStream(new FileOutputStream(file, false)));
            upload(filename, file.getPath(), completion);
            bos.flush();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void uploadCompressedImage(final String filename, final String filepath, final File filesDir, final TaskCompletionSource<String> completion) {
        Observable.fromCallable(new Callable<File>() {
            @Override
            public File call() {
                File image = new File(filepath);
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();

                Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(), bmOptions);
                bitmap = ExifUtil.rotateBitmap(filepath, bitmap);

                final int maxSize = 640;
                int outWidth;
                int outHeight;
                int inWidth = bitmap.getWidth();
                int inHeight = bitmap.getHeight();
                if (inWidth > inHeight) {
                    outWidth = maxSize;
                    outHeight = (inHeight * maxSize) / inWidth;
                } else {
                    outHeight = maxSize;
                    outWidth = (inWidth * maxSize) / inHeight;
                }

                Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, outWidth, outHeight, true);
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                resizedBitmap.compress(Bitmap.CompressFormat.JPEG, COMPRESSED_IMAGE_QUALITY, bos);
                try {
                    File file = new File(filesDir, filename);
                    bos.writeTo(new BufferedOutputStream(new FileOutputStream(file)));
                    return file;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        })
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<File>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(File file) {
                        upload(filename, file.getPath(), completion);
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    void uploadImage(File image, String filename, final TaskCompletionSource<String> completion) {
        upload(image, filename, completion);
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
        Long tsLong = Utility.getCurrentTime() / MILLIS_IN_SECOND;
        return (getFirst8(Long.toHexString(tsLong)) + generateRandom() + generateRandom());

    }

    private static String generateRandom() {
        BigInteger b = new BigInteger(NUM_BITS, new Random());
        long number = b.longValue();
        return getFirst8(Long.toHexString((number)));
    }

    private static String getFirst8(String str) {
        return str.substring(BEGIN_INDEX, Math.min(str.length(), END_INDEX));
    }
}
