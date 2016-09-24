package org.buildmlearn.toolkit.flashcardtemplate.data;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;

import org.buildmlearn.toolkit.flashcardtemplate.Constants;
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
 * Created by Anupam (opticod) on 10/8/16.
 */

/**
 * @brief Used to parse XML and save in database for flash card template's simulator.
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

    private void saveFlashData(ArrayList<FlashModel> Flashs) {

        Vector<ContentValues> cVVector = new Vector<>(Flashs.size());

        for (int i = 0; i < Flashs.size(); i++) {

            String question;
            String answer;
            String hint;
            String base64;

            FlashModel FlashFlash = Flashs.get(i);

            question = FlashFlash.getQuestion();
            answer = FlashFlash.getAnswer();
            hint = FlashFlash.getHint();
            base64 = FlashFlash.getBase64();

            ContentValues FlashValues = new ContentValues();

            FlashValues.put(FlashContract.FlashCards.QUESTION, question);
            FlashValues.put(FlashContract.FlashCards.ANSWER, answer);
            FlashValues.put(FlashContract.FlashCards.HINT, hint);
            FlashValues.put(FlashContract.FlashCards.BASE64, base64);

            cVVector.add(FlashValues);
        }
        // add to database
        if (cVVector.size() > 0) {
            ContentValues[] cvArray = new ContentValues[cVVector.size()];
            cVVector.toArray(cvArray);
            FlashDb db = new FlashDb(mContext);
            db.open();
            db.bulkInsertFlashCards(cvArray);
            db.close();
        }
    }

    @Override
    protected Void doInBackground(String... params) {

        if (params.length == 0) {
            return null;
        }
        ArrayList<FlashModel> mList;

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
                FlashModel app = new FlashModel();

                Node child = childNodes.item(i);

                if (child.getNodeType() == Node.ELEMENT_NODE) {
                    Element element2 = (Element) child;

                    app.setQuestion(getValue("question", element2));
                    app.setAnswer(getValue("answer", element2));
                    app.setHint(getValue("hint", element2));
                    app.setBase64(getValue("image", element2));
                }
                mList.add(app);
            }
            saveFlashData(mList);
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