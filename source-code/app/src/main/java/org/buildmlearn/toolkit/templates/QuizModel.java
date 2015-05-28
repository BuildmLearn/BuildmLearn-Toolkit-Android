package org.buildmlearn.toolkit.templates;

import java.io.Serializable;
import java.util.ArrayList;

/**
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

    public ArrayList<String> getOptions() {
        return options;
    }

    public int getCorrectAnswer() {
        return correctAnswer;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public void setOptions(ArrayList<String> options) {
        this.options = options;
    }

    public void setCorrectAnswer(int correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public String getXml() {
        return null;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }
}
