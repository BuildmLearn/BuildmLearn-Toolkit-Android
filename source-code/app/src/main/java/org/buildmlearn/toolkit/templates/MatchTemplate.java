package org.buildmlearn.toolkit.templates;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
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

import java.util.ArrayList;

/**
 * @brief Match template code implementing methods of TemplateInterface
 * <p/>
 * Created by Anupam (opticod) on 16/7/16.
 */
public class MatchTemplate implements TemplateInterface {

    private final ArrayList<MatchMetaModel> metaData;
    transient private MatchAdapter adapter;
    transient private MatchMetaAdapter metaAdapter;
    private ArrayList<MatchModel> matchData;
    private int templateId;

    public MatchTemplate() {
        matchData = new ArrayList<>();
        metaData = new ArrayList<>();
    }

    private static boolean validated(Context context, EditText title, EditText first_list_title, EditText second_list_title) {
        if (title == null || first_list_title == null || second_list_title == null) {
            return false;
        }

        String titleText = title.getText().toString();
        String first_list_titleText = first_list_title.getText().toString();
        String second_list_titleText = second_list_title.getText().toString();

        if ("".equals(titleText)) {
            Toast.makeText(context, R.string.comprehension_template_title_hint, Toast.LENGTH_SHORT).show();
            return false;
        } else if ("".equals(first_list_titleText)) {
            Toast.makeText(context, R.string.match_first_list_title, Toast.LENGTH_SHORT).show();
            return false;
        } else if ("".equals(second_list_titleText)) {
            Toast.makeText(context, R.string.match_second_list_title, Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private static boolean validated(Context context, EditText first_list_title, EditText second_list_title) {
        if (first_list_title == null || second_list_title == null) {
            return false;
        }

        String first_list_titleText = first_list_title.getText().toString();
        String second_list_titleText = second_list_title.getText().toString();

        if (first_list_titleText.equals("")) {
            Toast.makeText(context, R.string.match_first_list_title, Toast.LENGTH_SHORT).show();
            return false;
        } else if (second_list_titleText.equals("")) {
            Toast.makeText(context, R.string.match_second_list_title, Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    @Override
    public BaseAdapter newTemplateEditorAdapter(Context context) {
        adapter = new MatchAdapter(context, matchData);
        return adapter;
    }

    public BaseAdapter newMetaEditorAdapter(Context context) {
        metaAdapter = new MatchMetaAdapter(context, metaData);
        setEmptyView((Activity) context);
        return metaAdapter;
    }

    @Override
    public BaseAdapter currentTemplateEditorAdapter() {
        return adapter;
    }

    public BaseAdapter currentMetaEditorAdapter() {
        return metaAdapter;
    }

    @Override
    public BaseAdapter loadProjectTemplateEditor(Context context, ArrayList<Element> data) {
        matchData = new ArrayList<>();
        for (Element item : data) {
            String first_list_item = item.getElementsByTagName("first_list_item").item(0).getTextContent();
            String second_list_item = item.getElementsByTagName("second_list_item").item(0).getTextContent();

            matchData.add(new MatchModel(first_list_item, second_list_item));

        }
        adapter = new MatchAdapter(context, matchData);
        return adapter;
    }

    public BaseAdapter loadProjectMetaEditor(Context context, Document doc) {

        String title = doc.getElementsByTagName(MatchMetaModel.TITLE_TAG).item(0).getTextContent();
        String first_list_title = doc.getElementsByTagName(MatchMetaModel.FIRST_TITLE_TAG).item(0).getTextContent();
        String second_list_title = doc.getElementsByTagName(MatchMetaModel.SECOND_TITLE_TAG).item(0).getTextContent();
        metaData.add(new MatchMetaModel(title, first_list_title, second_list_title));
        metaAdapter = new MatchMetaAdapter(context, metaData);
        setEmptyView((Activity) context);

        return metaAdapter;

    }

    @Override
    public String getTitle() {
        String TEMPLATE_NAME = "Match Template";
        return TEMPLATE_NAME;
    }

    @Override
    public void addItem(final Activity activity) {
        LayoutInflater inflater = activity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.match_dialog_add_edit, null);
        final AlertDialog dialog = new AlertDialog.Builder(activity)
                .setTitle(R.string.match_dialog_add_title)
                .setView(dialogView,
                        activity.getResources().getDimensionPixelSize(R.dimen.spacing_left),
                        activity.getResources().getDimensionPixelSize(R.dimen.spacing_top),
                        activity.getResources().getDimensionPixelSize(R.dimen.spacing_right),
                        activity.getResources().getDimensionPixelSize(R.dimen.spacing_bottom))
                .setPositiveButton(R.string.quiz_add, null)
                .setNegativeButton(R.string.quiz_cancel, null)
                .create();
        dialog.show();

        final EditText first_list_item = (EditText) dialogView.findViewById(R.id.first_list_item);
        final EditText second_list_item = (EditText) dialogView.findViewById(R.id.second_list_item);

        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (validated(activity, first_list_item, second_list_item)) {
                    String first_list_itemText = first_list_item.getText().toString();
                    String second_list_itemText = second_list_item.getText().toString();

                    MatchModel temp = new MatchModel(first_list_itemText, second_list_itemText);
                    matchData.add(temp);
                    adapter.notifyDataSetChanged();
                    setEmptyView(activity);
                    dialog.dismiss();
                }

            }
        });
    }

    @Override
    public void addMetaData(final Activity activity) {
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

                if (validated(activity, title, first_list_title, second_list_title)) {

                    String titleText = title.getText().toString();
                    String first_list_titleText = first_list_title.getText().toString();
                    String second_list_titleText = second_list_title.getText().toString();
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
    public void editItem(final Activity activity, final int position) {
        if (position == -2) {
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

            final MatchMetaModel data = metaData.get(0);

            final EditText title = (EditText) dialogView.findViewById(R.id.meta_title);
            final EditText first_list_title = (EditText) dialogView.findViewById(R.id.meta_first_list_title);
            final EditText second_list_title = (EditText) dialogView.findViewById(R.id.meta_second_list_title);

            title.setText(data.getTitle());
            first_list_title.setText(data.getFirstListTitle());
            second_list_title.setText(data.getSecondListTitle());

            dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (validated(activity, title, first_list_title, second_list_title)) {

                        String titleText = title.getText().toString();
                        String first_list_titleText = first_list_title.getText().toString();
                        String second_list_titleText = second_list_title.getText().toString();

                        data.setTitle(titleText);
                        data.setFirstListTitle(first_list_titleText);
                        data.setSecond_list_title(second_list_titleText);
                        metaAdapter.notifyDataSetChanged();
                        dialog.dismiss();
                    }
                }
            });

        } else {

            final MatchModel data = matchData.get(position);

            LayoutInflater inflater = activity.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.match_dialog_add_edit, null);
            final AlertDialog dialog = new AlertDialog.Builder(activity)
                    .setTitle(R.string.match_dialog_edit_title)
                    .setView(dialogView,
                            activity.getResources().getDimensionPixelSize(R.dimen.spacing_left),
                            activity.getResources().getDimensionPixelSize(R.dimen.spacing_top),
                            activity.getResources().getDimensionPixelSize(R.dimen.spacing_right),
                            activity.getResources().getDimensionPixelSize(R.dimen.spacing_bottom))
                    .setPositiveButton(R.string.info_template_ok, null)
                    .setNegativeButton(R.string.info_template_cancel, null)
                    .create();
            dialog.show();

            final EditText first_list_item = (EditText) dialogView.findViewById(R.id.first_list_item);
            final EditText second_list_item = (EditText) dialogView.findViewById(R.id.second_list_item);

            first_list_item.setText(data.getMatchA());
            second_list_item.setText(data.getMatchB());

            dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (validated(activity, first_list_item, second_list_item)) {

                        String first_list_itemText = first_list_item.getText().toString();
                        String second_list_itemText = second_list_item.getText().toString();

                        data.setMatchA(first_list_itemText);
                        data.setMatchB(second_list_itemText);

                        adapter.notifyDataSetChanged();
                        dialog.dismiss();
                    }
                }
            });
        }
    }

    @Override
    public Object deleteItem(Activity activity, int position) {
        MatchMetaModel matchMetaModel =null;
        MatchModel matchModel = null;
        if (position == -2) {
            matchMetaModel = metaData.get(0);
            metaData.remove(0);
            setEmptyView(activity);
            metaAdapter.notifyDataSetChanged();
        } else {
            matchModel = matchData.get(position);
            matchData.remove(position);
            setEmptyView(activity);
            adapter.notifyDataSetChanged();
        }
        if (matchMetaModel==null)
        {
            return matchModel;
        }else
        {
            return matchMetaModel;
        }
    }

    @Override
    public void restoreItem(Activity activity, int position, Object object) {
        if (position==-2)
        {
            if (object instanceof MatchMetaModel) {
                MatchMetaModel matchMetaModel = (MatchMetaModel) object;
                if (matchMetaModel != null) {
                    metaData.add( matchMetaModel);
                    metaAdapter.notifyDataSetChanged();
                }
            }
        }else {
            if (object instanceof MatchModel) {
               MatchModel matchModel = (MatchModel) object;
                if (matchModel != null) {
                    matchData.add(position, matchModel);
                    adapter.notifyDataSetChanged();
                }
            }
        }
    }

    @Override
    public ArrayList<Element> getItems(Document doc) {
        ArrayList<Element> itemElements = new ArrayList<>();

        for (MatchMetaModel data : metaData) {
            itemElements.add(data.getXml(doc));
        }

        for (MatchModel data : matchData) {

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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && metaData.size() > 0) {
            activity.findViewById(R.id.shadow_meta).setVisibility(View.VISIBLE);
        }
        if (matchData.size() < 1 && metaData.size() < 1) {
            activity.findViewById(R.id.shadow_meta).setVisibility(View.GONE);
            ((TextViewPlus) activity.findViewById(R.id.empty_view_text)).setText(R.string.meta_add_help);
            activity.findViewById(R.id.empty).setVisibility(View.VISIBLE);
        } else if (matchData.size() < 1) {
            ((TextViewPlus) activity.findViewById(R.id.empty_view_text)).setText(R.string.add_item_help);
            activity.findViewById(R.id.empty).setVisibility(View.VISIBLE);
        } else if (metaData.size() < 1) {
            activity.findViewById(R.id.shadow_meta).setVisibility(View.GONE);
            ((TextViewPlus) activity.findViewById(R.id.empty_view_text)).setText(R.string.meta_add_help);
            activity.findViewById(R.id.empty).setVisibility(View.VISIBLE);
        } else {
            activity.findViewById(R.id.empty).setVisibility(View.GONE);
        }
    }
}
