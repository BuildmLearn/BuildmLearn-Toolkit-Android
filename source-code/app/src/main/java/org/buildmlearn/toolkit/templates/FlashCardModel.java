package org.buildmlearn.toolkit.templates;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;

/**
 * @brief Model class for Flash Card Template Editor data
 *
 * Created by abhishek on 11/07/15 at 7:34 PM.
 */
public class FlashCardModel implements Serializable {

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

    public FlashCardModel(String question, String answer, String hint, String image) {
        mQuestion = question;
        mAnswer = answer;
        mHint = hint;
        mImage = image;
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

    public Element getXml(Document doc) {
        Element rootElement = doc.createElement("item");
        Element questionElement = doc.createElement("question");
        questionElement.appendChild(doc.createTextNode(mQuestion));
        rootElement.appendChild(questionElement);

        Element answerElement = doc.createElement("answer");
        answerElement.appendChild(doc.createTextNode(String.valueOf(mAnswer)));
        rootElement.appendChild(answerElement);

        Element hintElement = doc.createElement("hint");
        hintElement.appendChild(doc.createTextNode(String.valueOf(mHint)));
        rootElement.appendChild(hintElement);

        Element imageElement = doc.createElement("image");
        imageElement.appendChild(doc.createTextNode(String.valueOf(mImage)));
        rootElement.appendChild(imageElement);
        return rootElement;
    }
}

