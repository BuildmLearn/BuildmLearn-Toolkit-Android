package org.buildmlearn.toolkit.comprehensiontemplate.data;

import android.provider.BaseColumns;

/**
 * Created by Anupam (opticod) on 5/6/16.
 */

/**
 * @brief Contains database contracts for comprehension template's simulator.
 */

class ComprehensionContract {

    public static final class Questions implements BaseColumns {

        public static final String TABLE_NAME = "questions";

        public static final String QUESTION = "question";
        public static final String OPTION_1 = "option_1";
        public static final String OPTION_2 = "option_2";
        public static final String OPTION_3 = "option_3";
        public static final String OPTION_4 = "option_4";
        public static final String CORRECT_ANSWER = "correct_answer";
        public static final String ANSWERED = "answered";
        public static final String ATTEMPTED = "attempted";

    }

    public static final class MetaDetails implements BaseColumns {

        public static final String TABLE_NAME = "meta_details";

        public static final String TITLE = "title";
        public static final String PASSAGE = "passage";
        public static final String TIME = "time";

    }
}
