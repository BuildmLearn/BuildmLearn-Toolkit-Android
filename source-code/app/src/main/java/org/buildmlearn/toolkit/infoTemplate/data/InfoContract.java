package org.buildmlearn.toolkit.infoTemplate.data;

import android.provider.BaseColumns;

/**
 * Created by Anupam (opticod) on 20/6/16.
 */

public class InfoContract {

    public static final class Info implements BaseColumns {

        public static final String TABLE_NAME = "mLearning";

        public static final String TITLE = "title";
        public static final String DESCRIPTION = "description";

    }
}
