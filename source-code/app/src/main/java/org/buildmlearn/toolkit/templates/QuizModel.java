package org.buildmlearn.toolkit.templates;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @brief Model class for Quiz Template Editor data
 * <p/>
 * Created by abhishek on 28/5/15.
 */

public class QuizModel implements Serializable {

    private final String question;
    private final ArrayList<String> options;
    private final int correctAnswer;
    private boolean isSelected;

    public QuizModel(String question, ArrayList<String> options, int correctAnswer) {
        this.question = question;
        this.options = options;
        this.correctAnswer = correctAnswer;
        this.isSelected = false;
    }

    public String getQuestion() {
        return question;
    }

    public ArrayList<String> getOptions() {
        return options;
    }

    public int getCorrectAnswer() {
        return correctAnswer;
    }

    public Element getXml(Document doc) {
        Element rootElement = doc.createElement("item");
        Element questionElement = doc.createElement("question");
        questionElement.appendChild(doc.createTextNode(question));
        rootElement.appendChild(questionElement);
        for (String option : options) {
            Element optionElement = doc.createElement("option");
            optionElement.appendChild(doc.createTextNode(option));
            rootElement.appendChild(optionElement);
        }

        Element answerElement = doc.createElement("answer");
        answerElement.appendChild(doc.createTextNode(String.valueOf(correctAnswer)));
        rootElement.appendChild(answerElement);
        return rootElement;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }
}
