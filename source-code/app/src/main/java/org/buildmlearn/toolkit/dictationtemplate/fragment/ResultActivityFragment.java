package org.buildmlearn.toolkit.dictationtemplate.fragment;

import android.app.FragmentManager;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.buildmlearn.toolkit.R;
import org.buildmlearn.toolkit.dictationtemplate.Constants;
import org.buildmlearn.toolkit.dictationtemplate.data.DataUtils;
import org.buildmlearn.toolkit.dictationtemplate.data.DictContract;
import org.buildmlearn.toolkit.dictationtemplate.data.DictDb;
import org.buildmlearn.toolkit.utilities.diff_match_patch;

import java.util.LinkedList;
import java.util.Locale;

/**
 * Created by Anupam (opticod) on 10/7/16.
 */

/**
 * @brief Fragment for displaying score to user in dictation template's simulator.
 */
public class ResultActivityFragment extends Fragment implements LoaderCallbacks<Cursor> {

    private static final int DETAIL_LOADER = 0;
    private final ContentValues dictValues;

    private View rootView;
    private String dict_Id;
    private String passageEntered;
    private DictDb db;

    public ResultActivityFragment() {
        setHasOptionsMenu(true);
        dictValues = new ContentValues();
    }

    public static Fragment newInstance() {
        return new ResultActivityFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        if (arguments != null) {
            dict_Id = arguments.getString(Intent.EXTRA_TEXT);
            passageEntered = arguments.getString(Constants.passage);
        }
        rootView = inflater.inflate(R.layout.fragment_result_dict, container, false);

        db = new DictDb(getContext());
        db.open();

        Toolbar maintoolbar = (Toolbar) rootView.findViewById(R.id.toolbar_main);
        final String result[] = DataUtils.readTitleAuthor();
        maintoolbar.setTitle(result[0]);
        maintoolbar.inflateMenu(R.menu.menu_main_white);
        maintoolbar.setNavigationIcon(R.drawable.ic_home_white_24dp);

        maintoolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                getActivity().getSupportFragmentManager().beginTransaction().replace(((ViewGroup) getView().getParent()).getId(), org.buildmlearn.toolkit.dictationtemplate.fragment.MainActivityFragment.newInstance()).addToBackStack(null).commit();
            }
        });
        maintoolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.action_about:
                        AlertDialog.Builder builder =
                                new AlertDialog.Builder(getActivity());
                        builder.setTitle(String.format("%1$s", getString(R.string.comprehension_about_us)));
                        builder.setMessage(getResources().getText(R.string.about_text_video));
                        builder.setPositiveButton("OK", null);
                        AlertDialog welcomeAlert = builder.create();
                        welcomeAlert.show();
                        assert welcomeAlert.findViewById(android.R.id.message) != null;
                        assert welcomeAlert.findViewById(android.R.id.message) != null;
                        assert ((TextView) welcomeAlert.findViewById(android.R.id.message)) != null;
                        ((TextView) welcomeAlert.findViewById(android.R.id.message)).setMovementMethod(LinkMovementMethod.getInstance());
                        break;
                    default: //do nothing
                        break;
                }
                return true;
            }
        });

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (null != dict_Id) {
            switch (id) {
                case DETAIL_LOADER:

                    return new CursorLoader(getActivity(), null, Constants.DICT_COLUMNS, null, null, null) {
                        @Override
                        public Cursor loadInBackground() {
                            return db.getDictCursorById(Integer.parseInt(dict_Id));
                        }
                    };
                default: //do nothing
                    break;
            }
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, final Cursor data) {
        if (!data.moveToFirst()) {
            return;
        }
        switch (loader.getId()) {
            case DETAIL_LOADER:

                String passage = data.getString(Constants.COL_PASSAGE);
                passageEntered += " ";
                passage += " ";

                diff_match_patch obj = new diff_match_patch();
                LinkedList<diff_match_patch.Diff> llDiffs = obj.diff_WordMode(passageEntered, passage);
                String result[] = obj.diff_prettyHtml(llDiffs);

                int numTWords = passage.split(" ").length;
                ((TextView) rootView.findViewById(R.id.score)).setText(String.format(Locale.ENGLISH, "SCORE : %s / %d", result[1], numTWords));

                ((TextView) rootView.findViewById(R.id.checked_text)).setText(Html.fromHtml(result[0]));

                rootView.findViewById(R.id.restart).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getActivity().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

                        Bundle arguments = new Bundle();
                        arguments.putString(Intent.EXTRA_TEXT, dict_Id);

                        Fragment frag = org.buildmlearn.toolkit.dictationtemplate.fragment.DetailActivityFragment.newInstance();
                        frag.setArguments(arguments);
                        getActivity().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        getActivity().getSupportFragmentManager().beginTransaction().replace(((ViewGroup) getView().getParent()).getId(), frag).addToBackStack(null).commit();
                    }
                });

                rootView.findViewById(R.id.exit).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getActivity().finish();
                    }
                });

                if (dictValues.size() == 0) {
                    dictValues.put(DictContract.Dict._ID, data.getString(Constants.COL_ID));
                    dictValues.put(DictContract.Dict.TITLE, data.getString(Constants.COL_TITLE));
                    dictValues.put(DictContract.Dict.PASSAGE, data.getString(Constants.COL_PASSAGE));
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown Loader");
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // This constructor is intentionally empty
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        db.close();
    }

}
