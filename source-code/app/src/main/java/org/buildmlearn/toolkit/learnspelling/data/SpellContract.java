package org.buildmlearn.toolkit.learnspelling.data;

import android.provider.BaseColumns;

/**
 * Created by Anupam (opticod) on 1/6/16.
 */

/**
 * @brief Contains database contracts for spell template's simulator.
 */
class SpellContract {

    public static final class Spellings implements BaseColumns {

        public static final String TABLE_NAME = "spellings";

        public static final String WORD = "word";
        public static final String MEANING = "meaning";
        public static final String ANSWERED = "answered";
        public static final String ATTEMPTED = "attempted";
    }
}
