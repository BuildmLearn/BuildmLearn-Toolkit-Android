package org.buildmlearn.toolkit.matchtemplate.fragment;

import android.app.FragmentManager;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.method.LinkMovementMethod;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import org.buildmlearn.toolkit.R;
import org.buildmlearn.toolkit.matchtemplate.Constants;
import org.buildmlearn.toolkit.matchtemplate.adapter.MatchArrayAdapter_A;
import org.buildmlearn.toolkit.matchtemplate.adapter.MatchArrayAdapter_B;
import org.buildmlearn.toolkit.matchtemplate.data.MatchDb;
import org.buildmlearn.toolkit.matchtemplate.data.MatchModel;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Anupam (opticod) on 24/7/16.
 */

/**
 * @brief Fragment for the users to match column A with column B in match template's simulator.
 */
public class MainFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String SELECTED_KEY_A = "selected_position_a";
    private static final String SELECTED_KEY_B = "selected_position_b";
    private static final int MATCH_LOADER = 0;


    private MatchArrayAdapter_A matchListAdapterA;
    private MatchArrayAdapter_B matchListAdapterB;
    private int mPositionA = ListView.INVALID_POSITION;
    private int mPositionB = ListView.INVALID_POSITION;
    private ListView listViewA;
    private ListView listViewB;

    private ArrayList<MatchModel> matchListA;
    private ArrayList<MatchModel> matchListB;
    private MatchDb db;

    private String match_Id;
    private int selectedPositionA = -1;
    private int selectedPositionB = -1;

    private View selectedViewA;
    private View selectedViewB;
    private View clickSourceA;

    private TextView firstListTitle,secondListTitle;
    private Toolbar toolbar;

    public static Fragment newInstance() {
        return new MainFragment();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("matchListA", matchListA);
        outState.putParcelableArrayList("matchListB", matchListB);
        outState.putString(Intent.EXTRA_TEXT,match_Id);
        if (mPositionA != ListView.INVALID_POSITION) {
            outState.putInt(SELECTED_KEY_A, mPositionA);
        }
        if (mPositionB != ListView.INVALID_POSITION) {
            outState.putInt(SELECTED_KEY_B, mPositionB);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null || !savedInstanceState.containsKey("matchListA") || !savedInstanceState.containsKey("matchListB")) {
            matchListA = new ArrayList<>();
            matchListB = new ArrayList<>();
            match_Id = "";
        } else {
            matchListA = savedInstanceState.getParcelableArrayList("matchListA");
            matchListB = savedInstanceState.getParcelableArrayList("matchListB");
            match_Id = savedInstanceState.getString(Intent.EXTRA_TEXT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        if (arguments != null) {
            match_Id = arguments.getString(Intent.EXTRA_TEXT);
        }

        db = new MatchDb(getContext());
        db.open();

        View rootView = inflater.inflate(R.layout.match_template_main, container, false);

        toolbar = (Toolbar) rootView.findViewById(R.id.toolbar_main);
        toolbar.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary_comprehension));
        toolbar.inflateMenu(R.menu.menu_main_white);
        toolbar.setNavigationIcon(R.drawable.ic_home_white_24dp);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                getActivity().getSupportFragmentManager().beginTransaction().replace(((ViewGroup) getView().getParent()).getId(), org.buildmlearn.toolkit.matchtemplate.fragment.MainActivityFragment.newInstance()).addToBackStack(null).commit();
            }
        });


        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.action_about:
                        AlertDialog.Builder builder =
                                new AlertDialog.Builder(getActivity());
                        builder.setTitle(String.format("%1$s", getString(R.string.comprehension_about_us)));
                        builder.setMessage(getResources().getText(R.string.comprehension_about_text));
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

        listViewA = (ListView) rootView.findViewById(R.id.list_view_match_A);
        listViewB = (ListView) rootView.findViewById(R.id.list_view_match_B);

        handleListViewListeners();

        listViewA.setAdapter(null);
        listViewB.setAdapter(null);

        View header_A = getLayoutInflater(savedInstanceState).inflate(R.layout.match_template_main_header_a, null);
        View footer_A = getLayoutInflater(savedInstanceState).inflate(R.layout.match_template_main_footer_a, null);
        listViewA.addHeaderView(header_A);
        listViewA.addFooterView(footer_A);

        View header_B = getLayoutInflater(savedInstanceState).inflate(R.layout.match_template_main_header_b, null);
        View footer_B = getLayoutInflater(savedInstanceState).inflate(R.layout.match_template_main_footer_b, null);
        listViewB.addHeaderView(header_B);
        listViewB.addFooterView(footer_B);

        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY_A) && savedInstanceState.containsKey(SELECTED_KEY_B)) {
            mPositionA = savedInstanceState.getInt(SELECTED_KEY_A);
            mPositionB = savedInstanceState.getInt(SELECTED_KEY_B);
        }

        handleButtonListener(rootView);

        rootView.findViewById(R.id.check_answer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle arguments = new Bundle();
                arguments.putString(Intent.EXTRA_TEXT, String.valueOf(match_Id));
                arguments.putParcelableArrayList(Constants.first_list, matchListA);
                arguments.putParcelableArrayList(Constants.second_list, matchListB);

                Fragment frag = DetailFragment.newInstance();
                frag.setArguments(arguments);
                getActivity().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

                getActivity().getSupportFragmentManager().beginTransaction().replace(((ViewGroup) getView().getParent()).getId(), frag).addToBackStack(null).commit();
            }
        });

        firstListTitle = (TextView) rootView.findViewById(R.id.first_list_title);
        secondListTitle = (TextView) rootView.findViewById(R.id.second_list_title);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
                    getActivity().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    getActivity().getSupportFragmentManager().beginTransaction().replace(((ViewGroup) getView().getParent()).getId(), org.buildmlearn.toolkit.matchtemplate.fragment.MainActivityFragment.newInstance()).addToBackStack(null).commit();
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(MATCH_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    private void handleListViewListeners() {
        listViewA.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (parent.equals(clickSourceA)) {
                    highlightListA(position, view);
                }
            }
        });

        listViewB.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!parent.equals(clickSourceA)) {
                    highlightListB(position, view);
                }
            }
        });

        listViewA.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    clickSourceA = v;
                }
                return false;
            }
        });

        listViewB.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // This is intentionally empty
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                View v = view.getChildAt(0);
                if (v != null)
                    listViewA.setSelectionFromTop(firstVisibleItem, v.getTop());
            }
        });

    }

    private void handleButtonListener(View rootView) {
        rootView.findViewById(R.id.first_list_up).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedPositionA != -1 && selectedPositionA >= 1 && selectedPositionA < matchListA.size()) {
                    Collections.swap(matchListA, selectedPositionA, selectedPositionA - 1);
                    matchListAdapterA.notifyDataSetChanged();
                    highlightListA(selectedPositionA, listViewA.getChildAt(selectedPositionA));
                }
            }
        });

        rootView.findViewById(R.id.first_list_down).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedPositionA != -1 && selectedPositionA >= 0 && selectedPositionA <= matchListA.size() - 2) {
                    Collections.swap(matchListA, selectedPositionA, selectedPositionA + 1);
                    matchListAdapterA.notifyDataSetChanged();
                    highlightListA(selectedPositionA + 2, listViewA.getChildAt(selectedPositionA + 2));
                }
            }
        });

        rootView.findViewById(R.id.second_list_up).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedPositionB != -1 && selectedPositionB >= 1 && selectedPositionB < matchListB.size()) {
                    Collections.swap(matchListB, selectedPositionB, selectedPositionB - 1);
                    matchListAdapterB.notifyDataSetChanged();
                    highlightListB(selectedPositionB, listViewB.getChildAt(selectedPositionB));
                }
            }
        });

        rootView.findViewById(R.id.second_list_down).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedPositionB != -1 && selectedPositionB >= 0 && selectedPositionB <= matchListB.size() - 2) {
                    Collections.swap(matchListB, selectedPositionB, selectedPositionB + 1);
                    matchListAdapterB.notifyDataSetChanged();
                    highlightListB(selectedPositionB + 2, listViewB.getChildAt(selectedPositionB + 2));
                }
            }
        });
    }

    private void highlightListA(int position, View view) {
        if (position == 0) {
            return;
        }

        if (selectedPositionA == position - 1) {
            selectedPositionA = -1;
            if (view instanceof CardView) {
                ((CardView) view).setCardBackgroundColor(Color.WHITE);
            } else {
                view.setBackgroundResource(0);
            }
        } else {
            if (selectedViewA != null) {
                if (selectedViewA instanceof CardView) {
                    ((CardView) selectedViewA).setCardBackgroundColor(Color.WHITE);
                } else {
                    selectedViewA.setBackgroundResource(0);
                }
            }
            selectedViewA = view;
            selectedPositionA = position - 1;
            if (view instanceof CardView) {
                ((CardView) view).setCardBackgroundColor(Color.LTGRAY);
            } else {
                view.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.color_divider));
            }
        }
    }

    private void highlightListB(int position, View view) {
        if (position == 0) {
            return;
        }

        if (selectedPositionB == position - 1) {
            selectedPositionB = -1;
            if (view instanceof CardView) {
                ((CardView) view).setCardBackgroundColor(Color.WHITE);
            } else {
                view.setBackgroundResource(0);
            }
        } else {
            if (selectedViewB != null) {
                if (selectedViewB instanceof CardView) {
                    ((CardView) selectedViewB).setCardBackgroundColor(Color.WHITE);
                } else {
                    selectedViewB.setBackgroundResource(0);
                }
            }
            selectedViewB = view;
            selectedPositionB = position - 1;
            if (view instanceof CardView) {
                ((CardView) view).setCardBackgroundColor(Color.LTGRAY);
            } else {
                view.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.color_divider));
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        db.close();
        ViewGroup mContainer = (ViewGroup) getActivity().findViewById(R.id.container);
        mContainer.removeAllViews();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (null != match_Id) {
            switch (id) {
                case MATCH_LOADER:

                    return new CursorLoader(getActivity(), null, Constants.MATCH_COLUMNS, null, null, null) {
                        @Override
                        public Cursor loadInBackground() {
                            return db.getMetaCursorById(Integer.parseInt(match_Id));
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
        if (!data.moveToFirst()) {
            return;
        }
        switch (loader.getId()) {
            case MATCH_LOADER:

                toolbar.setTitle(data.getString(Constants.COL_TITLE_META));
                firstListTitle.setText(data.getString(Constants.COL_FIRST_TITLE));
                secondListTitle.setText(data.getString(Constants.COL_SECOND_TITLE));
                Cursor cursorMatchesA = db.getRandMatchCursor(data.getString(Constants.COL_TITLE_META));
                Cursor cursorMatchesB = db.getRandMatchCursor(data.getString(Constants.COL_TITLE_META));

                if (cursorMatchesA != null) {
                    while (cursorMatchesA.moveToNext()) {
                        MatchModel match = new MatchModel();
                        match.setMatchA(cursorMatchesA.getString(Constants.COL_MATCH_A));
                        match.setMatchB(cursorMatchesA.getString(Constants.COL_MATCH_B));
                        matchListA.add(match);
                    }
                    cursorMatchesA.close();
                }

                if (cursorMatchesB != null) {
                    while (cursorMatchesB.moveToNext()) {
                        MatchModel match = new MatchModel();
                        match.setMatchA(cursorMatchesB.getString(Constants.COL_MATCH_A));
                        match.setMatchB(cursorMatchesB.getString(Constants.COL_MATCH_B));
                        matchListB.add(match);
                    }
                    cursorMatchesB.close();
                }

                matchListAdapterA =
                        new MatchArrayAdapter_A(
                                getActivity(), matchListA);

                matchListAdapterB =
                        new MatchArrayAdapter_B(
                                getActivity(), matchListB);

                listViewA.setAdapter(matchListAdapterA);
                listViewB.setAdapter(matchListAdapterB);

                break;
            default:
                throw new UnsupportedOperationException("Unknown Loader");
        }


    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // This constructor is intentionally empty
    }
}
