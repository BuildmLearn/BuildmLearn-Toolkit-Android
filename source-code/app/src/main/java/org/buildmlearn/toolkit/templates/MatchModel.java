package org.buildmlearn.toolkit.templates;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.Serializable;

/**
 * @brief Model class for Match The Following Template Editor data
 * <p/>
 * Created by Anupam (opticod) on 16/7/16.
 */

public class MatchModel implements Serializable {

    private String matchA;
    private String matchB;

    public MatchModel(String A, String B) {
        this.matchA = A;
        this.matchB = B;
    }

    public String getMatchA() {
        return matchA;
    }

    public void setMatchA(String matchA) {
        this.matchA = matchA;
    }

    public String getMatchB() {
        return matchB;
    }

    public void setMatchB(String matchB) {
        this.matchB = matchB;
    }

    public Element getXml(Document doc) {
        Element rootElement = doc.createElement("item");
        Element first_list_item = doc.createElement("first_list_item");
        first_list_item.appendChild(doc.createTextNode(matchA));
        rootElement.appendChild(first_list_item);
        Element second_list_item = doc.createElement("second_list_item");
        second_list_item.appendChild(doc.createTextNode(matchB));
        rootElement.appendChild(second_list_item);
        return rootElement;
    }
}
