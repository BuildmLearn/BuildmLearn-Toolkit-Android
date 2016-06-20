package org.buildmlearn.toolkit.infoTemplate.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Anupam (opticod) on 20/6/16.
 */
public class InfoModel implements Parcelable {
    public final Creator<InfoModel> CREATOR = new Creator<InfoModel>() {
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

    public InfoModel(String title, String description) {
        this.title = title;
        this.description = description;
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
