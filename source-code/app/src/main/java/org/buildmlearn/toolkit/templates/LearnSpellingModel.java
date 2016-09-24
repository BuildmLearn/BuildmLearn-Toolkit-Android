package org.buildmlearn.toolkit.templates;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.Serializable;

/**
 * @brief Model class for Learn Spelling Template Editor data
 * <p/>
 * Created by abhishek on 17/06/15 at 9:49 PM.
 */
public class LearnSpellingModel implements Serializable {

    private String mWord;
    private String mMeaning;

    public LearnSpellingModel(String infoObject, String meaning) {
        this.mWord = infoObject;
        this.mMeaning = meaning;
    }

    public String getWord() {
        return mWord;
    }

    public void setWord(String word) {
        this.mWord = word;
    }

    public String getMeaning() {
        return mMeaning;
    }

    public void setMeaning(String meaning) {
        this.mMeaning = meaning;
    }

    public Element getXml(Document doc) {
        Element rootElement = doc.createElement("item");
        Element questionElement = doc.createElement("word");
        questionElement.appendChild(doc.createTextNode(mWord));
        rootElement.appendChild(questionElement);
        Element answerElement = doc.createElement("meaning");
        answerElement.appendChild(doc.createTextNode(String.valueOf(mMeaning)));
        rootElement.appendChild(answerElement);
        return rootElement;
    }
}
