package org.buildmlearn.toolkit.videocollectiontemplate.data;

import android.provider.BaseColumns;

/**
 * @brief Contains database contracts for video collection template's simulator.
 * <p/>
 * Created by Anupam (opticod) on 13/5/16.
 */

public class VideoContract {

    public static final class Videos implements BaseColumns {

        public static final String TABLE_NAME = "videos";

        public static final String TITLE = "title";
        public static final String DESCRIPTION = "description";
        public static final String LINK = "link";
        public static final String THUMBNAIL_URL = "thumbnail_url";

    }
}
