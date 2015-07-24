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

package org.buildmlearn.toolkit.learnspelling;

import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class DataManager {
    private static DataManager instance = null;
    private String mTitle = null;
    private String mAuthor = null;
    private BufferedReader br;
    private ArrayList<WordModel> mList = null;
    private int countIndex = 0;
    private int countCorrect = 0;
    private int countWrong = 0;

    public static DataManager getInstance() {
        if (instance == null) {
            instance = new DataManager();
        }
        return instance;
    }

    public void readXml(String filePath) {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        dbf.setValidating(false);

        DocumentBuilder db;
        Document doc;
        try {
            mList = new ArrayList<>();
            File fXmlFile = new File(filePath);
            db = dbf.newDocumentBuilder();
            doc = db.parse(fXmlFile);
            doc.normalize();
            mTitle = doc.getElementsByTagName("title").item(0).getChildNodes()
                    .item(0).getNodeValue();
            mAuthor = doc.getElementsByTagName("name").item(0).getChildNodes()
                    .item(0).getNodeValue();
            NodeList childNodes = doc.getElementsByTagName("item");
            for (int i = 0; i < childNodes.getLength(); i++) {
                WordModel app = new WordModel();

                Node child = childNodes.item(i);

                if (child.getNodeType() == Node.ELEMENT_NODE) {
                    Element element2 = (Element) child;

                    app.setWord(getValue("word", element2));
                    app.setDescription(getValue("meaning", element2));

                }
                mList.add(app);

            }
        } catch (ParserConfigurationException | IOException | SAXException e) {
            Log.e("tag", e.getLocalizedMessage());
            e.printStackTrace();
        }

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
            Node node = (Node) nodeList.item(0);

            return node.getNodeValue();
        }
    }

    public ArrayList<WordModel> getList() {

        return mList;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public int getActiveWordCount() {
        return countIndex;

    }

    public void increaseCount() {
        countIndex++;
    }

    public void incrementCorrect() {
        countCorrect++;
    }

    public int getCorrect() {
        return countCorrect;
    }

    public int getWrong() {
        return countWrong;
    }

    public void incrementWrong() {
        countWrong++;
    }

    public void reset() {
        countCorrect = 0;
        mList.clear();
        countIndex = 0;
        countWrong = 0;
    }

}
