package org.buildmlearn.toolkit.videocollectiontemplate.fragment;

import android.app.FragmentManager;
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
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import org.buildmlearn.toolkit.R;
import org.buildmlearn.toolkit.videocollectiontemplate.Constants;
import org.buildmlearn.toolkit.videocollectiontemplate.data.VideoDb;

/**
 * @brief Fragment containing details of video items in video collection template's simulator.
 * <p/>
 * Created by Anupam (opticod) on 13/5/16.
 */
public class DetailActivityFragment extends Fragment implements LoaderCallbacks<Cursor> {

    private static final int DETAIL_LOADER = 0;

    private View rootView;
    private WebView player;
    private String video_Id;
    private VideoDb db;

    public DetailActivityFragment() {
        setHasOptionsMenu(true);
    }

    public static Fragment newInstance() {
        return new DetailActivityFragment();
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
        db.open();
        Toolbar maintoolbar = (Toolbar) rootView.findViewById(R.id.toolbar_main);
        maintoolbar.setTitle(getString(R.string.video_collection_title));
        maintoolbar.inflateMenu(R.menu.menu_main_white);
        maintoolbar.setNavigationIcon(R.drawable.ic_home_white_24dp);

        maintoolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                getActivity().getSupportFragmentManager().beginTransaction().replace(((ViewGroup) getView().getParent()).getId(), MainActivityFragment.newInstance()).addToBackStack(null).commit();
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
        if (null != video_Id) {
            switch (id) {
                case DETAIL_LOADER:

                    return new CursorLoader(getActivity(), null, Constants.VIDEO_COLUMNS, null, null, null) {
                        @Override
                        public Cursor loadInBackground() {
                            return db.getVideoCursorById(Integer.parseInt(video_Id));
                        }
                    };
                default: //do nothing
                    break;
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
                    .setText(Html.fromHtml(description));

            player = (WebView) rootView.findViewById(R.id.player);

            player.setWebChromeClient(new WebChromeClient());
            player.setWebViewClient(new WebViewClient());

            player.getSettings().setJavaScriptEnabled(true);
            player.getSettings().setAppCacheEnabled(true);
            player.getSettings().setDomStorageEnabled(true);
            player.getSettings().setAllowFileAccess(true);
            player.getSettings().setLoadWithOverviewMode(true);
            player.getSettings().setUseWideViewPort(true);
            player.getSettings().setTextZoom(140);

            String link = data.getString(Constants.COL_LINK);
            if (link.contains("youtube.com")) {

                int pos = link.indexOf("watch?v=");
                String videoId = link.substring(pos + 8);

                String playVideo = "<html><body style=\"margin: 0; padding: 0\">" +
                        " <iframe class=\"player\" type=\"text/html\" width=\"100%\" height=\"850\" src=\"http://www.youtube.com/embed/" + videoId + "\">" +
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

                String playVideo = "<html><body style=\"margin: 0; padding: 0\">" +
                        " <iframe class=\"player\" type=\"text/html\" width=\"100%\" height=\"850\" src=\"http://www.dailymotion.com/embed/video/" + videoId + "\">" +
                        "</body></html>";

                player.loadData(playVideo, "text/html", "utf-8");
            }

            rootView.findViewById(R.id.next).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

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
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // This constructor is intentionally empty
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        db.close();
    }

}
