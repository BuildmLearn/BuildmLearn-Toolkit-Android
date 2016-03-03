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

    private static String defaultComprehension = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nulla quis sapien et orci consequat feugiat. Praesent viverra hendrerit risus ac semper. Fusce vitae ex ut elit egestas consectetur et vel magna. Aenean non ligula sit amet urna tincidunt porta. Phasellus nec venenatis metus. Interdum et malesuada fames ac ante ipsum primis in faucibus. Cras faucibus nisi massa, at posuere felis commodo ac. Pellentesque porta egestas turpis eget gravida. Nunc ullamcorper leo nibh, non fermentum sapien feugiat in. Nunc faucibus tristique ipsum, ac placerat risus aliquam sit amet. Sed id enim rutrum massa consectetur faucibus. Mauris rhoncus elit ut pellentesque cursus. Fusce nec nibh nec dui viverra vehicula. Sed lacinia gravida placerat. Fusce sit amet turpis pulvinar, sodales risus vel, scelerisque turpis.";
    private static String defaultTitle = "Comprehension Title";
    private boolean isComprehension;
    private String comprehension;
    private String title;
    private int timeInMinute;
    private QuizModel quizModel;

    private ComprehensionModel(boolean isComprehension, String comprehension, String title, int timeInMinute, QuizModel quizModel) {
        this.isComprehension = isComprehension;
        this.comprehension = comprehension!=null ?comprehension : defaultComprehension;
        this.title = title!=null ? title: defaultTitle;
        this.timeInMinute = timeInMinute;
        this.quizModel = quizModel;
    }

    public static ComprehensionModel getComprehensionModelForQuizModel(QuizModel quizModel) {
        return new ComprehensionModel(false, null, null, -1, quizModel);
    }

    public static ComprehensionModel getComprehensionModelForComprehension(String comprehension, String title, int timeInMinute) {
        return new ComprehensionModel(true, comprehension, title, timeInMinute, new QuizModel(null, null, -1));
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
        this.comprehension = comprehension!=null ?comprehension : defaultComprehension;
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
            Element comprehensionTitleElement = doc.createElement("comprehensionTitle");
            comprehensionTitleElement.appendChild(doc.createTextNode(title));
            rootElement.appendChild(comprehensionTitleElement);
            Element minuteElement = doc.createElement("timeInMinute");
            minuteElement.appendChild(doc.createTextNode(timeInMinute + ""));
            rootElement.appendChild(minuteElement);
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
            String title = item.getElementsByTagName("comprehensionTitle").item(0).getTextContent();
            int timeInMinute = Integer.parseInt(item.getElementsByTagName("timeInMinute").item(0).getTextContent());
            return ComprehensionModel.getComprehensionModelForComprehension(comprehension, title, timeInMinute);
        } else {
            String question = item.getElementsByTagName("question").item(0).getTextContent();
            NodeList options = item.getElementsByTagName("option");
            ArrayList<String> answers = new ArrayList<>();
            for (int i = 0; i < options.getLength(); i++) {
               answers.add(options.item(i).getTextContent());
            }
            int answer = Integer.parseInt(item.getElementsByTagName("answer").item(0).getTextContent());
            return ComprehensionModel.getComprehensionModelForQuizModel(new QuizModel(question, answers, answer));
        }
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title!=null ? title: defaultTitle;
    }

    public int getTimeInMinute() {
        return timeInMinute;
    }

    public void setTimeInMinute(int timeInMinute) {
        this.timeInMinute = timeInMinute;
    }
}
