/* Copyright (c) 2012, BuildmLearn Contributors listed at http://buildmlearn.org/people/
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

 * Redistributions of source code must retain the above copyright notice, this
  list of conditions and the following disclaimer.

 * Redistributions in binary form must reproduce the above copyright notice,
  this list of conditions and the following disclaimer in the documentation
  and/or other materials provided with the distribution.

 * Neither the name of the BuildmLearn nor the names of its
  contributors may be used to endorse or promote products derived from
  this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. */

package org.buildmlearn.toolkit.comprehensiontemplate;

import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * @brief Simulator code for Comprehension Template
 */
public class GlobalData {
    private static GlobalData instance = null;
    String iTitle = null;
    String iAuthor = null;
    String iPassageTitle = null;
    String iPassage = null;
    String iTime = null;
    ComprehensionModel comprehensionModel = null;
    ArrayList<QuestionModel> questionModels = null;

    BufferedReader br;

    List<String> iQuizList = new ArrayList<String>();
    int correct = 0;
    int wrong = 0;
    int total = 0;

    int iSelectedIndex = -1;

    protected GlobalData() {
        // Exists only to defeat instantiation.
    }

    public static GlobalData getInstance() {
        if (instance == null) {
            instance = new GlobalData();
        }
        return instance;
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

    public void readXml(String filePath) {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setValidating(false);
        ArrayList<String> mOptions = null;
        DocumentBuilder db;
        Document doc;
        try {
            File fXmlFile = new File(filePath);
            questionModels = new ArrayList<QuestionModel>();
            db = dbf.newDocumentBuilder();
            doc = db.parse(fXmlFile);
            doc.normalize();
            iTitle = doc.getElementsByTagName("title").item(0)
                    .getChildNodes().item(0).getNodeValue();
            iAuthor = doc.getElementsByTagName("name").item(0)
                    .getChildNodes().item(0).getNodeValue();
            iPassageTitle = doc.getElementsByTagName("comprehensionTitle").item(0)
                    .getChildNodes().item(0).getNodeValue();
            iPassage = doc.getElementsByTagName("comprehension").item(0)
              .getChildNodes().item(0).getNodeValue();
            iTime = doc.getElementsByTagName("timeInMinute").item(0)
              .getChildNodes().item(0).getNodeValue();

            NodeList childNodes = doc.getElementsByTagName("item");
            for (int i = 0; i < childNodes.getLength(); i++) {
                Node child = childNodes.item(i);
                if (child.getNodeType() == Node.ELEMENT_NODE && getValue("isComprehension", (Element) child).equals("false")) {
                    QuestionModel app = new QuestionModel();
                    Element element2 = (Element) child;
                    app.setQuestion(getValue("question", element2));
                    mOptions = new ArrayList<String>();
                    NodeList optionNodes = element2
                            .getElementsByTagName("option");
                    for (int j = 0; j < optionNodes.getLength(); j++) {
                        mOptions.add(optionNodes.item(j).getChildNodes().item(0).getNodeValue());
                    }
                    app.setAnswer(getValue("answer", element2));
                    app.setOptions(mOptions);
                    questionModels.add(app);
                }
            }
            total = questionModels.size();
        } catch (ParserConfigurationException e) {
            Log.e("tag", e.getLocalizedMessage());
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            Log.e("tag", e.getLocalizedMessage());
            e.printStackTrace();
        } catch (SAXException e) {
            Log.e("tag", e.getLocalizedMessage());
            e.printStackTrace();
        } catch (IOException e) {
            Log.e("tag", e.getLocalizedMessage());
            e.printStackTrace();
        }
    }
}