package org.quizGen.shasha.dictationtemplate;

import org.quizGen.shasha.dictationtemplate.data.DictContract;


/**
 * @brief Constants used in dictation template's simulator relating databases.
 */
public class Constants {
    public static final String[] DICT_COLUMNS = {
            DictContract.Dict.TABLE_NAME + "." + DictContract.Dict._ID,
            DictContract.Dict.TITLE,
            DictContract.Dict.PASSAGE
    };
    public static final int COL_ID = 0;
    public static final int COL_TITLE = 1;
    public static final int COL_PASSAGE = 2;
    public static final String firstrun = "firstRun";
    public static final String passage = "PASSAGE";
    public static String XMLFileName = "dictation_content.xml";
}
