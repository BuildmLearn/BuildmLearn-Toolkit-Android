package org.buildmlearn.toolkit.videoCollectionTemplate.data;

import android.os.Parcel;
import android.os.Parcelable;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Created by Anupam (opticod) on 12/5/16.
 */
public class VideoModel implements Parcelable {
    private static final String ROOT_TAG = "item";
    private static final String TITLE_TAG = "video_title";
    private static final String DESCRIPTION_TAG = "video_description";
    private static final String LINK_TAG = "video_link";
    private static final String THUMB_LINK_TAG = "video_thumb_link";
    public final Creator<VideoModel> CREATOR = new Creator<VideoModel>() {
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

    public VideoModel(String title, String description, String link, String thumbnail_url) {
        this.title = title;
        this.description = description;
        this.link = link;
        this.thumbnail_url = thumbnail_url;
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

    public Element getXml(Document doc) {
        Element rootElement = doc.createElement(ROOT_TAG);
        Element titleElement = doc.createElement(TITLE_TAG);
        titleElement.appendChild(doc.createTextNode(title));
        rootElement.appendChild(titleElement);
        Element descriptionElement = doc.createElement(DESCRIPTION_TAG);
        descriptionElement.appendChild(doc.createTextNode(String.valueOf(description)));
        rootElement.appendChild(descriptionElement);
        Element linkElement = doc.createElement(LINK_TAG);
        linkElement.appendChild(doc.createTextNode(String.valueOf(link)));
        rootElement.appendChild(linkElement);
        Element videoLinkElement = doc.createElement(THUMB_LINK_TAG);
        videoLinkElement.appendChild(doc.createTextNode(String.valueOf(thumbnail_url)));
        rootElement.appendChild(videoLinkElement);
        return rootElement;
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

    public String getThumbnail_url() {
        return thumbnail_url;
    }

    public void setThumbnail_url(String thumbnail_url) {
        this.thumbnail_url = thumbnail_url;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

}
