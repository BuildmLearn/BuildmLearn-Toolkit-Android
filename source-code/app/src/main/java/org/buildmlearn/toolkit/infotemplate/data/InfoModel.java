package org.buildmlearn.toolkit.infotemplate.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Anupam (opticod) on 20/6/16.
 */

/**
 * @brief Model used to save info entries in database for info template's simulator.
 */
public class InfoModel implements Parcelable {
    public final static Parcelable.Creator<InfoModel> CREATOR = new Parcelable.Creator<InfoModel>() {
        @Override
        public InfoModel createFromParcel(Parcel parcel) {
            return new InfoModel(parcel);
        }

        @Override
        public InfoModel[] newArray(int size) {
            return new InfoModel[size];
        }
    };
    private String title;
    private String description;

    public InfoModel() {
    }

    private InfoModel(Parcel in) {
        this.title = in.readString();
        this.description = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(description);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
