package org.buildmlearn.toolkit.matchtemplate.data;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;

import org.buildmlearn.toolkit.matchtemplate.Constants;
import org.buildmlearn.toolkit.matchtemplate.data.MatchContract.Matches;
import org.buildmlearn.toolkit.matchtemplate.data.MatchContract.MetaDetails;
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
 * Created by Anupam (opticod) on 24/7/16.
 */

/**
 * @brief Used to parse XML and save in database for match template's simulator.
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

    private void saveMetaData(MatchMetaModel metaDetails) {

        String title;
        String first_list_title;
        String second_list_title;

        title = metaDetails.getTitle();
        first_list_title = metaDetails.getFirstListTitle();
        second_list_title = metaDetails.getSecondListTitle();

        ContentValues metaValues = new ContentValues();

        metaValues.put(MetaDetails.TITLE, title);
        metaValues.put(MetaDetails.FIRST_TITLE_TAG, first_list_title);
        metaValues.put(MetaDetails.SECOND_TITLE_TAG, second_list_title);

        MatchDb db = new MatchDb(mContext);
        db.open();
        db.bulkInsertMetaDetails(new ContentValues[]{metaValues});
        db.close();
    }

    private void saveMatches(ArrayList<MatchModel> matches) {

        Vector<ContentValues> cVVector = new Vector<>(matches.size());

        for (int i = 0; i < matches.size(); i++) {

            MatchModel matchInfo = matches.get(i);
            ContentValues matchValues = new ContentValues();

            matchValues.put(Matches.MATCH_A, matchInfo.getMatchA());
            matchValues.put(Matches.MATCH_B, matchInfo.getMatchB());

            cVVector.add(matchValues);
        }
        // add to database
        if (cVVector.size() > 0) {
            ContentValues[] cvArray = new ContentValues[cVVector.size()];
            cVVector.toArray(cvArray);
            MatchDb db = new MatchDb(mContext);
            db.open();
            db.bulkInsertMatches(cvArray);
            db.close();
        }
    }

    @Override
    protected Void doInBackground(String... params) {

        if (params.length == 0) {
            return null;
        }
        ArrayList<MatchModel> mList;

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

            String match_title = doc.getElementsByTagName(MatchMetaModel.TITLE_TAG).item(0).getTextContent();
            String first_list_title = doc.getElementsByTagName(MatchMetaModel.FIRST_TITLE_TAG).item(0).getTextContent();
            String second_list_title = doc.getElementsByTagName(MatchMetaModel.SECOND_TITLE_TAG).item(0).getTextContent();

            saveMetaData(new MatchMetaModel(match_title, first_list_title, second_list_title));

            NodeList childNodes = doc.getElementsByTagName("item");

            for (int i = 0; i < childNodes.getLength(); i++) {
                MatchModel app = new MatchModel();

                Node child = childNodes.item(i);

                if (child.getNodeType() == Node.ELEMENT_NODE) {
                    Element element2 = (Element) child;

                    app.setMatchA(getValue("first_list_item", element2));
                    app.setMatchB(getValue("second_list_item", element2));
                }
                mList.add(app);
            }
            saveMatches(mList);
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