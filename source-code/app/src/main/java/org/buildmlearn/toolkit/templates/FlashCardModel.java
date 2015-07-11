package org.buildmlearn.toolkit.templates;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

/**
 * Created by abhishek on 11/07/15 at 7:34 PM.
 */
public class FlashCardModel {

    private String mQuestion;
    private String mAnswer;
    private String mHint;
    private String mImage;

    public FlashCardModel(String question, String answer, String hint, Bitmap image) {
        mQuestion = question;
        mAnswer = answer;
        mHint = hint;
        mImage = convertBitmapToBase64(image);
    }

    private String convertBitmapToBase64(Bitmap image) {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);

        return encoded;

    }

    public String getQuestion() {
        return mQuestion;
    }

    public String getAnswer() {
        return mAnswer;
    }

    public String getHint() {
        return mHint;
    }

    public String getImage() {
        return mImage;
    }

    public Bitmap getImageBitmap() {
        byte[] decodedString = Base64.decode(mImage, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return decodedByte;
    }
}

