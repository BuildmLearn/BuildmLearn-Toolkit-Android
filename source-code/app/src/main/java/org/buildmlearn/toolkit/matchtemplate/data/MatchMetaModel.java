package org.buildmlearn.toolkit.matchtemplate.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @brief Model class for Match The Following Meta Template Editor data
 * Created by Anupam (opticod) on 24/7/16.
 */

/**
 * @brief Model used to save match meta entries in database for match template's simulator.
 */
public class MatchMetaModel implements Parcelable {

    public static final String TITLE_TAG = "meta_title";
    public static final String FIRST_TITLE_TAG = "meta_first_list_title";
    public static final String SECOND_TITLE_TAG = "meta_second_list_title";
    public final static Creator<MatchMetaModel> CREATOR = new Creator<MatchMetaModel>() {
        @Override
        public MatchMetaModel createFromParcel(Parcel parcel) {
            return new MatchMetaModel(parcel);
        }

        @Override
        public MatchMetaModel[] newArray(int size) {
            return new MatchMetaModel[size];
        }
    };
    private final String title;
    private final String firstListTitle;
    private final String secondListTitle;

    public MatchMetaModel(String t, String A, String B) {
        this.title = t;
        this.firstListTitle = A;
        this.secondListTitle = B;
    }

    private MatchMetaModel(Parcel in) {
        this.title = in.readString();
        this.firstListTitle = in.readString();
        this.secondListTitle = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(firstListTitle);
        dest.writeString(secondListTitle);
    }

    public String getFirstListTitle() {
        return firstListTitle;
    }

    public String getSecondListTitle() {
        return secondListTitle;
    }

    public String getTitle() {
        return title;
    }

}
