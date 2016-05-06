package org.buildmlearn.toolkit.templates;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import org.buildmlearn.toolkit.R;
import org.buildmlearn.toolkit.infotemplate.TFTFragment;
import org.buildmlearn.toolkit.model.TemplateInterface;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
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

    private String convertLink(String link) {

        if (link.contains("youtube")) {
            return link;

        } else if (link.contains("dailymotion")) {
            if (!link.contains("www.")) {
                link = "https://www." + link;
            } else if (!(link.contains("http:") || link.contains("https:"))) {
                link = "http" + link;
            }

            return "http://www.dailymotion.com/services/oembed?url=" + link;

        } else if (link.contains("vimeo")) {
            if (!(link.contains("http:") || link.contains("https:"))) {
                link = "http" + link;
            }

            return "https://vimeo.com/api/oembed.json?url=" + link;
        }

        return null;
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
                    String linkText = link.getText().toString();
                    String convertedLink = convertLink(linkText);

                    progress = new ProgressDialog(activity);
                    progress.setCancelable(false);
                    progress.show();
                    new VideoInfoTask().execute(convertedLink, linkText);

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
    private class VideoInfoTask extends AsyncTask<String, Integer, String> {

        protected String link;

        @Override
        protected String doInBackground(String... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String jsonStr = null;
            link = params[1];
            if (link.contains("youtube")) {
                try {
                    org.jsoup.nodes.Document document = Jsoup.connect(link)
                            .timeout(60000)
                            .userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.94 Safari/537.36")
                            .ignoreContentType(true)
                            .get();

                    Elements titleElem = document.select("meta[property=og:title]");
                    String title = titleElem.attr("content");

                    Elements descriptionElem = document.select("meta[property=og:description]");
                    String description = descriptionElem.attr("content");

                    Elements thumbnailElem = document.select("meta[property=og:image]");
                    String thumbnail_url = thumbnailElem.attr("content");

                    VideoModel temp = new VideoModel(title, description, link, thumbnail_url);
                    videoData.add(temp);

                } catch (IOException e) {
                    Toast.makeText(mContext, "Error while fetching video info!", Toast.LENGTH_SHORT);
                }
            } else {
                try {
                    final String BASE_URL = params[0];

                    Uri builtUri = Uri.parse(BASE_URL).buildUpon().build();
                    URL url = new URL(builtUri.toString());
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.connect();
                    InputStream inputStream = urlConnection.getInputStream();
                    StringBuffer buffer = new StringBuffer();
                    if (inputStream == null) {
                        return null;
                    }
                    reader = new BufferedReader(new InputStreamReader(inputStream));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        buffer.append(line + "\n");
                    }
                    if (buffer.length() == 0) {
                        return null;
                    }
                    jsonStr = buffer.toString();
                } catch (IOException e) {
                    return null;
                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (final IOException e) {
                        }
                    }
                }

                return jsonStr;
            }
            return "done";
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (!result.equals("done")) {

                try {
                    JSONObject json = new JSONObject(result);
                    String title = json.getString("title");
                    String description = json.getString("description");
                    String thumbnail_url = json.getString("thumbnail_url");
                    VideoModel temp = new VideoModel(title, description, link, thumbnail_url);
                    videoData.add(temp);

                } catch (JSONException e) {
                    Toast.makeText(mContext, "Error while fetching video info!", Toast.LENGTH_SHORT);
                }
            }
            adapter.notifyDataSetChanged();
            progress.dismiss();
        }
    }
}
