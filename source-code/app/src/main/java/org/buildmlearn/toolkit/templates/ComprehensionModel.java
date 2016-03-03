package org.buildmlearn.toolkit.templates;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @brief Model class for Comprehension Template Editor data
 *
 * Created by shikher on 02/03/16.
 */
public class ComprehensionModel implements Serializable {

    private boolean isComprehension;
    private String comprehension;
    private QuizModel quizModel;

    public ComprehensionModel(boolean isComprehension, String comprehension, QuizModel quizModel) {
        this.isComprehension = isComprehension;
        this.comprehension = comprehension;
        this.quizModel = quizModel;
    }

    public boolean isComprehension() {
        return isComprehension;
    }

    public void setIsComprehension(boolean isComprehension) {
        this.isComprehension = isComprehension;
    }

    public QuizModel getQuizModel() {
        return quizModel;
    }

    public void setQuizModel(QuizModel quizModel) {
        this.quizModel = quizModel;
    }

    public String getComprehension() {
        return comprehension;
    }

    public void setComprehension(String comprehension) {
        this.comprehension = comprehension;
    }

    public Element getXml(Document doc) {
        Element rootElement = doc.createElement("item");
        Element isComprehensionElement = doc.createElement("isComprehension");
        isComprehensionElement.appendChild(doc.createTextNode(isComprehension?"true":"false"));
        rootElement.appendChild(isComprehensionElement);
        if(isComprehension) {
            Element comprehensionElement = doc.createElement("comprehension");
            comprehensionElement.appendChild(doc.createTextNode(comprehension));
            rootElement.appendChild(comprehensionElement);
        } else {
            Element quizElement = doc.createElement("quiz");
            quizElement.appendChild(quizModel.getXml(doc));
        }
        return rootElement;
    }

    public static ComprehensionModel getModelFromElement(Element item) {
        boolean isComprehension = (item.getElementsByTagName("isComprehension").item(0).getTextContent()).equals("true");
        if(isComprehension) {
            String comprehension = item.getElementsByTagName("comprehension").item(0).getTextContent();
            return new ComprehensionModel(isComprehension, comprehension, new QuizModel(null, null, -1));
        } else {
            String question = item.getElementsByTagName("question").item(0).getTextContent();
            NodeList options = item.getElementsByTagName("option");
            ArrayList<String> answers = new ArrayList<>();
            for (int i = 0; i < options.getLength(); i++) {
               answers.add(options.item(i).getTextContent());
            }
            int answer = Integer.parseInt(item.getElementsByTagName("answer").item(0).getTextContent());
            return new ComprehensionModel(isComprehension, null, new QuizModel(question, answers, answer));
        }
    }
}
