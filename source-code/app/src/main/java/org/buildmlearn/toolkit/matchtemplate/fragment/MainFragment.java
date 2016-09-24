package org.buildmlearn.toolkit.matchtemplate.fragment;

import android.app.FragmentManager;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.method.LinkMovementMethod;
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
public class MainFragment extends Fragment {

    private static final String SELECTED_KEY_A = "selected_position_a";
    private static final String SELECTED_KEY_B = "selected_position_b";

    private MatchArrayAdapter_A matchListAdapterA;
    private MatchArrayAdapter_B matchListAdapterB;
    private int mPositionA = ListView.INVALID_POSITION;
    private int mPositionB = ListView.INVALID_POSITION;
    private ListView listViewA;
    private ListView listViewB;

    private ArrayList<MatchModel> matchListA;
    private ArrayList<MatchModel> matchListB;
    private MatchDb db;

    private int selectedPositionA = -1;
    private int selectedPositionB = -1;

    private View selectedViewA;
    private View selectedViewB;
    private View clickSourceA;

    public static Fragment newInstance() {
        return new MainFragment();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("matchListA", matchListA);
        outState.putParcelableArrayList("matchListB", matchListB);
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
        } else {
            matchListA = savedInstanceState.getParcelableArrayList("matchListA");
            matchListB = savedInstanceState.getParcelableArrayList("matchListB");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        db = new MatchDb(getContext());
        db.open();

        Cursor cursorMatchesA = db.getRandMatchCursor();
        Cursor cursorMatchesB = db.getRandMatchCursor();

        Cursor meta = db.getMetaCursor();

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

        View rootView = inflater.inflate(R.layout.match_template_main, container, false);

        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar_main);
        toolbar.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary_comprehension));
        toolbar.inflateMenu(R.menu.menu_main_white);

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

        listViewA.setAdapter(matchListAdapterA);
        listViewB.setAdapter(matchListAdapterB);

        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY_A) && savedInstanceState.containsKey(SELECTED_KEY_B)) {
            mPositionA = savedInstanceState.getInt(SELECTED_KEY_A);
            mPositionB = savedInstanceState.getInt(SELECTED_KEY_B);
        }

        handleButtonListener(rootView);

        rootView.findViewById(R.id.check_answer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle arguments = new Bundle();
                arguments.putParcelableArrayList(Constants.first_list, matchListA);
                arguments.putParcelableArrayList(Constants.second_list, matchListB);

                Fragment frag = DetailFragment.newInstance();
                frag.setArguments(arguments);
                getActivity().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

                getActivity().getSupportFragmentManager().beginTransaction().replace(((ViewGroup) getView().getParent()).getId(), frag).addToBackStack(null).commit();
            }
        });

        meta.moveToFirst();
        toolbar.setTitle(meta.getString(Constants.COL_TITLE_META));
        ((TextView) rootView.findViewById(R.id.first_list_title)).setText(meta.getString(Constants.COL_FIRST_TITLE));
        ((TextView) rootView.findViewById(R.id.second_list_title)).setText(meta.getString(Constants.COL_SECOND_TITLE));

        return rootView;
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
}
