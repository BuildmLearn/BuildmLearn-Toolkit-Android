package org.buildmlearn.toolkit.quiztemplate.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by Anupam (opticod) on 11/8/16.
 */

/**
 * @brief Model used to save quiz entries in database for quiz template's simulator.
 */
public class QuizModel implements Parcelable {
    public final static Creator<QuizModel> CREATOR = new Creator<QuizModel>() {
        @Override
        public QuizModel createFromParcel(Parcel parcel) {
            return new QuizModel(parcel);
        }

        @Override
        public QuizModel[] newArray(int size) {
            return new QuizModel[size];
        }
    };
    private String question;
    private ArrayList<String> options;
    private int correctAnswer;

    private QuizModel(Parcel in) {
        this.question = in.readString();
        this.options = in.createStringArrayList();
        this.correctAnswer = in.readInt();
    }

    public QuizModel() {

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(question);
        dest.writeStringList(options);
        dest.writeInt(correctAnswer);
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

}
