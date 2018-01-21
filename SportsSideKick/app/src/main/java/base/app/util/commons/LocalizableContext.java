package base.app.util.commons;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.LocaleList;

import java.util.Locale;

import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.N;

/**
 * Created by Filip on 8/14/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class LocalizableContext extends android.content.ContextWrapper {

    public LocalizableContext(Context base) {
        super(base);
    }

    public static LocalizableContext wrap(Context context, Locale newLocale) {

        Resources res = context.getResources();
        Configuration configuration = res.getConfiguration();

        if (SDK_INT >= N) {
            configuration.setLocale(newLocale);

            LocaleList localeList = new LocaleList(newLocale);
            LocaleList.setDefault(localeList);
            configuration.setLocales(localeList);

            context = context.createConfigurationContext(configuration);
        } else {
            configuration.setLocale(newLocale);
            context = context.createConfigurationContext(configuration);

        }
        return new LocalizableContext(context);
    }
}