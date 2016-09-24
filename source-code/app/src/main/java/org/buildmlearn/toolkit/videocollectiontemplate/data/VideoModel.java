package org.buildmlearn.toolkit.videocollectiontemplate.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @brief Model used to save video entries in database for video collection template's simulator.
 * <p/>
 * Created by Anupam (opticod) on 12/5/16.
 */
public class VideoModel implements Parcelable {
    public final static Parcelable.Creator<VideoModel> CREATOR = new Parcelable.Creator<VideoModel>() {
        @Override
        public VideoModel createFromParcel(Parcel parcel) {
            return new VideoModel(parcel);
        }

        @Override
        public VideoModel[] newArray(int size) {
            return new VideoModel[size];
        }
    };

    private String title;
    private String description;
    private String link;
    private String thumbnail_url;

    public VideoModel() {
    }

    private VideoModel(Parcel in) {
        this.title = in.readString();
        this.description = in.readString();
        this.link = in.readString();
        this.thumbnail_url = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(link);
        dest.writeString(thumbnail_url);
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

    public String getThumbnailUrl() {
        return thumbnail_url;
    }

    public void setThumbnailUrl(String thumbnail_url) {
        this.thumbnail_url = thumbnail_url;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

}
