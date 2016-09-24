package org.buildmlearn.toolkit.quiztemplate.data;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;

import org.buildmlearn.toolkit.quiztemplate.Constants;
import org.buildmlearn.toolkit.quiztemplate.data.QuizContract.Questions;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by Anupam (opticod) on 14/8/16.
 */

/**
 * @brief Used to parse XML and save in database for quiz template's simulator.
 */
public class FetchXMLTask extends AsyncTask<String, Void, Void> {

    private final Context mContext;

    public FetchXMLTask(Context context) {
        mContext = context;
    }

    private static String getValue(String tag, Element element) {
        NodeList nodeList = null;
        NodeList node1 = element.getElementsByTagName(tag);
        if (node1 != null && node1.getLength() != 0)
            nodeList = node1.item(0).getChildNodes();
        if (nodeList == null)
            return "";
        else if (nodeList.getLength() == 0)
            return "";
        else {
            Node node = nodeList.item(0);
            return node.getNodeValue();
        }
    }

    private void saveQuestions(ArrayList<QuizModel> questions) {

        Vector<ContentValues> cVVector = new Vector<>(questions.size());

        for (int i = 0; i < questions.size(); i++) {

            String question;
            ArrayList<String> options;
            int correctAnswer;

            QuizModel questionInfo = questions.get(i);

            question = questionInfo.getQuestion();
            options = questionInfo.getOptions();
            correctAnswer = questionInfo.getCorrectAnswer();

            ContentValues quesValues = new ContentValues();

            quesValues.put(Questions.QUESTION, question);
            if (options.size() >= 1) {
                quesValues.put(Questions.OPTION_1, options.get(0));
            }
            if (options.size() >= 2) {
                quesValues.put(Questions.OPTION_2, options.get(1));
            }
            if (options.size() >= 3) {
                quesValues.put(Questions.OPTION_3, options.get(2));
            }
            if (options.size() >= 4) {
                quesValues.put(Questions.OPTION_4, options.get(3));
            }
            quesValues.put(Questions.CORRECT_ANSWER, correctAnswer);
            quesValues.put(Questions.ATTEMPTED, 0);

            cVVector.add(quesValues);
        }
        // add to database
        if (cVVector.size() > 0) {
            ContentValues[] cvArray = new ContentValues[cVVector.size()];
            cVVector.toArray(cvArray);
            QuizDb db = new QuizDb(mContext);
            db.open();
            db.bulkInsertQuestions(cvArray);
            db.close();
        }
    }

    @Override
    protected Void doInBackground(String... params) {

        if (params.length == 0) {
            return null;
        }
        ArrayList<QuizModel> mList;
        ArrayList<String> mOptions;

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        dbf.setValidating(false);

        DocumentBuilder db;
        Document doc;
        try {
            File fXmlFile = new File(Constants.XMLFileName);
            db = dbf.newDocumentBuilder();
            doc = db.parse(fXmlFile);
            doc.normalize();
            mList = new ArrayList<>();

            NodeList childNodes = doc.getElementsByTagName("item");

            for (int i = 0; i < childNodes.getLength(); i++) {
                QuizModel app = new QuizModel();

                Node child = childNodes.item(i);

                if (child.getNodeType() == Node.ELEMENT_NODE) {
                    Element element2 = (Element) child;

                    app.setQuestion(getValue("question", element2));
                    mOptions = new ArrayList<>();
                    NodeList optionNodes = element2
                            .getElementsByTagName("option");
                    for (int j = 0; j < optionNodes.getLength(); j++) {
                        mOptions.add(optionNodes.item(j).getChildNodes().item(0).getNodeValue());
                    }
                    app.setCorrectAnswer(Integer.parseInt(getValue("answer", element2)));
                    app.setOptions(mOptions);
                }
                mList.add(app);
            }
            saveQuestions(mList);
        } catch (ParserConfigurationException e) {
            return null;
        } catch (FileNotFoundException e) {
            return null;
        } catch (SAXException e) {
            return null;
        } catch (IOException e) {
            return null;
        }
        return null;
    }

}