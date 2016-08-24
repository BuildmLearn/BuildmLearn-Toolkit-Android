package org.buildmlearn.toolkit.flashcardtemplate.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Anupam (opticod) on 10/8/16.
 */

/**
 * @brief Model used to save flash entries in database for flash card template's simulator.
 */
public class FlashModel implements Parcelable {
    public final static Creator<FlashModel> CREATOR = new Creator<FlashModel>() {
        @Override
        public FlashModel createFromParcel(Parcel parcel) {
            return new FlashModel(parcel);
        }

        @Override
        public FlashModel[] newArray(int size) {
            return new FlashModel[size];
        }
    };

    private String question;
    private String answer;
    private String hint;
    private String base64;

    private FlashModel(Parcel in) {
        this.question = in.readString();
        this.answer = in.readString();
        this.hint = in.readString();
        this.base64 = in.readString();
    }

    public FlashModel() {

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(question);
        dest.writeString(answer);
        dest.writeString(hint);
        dest.writeString(base64);
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getBase64() {
        return base64;
    }

    public void setBase64(String base64) {
        this.base64 = base64;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }
}
