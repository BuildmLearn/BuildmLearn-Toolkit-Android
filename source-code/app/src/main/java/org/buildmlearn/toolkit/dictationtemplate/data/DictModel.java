package org.buildmlearn.toolkit.dictationtemplate.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Anupam (opticod) on 10/7/16.
 */

/**
 * @brief Model used to save dict entries in database for comprehension template's simulator.
 */
public class DictModel implements Parcelable {
    public final static Parcelable.Creator<DictModel> CREATOR = new Parcelable.Creator<DictModel>() {
        @Override
        public DictModel createFromParcel(Parcel parcel) {
            return new DictModel(parcel);
        }

        @Override
        public DictModel[] newArray(int size) {
            return new DictModel[size];
        }
    };
    private String title;
    private String passage;

    public DictModel() {
    }

    private DictModel(Parcel in) {
        this.title = in.readString();
        this.passage = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(passage);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPassage() {
        return passage;
    }

    public void setPassage(String passage) {
        this.passage = passage;
    }

}
