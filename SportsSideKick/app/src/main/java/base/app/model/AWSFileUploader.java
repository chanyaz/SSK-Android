package base.app.model;

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

import org.greenrobot.eventbus.EventBus;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Random;

import base.app.events.GameSparksEvent;

/**
 * Created by Filip on 3/17/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class AWSFileUploader {

    private static String EUWest_PoolId = "eu-west-1:8a0d240e-2ebb-4b62-b66b-2288bc88ce1f";
    private static String USEast_PoolId = "us-east-1:7a808834-6423-4d2c-b11e-d2dc2b49f223";

    private static String EUWest_BaseUrl = "https://s3-eu-west-1.amazonaws.com/sskirbucket/";
    private static String USEast_BaseUrl = "https://s3.amazonaws.com/sskusbucket/";

    private static String EUWest_Bucket = "sskirbucket";
    private static String USEast_Bucket = "sskusbucket";

    private static AWSFileUploader instance;

    private String poolId;
    private String baseUrl;
    private String bucket;
    private TransferUtility transferUtility;
    public static AWSFileUploader getInstance(){
        if(instance==null){
            instance = new AWSFileUploader();
        }
        return instance;
    }

    public void initialize(Context context){
        Regions regionType = Regions.EU_WEST_1;
        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                context,    /* get the context for the application */
                poolId,    /* Identity Pool ID */
                regionType /* Region for your identity pool--US_EAST_1 or EU_WEST_1*/
        );

        AmazonS3Client s3 = new AmazonS3Client(credentialsProvider);
        transferUtility = new TransferUtility(s3, context);
    }

    private AWSFileUploader(){
        poolId = AWSFileUploader.EUWest_PoolId;
        baseUrl = AWSFileUploader.EUWest_BaseUrl;
        bucket = AWSFileUploader.EUWest_Bucket;
    }

    public void upload(final String filename, String filepath, final GameSparksEvent.Type event){
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
                        EventBus.getDefault().post(new GameSparksEvent("File uploaded!", event, baseUrl + filename));
                    }

                }

                @Override
                public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                }

                @Override
                public void onError(int id, Exception ex) {
                    // Notify about error
                    EventBus.getDefault().post(new GameSparksEvent("Something went wrong, file not uploaded!", event, null));
                }
            });
        } catch (Exception e){
            EventBus.getDefault().post(new GameSparksEvent("Something went wrong, file not uploaded!", event, null));
        }
    }

    public void uploadThumbnail(String filename, String filepath,File filesDir,  GameSparksEvent.Type event) {
        Bitmap bmThumbnail = ThumbnailUtils.createVideoThumbnail(filepath, MediaStore.Video.Thumbnails.MINI_KIND);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bmThumbnail.compress(Bitmap.CompressFormat.JPEG, 70, bos);
        try {
            File file = new File(filesDir,"temp_thumbnail_video.jpg");
            bos.writeTo(new BufferedOutputStream(new FileOutputStream(file)));
            upload(filename,file.getPath(),event);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void uploadCircularProfileImage(String filename, String filepath, File filesDir, GameSparksEvent.Type event){
        File image = new File(filepath);
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(),bmOptions);
        bitmap = Bitmap.createScaledBitmap(bitmap,250,250,true);
        bitmap = getCircleBitmap(bitmap);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 70, bos);
        try {
            File file = new File(filesDir,"temp_profile_circled.jpg");
            bos.writeTo(new BufferedOutputStream(new FileOutputStream(file)));
            upload(filename,file.getPath(),event);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Bitmap getCircleBitmap(Bitmap bm) {
        int sice = Math.min((bm.getWidth()), (bm.getHeight()));
        Bitmap bitmap = ThumbnailUtils.extractThumbnail(bm, sice, sice);
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

    public static String generateRandName(int length){
        String letters = "1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        String randomString = "";
        Random random = new Random();
        for(int i = 0; i<length;i++){
            int rand = random.nextInt(letters.length());
            char nextChar = letters.charAt(rand);
            randomString += nextChar;
        }
        return randomString;
    }

    public static String generateMongoOID() {
        Long tsLong = System.currentTimeMillis() / 1000L;
        return (getFirst8(Long.toHexString(tsLong)) + generateRandom() + generateRandom());

    }

    private static String generateRandom() {
        BigInteger b = new BigInteger(256, new Random());
        long number = b.longValue();
        return getFirst8(Long.toHexString((number)));
    }

    private static String getFirst8(String str) {
        return str.substring(0, Math.min(str.length(), 8));
    }
}
