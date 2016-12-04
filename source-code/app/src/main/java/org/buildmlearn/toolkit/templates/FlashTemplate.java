package org.buildmlearn.toolkit.templates;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.buildmlearn.toolkit.R;
import org.buildmlearn.toolkit.ToolkitApplication;
import org.buildmlearn.toolkit.flashcardtemplate.fragment.SplashFragment;
import org.buildmlearn.toolkit.model.Template;
import org.buildmlearn.toolkit.model.TemplateInterface;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * @brief Flash Card template code implementing methods of TemplateInterface
 * <p/>
 * Created by abhishek on 11/07/15 at 7:33 PM.
 */
public class FlashTemplate implements TemplateInterface {
    private static final int REQUEST_TAKE_PHOTO = 6677;
    private static final String TAG = "FLASH TEMPLATE";
    private ArrayList<FlashCardModel> mData;
    transient private Uri mImageUri;
    transient private ImageView mBannerImage;
    private boolean mIsPhotoAttached;
    transient private FlashCardAdapter mAdapter;
    private int templateId;

    public FlashTemplate() {
        mData = new ArrayList<>();
    }

    @Override
    public BaseAdapter newTemplateEditorAdapter(Context context) {

        mAdapter = new FlashCardAdapter(context, mData);
        setEmptyView((Activity) context);
        return mAdapter;
    }

    @Override
    public BaseAdapter newMetaEditorAdapter(Context context) {
        return null;
    }

    @Override
    public BaseAdapter currentTemplateEditorAdapter() {
        return mAdapter;
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

        mData = new ArrayList<>();
        for (Element item : data) {
            String question = item.getElementsByTagName("question").item(0).getTextContent();
            String answer = item.getElementsByTagName("answer").item(0).getTextContent();
            String hint = item.getElementsByTagName("hint").item(0).getTextContent();
            String image = item.getElementsByTagName("image").item(0).getTextContent();
            mData.add(new FlashCardModel(question, answer, hint, image));

        }
        mAdapter = new FlashCardAdapter(context, mData);
        setEmptyView((Activity) context);
        return mAdapter;
    }

    @Override
    public void setTemplateId(int templateId) {
        this.templateId = templateId;
    }

    @Override
    public String getTitle() {
        return "Flash Template";
    }

    @Override
    public void addItem(final Activity activity) {

        mIsPhotoAttached = false;

        LayoutInflater inflater = activity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.flash_dialog_add_edit_item, null);
        final AlertDialog dialog = new AlertDialog.Builder(activity)
                .setTitle(R.string.info_add_new_title)
                .setView(dialogView,
                        activity.getResources().getDimensionPixelSize(R.dimen.spacing_left),
                        activity.getResources().getDimensionPixelSize(R.dimen.spacing_top),
                        activity.getResources().getDimensionPixelSize(R.dimen.spacing_right),
                        activity.getResources().getDimensionPixelSize(R.dimen.spacing_bottom))
                .setPositiveButton(R.string.info_template_add, null)
                .setNegativeButton(R.string.info_template_cancel, null)
                .create();
        dialog.show();

