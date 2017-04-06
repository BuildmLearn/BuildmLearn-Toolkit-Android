package org.quizGen.shasha.infotemplate.data;

import android.provider.BaseColumns;

/**
 * @brief Contains database contracts for info template's simulator.
 */
public class InfoContract {

    public static final class Info implements BaseColumns {

        public static final String TABLE_NAME = "mLearning";

        public static final String TITLE = "title";
        public static final String DESCRIPTION = "description";

    }
}
