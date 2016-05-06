package org.buildmlearn.toolkit.templates;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import org.buildmlearn.toolkit.R;
import org.buildmlearn.toolkit.infotemplate.TFTFragment;
import org.buildmlearn.toolkit.model.TemplateInterface;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.ArrayList;

/**
 * Created by Anupam (opticod) on 4/5/16.
 */
public class VideoCollectionTemplate implements TemplateInterface {
    transient private VideoCollectionAdapter adapter;
    private ArrayList<VideoModel> videoData;
    private ProgressDialog progress;
    private Context mContext;

    public VideoCollectionTemplate() {
        videoData = new ArrayList<>();
    }

    public static boolean validated(Context context, EditText link) {
        if (link == null) {
            return false;
        }

        String linkText = link.getText().toString();

        if (link.equals("")) {
            Toast.makeText(context, "Enter Link", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!(linkText.contains("youtube.com") || linkText.contains("dailymotion.com") || linkText.contains("vimeo.com"))) {
            Toast.makeText(context, "We only support Youtube, Dailymotion and Vimeo.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;

    }

    @Override
    public BaseAdapter newTemplateEditorAdapter(Context context) {
        mContext = context;
        adapter = new VideoCollectionAdapter(context, videoData);
        return adapter;
    }

    @Override
    public BaseAdapter currentTemplateEditorAdapter() {
        return adapter;
    }

    @Override
    public BaseAdapter loadProjectTemplateEditor(Context context, ArrayList<Element> data) {
        videoData = new ArrayList<>();
        for (Element item : data) {
            String videoTitle = item.getElementsByTagName("video_title").item(0).getTextContent();
            String videoDescription = item.getElementsByTagName("video_description").item(0).getTextContent();
            String videoLink = item.getElementsByTagName("video_link").item(0).getTextContent();
            String videoThumbLink = item.getElementsByTagName("video_thumb_link").item(0).getTextContent();
            videoData.add(new VideoModel(videoTitle, videoDescription, videoLink, videoThumbLink));
        }
        adapter = new VideoCollectionAdapter(context, videoData);
        return adapter;
    }

    @Override
    public String onAttach() {
        return "VideoCollection Template";
    }

    @Override
    public String getTitle() {
        return "VideoCollection Template";
    }

    @Override
    public void addItem(final Activity activity) {

        final MaterialDialog dialog = new MaterialDialog.Builder(activity)
                .title(R.string.info_add_new_title)
                .customView(R.layout.video_dialog_add_data, true)
                .positiveText(R.string.info_template_add)
                .negativeText(R.string.info_template_cancel)
                .build();

        final EditText link = (EditText) dialog.findViewById(R.id.video_link);

        dialog.getActionButton(DialogAction.POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (validated(activity, link)) {
                    dialog.dismiss();
                }

            }
        });

        dialog.show();

    }

    @Override
    public void editItem(final Activity activity, int position) {
    }

    @Override
    public void deleteItem(int position) {
        videoData.remove(position);
        adapter.notifyDataSetChanged();
    }

    @Override
    public ArrayList<Element> getItems(Document doc) {
        ArrayList<Element> itemElements = new ArrayList<>();

        for (VideoModel data : videoData) {

            itemElements.add(data.getXml(doc));
        }

        return itemElements;
    }

    @Override
    public Fragment getSimulatorFragment(String filePathWithName) {
        return TFTFragment.newInstance(filePathWithName);       //TODO: Simulator
    }

    @Override
    public String getAssetsFileName() {
        return "video_content.xml";
    }

    @Override
    public String getAssetsFilePath() {
        return "assets/";
    }

    @Override
    public String getApkFilePath() {
        return "BasicmLearningApp.apk";     //TODO: VideoCollectionApp.apk
    }

    @Override
    public void onActivityResult(Context context, int requestCode, int resultCode, Intent intent) {

    }
}
