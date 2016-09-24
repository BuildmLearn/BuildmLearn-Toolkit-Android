package org.buildmlearn.toolkit.learnspelling.fragment;

import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.buildmlearn.toolkit.R;
import org.buildmlearn.toolkit.learnspelling.Constants;
import org.buildmlearn.toolkit.learnspelling.data.SpellDb;

import java.util.HashMap;
import java.util.Locale;

/**
 * Created by Anupam (opticod) on 31/5/16.
 */

/**
 * @brief Fragment for the users to test their spelling skills. spelling template's simulator.
 */

public class MainFragment extends Fragment
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final float MIN_SPEECH_RATE = 0.01f;
    private android.app.AlertDialog mAlert;
    private Context mContext;
    private Button mBtn_Spell, mBtn_Skip;
    private EditText mEt_Spelling;
    private SeekBar mSb_SpeechRate;
    private SpellDb db;
    private TextToSpeech tts;
    private ProgressDialog progress;
    private View rootView;


    public static Fragment newInstance() {
        return new MainFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_main_spell, container, false);
        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary_comprehension));
        toolbar.inflateMenu(R.menu.menu_main_white);
        toolbar.setTitle(getResources().getString(R.string.main_title_spell));

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

        DrawerLayout drawer = (DrawerLayout) rootView.findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                getActivity(), drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        assert drawer != null;
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) rootView.findViewById(R.id.nav_view);
        assert navigationView != null;
        navigationView.setNavigationItemSelectedListener(this);

        mContext = getActivity();

        db = new SpellDb(mContext);
        db.open();

        Bundle extras = getArguments();
        String spellId = "1";
        if (extras != null) {
            spellId = extras.getString(Intent.EXTRA_TEXT);
        }

        Menu m = navigationView.getMenu();
        SubMenu topChannelMenu = m.addSubMenu("Spellings");
        long numQues = db.getCountSpellings();

        for (int i = 1; i <= numQues; i++) {
            topChannelMenu.add(String.format(Locale.getDefault(), "Spelling %1$d", i));
            topChannelMenu.getItem(i - 1).setIcon(R.drawable.ic_assignment_black_24dp);
            final int finalI = i;
            topChannelMenu.getItem(i - 1).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {

                    Bundle arguments = new Bundle();
                    arguments.putString(Intent.EXTRA_TEXT, String.valueOf(finalI));

                    Fragment frag = MainFragment.newInstance();
                    frag.setArguments(arguments);
                    getActivity().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    getActivity().getSupportFragmentManager().beginTransaction().replace(((ViewGroup) getView().getParent()).getId(), frag).addToBackStack(null).commit();

                    return false;
                }
            });
        }

        MenuItem mi = m.getItem(m.size() - 1);
        mi.setTitle(mi.getTitle());


        mBtn_Spell = (Button) rootView.findViewById(R.id.spell_it);
        mBtn_Skip = (Button) rootView.findViewById(R.id.skip);
        mSb_SpeechRate = (SeekBar) rootView.findViewById(R.id.seek_bar);
        TextView mTv_WordNumber = (TextView) rootView.findViewById(R.id.intro_number);

        mBtn_Spell.setEnabled(false);
        mBtn_Skip.setEnabled(false);
        mTv_WordNumber.setText(String.format(Locale.ENGLISH, "Word #%d of %d", Integer.parseInt(spellId), numQues));

        Cursor spell_cursor = db.getSpellingCursorById(Integer.parseInt(spellId));
        spell_cursor.moveToFirst();
        String word = spell_cursor.getString(Constants.COL_WORD);
        String answered = spell_cursor.getString(Constants.COL_ANSWERED);

        setListeners(spellId, word, answered);

        return rootView;
    }

    private void setListeners(final String spellId, final String word, final String answered) {

        rootView.findViewById(R.id.skip).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                long numColumns = db.getCountSpellings();

                long nextSpellId = Integer.parseInt(spellId) + 1;

                if (nextSpellId <= numColumns) {

                    Bundle arguments = new Bundle();
                    arguments.putString(Intent.EXTRA_TEXT, String.valueOf(nextSpellId));

                    Fragment frag = MainFragment.newInstance();
                    frag.setArguments(arguments);

                    getActivity().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    getActivity().getSupportFragmentManager().beginTransaction().replace(((ViewGroup) getView().getParent()).getId(), frag).addToBackStack(null).commit();

                } else {

                    Bundle arguments = new Bundle();
                    arguments.putString(Intent.EXTRA_TEXT, String.valueOf(nextSpellId));

                    Fragment frag = LastFragment.newInstance();
                    frag.setArguments(arguments);

                    getActivity().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    getActivity().getSupportFragmentManager().beginTransaction().replace(((ViewGroup) getView().getParent()).getId(), frag).addToBackStack(null).commit();

                }
            }
        });

        rootView.findViewById(R.id.speak_ico).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progress = new ProgressDialog(mContext);
                progress.setCancelable(false);
                progress.setMessage("Loading TTS Engine...");
                progress.show();


                float speechRate = getProgressValue(mSb_SpeechRate.getProgress());
                tts.setSpeechRate(speechRate);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    String utteranceId = word.hashCode() + "";
                    tts.speak(word, TextToSpeech.QUEUE_FLUSH, null, utteranceId);
                } else {
                    HashMap<String, String> map = new HashMap<>();
                    map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "dict");
                    tts.speak(word, TextToSpeech.QUEUE_FLUSH, map);
                }
                mBtn_Spell.setEnabled(true);
                mBtn_Skip.setEnabled(true);
                mBtn_Skip.setTextColor(ContextCompat.getColor(mContext, R.color.colorAccent_spell));
                mBtn_Spell.setTextColor(ContextCompat.getColor(mContext, R.color.colorAccent_spell));
            }
        });

        rootView.findViewById(R.id.spell_it).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LayoutInflater factory = LayoutInflater.from(mContext);
                final View textEntryView = factory.inflate(
                        R.layout.spelling_dialog_spellinginput, null);
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(mContext);
                mAlert = builder.create();
                mAlert.setCancelable(true);
                mAlert.setView(textEntryView, 10, 10, 10, 10);
                if (mAlert != null && !mAlert.isShowing()) {
                    mAlert.show();
                }
                mEt_Spelling = (EditText) mAlert.findViewById(R.id.et_spelling);
                mEt_Spelling.setText(answered);

                Button mBtn_Submit = (Button) mAlert.findViewById(R.id.btn_submit);
                mBtn_Submit.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        submit(spellId);
                    }
                });
            }
        });

    }

    private void submit(String spell) {
        String input = mEt_Spelling.getText().toString().trim();
        if (input.length() == 0) {
            Toast.makeText(mContext, "Please enter the spelling",
                    Toast.LENGTH_SHORT).show();

        } else {
            mAlert.dismiss();

            String answered = mEt_Spelling.getText().toString().trim();
            db.markAnswered(Integer.parseInt(spell), answered);

            Bundle arguments = new Bundle();
            arguments.putString(Intent.EXTRA_TEXT, String.valueOf(spell));

            Fragment frag = ResponseFragment.newInstance();
            frag.setArguments(arguments);

            getActivity().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            getActivity().getSupportFragmentManager().beginTransaction().replace(((ViewGroup) getView().getParent()).getId(), frag).addToBackStack(null).commit();

        }
    }

    @Override
    public void onDestroy() {
        db.close();
        super.onDestroy();
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        DrawerLayout drawer = (DrawerLayout) rootView.findViewById(R.id.drawer_layout);
        assert drawer != null;
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onPause() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        tts = new TextToSpeech(mContext, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = tts.setLanguage(Locale.US);
                    if (result == TextToSpeech.LANG_MISSING_DATA
                            || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Toast.makeText(mContext, "US English is not supported. Playing in device's default installed language.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(mContext, "Initialization Failed!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
            tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                @Override
                public void onStart(String utteranceId) {
                    progress.dismiss();
                }

                @Override
                public void onDone(String utteranceId) {
                    // This is intentionally empty
                }

                @Override
                public void onError(String utteranceId) {
                    // This is intentionally empty
                }
            });
        }
    }

    private float getProgressValue(int percent) {
        float temp = ((float) percent / 100);
        float speechRate = temp * 2;

        if (speechRate < MIN_SPEECH_RATE)
            speechRate = MIN_SPEECH_RATE;
        return speechRate;
    }

}
