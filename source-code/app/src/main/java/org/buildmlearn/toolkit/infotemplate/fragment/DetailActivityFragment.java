package org.buildmlearn.toolkit.infotemplate.fragment;

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
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.buildmlearn.toolkit.R;
import org.buildmlearn.toolkit.infotemplate.Constants;
import org.buildmlearn.toolkit.infotemplate.data.DataUtils;
import org.buildmlearn.toolkit.infotemplate.data.InfoContract;
import org.buildmlearn.toolkit.infotemplate.data.InfoDb;

/**
 * Created by Anupam (opticod) on 20/6/16.
 */

/**
 * @brief Fragment for details of items in info template's simulator.
 */
public class DetailActivityFragment extends Fragment implements LoaderCallbacks<Cursor> {

    private static final int DETAIL_LOADER = 0;
    private final ContentValues infoValues;

    private View rootView;
    private String info_Id;
    private InfoDb db;

    public DetailActivityFragment() {
        setHasOptionsMenu(true);
        infoValues = new ContentValues();
    }

    public static Fragment newInstance() {
        return new DetailActivityFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        if (arguments != null) {
            info_Id = arguments.getString(Intent.EXTRA_TEXT);
        }
        rootView = inflater.inflate(R.layout.fragment_detail_info, container, false);

        db = new InfoDb(getContext());
        db.open();
        Toolbar maintoolbar = (Toolbar) rootView.findViewById(R.id.toolbar_main);
        String result[] = DataUtils.readTitleAuthor();
        maintoolbar.setTitle(result[0]);
        maintoolbar.inflateMenu(R.menu.menu_main_white);
        maintoolbar.setNavigationIcon(R.drawable.ic_home_white_24dp);

        maintoolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                getActivity().getSupportFragmentManager().beginTransaction().replace(((ViewGroup) getView().getParent()).getId(), org.buildmlearn.toolkit.infotemplate.fragment.MainActivityFragment.newInstance()).addToBackStack(null).commit();
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
        if (null != info_Id) {
            switch (id) {
                case DETAIL_LOADER:

                    return new CursorLoader(getActivity(), null, Constants.INFO_COLUMNS, null, null, null) {
                        @Override
                        public Cursor loadInBackground() {
                            return db.getInfoCursorById(Integer.parseInt(info_Id));
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
                String title = data.getString(Constants.COL_TITLE);

                ((TextView) rootView.findViewById(R.id.title))
                        .setText(title);

                String description = data.getString(Constants.COL_DESCRIPTION);

                ((TextView) rootView.findViewById(R.id.description))
                        .setText(description);

                final long numColumns = db.getCount();

                if (Integer.parseInt(info_Id) == numColumns) {

                    rootView.findViewById(R.id.next).setVisibility(View.INVISIBLE);
                    rootView.findViewById(R.id.exit).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            getActivity().finish();
                        }
                    });

                } else {

                    rootView.findViewById(R.id.exit).setVisibility(View.INVISIBLE);
                    rootView.findViewById(R.id.next).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            long nextInfoId = Integer.parseInt(info_Id) + 1;

                            Bundle arguments = new Bundle();
                            arguments.putString(Intent.EXTRA_TEXT, String.valueOf(nextInfoId));

                            Fragment frag = DetailActivityFragment.newInstance();
                            frag.setArguments(arguments);
                            getActivity().getSupportFragmentManager().beginTransaction().replace(((ViewGroup) getView().getParent()).getId(), frag).addToBackStack(null).commit();

                        }
                    });
                }

                if (Integer.parseInt(info_Id) == 1) {

                    rootView.findViewById(R.id.previous).setVisibility(View.INVISIBLE);

                } else {

                    rootView.findViewById(R.id.previous).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            int prevInfoId = Integer.parseInt(info_Id) - 1;

                            if (prevInfoId >= 1) {

                                Bundle arguments = new Bundle();
                                arguments.putString(Intent.EXTRA_TEXT, String.valueOf(prevInfoId));

                                Fragment frag = DetailActivityFragment.newInstance();
                                frag.setArguments(arguments);
                                getActivity().getSupportFragmentManager().beginTransaction().replace(((ViewGroup) getView().getParent()).getId(), frag).addToBackStack(null).commit();

                            }
                        }
                    });
                }

                if (infoValues.size() == 0) {
                    infoValues.put(InfoContract.Info._ID, data.getString(Constants.COL_ID));
                    infoValues.put(InfoContract.Info.TITLE, data.getString(Constants.COL_TITLE));
                    infoValues.put(InfoContract.Info.DESCRIPTION, data.getString(Constants.COL_DESCRIPTION));
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown Loader");
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //This constructor is intentionally empty
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        db.close();
    }

}
