package org.buildmlearn.toolkit.quiztemplate.data;

import android.provider.BaseColumns;

/**
 * Created by Anupam (opticod) on 14/8/16.
 */

/**
 * @brief Contains database contracts for quiz template's simulator.
 */
class QuizContract {

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
}
