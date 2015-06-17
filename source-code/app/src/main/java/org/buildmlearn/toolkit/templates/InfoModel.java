package org.buildmlearn.toolkit.templates;

import java.io.Serializable;

/**
 * Created by abhishek on 17/06/15 at 9:49 PM.
 */
public class InfoModel implements Serializable {

    private String word;
    private String meaning;

    public InfoModel(String word, String meaning) {
        this.word = word;
        this.meaning = meaning;
    }

    public String getWord() {
        return word;
    }

    public String getMeaning() {
        return meaning;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public void setMeaning(String meaning) {
        this.meaning = meaning;
    }
}
