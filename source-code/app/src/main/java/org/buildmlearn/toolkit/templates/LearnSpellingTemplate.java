package org.buildmlearn.toolkit.templates;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import org.buildmlearn.toolkit.R;
import org.buildmlearn.toolkit.learnspelling.SpellingMainFragment;
import org.buildmlearn.toolkit.model.Template;
import org.buildmlearn.toolkit.model.TemplateInterface;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.ArrayList;

/**
 * @brief Learn Spelling template code implementing methods of TemplateInterface
 *
 * Created by abhishek on 16/06/15 at 9:59 PM.
 */
public class LearnSpellingTemplate implements TemplateInterface {

    transient private LearnSpellingAdapter adapter;
    private ArrayList<LearnSpellingModel> mLearnSpellingData;
    private int templateId;

    public LearnSpellingTemplate() {
        mLearnSpellingData = new ArrayList<>();
    }

    private static boolean validated(Context context, EditText word, EditText meaning) {
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
    public BaseAdapter newTemplateEditorAdapter(Context context) {
        adapter = new LearnSpellingAdapter(context, mLearnSpellingData);
        setEmptyView((Activity) context);
        return adapter;
    }

    @Override
    public BaseAdapter newMetaEditorAdapter(Context context) {
        return null;
    }

    @Override
    public BaseAdapter currentTemplateEditorAdapter() {
        return adapter;
    }

    @Override
    public BaseAdapter currentMetaEditorAdapter() {
        return null;
    }

    @Override
    public BaseAdapter loadProjectMetaEditor(Context context, Document doc) {
        return null;
    }

    @Override
    public BaseAdapter loadProjectTemplateEditor(Context context, ArrayList<Element> data) {
        mLearnSpellingData = new ArrayList<>();
        for (Element item : data) {
            String infoObject = item.getElementsByTagName("word").item(0).getTextContent();
            String infoDescription = item.getElementsByTagName("meaning").item(0).getTextContent();
            mLearnSpellingData.add(new LearnSpellingModel(infoObject, infoDescription));
        }
        adapter = new LearnSpellingAdapter(context, mLearnSpellingData);
        setEmptyView((Activity) context);
        return adapter;
    }

    @Override
    public String onAttach() {
        return "Learn Spelling Template";
    }

    @Override
    public void setTemplateId(int templateId) {
        this.templateId = templateId;
    }

    @Override
    public String getTitle() {
        return "Learn Spelling Template";
    }

    @Override
    public void addItem(final Activity activity) {

        final MaterialDialog dialog = new MaterialDialog.Builder(activity)
                .title(R.string.info_add_new_title)
                .customView(R.layout.info_dialog_add_edit_data, true)
                .positiveText(R.string.info_template_add)
                .negativeText(R.string.info_template_cancel)
                .build();

        final EditText word = (EditText) dialog.findViewById(R.id.info_word);
        final EditText meaning = (EditText) dialog.findViewById(R.id.info_meaning);

        dialog.getActionButton(DialogAction.POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (validated(activity, word, meaning)) {
                    String wordText = word.getText().toString().trim();
                    String meaningText = meaning.getText().toString().trim();

                    LearnSpellingModel temp = new LearnSpellingModel(wordText, meaningText);
                    mLearnSpellingData.add(temp);
                    adapter.notifyDataSetChanged();
                    setEmptyView(activity);
                    dialog.dismiss();
                }

            }
        });

        dialog.show();

    }

    @Override
    public void addMetaData(Activity activity) {

    }

    @Override
    public void editItem(final Activity activity, int position) {
        final MaterialDialog dialog = new MaterialDialog.Builder(activity)
                .title(R.string.info_edit_title)
                .customView(R.layout.info_dialog_add_edit_data, true)
                .positiveText(R.string.info_template_ok)
                .negativeText(R.string.info_template_cancel)
                .build();

        final LearnSpellingModel data = mLearnSpellingData.get(position);

        final EditText word = (EditText) dialog.findViewById(R.id.info_word);
        final EditText meaning = (EditText) dialog.findViewById(R.id.info_meaning);
        word.setText(data.getWord());
        meaning.setText(data.getMeaning());

        dialog.getActionButton(DialogAction.POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (validated(activity, word, meaning)) {
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
    public void deleteItem(Activity activity, int position) {


        mLearnSpellingData.remove(position);
        setEmptyView(activity);
        adapter.notifyDataSetChanged();

    }

    @Override
    public ArrayList<Element> getItems(Document doc) {
        ArrayList<Element> itemElements = new ArrayList<>();


        for (LearnSpellingModel data : mLearnSpellingData) {

            itemElements.add(data.getXml(doc));
        }

        return itemElements;
    }

    @Override
    public android.support.v4.app.Fragment getSimulatorFragment(String filePathWithName) {
        return SpellingMainFragment.newInstance(filePathWithName);
    }

    @Override
    public String getAssetsFileName(Context context) {
        Template[] templates = Template.values();
        return context.getString(templates[templateId].getAssetsName());
    }

    @Override
    public String getAssetsFilePath() {
        return "assets/";
    }

    @Override
    public String getApkFilePath() {
        return "LearnSpellingsApp.apk";
    }

    @Override
    public void onActivityResult(Context context, int requestCode, int resultCode, Intent intent) {

    }

    /**
     * @brief Toggles the visibility of empty text if Array has zero elements
     */
    @Override
    public void setEmptyView(Activity activity) {
        if (mLearnSpellingData.size() < 1) {
            activity.findViewById(R.id.empty).setVisibility(View.VISIBLE);
        } else {
            activity.findViewById(R.id.empty).setVisibility(View.GONE);
        }
    }
}
