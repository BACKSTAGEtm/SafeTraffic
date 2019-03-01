package by.backstagedevteam.safetraffic;

import android.text.TextUtils;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;


/**
 * This class implements parsing GPX data from markers and back
 *
 * @author Dmitry Kostyuchenko
 * @see by.backstagedevteam.safetraffic
 * @since 2019
 */
public class GPXParser {
    /**
     * Test data set
     */
    private static final String gpxData = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<gpx version=\"1.1\" creator=\"Yandex Map Constructor\" xmlns=\"http://www.topografix.com/GPX/1/1\"><metadata><name><![CDATA[Речица]]></name><desc/><time>2019-02-21T18:01:46.155Z</time></metadata>\n" +
            "    <wpt lon=\"30.407151382660878\" lat=\"52.36707850789525\"><name><![CDATA[Гимназия. Нерегулируемый пешеходный переход.]]></name></wpt>\n" +
            "    <wpt lon=\"30.40673368479986\" lat=\"52.36679569292205\"><name><![CDATA[Наумова. Нерегулируемый пешеходный переход]]></name></wpt>\n" +
            "    <wpt lon=\"30.402752484485024\" lat=\"52.36825683931516\"><name><![CDATA[Я]]></name></wpt>\n" +
            "</gpx>\n";

    /**
     * This method parsing starting Data set
     *
     * @return array Markers
     */
    public static ArrayList<Markers> initAppParse() {
        ArrayList<Markers> markers = new ArrayList<>();
        try {
            XmlPullParser xpp = prepareXppFromStr(gpxData);
            markers = parser(xpp);
        } catch (Exception e) {
            Log.d("GPXParser", "Error init parse");
        }
        return markers;
    }

    /**
     * This method implements parsing GPX data to Markers
     *
     * @param parser - XmlPullParser prepared for work
     * @return array Markers
     */
    private static ArrayList<Markers> parser(XmlPullParser parser) {
        ArrayList<Markers> markers = new ArrayList<>();
        final String TAG = "GPX Read";
        try {
            boolean isGPX = false;
            boolean isWPT = false;
            boolean isName = false;
            double lon = 0, lat = 0;
            String name = "";
            while (parser.getEventType() != XmlPullParser.END_DOCUMENT) {
                String tmp = "";

                switch (parser.getEventType()) {
                    case XmlPullParser.START_DOCUMENT:
                        Log.d(TAG, "Start document");
                        break;
                    case XmlPullParser.START_TAG:
                        Log.d(TAG, "START TAG: tag name = " + parser.getName()
                                + ", level = " + parser.getDepth()
                                + ", num attrib = " + parser.getAttributeCount());
                        tmp = "";
                        for (int i = 0; i < parser.getAttributeCount(); i++) {
                            tmp = tmp + parser.getAttributeName(i) + " = "
                                    + parser.getAttributeValue(i) + ", ";

                        }
                        if (!TextUtils.isEmpty(tmp)) {
                            Log.d(TAG, "Attribute: " + tmp);
                        }
                        /*****/
                        if (parser.getName().equals("gpx")) {
                            isGPX = true;
                            //Parse metadata
                        }
                        if (isGPX && parser.getName().equals("wpt")) {
                            for (int i = 0; i < parser.getAttributeCount(); i++) {
                                if (parser.getAttributeName(i).equals("lon")) {
                                    lon = Double.valueOf(parser.getAttributeValue(i));
                                }
                                if (parser.getAttributeName(i).equals("lat")) {
                                    lat = Double.valueOf(parser.getAttributeValue(i));
                                }
                                if (lon != 0 && lat != 0) {
                                    isWPT = true;
                                }
                            }
                        }
                        if (isWPT && parser.getName().equals("name")) {
                            isName = true;
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        Log.d(TAG, "END TAG: tag name = " + parser.getName());
                        if (parser.getName().equals("wpt")) {
                            //TODO AddHint!
                            markers.add(new Markers(lat, lon, MarkerType.Crosswalk));
                            isWPT = false;
                            isName = false;
                            lat = 0;
                            lon = 0;
                        }
                        break;
                    case XmlPullParser.TEXT:
                        Log.d(TAG, "text: " + parser.getText());
                        /******/
                        if (isName) {
                            name = parser.getText();
                        }
                        break;
                    default:
                        break;
                }
                parser.next();
            }

        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //Log.d(TAG, "Parse is Done");
        for (Markers item :
                markers) {
            item.printDBG();
        }
        return markers;
    }

    /**
     * This method implements prepared XmlPullParser for parsing from string
     *
     * @param str is GPX data
     * @return prepared XmlPullParser
     * @throws XmlPullParserException
     */
    public static XmlPullParser prepareXppFromStr(String str) throws XmlPullParserException {
        XmlPullParserFactory factory;
        factory = XmlPullParserFactory.newInstance();
        //factory.setNamespaceAware(true);
        XmlPullParser xpp = factory.newPullParser();
        xpp.setInput(new StringReader(str));
        return xpp;
    }

    /**
     * This method implements prepared XmlPullParser for parsing from files
     *
     * @param filePath to GPX file
     * @return prepared XmlPullParser
     * @throws XmlPullParserException
     * @throws FileNotFoundException
     */
    public static XmlPullParser prepareXppFromFile(String filePath) throws XmlPullParserException, FileNotFoundException {
        //TODO: Fix unknow files!
        //TODO: Test implements!
        XmlPullParserFactory factory;
        factory = XmlPullParserFactory.newInstance();
        XmlPullParser xpp = factory.newPullParser();
        File file = new File(filePath);
        FileInputStream fis = new FileInputStream(file);
        xpp.setInput(new InputStreamReader(fis));
        return xpp;
    }

    public static XmlPullParser prepareXpp() throws XmlPullParserException {
        /***String****/
        //TODO: Old method. Delete.
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory = XmlPullParserFactory.newInstance();
        //factory.setNamespaceAware(true);
        XmlPullParser xpp = factory.newPullParser();
        //xpp.setInput(new StringReader("<data><phone><company>Samsung</company></phone></data>"));
        xpp.setInput(new StringReader("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<gpx version=\"1.1\" creator=\"Yandex Map Constructor\" xmlns=\"http://www.topografix.com/GPX/1/1\"><metadata><name><![CDATA[Речица]]></name><desc/><time>2019-02-21T18:01:46.155Z</time></metadata>\n" +
                "    <wpt lon=\"30.407151382660878\" lat=\"52.36707850789525\"><name><![CDATA[Гимназия. Нерегулируемый пешеходный переход.]]></name></wpt>\n" +
                "    <wpt lon=\"30.40673368479986\" lat=\"52.36679569292205\"><name><![CDATA[Наумова. Нерегулируемый пешеходный переход]]></name></wpt>\n" +
                "</gpx>\n"));
        /***********/
        ////TODO Fix unknow files!
        //File file = new File(Environment.getExternalStorageDirectory() + "/" + fileName);

        //Log.d(TAG, "Data Directory: " + Coontext.fileList());
        //File file = new File("/files" + "/" + fileName);
        //FileInputStream fis = new FileInputStream(file);
        //parser.setInput(new InputStreamReader(fis));

        return xpp;
    }

    //export Markers from file
    public static boolean exportFromFile(ArrayList<Markers> markers) {

        return true;
    }

    //export collection from file


}
