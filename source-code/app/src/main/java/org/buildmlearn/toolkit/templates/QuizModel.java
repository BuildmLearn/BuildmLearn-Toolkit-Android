package org.buildmlearn.toolkit.templates;

import java.util.ArrayList;

/**
 * Created by abhishek on 28/5/15.
 */
public class QuizModel {

    private String question;
    private ArrayList<String> options;
    private int correctAnswer;

    public QuizModel(String question, ArrayList<String> options, int correctAnswer) {
        this.question = question;
        this.options = options;
        this.correctAnswer = correctAnswer;
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
}
