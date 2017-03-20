package tv.sportssidekick.sportssidekick.util;

import android.content.Context;
import android.graphics.Bitmap;
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

import tv.sportssidekick.sportssidekick.service.GameSparksEvent;

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
                regionType           /* Region for your identity pool--US_EAST_1 or EU_WEST_1*/
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
        TransferObserver observer = transferUtility.upload(
                bucket,     /* The bucket to upload to */
                filename,    /* The key for the uploaded object */
                new File(filepath)/* The file where the data to upload exists */
        );
        observer.setTransferListener(new TransferListener() {
            @Override
            public void onStateChanged(int id, TransferState state) {
                if(TransferState.COMPLETED.equals(state)){
                    EventBus.getDefault().post(new GameSparksEvent("File uploaded!", event, baseUrl+filename));
                }

            }
            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) { }
            @Override
            public void onError(int id, Exception ex) {
                // Notify about error
                EventBus.getDefault().post(new GameSparksEvent("Something went wrong, file not uploaded!", event, null));
            }
        });
    }

    public void uploadThumbnail(String filename, String filepath,  GameSparksEvent.Type event) {
        Bitmap bmThumbnail = ThumbnailUtils.createVideoThumbnail(filepath, MediaStore.Video.Thumbnails.MINI_KIND);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bmThumbnail.compress(Bitmap.CompressFormat.JPEG, 70, bos);
        try {
            File file = new File("temp_thumbnail_video.jpg");
            bos.writeTo(new BufferedOutputStream(new FileOutputStream(file)));
            upload(filename,file.getPath(),event);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
