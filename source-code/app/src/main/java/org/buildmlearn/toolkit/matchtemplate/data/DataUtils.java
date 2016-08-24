package org.buildmlearn.toolkit.matchtemplate.data;

import org.buildmlearn.toolkit.matchtemplate.Constants;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by Anupam (opticod) on 24/7/16.
 */

/**
 * @brief Contains xml data utils for match template's simulator.
 */
public class DataUtils {

    public static String[] readTitleAuthor() {
        String result[] = new String[2];
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        dbf.setValidating(false);

        DocumentBuilder db;
        Document doc;
        try {

            File fXmlFile = new File(Constants.XMLFileName);
            db = dbf.newDocumentBuilder();
            doc = db.parse(fXmlFile);
            doc.normalize();

            result[0] = doc.getElementsByTagName("title").item(0).getChildNodes()
                    .item(0).getNodeValue();

            result[1] = doc.getElementsByTagName("name").item(0).getChildNodes()
                    .item(0).getNodeValue();

        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
