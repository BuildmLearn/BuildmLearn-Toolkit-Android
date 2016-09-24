package org.buildmlearn.toolkit.videocollectiontemplate;

import org.buildmlearn.toolkit.videocollectiontemplate.data.VideoContract;

/**
 * @brief Constants used in video collection template's simulator relating databases.
 * <p/>
 * Created by Anupam (opticod) on 13/5/16.
 */
public class Constants {
    public static final String[] VIDEO_COLUMNS = {
            VideoContract.Videos.TABLE_NAME + "." + VideoContract.Videos._ID,
            VideoContract.Videos.TITLE,
            VideoContract.Videos.DESCRIPTION,
            VideoContract.Videos.LINK,
            VideoContract.Videos.THUMBNAIL_URL
    };
    public static final int COL_TITLE = 1;
    public static final int COL_DESCRIPTION = 2;
    public static final int COL_LINK = 3;
    public static final int COL_THUMBNAIL_URL = 4;
    public static String XMLFileName = "video_content.xml";
}
