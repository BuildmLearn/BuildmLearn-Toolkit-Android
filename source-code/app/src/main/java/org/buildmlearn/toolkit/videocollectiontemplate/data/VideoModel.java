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
    private String thumbnailUrl;

    public VideoModel() {
    }

    private VideoModel(Parcel in) {
        this.title = in.readString();
        this.description = in.readString();
        this.link = in.readString();
        this.thumbnailUrl = in.readString();
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
        dest.writeString(thumbnailUrl);
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
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

}
