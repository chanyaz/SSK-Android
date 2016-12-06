package tv.sportssidekick.sportssidekick;

import android.content.Context;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.utils.L;

import tv.sportssidekick.sportssidekick.model.Model;

/**
 * Created by Djordje Krutil on 5.12.2016..
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */
public class Application extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();
        initImageLoader(getApplicationContext());
        Model.getInstance();
    }

    //region AppImage Loader
    public static void initImageLoader(Context context) {
        // This configuration tuning is custom. You can tune every option, you may tune some of them,
        // or you can create default configuration by
        // ImageLoaderConfiguration.createDefault(this);
        // method.
        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context);
        config.threadPriority(Thread.NORM_PRIORITY);
        config.denyCacheImageMultipleSizesInMemory();
        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        config.diskCacheSize(50 * 1024 * 1024); // 50 MiB
        config.diskCacheExtraOptions(480, 320, null);
        config.tasksProcessingOrder(QueueProcessingType.LIFO);
        // config.writeDebugLogs(); //
        L.writeLogs(false);
        ImageLoader.getInstance().init(config.build());
    }
}
