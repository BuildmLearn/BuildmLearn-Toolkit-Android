package org.buildmlearn.toolkit.videocollectiontemplate.data;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;

import org.buildmlearn.toolkit.videocollectiontemplate.Constants;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * @brief Used to parse XML and save in database for video collection template's simulator.
 * <p/>
 * Created by Anupam (opticod) on 13/5/16.
 */

public class FetchXMLTask extends AsyncTask<String, Void, Void> {

    private final Context mContext;

    public FetchXMLTask(Context context) {
        mContext = context;
    }

    private static String getValue(String tag, Element element) {
        NodeList nodeList = null;
        NodeList node1 = element.getElementsByTagName(tag);
        if (node1 != null && node1.getLength() != 0)
            nodeList = node1.item(0).getChildNodes();
        if (nodeList == null)
            return "";
        else if (nodeList.getLength() == 0)
            return "";
        else {
            Node node = nodeList.item(0);
            return node.getNodeValue();
        }
    }

    private void saveVideoData(ArrayList<VideoModel> videos) {

        Vector<ContentValues> cVVector = new Vector<>(videos.size());

        for (int i = 0; i < videos.size(); i++) {

            String title;
            String description;
            String link;
            String thumbnail_url;

            VideoModel videoInfo = videos.get(i);

            title = videoInfo.getTitle();
            description = videoInfo.getDescription();
            link = videoInfo.getLink();
            thumbnail_url = videoInfo.getThumbnailUrl();

            ContentValues videoValues = new ContentValues();

            videoValues.put(VideoContract.Videos.TITLE, title);
            videoValues.put(VideoContract.Videos.DESCRIPTION, description);
            videoValues.put(VideoContract.Videos.LINK, link);
            videoValues.put(VideoContract.Videos.THUMBNAIL_URL, thumbnail_url);

            cVVector.add(videoValues);
        }
        // add to database
        if (cVVector.size() > 0) {
            ContentValues[] cvArray = new ContentValues[cVVector.size()];
            cVVector.toArray(cvArray);
            VideoDb db = new VideoDb(mContext);
            db.open();
            db.bulkInsert(cvArray);
            db.close();
        }
    }

    @Override
    protected Void doInBackground(String... params) {

        if (params.length == 0) {
            return null;
        }
        ArrayList<VideoModel> mList;

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        dbf.setValidating(false);

        DocumentBuilder db;
        Document doc;
        try {
            File fXmlFile = new File(Constants.XMLFileName);
            db = dbf.newDocumentBuilder();
            doc = db.parse(fXmlFile);
            doc.normalize();
            mList = new ArrayList<>();

            NodeList childNodes = doc.getElementsByTagName("item");

            for (int i = 0; i < childNodes.getLength(); i++) {
                VideoModel app = new VideoModel();

                Node child = childNodes.item(i);

                if (child.getNodeType() == Node.ELEMENT_NODE) {
                    Element element2 = (Element) child;

                    app.setTitle(getValue("video_title", element2));
                    app.setDescription(getValue("video_description", element2));
                    app.setLink(getValue("video_link", element2));
                    app.setThumbnailUrl(getValue("video_thumb_link", element2));

                }
                mList.add(app);
            }
            saveVideoData(mList);
        } catch (ParserConfigurationException e) {
            return null;
        } catch (FileNotFoundException e) {
            return null;
        } catch (SAXException e) {
            return null;
        } catch (IOException e) {
            return null;
        }
        return null;
    }
}