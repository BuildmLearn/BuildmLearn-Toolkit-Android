package org.buildmlearn.toolkit.templates;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.Serializable;

/**
 * @brief Model class for Match The Following Meta Template Editor data
 * Created by Anupam (opticod) on 16/7/16.
 */
public class MatchMetaModel implements Serializable {

    public static final String TITLE_TAG = "meta_title";
    public static final String FIRST_TITLE_TAG = "meta_first_list_title";
    public static final String SECOND_TITLE_TAG = "meta_second_list_title";
    private static final String ROOT_TAG = "meta_details";

    private String title;
    private String first_list_title;
    private String second_list_title;

    public MatchMetaModel(String t, String A, String B) {
        this.title = t;
        this.first_list_title = A;
        this.second_list_title = B;
    }

    public String getFirstListTitle() {
        return first_list_title;
    }

    public void setFirstListTitle(String first_list_title) {
        this.first_list_title = first_list_title;
    }

    public String getSecondListTitle() {
        return second_list_title;
    }

    public void setSecond_list_title(String second_list_title) {
        this.second_list_title = second_list_title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Element getXml(Document doc) {
        Element rootElement = doc.createElement(ROOT_TAG);
        Element title_elem = doc.createElement(TITLE_TAG);
        title_elem.appendChild(doc.createTextNode(title));
        rootElement.appendChild(title_elem);
        Element first_list_elem = doc.createElement(FIRST_TITLE_TAG);
        first_list_elem.appendChild(doc.createTextNode(first_list_title));
        rootElement.appendChild(first_list_elem);
        Element second_list_elem = doc.createElement(SECOND_TITLE_TAG);
        second_list_elem.appendChild(doc.createTextNode(second_list_title));
        rootElement.appendChild(second_list_elem);
        return rootElement;
    }
}
