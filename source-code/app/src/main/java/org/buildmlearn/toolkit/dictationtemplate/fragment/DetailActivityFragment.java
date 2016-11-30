package org.buildmlearn.toolkit.dictationtemplate.fragment;

import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
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
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.buildmlearn.toolkit.R;
import org.buildmlearn.toolkit.dictationtemplate.Constants;
import org.buildmlearn.toolkit.dictationtemplate.data.DataUtils;
import org.buildmlearn.toolkit.dictationtemplate.data.DictDb;

import java.util.HashMap;
import java.util.Locale;

/**
 * Created by Anupam (opticod) on 10/7/16.
 */

/**
 * @brief Fragment for taking user input(dictation) in dictation template's simulator.
 */
public class DetailActivityFragment extends Fragment implements LoaderCallbacks<Cursor> {

    private static final int DETAIL_LOADER = 0;

    private View rootView;
    private String dict_Id;
    private DictDb db;
    private TextToSpeech tts;
    private ProgressDialog progress;

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
            dict_Id = arguments.getString(Intent.EXTRA_TEXT);
        }
        rootView = inflater.inflate(R.layout.fragment_detail_dict, container, false);

        db = new DictDb(getContext());
        db.open();

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

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

                final EditText passageText = (EditText) rootView.findViewById(R.id.enter_passage);
                final String passage = data.getString(Constants.COL_PASSAGE);

                rootView.findViewById(R.id.ico_speak).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        progress = new ProgressDialog(getActivity());
                        progress.setCancelable(false);
                        progress.setMessage("Loading TTS Engine...");
                        progress.show();

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            String utteranceId = passage.hashCode() + "";
                            tts.speak(passage, TextToSpeech.QUEUE_FLUSH, null, utteranceId);
                        } else {
                            HashMap<String, String> map = new HashMap<>();
                            map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "dict");
                            tts.speak(passage, TextToSpeech.QUEUE_FLUSH, map);
                        }
                    }
                });

                rootView.findViewById(R.id.submit).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String passage_usr = passageText.getText().toString();

                        Bundle arguments = new Bundle();
                        arguments.putString(Intent.EXTRA_TEXT, String.valueOf(dict_Id));
                        arguments.putString(Constants.passage, passage_usr);

                        Fragment frag = ResultActivityFragment.newInstance();
                        frag.setArguments(arguments);
                        getActivity().getSupportFragmentManager().beginTransaction().replace(((ViewGroup) getView().getParent()).getId(), frag).addToBackStack(null).commit();
                    }
                });

                rootView.findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getActivity().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        getActivity().getSupportFragmentManager().beginTransaction().replace(((ViewGroup) getView().getParent()).getId(), org.buildmlearn.toolkit.dictationtemplate.fragment.MainActivityFragment.newInstance()).addToBackStack(null).commit();
                    }
                });

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
        db.close();
        super.onDestroyView();
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
        tts = new TextToSpeech(getContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = tts.setLanguage(Locale.US);
                    if (result == TextToSpeech.LANG_MISSING_DATA
                            || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Toast.makeText(getContext(), "US English is not supported. Playing in device's default installed language.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Initialization Failed!", Toast.LENGTH_SHORT).show();
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

        SharedPreferences prefs = getActivity().getSharedPreferences("Radio", getContext().MODE_PRIVATE);
        float rate = prefs.getInt("radio_b", 1);
        if (rate == 0) {
            rate = 0.5F;
        }
        tts.setSpeechRate(rate);
    }
}
