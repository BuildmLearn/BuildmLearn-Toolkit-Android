package org.quizGen.shasha.matchtemplate.data;

import android.provider.BaseColumns;

/**
 * @brief Contains database contracts for match template's simulator.
 */
class MatchContract {

    public static final class Matches implements BaseColumns {

        public static final String TABLE_NAME = "matches";

        public static final String MATCH_A = "match_a";
        public static final String MATCH_B = "match_b";

    }

    public static final class MetaDetails implements BaseColumns {

        public static final String TABLE_NAME = "meta_details";

        public static final String TITLE = "title";
        public static final String FIRST_TITLE_TAG = "first_list_title";
        public static final String SECOND_TITLE_TAG = "second_list_title";

    }
}
