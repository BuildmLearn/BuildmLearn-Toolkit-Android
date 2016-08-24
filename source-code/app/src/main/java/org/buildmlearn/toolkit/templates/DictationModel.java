package org.buildmlearn.toolkit.templates;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.Serializable;

/**
 * @brief Model class for Dictation Template Editor data
 * <p/>
 * Created by Anupam (opticod) on 4/7/16.
 */
public class DictationModel implements Serializable {

    public static final String TITLE_TAG = "dictation_title";
    public static final String PASSAGE_TAG = "dictation_passage";
    private static final String ROOT_TAG = "item";
    private String title;
    private String passage;

    private boolean expanded;

    public DictationModel(String title, String passage) {
        this.title = title;
        this.passage = passage;
        this.expanded = false;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPassage() {
        return passage;
    }

    public void setPassage(String passage) {
        this.passage = passage;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    public Element getXml(Document doc) {
        Element rootElement = doc.createElement(ROOT_TAG);
        Element titleElement = doc.createElement(TITLE_TAG);
        titleElement.appendChild(doc.createTextNode(title));
        rootElement.appendChild(titleElement);
        Element passageElement = doc.createElement(PASSAGE_TAG);
        passageElement.appendChild(doc.createTextNode(passage));
        rootElement.appendChild(passageElement);
        return rootElement;
    }
}
