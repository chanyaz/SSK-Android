package base.app.util.ui;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

public class ImageLoader {

    private static ImageLoader instance;

    public static ImageLoader getInstance() {
        if (instance == null) {
            instance = new ImageLoader();
        }
        return instance;
    }

    public void displayImage(String uri, ImageView imageView) {
        displayImage(uri, imageView, null);
    }

    public void displayImage(String uri, ImageView imageView, DisplayImageOptions options) {
        Context ctx = imageView.getContext();
        Glide.with(ctx).load(uri).into(imageView);
    }
}