        final EditText question = (EditText) dialogView.findViewById(R.id.flash_question);
        final EditText answer = (EditText) dialogView.findViewById(R.id.flash_answer);
        final EditText answerHint = (EditText) dialogView.findViewById(R.id.flash_hint);
        mBannerImage = (ImageView) dialogView.findViewById(R.id.banner_image);
        final ImageView uploadButton = (ImageView) dialogView.findViewById(R.id.flash_upload_image);
        uploadButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    uploadButton.setImageResource(R.drawable.upload_button_presses);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    uploadButton.setImageResource(R.drawable.upload_button);
                    Intent photoPickerIntent = makePhotoIntent(activity.getString(R.string.flash_photo_source), activity);
                    activity.startActivityForResult(photoPickerIntent, REQUEST_TAKE_PHOTO);
                }
                return true;
            }
        });

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // This is intentionally empty
            }
        });
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateData(question, answer, answerHint, activity)) {
                    dialog.dismiss();
                    Bitmap bitmap = ((BitmapDrawable) mBannerImage.getDrawable()).getBitmap();
                    String questionText = question.getText().toString();
                    String answerText = answer.getText().toString();
                    String hintText = answerHint.getText().toString();
                    mData.add(new FlashCardModel(questionText, answerText, hintText, bitmap));
                    setEmptyView(activity);
                    mAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public void addMetaData(Activity activity) {
        // This is intentionally empty
    }


    @Override
    public void editItem(final Activity activity, final int position) {
        mIsPhotoAttached = true;

        FlashCardModel data = mData.get(position);

        LayoutInflater inflater = activity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.flash_dialog_add_edit_item, null);
        final AlertDialog dialog = new AlertDialog.Builder(activity)
                .setTitle(R.string.info_edit_title)
                .setView(dialogView,
                        activity.getResources().getDimensionPixelSize(R.dimen.spacing_left),
                        activity.getResources().getDimensionPixelSize(R.dimen.spacing_top),
                        activity.getResources().getDimensionPixelSize(R.dimen.spacing_right),
                        activity.getResources().getDimensionPixelSize(R.dimen.spacing_bottom))
                .setPositiveButton(R.string.info_template_ok, null)
                .setNegativeButton(R.string.info_template_cancel, null)
                .create();
        dialog.show();

        final EditText question = (EditText) dialogView.findViewById(R.id.flash_question);
        final EditText answer = (EditText) dialogView.findViewById(R.id.flash_answer);
        final EditText answerHint = (EditText) dialogView.findViewById(R.id.flash_hint);
        mBannerImage = (ImageView) dialogView.findViewById(R.id.banner_image);
        question.setText(data.getQuestion());
        answer.setText(data.getAnswer());
        answerHint.setText(data.getHint());

        mBannerImage.setImageBitmap(data.getImageBitmap());

        final ImageView uploadButton = (ImageView) dialogView.findViewById(R.id.flash_upload_image);
        uploadButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    uploadButton.setImageResource(R.drawable.upload_button_presses);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    uploadButton.setImageResource(R.drawable.upload_button);
                    Intent photoPickerIntent = makePhotoIntent(activity.getString(R.string.flash_photo_source), activity);
                    activity.startActivityForResult(photoPickerIntent, REQUEST_TAKE_PHOTO);
                }
                return true;
            }
        });

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // This is intentionally empty
            }
        });

        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateData(question, answer, answerHint, activity)) {
                    dialog.dismiss();
                    Bitmap bitmap = ((BitmapDrawable) mBannerImage.getDrawable()).getBitmap();
                    String questionText = question.getText().toString();
                    String answerText = answer.getText().toString();
                    String hintText = answerHint.getText().toString();
                    mData.set(position, new FlashCardModel(questionText, answerText, hintText, bitmap));
                    mAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    private boolean validateData(EditText question, EditText answer, EditText answerHint, Context context) {
        String questionText = question.getText().toString();
        String answerText = answer.getText().toString();
        String hintText = answerHint.getText().toString();

        if (questionText.isEmpty()) {
            Toast.makeText(context, "Enter question", Toast.LENGTH_SHORT).show();
            return false;
        } else if (answerText.isEmpty()) {
            Toast.makeText(context, "Enter answer", Toast.LENGTH_SHORT).show();
            return false;
        } else if (hintText.isEmpty()) {
            Toast.makeText(context, "Enter hint", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!mIsPhotoAttached) {
            Toast.makeText(context, "Attach an image", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    @Override
    public Object deleteItem(Activity activity, int position) {
        FlashCardModel flashCardModel = mData.get(position);
        mData.remove(position);
        setEmptyView(activity);
        mAdapter.notifyDataSetChanged();

        return flashCardModel;
    }

    @Override
    public void restoreItem(Activity activity, int position, Object object) {
        if (object instanceof FlashCardModel)
        {
            FlashCardModel flashCardModel = (FlashCardModel)object;
            if (flashCardModel!=null)
            {
                mData.add(position,flashCardModel);
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public ArrayList<Element> getItems(Document doc) {

        ArrayList<Element> itemElements = new ArrayList<>();


        for (FlashCardModel data : mData) {

            itemElements.add(data.getXml(doc));
        }

        return itemElements;
    }

    @Override
    public android.support.v4.app.Fragment getSimulatorFragment(String filePathWithName) {
        return SplashFragment.newInstance(filePathWithName);
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
        return "FlashCardTemplateApp.apk";
    }

    @Override
    public void onActivityResult(Context context, int requestCode, int resultCode, Intent intent) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == Activity.RESULT_OK) {
            Bitmap bitmap = grabImage(context);
            if (bitmap != null) {
                bitmap = getResizedBitmap(bitmap);
                if (bitmap != null) {
                    Log.d(TAG, "Bitmap not null: From Camera");
                }
            } else {
                InputStream stream;
                try {
                    stream = context.getContentResolver().openInputStream(
                            intent.getData());
                    bitmap = BitmapFactory.decodeStream(stream);
                    bitmap = getResizedBitmap(bitmap);
                    if (bitmap != null) {
                        Log.d(TAG, "Bitmap not null: From Gallery");
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

            }

            if (bitmap != null) {
                mBannerImage.setImageBitmap(bitmap);
                mIsPhotoAttached = true;
            }
        }
    }

    private Bitmap getResizedBitmap(Bitmap image) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 0) {
            width = 300;
            height = (int) (width / bitmapRatio);
        } else {
            height = 300;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    private Bitmap grabImage(Context context) {
        context.getContentResolver().notifyChange(mImageUri, null);
        ContentResolver cr = context.getContentResolver();
        Bitmap bitmap;
        try {
            bitmap = android.provider.MediaStore.Images.Media.getBitmap(cr, mImageUri);
            return bitmap;
        } catch (Exception e) {
            Log.d(TAG, "Failed to load", e);
        }
        return null;
    }

    private Intent makePhotoIntent(String title, Context context) {

        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryIntent.setType("image/*");

        Intent cameraIntent = new Intent("android.media.action.IMAGE_CAPTURE");
        File photo;
        mImageUri = null;
        try {

            photo = createTemporaryFile(context);
            mImageUri = Uri.fromFile(photo);
            photo.delete();
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
            Toast.makeText(context, "Please check SD card! Image shot is impossible!", Toast.LENGTH_SHORT).show();
        }

        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);

        Intent chooser = Intent.createChooser(cameraIntent, title);
        Intent[] extraIntents = {galleryIntent};

        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, extraIntents);
        return chooser;
    }

    private File createTemporaryFile(Context context) throws Exception {

        ToolkitApplication toolkitApplication = (ToolkitApplication) context.getApplicationContext();

        File tempDir;
        tempDir = new File(toolkitApplication.getProjectDir() + ".temp/");
        if (!tempDir.exists()) {
            tempDir.mkdir();
        }
        return File.createTempFile("picture", ".jpg", tempDir);
    }

    /**
     * @brief Toggles the visibility of empty text if Array has zero elements
     */
    private void setEmptyView(Activity activity) {
        if (mData.size() < 1) {
            activity.findViewById(R.id.empty).setVisibility(View.VISIBLE);
        } else {
            activity.findViewById(R.id.empty).setVisibility(View.GONE);
        }
    }

}
