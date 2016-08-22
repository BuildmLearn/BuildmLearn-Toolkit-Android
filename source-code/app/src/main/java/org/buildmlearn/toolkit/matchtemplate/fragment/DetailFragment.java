package org.buildmlearn.toolkit.matchtemplate.fragment;

import android.app.FragmentManager;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;

import org.buildmlearn.toolkit.R;
import org.buildmlearn.toolkit.matchtemplate.Constants;
import org.buildmlearn.toolkit.matchtemplate.adapter.MatchArrayAdapter_A;
import org.buildmlearn.toolkit.matchtemplate.adapter.MatchArrayAdapter_B;
import org.buildmlearn.toolkit.matchtemplate.data.MatchDb;
import org.buildmlearn.toolkit.matchtemplate.data.MatchModel;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by Anupam (opticod) on 26/7/16.
 */

/**
 * @brief Fragment for displaying score with matched results in match template's simulator.
 */
public class DetailFragment extends Fragment {

    private static final String SELECTED_KEY_A = "selected_position_a";
    private static final String SELECTED_KEY_B = "selected_position_b";

    private int mPositionA = ListView.INVALID_POSITION;
    private int mPositionB = ListView.INVALID_POSITION;
    private ListView listViewA;
    private ListView listViewB;

    private ArrayList<MatchModel> matchListA;
    private ArrayList<MatchModel> matchListB;
    private MatchDb db;

    public static Fragment newInstance() {
        return new DetailFragment();
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

        Bundle arguments = getArguments();
        if (arguments != null) {
            matchListA = arguments.getParcelableArrayList(Constants.first_list);
            matchListB = arguments.getParcelableArrayList(Constants.second_list);
        }

        long countScore = 0;
        for (int i = 0; i < matchListA.size(); i++) {
            MatchModel matchA = matchListA.get(i);
            MatchModel matchB = matchListB.get(i);
            if (!matchA.getMatchA().equals(matchB.getMatchA())) {
                matchA.setCorrect(1);
                matchB.setCorrect(1);
            } else {
                countScore++;
                matchA.setCorrect(2);
                matchB.setCorrect(2);
            }
        }


        MatchArrayAdapter_A matchListAdapterA = new MatchArrayAdapter_A(
                getActivity(), matchListA);

        MatchArrayAdapter_B matchListAdapterB = new MatchArrayAdapter_B(
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

        ColorDrawable colDivider = new ColorDrawable(ContextCompat.getColor(getContext(), R.color.white_primary_text));
        listViewA.setDivider(colDivider);
        listViewB.setDivider(colDivider);

        listViewA.setDividerHeight(2);
        listViewB.setDividerHeight(2);

        handleListViewListeners();

        listViewA.setAdapter(matchListAdapterA);
        listViewB.setAdapter(matchListAdapterB);

        View header_A = getLayoutInflater(savedInstanceState).inflate(R.layout.match_template_detail_header_a, null);
        View footer_A = getLayoutInflater(savedInstanceState).inflate(R.layout.match_template_detail_footer_a, null);
        listViewA.addHeaderView(header_A);
        listViewA.addFooterView(footer_A);

        View header_B = getLayoutInflater(savedInstanceState).inflate(R.layout.match_template_detail_header_b, null);
        View footer_B = getLayoutInflater(savedInstanceState).inflate(R.layout.match_template_detail_footer_b, null);
        listViewB.addHeaderView(header_B);
        listViewB.addFooterView(footer_B);

        ((TextView) rootView.findViewById(R.id.score)).setText(String.format(Locale.ENGLISH, "Score : %d of %d", countScore, matchListA.size()));

        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY_A) && savedInstanceState.containsKey(SELECTED_KEY_B)) {
            mPositionA = savedInstanceState.getInt(SELECTED_KEY_A);
            mPositionB = savedInstanceState.getInt(SELECTED_KEY_B);
        }

        rootView.findViewById(R.id.try_again).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle arguments = new Bundle();
                arguments.putParcelableArrayList(Constants.first_list, matchListA);
                arguments.putParcelableArrayList(Constants.second_list, matchListB);

                Fragment frag = MainFragment.newInstance();
                frag.setArguments(arguments);
                getActivity().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

                getActivity().getSupportFragmentManager().beginTransaction().replace(((ViewGroup) getView().getParent()).getId(), frag).addToBackStack(null).commit();
            }
        });

        Cursor meta = db.getMetaCursor();

        meta.moveToFirst();
        toolbar.setTitle(meta.getString(Constants.COL_TITLE_META));
        ((TextView) rootView.findViewById(R.id.first_list_title)).setText(meta.getString(Constants.COL_FIRST_TITLE));
        ((TextView) rootView.findViewById(R.id.second_list_title)).setText(meta.getString(Constants.COL_SECOND_TITLE));

        return rootView;
    }

    private void handleListViewListeners() {

        listViewA.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                return false;
            }
        });

        listViewB.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                //Left empty
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                View v = view.getChildAt(0);
                if (v != null)
                    listViewA.setSelectionFromTop(firstVisibleItem, v.getTop());
            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        db.close();
        ViewGroup mContainer = (ViewGroup) getActivity().findViewById(R.id.container);
        mContainer.removeAllViews();
    }
}
