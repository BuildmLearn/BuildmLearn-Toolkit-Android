package org.buildmlearn.toolkit.templates;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.squareup.picasso.Picasso;

import org.buildmlearn.toolkit.R;
import org.buildmlearn.toolkit.model.TemplateInterface;
import org.buildmlearn.toolkit.utilities.NetworkUtils;
import org.buildmlearn.toolkit.videoCollectionTemplate.fragment.SplashFragment;
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

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

/**
 * Created by Anupam (opticod) on 4/5/16.
 */
public class VideoCollectionTemplate implements TemplateInterface {

    private static final String YOUTUBE = "youtube";
    private static final String DAILYMOTION = "dailymotion";
    private static final String VIMEO = "vimeo";
    private final String JSON_TITLE = "title";
    private final String JSON_DESCRIPTION = "description";
    private final String JSON_THUMBNAIL_URL = "thumbnail_url";
    private final String META_PROPERTY_TITLE = "meta[property=og:title]";
    private final String META_PROPERTY_DESCRIPTION = "meta[property=og:description]";
    private final String META_PROPERTY_THUMBNAIL_URL = "meta[property=og:image]";
    private final String META_CONTENT = "content";
    private final String USER_AGENT = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.94 Safari/537.36";
    private final int TIMEOUT_LIMIT = 60000;
    private final String DAILYMOTION_OEMBED_LINK = "http://www.dailymotion.com/services/oembed?url=";
    private final String VIMEO_OEMBED_LINK = "https://vimeo.com/api/oembed.json?url=";
    private final String TEMPLATE_NAME = "VideoCollection Template";
    transient private VideoCollectionAdapter adapter;
    private ArrayList<VideoModel> videoData;
    transient private ProgressDialog progress;
    transient private Context mContext;


    public VideoCollectionTemplate() {
        videoData = new ArrayList<>();
    }

    private static boolean validated(Context context, EditText link) {
        if (link == null) {
            return false;
        }

        String linkText = link.getText().toString();

        if (linkText.equals("")) {
            Toast.makeText(context, R.string.video_collection_template_link_hint, Toast.LENGTH_SHORT).show();
            return false;
        } else if (!(linkText.contains(YOUTUBE + ".com") || linkText.contains(DAILYMOTION + ".com") || linkText.contains(VIMEO + ".com"))) {
            Toast.makeText(context, R.string.video_support_error, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;

    }

    private static boolean validated(Context context, EditText title, EditText description, EditText link) {
        if (link == null || title == null || description == null) {
            return false;
        }

        String titleText = title.getText().toString();
        String descriptionText = description.getText().toString();
        String linkText = link.getText().toString();

        if (titleText.equals("")) {
            Toast.makeText(context, R.string.video_collection_template_title_hint, Toast.LENGTH_SHORT).show();
            return false;
        } else if (descriptionText.equals("")) {
            Toast.makeText(context, R.string.video_collection_template_description_hint, Toast.LENGTH_SHORT).show();
            return false;
        } else if (linkText.equals("")) {
            Toast.makeText(context, R.string.video_collection_template_link_hint, Toast.LENGTH_SHORT).show();
            return false;
        } else if (!(linkText.contains(YOUTUBE + ".com") || linkText.contains(DAILYMOTION + ".com") || linkText.contains(VIMEO + ".com"))) {
            Toast.makeText(context, R.string.video_support_error, Toast.LENGTH_SHORT).show();
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
        mContext = context;
        videoData = new ArrayList<>();
        for (Element item : data) {
            String videoTitle = item.getElementsByTagName(VideoModel.TITLE_TAG).item(0).getTextContent();
            String videoDescription = item.getElementsByTagName(VideoModel.DESCRIPTION_TAG).item(0).getTextContent();
            String videoLink = item.getElementsByTagName(VideoModel.LINK_TAG).item(0).getTextContent();
            String videoThumbLink = item.getElementsByTagName(VideoModel.THUMB_LINK_TAG).item(0).getTextContent();
            videoData.add(new VideoModel(videoTitle, videoDescription, videoLink, videoThumbLink));
        }
        adapter = new VideoCollectionAdapter(context, videoData);
        return adapter;
    }

    @Override
    public String onAttach() {
        return TEMPLATE_NAME;
    }

    @Override
    public String getTitle() {
        return TEMPLATE_NAME;
    }

    private String convertLink(String link) {

        if (link.contains(YOUTUBE)) {
            return link;

        } else if (link.contains(DAILYMOTION)) {
            if (!link.contains("www.")) {
                link = "https://www." + link;
            } else if (!(link.contains("http:") || link.contains("https:"))) {
                link = "http" + link;
            }

            return DAILYMOTION_OEMBED_LINK + link;

        } else if (link.contains(VIMEO)) {
            if (!(link.contains("http:") || link.contains("https:"))) {
                link = "http" + link;
            }

            return VIMEO_OEMBED_LINK + link;
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

                    if (NetworkUtils.isNetworkAvailable(mContext)) {
                        progress = new ProgressDialog(activity);
                        progress.setCancelable(false);
                        progress.show();
                        new VideoInfoTask().execute(convertedLink, linkText, "-1");
                    } else {
                        Toast.makeText(mContext, R.string.network_error, Toast.LENGTH_SHORT).show();
                    }

                    dialog.dismiss();
                }

            }
        });

        dialog.show();

    }

