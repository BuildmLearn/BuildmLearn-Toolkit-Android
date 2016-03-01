package org.buildmlearn.toolkit.templates;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.Serializable;

/**
 * @brief Model class for Info Template Editor data
 *
 * Created by abhishek on 17/06/15 at 9:49 PM.
 */
public class InfoModel implements Serializable {

    private String infoObject;
    private String infoDescription;

    public InfoModel(String infoObject, String meaning) {
        this.infoObject = infoObject;
        this.infoDescription = meaning;
    }

    public String getInfoObject() {
        return infoObject;
    }

    public String getInfoDescription() {
        return infoDescription;
    }

    public void setInfoDescription(String infoDescription) {
        this.infoDescription = infoDescription;
    }

    public void setWord(String word) {
        this.infoObject = word;
    }

    public Element getXml(Document doc) {
        Element rootElement = doc.createElement("item");
        Element questionElement = doc.createElement("item_title");
        questionElement.appendChild(doc.createTextNode(infoObject));
        rootElement.appendChild(questionElement);
        Element answerElement = doc.createElement("item_description");
        answerElement.appendChild(doc.createTextNode(String.valueOf(infoDescription)));
        rootElement.appendChild(answerElement);
        return rootElement;
    }

    /**
     * Test whether current Model satisfy given query
     *
     * @param query
     * @return
     */
    public boolean contains(String query) {
        query = query.toLowerCase();
        return infoObject.toLowerCase().contains(query)
                || infoDescription.toLowerCase().contains(query);
    }
}
