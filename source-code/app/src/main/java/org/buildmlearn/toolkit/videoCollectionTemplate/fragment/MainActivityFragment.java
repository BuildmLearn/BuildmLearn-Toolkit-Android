package org.buildmlearn.toolkit.videoCollectionTemplate.fragment;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import org.buildmlearn.toolkit.R;
import org.buildmlearn.toolkit.videoCollectionTemplate.Constants;
import org.buildmlearn.toolkit.videoCollectionTemplate.adapter.VideoArrayAdapter;
import org.buildmlearn.toolkit.videoCollectionTemplate.data.DataUtils;
import org.buildmlearn.toolkit.videoCollectionTemplate.data.VideoContract;
import org.buildmlearn.toolkit.videoCollectionTemplate.data.VideoDb;
import org.buildmlearn.toolkit.videoCollectionTemplate.data.VideoModel;

import java.util.ArrayList;

/**
 * Created by Anupam (opticod) on 20/5/16.
 */
public class MainActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String SELECTED_KEY = "selected_position";
    private static final int VIDEO_LOADER = 0;

    private VideoArrayAdapter videoListAdapter;
    private int mPosition = ListView.INVALID_POSITION;
    private ListView listView;
    private ArrayList<VideoModel> videoList;
    private View rootView;
    private VideoDb db;

    public MainActivityFragment() {

    }

    public static Fragment newInstance() {
        MainActivityFragment fragment = new MainActivityFragment();
        return fragment;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("videoList", videoList);
        if (mPosition != ListView.INVALID_POSITION) {
            outState.putInt(SELECTED_KEY, mPosition);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null || !savedInstanceState.containsKey("videoList")) {
            videoList = new ArrayList<>();
        } else {
            videoList = savedInstanceState.getParcelableArrayList("videoList");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        videoListAdapter =
                new VideoArrayAdapter(
                        getActivity());

        rootView = inflater.inflate(R.layout.fragment_main_video, container, false);

        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.card_toolbar);
        toolbar.setTitle("List of Videos :");

        Toolbar maintoolbar = (Toolbar) rootView.findViewById(R.id.toolbar_main);
        final String result[] = DataUtils.read_Title_Author();
        maintoolbar.setTitle(result[0]);
        maintoolbar.inflateMenu(R.menu.menu_main_video);
        maintoolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.action_about:
                        AlertDialog.Builder builder =
                                new AlertDialog.Builder(getActivity());
                        builder.setTitle(String.format("%1$s", getString(R.string.about_us)));
                        builder.setMessage(getResources().getText(R.string.about_text_video));
                        builder.setPositiveButton("OK", null);
                        AlertDialog welcomeAlert = builder.create();
                        welcomeAlert.show();
                        assert ((TextView) welcomeAlert.findViewById(android.R.id.message)) != null;
                        ((TextView) welcomeAlert.findViewById(android.R.id.message)).setMovementMethod(LinkMovementMethod.getInstance());
                        break;
                }
                return true;
            }
        });

        listView = (ListView) rootView.findViewById(R.id.list_view_video);
        listView.setAdapter(videoListAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                Bundle arguments = new Bundle();
                arguments.putString(Intent.EXTRA_TEXT, String.valueOf(position + 1));

                Fragment frag = DetailActivityFragment.newInstance();
                frag.setArguments(arguments);
                getActivity().getSupportFragmentManager().beginTransaction().replace(((ViewGroup) getView().getParent()).getId(), frag).addToBackStack(null).commit();
                mPosition = position;
            }
        });

        db = new VideoDb(getContext());

        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(VIDEO_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        String sortOrder = VideoContract.Videos._ID + " ASC";

        return new CursorLoader(getActivity(), null, Constants.VIDEO_COLUMNS, null, null, sortOrder) {
            @Override
            public Cursor loadInBackground() {
                db.open();
                return db.getVideosCursor();
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        videoListAdapter.swapCursor(cursor);
        if (mPosition != ListView.INVALID_POSITION) {
            listView.smoothScrollToPosition(mPosition);
        }
        try {
            TextView info = (TextView) rootView.findViewById(R.id.empty);
            if (videoListAdapter.getCount() == 0) {
                info.setText(R.string.list_empty_video);
                info.setVisibility(View.VISIBLE);
            } else {
                info.setVisibility(View.GONE);
            }
        } catch (Exception ignored) {
        }
        db.close();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        videoListAdapter.swapCursor(null);
    }

    @Override
    public void onDestroyView() {
        ViewGroup mContainer = (ViewGroup) getActivity().findViewById(R.id.container);
        mContainer.removeAllViews();
        super.onDestroyView();
    }
}