    @Override
    public void editItem(final Activity activity, final int position) {

        final MaterialDialog dialog = new MaterialDialog.Builder(activity)
                .title(R.string.info_edit_title)
                .customView(R.layout.video_dialog_edit_data, true)
                .positiveText(R.string.info_template_ok)
                .negativeText(R.string.info_template_cancel)
                .build();

        final VideoModel data = videoData.get(position);

        final ImageView thumb = (ImageView) dialog.findViewById(R.id.thumb);
        final EditText title = (EditText) dialog.findViewById(R.id.video_title);
        final EditText description = (EditText) dialog.findViewById(R.id.video_description);
        final EditText link = (EditText) dialog.findViewById(R.id.video_link);

        Picasso
                .with(mContext)
                .load(data.getThumbnail_url())
                .transform(new RoundedCornersTransformation(10, 10))
                .fit()
                .centerCrop()
                .into(thumb);

        thumb.setAdjustViewBounds(true);

        title.setText(data.getTitle());
        description.setText(data.getDescription());
        link.setText(data.getLink());

        dialog.getActionButton(DialogAction.POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (validated(activity, title, description, link)) {

                    String titleText = title.getText().toString();
                    String descriptionText = description.getText().toString();
                    String linkText = link.getText().toString();

                    if (linkText.equals(data.getLink())) {
                        data.setTitle(titleText);
                        data.setDescription(descriptionText);
                        adapter.notifyDataSetChanged();

                    } else {
                        String convertedLink = convertLink(linkText);

                        if (NetworkUtils.isNetworkAvailable(mContext)) {
                            progress = new ProgressDialog(activity);
                            progress.setCancelable(false);
                            progress.show();
                            new VideoInfoTask().execute(convertedLink, linkText, String.valueOf(position));
                        } else {
                            Toast.makeText(mContext, R.string.network_error, Toast.LENGTH_SHORT).show();
                        }
                    }

                    dialog.dismiss();
                }

            }
        });

        dialog.show();

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
    public android.support.v4.app.Fragment getSimulatorFragment(String filePathWithName) {
        return SplashFragment.newInstance(filePathWithName);
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
        return "VideoCollectionApp.apk";
    }

    @Override
    public void onActivityResult(Context context, int requestCode, int resultCode, Intent intent) {

    }

    private class VideoInfoTask extends AsyncTask<String, Integer, String> {

        String link;
        String position;
        boolean success;

        @Override
        protected String doInBackground(String... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String jsonStr = null;
            link = params[1];
            position = params[2];
            success = true;

            if (link.contains(YOUTUBE)) {
                try {
                    org.jsoup.nodes.Document document = Jsoup.connect(link)
                            .timeout(TIMEOUT_LIMIT)
                            .userAgent(USER_AGENT)
                            .ignoreContentType(true)
                            .get();

                    Elements titleElem = document.select(META_PROPERTY_TITLE);
                    String title = titleElem.attr(META_CONTENT);

                    Elements descriptionElem = document.select(META_PROPERTY_DESCRIPTION);
                    String description = descriptionElem.attr(META_CONTENT);

                    Elements thumbnailElem = document.select(META_PROPERTY_THUMBNAIL_URL);
                    String thumbnail_url = thumbnailElem.attr(META_CONTENT);

                    if (position.equals("-1")) {
                        VideoModel temp = new VideoModel(title, description, link, thumbnail_url);
                        videoData.add(temp);
                    } else {
                        VideoModel data = videoData.get(Integer.parseInt(position));
                        data.setTitle(title);
                        data.setDescription(description);
                        data.setLink(link);
                        data.setThumbnail_url(thumbnail_url);
                    }

                } catch (Exception e) {
                    success = false;
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
                    StringBuilder buffer = new StringBuilder();
                    if (inputStream == null) {
                        success = false;
                        return null;
                    }
                    reader = new BufferedReader(new InputStreamReader(inputStream));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        buffer.append(line).append("\n");
                    }
                    if (buffer.length() == 0) {
                        return null;
                    }
                    jsonStr = buffer.toString();
                } catch (IOException e) {
                    success = false;
                    return null;
                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (final IOException ignored) {
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
                    String title = json.getString(JSON_TITLE);
                    String description = json.getString(JSON_DESCRIPTION);
                    String thumbnail_url = json.getString(JSON_THUMBNAIL_URL);

                    if (position.equals("-1")) {
                        VideoModel temp = new VideoModel(title, description, link, thumbnail_url);
                        videoData.add(temp);
                    } else {
                        VideoModel data = videoData.get(Integer.parseInt(position));
                        data.setTitle(title);
                        data.setDescription(description);
                        data.setLink(link);
                        data.setThumbnail_url(thumbnail_url);
                    }

                } catch (JSONException e) {
                    success = false;
                }
            }
            if (!success) {
                Toast.makeText(mContext, R.string.video_fetching_error, Toast.LENGTH_SHORT).show();
            }
            adapter.notifyDataSetChanged();
            progress.dismiss();
        }
    }
}
