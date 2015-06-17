package org.buildmlearn.toolkit.templates;

import android.app.Fragment;
import android.content.Context;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import org.buildmlearn.toolkit.R;
import org.buildmlearn.toolkit.model.TemplateInterface;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.ArrayList;

/**
 * Created by abhishek on 16/06/15 at 9:59 PM.
 */
public class InfoTemplate implements TemplateInterface {

    transient private InfoAdapter adapter;
    private ArrayList<InfoModel> infoData;

    public InfoTemplate() {
        infoData = new ArrayList<>();
    }

    @Override
    public BaseAdapter newTemplateEditorAdapter(Context context) {
        adapter = new InfoAdapter(context, infoData);
        return adapter;
    }

    @Override
    public BaseAdapter currentTemplateEditorAdapter() {
        return adapter;
    }

    @Override
    public BaseAdapter loadProjectTemplateEditor(Context context, ArrayList<Element> data) {
        return null;
    }

    @Override
    public String onAttach() {
        return "Info Template";
    }

    @Override
    public String getTitle() {
        return "Info Template";
    }

    @Override
    public void addItem(final Context context) {

        final MaterialDialog dialog = new MaterialDialog.Builder(context)
                .title(R.string.info_add_new_title)
                .customView(R.layout.info_dialog_add_edit_data, true)
                .positiveText(R.string.info_template_add)
                .negativeText(R.string.info_template_delete)
                .build();

        final EditText word = (EditText) dialog.findViewById(R.id.info_word);
        final EditText meaning = (EditText) dialog.findViewById(R.id.info_meaning);

        dialog.getActionButton(DialogAction.POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (validated(context, word, meaning)) {
                    String wordText = word.getText().toString();
                    String meaningText = meaning.getText().toString();

                    InfoModel temp = new InfoModel(wordText, meaningText);
                    infoData.add(temp);
                    adapter.notifyDataSetChanged();
                    dialog.dismiss();
                }

            }
        });

        dialog.show();

    }

    public static boolean validated(Context context, EditText word, EditText meaning) {
        if (word == null || meaning == null) {
            return false;
        }

        String wordText = word.getText().toString();
        String meaningText = meaning.getText().toString();

        if (wordText.equals("")) {
            Toast.makeText(context, "Enter word", Toast.LENGTH_SHORT).show();
            return false;
        } else if (meaningText.equals("")) {
            Toast.makeText(context, "Enter meaning", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;

    }

    @Override
    public void editItem(final Context context, int position) {
        final MaterialDialog dialog = new MaterialDialog.Builder(context)
                .title(R.string.info_add_new_title)
                .customView(R.layout.info_dialog_add_edit_data, true)
                .positiveText(R.string.info_template_add)
                .negativeText(R.string.info_template_delete)
                .build();

        final InfoModel data = infoData.get(position);

        final EditText word = (EditText) dialog.findViewById(R.id.info_word);
        final EditText meaning = (EditText) dialog.findViewById(R.id.info_meaning);
        word.setText(data.getWord());
        meaning.setText(data.getMeaning());

        dialog.getActionButton(DialogAction.POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (validated(context, word, meaning)) {
                    String wordText = word.getText().toString();
                    String meaningText = meaning.getText().toString();

                    data.setWord(wordText);
                    data.setMeaning(meaningText);

                    adapter.notifyDataSetChanged();
                    dialog.dismiss();
                }

            }
        });

        dialog.show();

    }

    @Override
    public void deleteItem(int position) {

        infoData.remove(position);
        adapter.notifyDataSetChanged();

    }

    @Override
    public ArrayList<Element> getItems(Document doc) {
        return null;
    }

    @Override
    public Fragment getSimulatorFragment(String filePathWithName) {
        return null;
    }

    @Override
    public String getAssetsFilePath() {
        return null;
    }

    @Override
    public String getApkFilePath() {
        return null;
    }
}
