package org.buildmlearn.toolkit.matchtemplate.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @brief Model class for Match The Following Template Editor data
 * <p/>
 * Created by Anupam (opticod) on 24/7/16.
 */

/**
 * @brief Model used to save match entries in database for match template's simulator.
 */
public class MatchModel implements Parcelable {
    public final static Creator<MatchModel> CREATOR = new Creator<MatchModel>() {
        @Override
        public MatchModel createFromParcel(Parcel parcel) {
            return new MatchModel(parcel);
        }

        @Override
        public MatchModel[] newArray(int size) {
            return new MatchModel[size];
        }
    };

    private String matchA;
    private String matchB;
    private int correct;

    public MatchModel() {

    }

    private MatchModel(Parcel in) {
        this.matchA = in.readString();
        this.matchB = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(matchA);
        dest.writeString(matchB);
    }

    public int getCorrect() {
        return correct;
    }

    public void setCorrect(int correct) {
        this.correct = correct;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getMatchA() {
        return matchA;
    }

    public void setMatchA(String matchA) {
        this.matchA = matchA;
    }

    public String getMatchB() {
        return matchB;
    }

    public void setMatchB(String matchB) {
        this.matchB = matchB;
    }

}
