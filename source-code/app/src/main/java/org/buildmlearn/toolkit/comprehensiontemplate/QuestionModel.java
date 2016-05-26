package org.buildmlearn.toolkit.comprehensiontemplate;

import java.util.ArrayList;

/**
 * @brief Simulator code for Comprehension Template
 */
public class QuestionModel {

    private String question, answer;
    private ArrayList<String> options;

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public ArrayList<String> getOptions() {
        return options;
    }

    public void setOptions(ArrayList<String> options) {
        this.options = options;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }


}
