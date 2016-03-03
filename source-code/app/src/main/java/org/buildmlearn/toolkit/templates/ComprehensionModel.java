package org.buildmlearn.toolkit.templates;

import java.io.Serializable;

/**
 * @brief Model class for Quiz Template Editor data
 *
 * Created by shikher on 02/03/16.
 */
public class ComprehensionModel implements Serializable {

    private boolean isComprehension;
    private String comprehension;
    private QuizModel quizModel;

    public ComprehensionModel(boolean isComprehension, String comprehension, QuizModel quizModel) {
        this.isComprehension = isComprehension;
        this.comprehension = comprehension;
        this.quizModel = quizModel;
    }

    public boolean isComprehension() {
        return isComprehension;
    }

    public void setIsComprehension(boolean isComprehension) {
        this.isComprehension = isComprehension;
    }

    public QuizModel getQuizModel() {
        return quizModel;
    }

    public void setQuizModel(QuizModel quizModel) {
        this.quizModel = quizModel;
    }

    public String getComprehension() {
        return comprehension;
    }

    public void setComprehension(String comprehension) {
        this.comprehension = comprehension;
    }
}
