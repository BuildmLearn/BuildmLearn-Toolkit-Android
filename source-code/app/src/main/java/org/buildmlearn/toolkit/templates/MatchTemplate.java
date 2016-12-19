package org.buildmlearn.toolkit.templates;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Toast;

import org.buildmlearn.toolkit.R;
import org.buildmlearn.toolkit.matchtemplate.fragment.SplashFragment;
import org.buildmlearn.toolkit.model.Template;
import org.buildmlearn.toolkit.model.TemplateInterface;
import org.buildmlearn.toolkit.views.TextViewPlus;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;

/**
 * @brief Match template code implementing methods of TemplateInterface
 * <p/>
 * Created by Anupam (opticod) on 16/7/16.
 */
public class MatchTemplate implements TemplateInterface {

    private ArrayList<MatchMetaModel> metaData;
    transient private MatchAdapter adapter;
    transient private MatchMetaAdapter metaAdapter;
    private ArrayList<MatchModel> matchData;
    private int templateId;

    public MatchTemplate() {
        matchData = new ArrayList<>();
        metaData = new ArrayList<>();
    }

    private boolean validated(Context context, EditText title, EditText first_list_title, EditText second_list_title,int currentPosition) {
        if (title == null || first_list_title == null || second_list_title == null) {
            return false;
        }

        String titleText = title.getText().toString().trim();
        String first_list_titleText = first_list_title.getText().toString().trim();
        String second_list_titleText = second_list_title.getText().toString().trim();

        if ("".equals(titleText)) {
            title.hasFocus();
            title.setError(context.getString(R.string.match_main_title));
            return false;
        } else if ("".equals(first_list_titleText)) {
            first_list_title.hasFocus();
            first_list_title.setError(context.getString(R.string.match_first_list_title));
            return false;
        } else if ("".equals(second_list_titleText)) {
            second_list_title.hasFocus();
            second_list_title.setError(context.getString(R.string.match_second_list_title));
            return false;
        } else if (first_list_titleText.equalsIgnoreCase(second_list_titleText)){
            Toast.makeText(context, "Title of two lists cannot be same.", Toast.LENGTH_SHORT).show();
            return false;
        }

        for(int i = 0; i < metaData.size(); i++) {
            if (i == currentPosition) {
                continue;
            }
            if (metaData.get(i).getTitle().equals(titleText)) {
                title.hasFocus();
                title.setError(context.getString(R.string.match_main_title_exists));
                return false;
            }
        }
        return true;
    }
    
    @Override
    public BaseAdapter newTemplateEditorAdapter(Context context) {
        metaAdapter = new MatchMetaAdapter(context, metaData);
        return metaAdapter;
    }

    public BaseAdapter newMetaEditorAdapter(Context context) {
        return null;
    }

    @Override
    public BaseAdapter currentTemplateEditorAdapter() {
        return metaAdapter;
    }

    public BaseAdapter currentMetaEditorAdapter() {
        return null;
    }

    @Override
    public BaseAdapter loadProjectTemplateEditor(Context context, ArrayList<Element> data) {
        metaData = new ArrayList<>();
        for(Element item : data) {
            String title = item.getElementsByTagName("meta_title").item(0).getTextContent();
            String first_title = item.getElementsByTagName("meta_first_list_title").item(0).getTextContent();
            String second_title = item.getElementsByTagName("meta_second_list_title").item(0).getTextContent();
            NodeList nList = item.getElementsByTagName("matchdataitem");
            ArrayList<Element> items = new ArrayList<>();
            for (int i = 0; i < nList.getLength(); i++) {
                Node nodeItem = nList.item(i);
                if (nodeItem.getNodeType() == Node.ELEMENT_NODE) {
                    items.add((Element) nodeItem);
                }
            }
            ArrayList<MatchModel> dataModels = new ArrayList<>();
            for(Element dataElem : items) {
                MatchModel dataModel = new MatchModel("","");
                dataModel.setMatchA(dataElem.getElementsByTagName("first_list_item").item(0).getTextContent());
                dataModel.setMatchB(dataElem.getElementsByTagName("second_list_item").item(0).getTextContent());
                dataModels.add(dataModel);
            }
            MatchMetaModel matchMetaModel = new MatchMetaModel(title, first_title, second_title);
            matchMetaModel.setMatchModels(dataModels);
            metaData.add(matchMetaModel);
        }
        metaAdapter = new MatchMetaAdapter(context, metaData);
        return metaAdapter;
    }

    public BaseAdapter loadProjectMetaEditor(Context context, Document doc) {
        return null;
    }

    @Override
    public String getTitle() {
        String TEMPLATE_NAME = "Match Template";
        return TEMPLATE_NAME;
    }

    @Override
    public void addItem(final Activity activity) {

        LayoutInflater inflater = activity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.match_meta_dialog_add_edit_data, null);
        final AlertDialog dialog = new AlertDialog.Builder(activity)
                .setTitle(R.string.comprehension_add_meta_title)
                .setView(dialogView,
                        activity.getResources().getDimensionPixelSize(R.dimen.spacing_left),
                        activity.getResources().getDimensionPixelSize(R.dimen.spacing_top),
                        activity.getResources().getDimensionPixelSize(R.dimen.spacing_right),
                        activity.getResources().getDimensionPixelSize(R.dimen.spacing_bottom))
                .setPositiveButton(R.string.info_template_add, null)
                .setNegativeButton(R.string.info_template_cancel, null)
                .create();
        dialog.show();

