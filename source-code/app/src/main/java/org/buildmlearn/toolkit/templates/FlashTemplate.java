package org.buildmlearn.toolkit.templates;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import org.buildmlearn.toolkit.R;
import org.buildmlearn.toolkit.ToolkitApplication;
import org.buildmlearn.toolkit.model.TemplateInterface;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by abhishek on 11/07/15 at 7:33 PM.
 */
public class FlashTemplate implements TemplateInterface {
    @Override
    public BaseAdapter newTemplateEditorAdapter(Context context) {
        return null;
    }

    @Override
    public BaseAdapter currentTemplateEditorAdapter() {
        return null;
    }

    @Override
    public BaseAdapter loadProjectTemplateEditor(Context context, ArrayList<Element> data) {
        return null;
    }

    @Override
    public String onAttach() {
        return null;
    }

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public void addItem(final Activity activity) {
        final MaterialDialog dialog = new MaterialDialog.Builder(activity)
                .title(R.string.info_add_new_title)
                .customView(R.layout.flash_dialog_add_edit_item, true)
                .positiveText(R.string.info_template_add)
                .negativeText(R.string.info_template_delete)
                .build();

        final EditText word = (EditText) dialog.findViewById(R.id.info_word);
        final EditText meaning = (EditText) dialog.findViewById(R.id.info_meaning);

        final ImageView uploadButton = (ImageView) dialog.findViewById(R.id.flash_upload_image);
        uploadButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    uploadButton.setImageResource(R.drawable.upload_button_presses);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    uploadButton.setImageResource(R.drawable.upload_button);
                    Intent photoPickerIntent = makePhotoIntent(activity.getString(R.string.flash_photo_source), activity);
                    activity.startActivityForResult(photoPickerIntent, 1000);
                }
                return true;
            }
        });

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        dialog.getActionButton(DialogAction.POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Validate data here

            }
        });

        dialog.show();
    }


    @Override
    public void editItem(Context context, int position) {

    }

    @Override
    public void deleteItem(int position) {

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
    public String getAssetsFileName() {
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

    public Intent makePhotoIntent(String title, Context context) {

        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryIntent.setType("image/*");


        Intent cameraIntent = new Intent("android.media.action.IMAGE_CAPTURE");
        File photo = null;
        Uri mImageUri = null;
        try {

            photo = createTemporaryFile(context, "picture", ".jpg");
            mImageUri = Uri.fromFile(photo);
            photo.delete();
        } catch (Exception e) {
            e.printStackTrace();
            Log.v("POST", "Can't create file to take picture!");
            Toast.makeText(context, "Please check SD card! Image shot is impossible!", Toast.LENGTH_SHORT).show();
        }

        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);

        Intent chooser = Intent.createChooser(cameraIntent, title);
        Intent[] extraIntents = {galleryIntent};

        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, extraIntents);
        return chooser;
    }

    private File createTemporaryFile(Context context, String part, String ext) throws Exception {

        ToolkitApplication toolkitApplication = (ToolkitApplication) context.getApplicationContext();


        File tempDir;
        tempDir = new File(toolkitApplication.getSavedDir() + ".temp/");
        if (!tempDir.exists()) {
            tempDir.mkdir();
        }
        return File.createTempFile(part, ext, tempDir);
    }

}
