package org.buildmlearn.toolkit.templates;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.Serializable;

/**
 * @brief Model class for VideoCollection Template Editor data
 * <p/>
 * Created by Anupam (opticod) on 4/5/16.
 */
public class VideoModel implements Serializable {

    public static final String TITLE_TAG = "video_title";
    public static final String DESCRIPTION_TAG = "video_description";
    public static final String LINK_TAG = "video_link";
    public static final String THUMB_LINK_TAG = "video_thumb_link";
    private static final String ROOT_TAG = "item";
    private String title;
    private String description;
    private String link;
    private String thumbnail_url;

    public VideoModel(String title, String description, String link, String thumbnail_url) {
        this.title = title;
        this.description = description;
        this.link = link;
        this.thumbnail_url = thumbnail_url;
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
}
