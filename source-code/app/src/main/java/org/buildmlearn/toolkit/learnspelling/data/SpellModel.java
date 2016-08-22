package org.buildmlearn.toolkit.learnspelling.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Anupam (opticod) on 1/6/16.
 */

/**
 * @brief Model used to save spell entries in database for spell template's simulator.
 */
public class SpellModel implements Parcelable {
    public final static Creator<SpellModel> CREATOR = new Creator<SpellModel>() {
        @Override
        public SpellModel createFromParcel(Parcel parcel) {
            return new SpellModel(parcel);
        }

        @Override
        public SpellModel[] newArray(int size) {
            return new SpellModel[size];
        }
    };

    private String word;
    private String meaning;

    private SpellModel(Parcel in) {
        this.word = in.readString();
        this.meaning = in.readString();
    }

    public SpellModel() {

    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getMeaning() {
        return meaning;
    }

    public void setMeaning(String meaning) {
        this.meaning = meaning;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(word);
        dest.writeString(meaning);
    }

}
