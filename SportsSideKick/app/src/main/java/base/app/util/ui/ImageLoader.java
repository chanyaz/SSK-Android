package base.app.util.ui;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;


public class ImageLoader {

    private static ImageLoader instance;

    public static ImageLoader getInstance() {
        if (instance == null) {
            instance = new ImageLoader();
        }
        return instance;
    }

    public void displayImage(String uri, ImageView imageView) {
        Context ctx = imageView.getContext();
        Glide.with(ctx).load(uri).into(imageView);
    }
}
