package org.buildmlearn.toolkit.dictationtemplate.data;

import android.provider.BaseColumns;

/**
 * Created by Anupam (opticod) on 10/7/16.
 */

/**
 * @brief Contains database contracts for dictation template's simulator.
 */

public class DictContract {

    public static final class Dict implements BaseColumns {

        public static final String TABLE_NAME = "dictation";

        public static final String TITLE = "title";
        public static final String PASSAGE = "passage";

    }
}
