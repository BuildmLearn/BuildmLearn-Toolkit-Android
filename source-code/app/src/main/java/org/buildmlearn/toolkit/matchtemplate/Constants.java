package org.buildmlearn.toolkit.matchtemplate;

/**
 * Created by Anupam (opticod) on 24/7/16.
 */

import org.buildmlearn.toolkit.matchtemplate.data.MatchContract;

/**
 * @brief Constants used in match template's simulator relating databases.
 */
public class Constants {
    public static final String[] MATCH_COLUMNS = {
            MatchContract.MetaDetails.TABLE_NAME + "." + MatchContract.MetaDetails._ID,
            MatchContract.MetaDetails.TITLE,
            MatchContract.MetaDetails.FIRST_TITLE_TAG,
            MatchContract.MetaDetails.SECOND_TITLE_TAG,
            MatchContract.Matches.TABLE_NAME
    };
    public static final int COL_TITLE_META = 1;
    public static final int COL_FIRST_TITLE = 2;
    public static final int COL_SECOND_TITLE = 3;
    public static final int COL_MATCH_A = 1;
    public static final int COL_MATCH_B = 2;
    public static final String first_list = "firstList";
    public static final String second_list = "secondList";
    public static String XMLFileName = "match_content.xml";
}
