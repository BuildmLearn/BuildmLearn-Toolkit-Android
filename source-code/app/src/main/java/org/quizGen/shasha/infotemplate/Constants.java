package org.quizGen.shasha.infotemplate;

import org.quizGen.shasha.infotemplate.data.InfoContract;

/**
 * @brief Constants used in info template's simulator relating databases.
 */
public class Constants {
    public static final String[] INFO_COLUMNS = {
            InfoContract.Info.TABLE_NAME + "." + InfoContract.Info._ID,
            InfoContract.Info.TITLE,
            InfoContract.Info.DESCRIPTION
    };
    public static final int COL_ID = 0;
    public static final int COL_TITLE = 1;
    public static final int COL_DESCRIPTION = 2;
    public static final String firstrun = "firstRun";
    public static String XMLFileName = "info_content.xml";
}
