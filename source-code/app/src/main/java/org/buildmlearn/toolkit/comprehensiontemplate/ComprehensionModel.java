package org.buildmlearn.toolkit.comprehensiontemplate;

import java.util.ArrayList;

/**
 * @brief Simulator code for Quiz Template
 */
public class ComprehensionModel {

    private String title, content;
    private int readTime;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getReadTime() {
        return readTime;
    }

    public void setReadTime(int readTime) {
        this.readTime = readTime;
    }
}
