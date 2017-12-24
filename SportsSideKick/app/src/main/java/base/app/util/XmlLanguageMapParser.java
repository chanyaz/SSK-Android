package base.app.util;

import android.content.Context;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by Filip on 9/12/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class XmlLanguageMapParser {



    private final static String KEY = "key", STRING = "string", DICT = "dict";
    private final static String LANGUAGE_TAG = "language", NAME_TAG = "name";

    /**
     * This class parses an iOS plist with a dict element of language details into a hash map.
     * Map contains a list of pairs where languageShortCode is key, and full language name is a value
     */
    public static HashMap<String, String> parseLanguage(Context context, int xmlid) {


        XmlPullParser parser = context.getResources().getXml(xmlid);

        HashMap<String,String> map = new HashMap<>();


        try {
            parser.next();
            int eventType = parser.getEventType();
            String lastTag = null;
            String lastKey = null;
            String languageName = null;
            String languageShortCode = null;
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    lastTag = parser.getName();
                    if(DICT.equalsIgnoreCase(lastTag)){
                        languageName = null;
                        languageShortCode = null;
                    }
                }
                else if (eventType == XmlPullParser.TEXT) {
                    // some text
                    if (KEY.equalsIgnoreCase(lastTag)) {
                        // start tracking a new key
                        lastKey = parser.getText();
                    }
                    else if (STRING.equalsIgnoreCase(lastTag)) {
                        // a new string for the last encountered key
                        if(LANGUAGE_TAG.equals(lastKey)){
                            languageShortCode = parser.getText();
                        }
                        else if(NAME_TAG.equals(lastKey)){
                            languageName = parser.getText();
                        }
                        if(languageName!=null && languageShortCode!=null){
                            map.put(languageShortCode,languageName);
                        }
                    }
                }
                eventType = parser.next();
            }
        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }
        return map;
    }
}