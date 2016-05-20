package org.buildmlearn.toolkit.videoCollectionTemplate.fragment;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import org.buildmlearn.toolkit.R;
import org.buildmlearn.toolkit.videoCollectionTemplate.Constants;
import org.buildmlearn.toolkit.videoCollectionTemplate.data.VideoDb;

/**
 * Created by Anupam (opticod) on 13/5/16.
 */
public class DetailActivityFragment extends Fragment implements LoaderCallbacks<Cursor> {

    private static final int DETAIL_LOADER = 0;
    private final ContentValues videoValues;

    private View rootView;
    private WebView player;
    private String video_Id;
    private VideoDb db;

    public DetailActivityFragment() {
        setHasOptionsMenu(true);
        videoValues = new ContentValues();
    }

    public static Fragment newInstance() {
        DetailActivityFragment fragment = new DetailActivityFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        if (arguments != null) {
            video_Id = arguments.getString(Intent.EXTRA_TEXT);
        }
        rootView = inflater.inflate(R.layout.fragment_detail_video, container, false);

        db = new VideoDb(getActivity());

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (null != video_Id) {
            switch (id) {
                case DETAIL_LOADER:

                    return new CursorLoader(getActivity(), null, Constants.VIDEO_COLUMNS, null, null, null) {
                        @Override
                        public Cursor loadInBackground() {
                            db.open();
                            return db.getVideoCursorById(Integer.parseInt(video_Id));
                        }
                    };
            }
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {
            String title = data.getString(Constants.COL_TITLE);

            ((TextView) rootView.findViewById(R.id.title))
                    .setText(title);

            String description = data.getString(Constants.COL_DESCRIPTION);

            ((TextView) rootView.findViewById(R.id.description))
                    .setText(description);

            player = (WebView) rootView.findViewById(R.id.player);

            player.setWebChromeClient(new WebChromeClient());
            player.setWebViewClient(new WebViewClient());

            player.getSettings().setJavaScriptEnabled(true);
            player.getSettings().setAppCacheEnabled(true);
            player.getSettings().setDomStorageEnabled(true);
            player.getSettings().setAllowFileAccess(true);
            player.getSettings().setLoadWithOverviewMode(true);
            player.getSettings().setUseWideViewPort(true);

            String link = data.getString(Constants.COL_LINK);
            if (link.contains("youtube.com")) {

                int pos = link.indexOf("watch?v=");
                String videoId = link.substring(pos + 8);

                String playVideo = "<html><body>" +
                        " <iframe class=\"player\" type=\"text/html\" width=\"1000\" height=\"850\" src=\"http://www.youtube.com/embed/" + videoId + "\">" +
                        "</body></html>";

                player.loadData(playVideo, "text/html", "utf-8");

            } else if (link.contains("vimeo.com")) {
                int pos;

                if (link.contains("/")) {
                    pos = link.lastIndexOf("/");
                } else {
                    pos = link.lastIndexOf("\\");
                }
                String videoId = link.substring(pos + 1);

                player.loadUrl("http://player.vimeo.com/video/" + videoId + "?player_id=player&autoplay=1&title=0&byline=0&portrait=0&api=1&maxheight=480&maxwidth=800");

            } else if (link.contains("dailymotion.com")) {
                int pos;

                if (link.contains("/")) {
                    pos = link.lastIndexOf("/");
                } else {
                    pos = link.lastIndexOf("\\");
                }
                String videoId = link.substring(pos + 1);

                String playVideo = "<html><body>" +
                        " <iframe class=\"player\" type=\"text/html\" width=\"1000\" height=\"850\" src=\"http://www.dailymotion.com/embed/video/" + videoId + "\">" +
                        "</body></html>";

                player.loadData(playVideo, "text/html", "utf-8");
            }

            rootView.findViewById(R.id.next).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (!db.isOpen()) {
                        db.open();
                    }

                    long numColumns = db.getCount();

                    long nextVideoId = Integer.parseInt(video_Id) + 1;

                    if (nextVideoId <= numColumns) {

                        Bundle arguments = new Bundle();
                        arguments.putString(Intent.EXTRA_TEXT, String.valueOf(nextVideoId));

                        Fragment frag = DetailActivityFragment.newInstance();
                        frag.setArguments(arguments);
                        getActivity().getSupportFragmentManager().beginTransaction().replace(((ViewGroup) getView().getParent()).getId(), frag).addToBackStack(null).commit();

                    } else {
                        getActivity().getSupportFragmentManager().beginTransaction().replace(((ViewGroup) getView().getParent()).getId(), LastFragment.newInstance()).addToBackStack(null).commit();
                    }
                }
            });

            if (Integer.parseInt(video_Id) == 1) {

                rootView.findViewById(R.id.previous).setVisibility(View.INVISIBLE);

            } else {

                rootView.findViewById(R.id.previous).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        int prevVideoId = Integer.parseInt(video_Id) - 1;

                        if (prevVideoId >= 1) {
                            Bundle arguments = new Bundle();
                            arguments.putString(Intent.EXTRA_TEXT, String.valueOf(prevVideoId));

                            Fragment frag = DetailActivityFragment.newInstance();
                            frag.setArguments(arguments);
                            getActivity().getSupportFragmentManager().beginTransaction().replace(((ViewGroup) getView().getParent()).getId(), frag).addToBackStack(null).commit();

                        }
                    }
                });
            }
        }
        db.close();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    @Override
    public void onPause() {
        super.onPause();
        if (player != null) {
            player.onPause();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (player != null) {
            player.onResume();
        }
    }
}
