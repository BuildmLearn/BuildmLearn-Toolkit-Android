package org.buildmlearn.toolkit.templates;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @brief Model class for Quiz Template Editor data
 *
 * Created by abhishek on 28/5/15.
 */

public class QuizModel implements Serializable {

    private String question;
    private ArrayList<String> options;
    private int correctAnswer;
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

    public void setQuestion(String question) {
        this.question = question;
    }

    public ArrayList<String> getOptions() {
        return options;
    }

    public void setOptions(ArrayList<String> options) {
        this.options = options;
    }

    public int getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(int correctAnswer) {
        this.correctAnswer = correctAnswer;
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

    /**
     * Test whether current Model satisfy given query
     *
     * @param query
     * @return
     */
    public boolean contains(String query) {
        query = query.toLowerCase();
        if(question.toLowerCase().contains(query) )
            return true;
        for (String option : options) {
            if (option.toLowerCase().contains(query))
                return true;
        }
        return false;
    }
}
