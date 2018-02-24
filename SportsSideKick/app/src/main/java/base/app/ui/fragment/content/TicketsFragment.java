package base.app.ui.fragment.content;

import android.support.annotation.NonNull;

import com.pixplicity.easyprefs.library.Prefs;

import base.app.R;

import static base.app.util.commons.Utility.CHOSEN_LANGUAGE;

public class TicketsFragment extends StoreFragment {

    @NonNull
    @Override
    protected String getUrl() {
        if (!isAdded()) return "";

        String urlTemplate = getResources().getString(R.string.tickets_url);
        String userLanguage = Prefs.getString(CHOSEN_LANGUAGE, "en");
        if (userLanguage.equals("pt")) {
            urlTemplate = String.format(urlTemplate, userLanguage);
        } else {
            urlTemplate = String.format(urlTemplate, "en");
        }
        return urlTemplate;
    }
}