        final EditText title = (EditText) dialogView.findViewById(R.id.meta_title);
        final EditText first_list_title = (EditText) dialogView.findViewById(R.id.meta_first_list_title);
        final EditText second_list_title = (EditText) dialogView.findViewById(R.id.meta_second_list_title);

        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (validated(activity, title, first_list_title, second_list_title, metaData.size())) {

                    String titleText = title.getText().toString().trim();
                    String first_list_titleText = first_list_title.getText().toString().trim();
                    String second_list_titleText = second_list_title.getText().toString().trim();
                    MatchMetaModel temp = new MatchMetaModel(titleText, first_list_titleText, second_list_titleText);
                    metaData.add(temp);
                    setEmptyView(activity);
                    metaAdapter.notifyDataSetChanged();
                    dialog.dismiss();
                }
            }
        });

    }

    @Override
    public void addMetaData(final Activity activity) {
        //This is intentionally left blank
    }

    @Override
    public void editItem(final Activity activity, final int position) {
        if (position >=0 ) {
            LayoutInflater inflater = activity.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.match_meta_dialog_add_edit_data, null);
            final AlertDialog dialog = new AlertDialog.Builder(activity)
                    .setTitle(R.string.comprehension_edit_meta_title)
                    .setView(dialogView,
                            activity.getResources().getDimensionPixelSize(R.dimen.spacing_left),
                            activity.getResources().getDimensionPixelSize(R.dimen.spacing_top),
                            activity.getResources().getDimensionPixelSize(R.dimen.spacing_right),
                            activity.getResources().getDimensionPixelSize(R.dimen.spacing_bottom))
                    .setPositiveButton(R.string.info_template_ok, null)
                    .setNegativeButton(R.string.info_template_cancel, null)
                    .create();
            dialog.show();

            final MatchMetaModel data = metaData.get(position);

            final EditText title = (EditText) dialogView.findViewById(R.id.meta_title);
            final EditText first_list_title = (EditText) dialogView.findViewById(R.id.meta_first_list_title);
            final EditText second_list_title = (EditText) dialogView.findViewById(R.id.meta_second_list_title);

            title.setText(data.getTitle().trim());
            first_list_title.setText(data.getFirstListTitle().trim());
            second_list_title.setText(data.getSecondListTitle().trim());

            dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (validated(activity, title, first_list_title, second_list_title, position)) {

                        String titleText = title.getText().toString().trim();
                        String first_list_titleText = first_list_title.getText().toString().trim();
                        String second_list_titleText = second_list_title.getText().toString().trim();

                        data.setTitle(titleText);
                        data.setFirstListTitle(first_list_titleText);
                        data.setSecond_list_title(second_list_titleText);
                        metaAdapter.notifyDataSetChanged();
                        dialog.dismiss();
                    }
                }
            });

        }
    }

    @Override
    public Object deleteItem(Activity activity, int position) {
        MatchMetaModel matchMetaModel = null;
        matchMetaModel = metaData.get(position);
        metaData.remove(position);
        metaAdapter.notifyDataSetChanged();
        setEmptyView(activity);
        return matchMetaModel;
    }

    @Override
    public void restoreItem(Activity activity, int position, Object object) {

        if (object instanceof MatchMetaModel) {
            MatchMetaModel matchMetaModel = (MatchMetaModel) object;
            if (matchMetaModel != null) {
                metaData.add(matchMetaModel);
                metaAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public ArrayList<Element> getItems(Document doc) {
        ArrayList<Element> itemElements = new ArrayList<>();

        for (MatchMetaModel data : metaData) {
            itemElements.add(data.getXml(doc));
        }

        return itemElements;
    }

    @Override
    public android.support.v4.app.Fragment getSimulatorFragment(String filePathWithName) {
        return SplashFragment.newInstance(filePathWithName);
    }

    @Override
    public void setTemplateId(int templateId) {
        this.templateId = templateId;
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
        return "MatchApp.apk";
    }

    @Override
    public void onActivityResult(Context context, int requestCode, int resultCode, Intent intent) {
        // This is intentionally empty
    }

    /**
     * @brief Toggles the visibility of empty text if Array has zero elements
     */
    private void setEmptyView(Activity activity) {

        if (metaData.size() < 1) {
            activity.findViewById(R.id.shadow_meta).setVisibility(View.GONE);
            ((TextViewPlus) activity.findViewById(R.id.empty_view_text)).setText(R.string.meta_add_help);
            activity.findViewById(R.id.empty).setVisibility(View.VISIBLE);
        } else {
            activity.findViewById(R.id.empty).setVisibility(View.GONE);
        }
    }

    public boolean validProject() {
        for(int i = 0; i < metaAdapter.getCount(); i++) {
            if(metaAdapter.getItem(i).getMatchModels().size() == 0) {
                return false;
            }
        }
        return true;
    }
}