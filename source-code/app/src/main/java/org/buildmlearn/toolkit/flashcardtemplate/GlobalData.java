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

package org.buildmlearn.toolkit.flashcardtemplate;

import android.content.Context;
import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class GlobalData {
	private static GlobalData instance = null;
	String iQuizTitle = null;
	String iQuizAuthor = null;
	int totalCards = 0;

	BufferedReader br;
	List<String> iQuizList = new ArrayList<String>();

	ArrayList<FlashModel> model = null;

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

	public void ReadContent(Context myContext) {
		try {
			br = new BufferedReader(new InputStreamReader(myContext.getAssets()
					.open("flashcard_content.txt"))); // throwing a
														// FileNotFoundException?
			iQuizTitle = br.readLine();
			iQuizAuthor = br.readLine();
			String text;
			while ((text = br.readLine()) != null) {
				iQuizList.add(text);
			}
			totalCards = iQuizList.size();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				br.close(); // stop reading
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	public void readXmlContent(Context myContext, String fileName) {
		XmlPullParserFactory factory;
		XmlPullParser parser;
		InputStreamReader is;
		try {
			factory = XmlPullParserFactory.newInstance();
			// .setNamespaceAware(true);
			factory.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);

			parser = factory.newPullParser();

			is = new InputStreamReader(myContext.getAssets().open(fileName));

			parser.setInput(is);
			int eventType = parser.getEventType();
			FlashModel app = null;

			while (eventType != XmlPullParser.END_DOCUMENT) {
				String name = null;
				switch (eventType) {
				case XmlPullParser.START_DOCUMENT:
					model = new ArrayList<FlashModel>();
					break;
				case XmlPullParser.START_TAG:
					name = parser.getName();

					if (name.equalsIgnoreCase("title")) {
						iQuizTitle = parser.nextText();
					} else if (name.equalsIgnoreCase("author")) {
						iQuizAuthor = parser.nextText();
					} else if (name.equalsIgnoreCase("item")) {
						app = new FlashModel();
					} else if (app != null) {
						if (name.equalsIgnoreCase("question")) {
							app.setQuestion(parser.nextText());
						} else if (name.equalsIgnoreCase("answer")) {
							app.setAnswer(parser.nextText());
						} else if (name.equalsIgnoreCase("hint")) {
							app.setHint(parser.nextText());
						} else if (name.equalsIgnoreCase("image")) {
							app.setBase64(parser.nextText());
						}
					}
					break;
				case XmlPullParser.END_TAG:
					name = parser.getName();
					if (name.equalsIgnoreCase("item") && app != null) {
						model.add(app);
						totalCards = model.size();
					}
				}
				eventType = parser.next();

			}
		} catch (XmlPullParserException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void readXml(Context myContext, String fileName) {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

		dbf.setValidating(false);

		DocumentBuilder db;
		Document doc;
		try {
			model = new ArrayList<FlashModel>();
			db = dbf.newDocumentBuilder();
			doc = db.parse(myContext.getAssets().open(fileName));
			doc.normalize();
			/*
			 * NodeList app_nodes = doc
			 * .getElementsByTagName("buildmlearn_application");
			 */
			// NamedNodeMap node = app_nodes.item(0).getAttributes();
			iQuizTitle = doc.getElementsByTagName("title").item(0)
					.getChildNodes().item(0).getNodeValue();
			// NodeList author_nodes = doc.getElementsByTagName("author");
			// NamedNodeMap node1 = author_nodes.item(0).getAttributes();
			iQuizAuthor = doc.getElementsByTagName("name").item(0)
					.getChildNodes().item(0).getNodeValue();
			;
			// node1.getNamedItem("name").getNodeValue();
			NodeList childNodes = doc.getElementsByTagName("item");
			// Log.e("tag", "childNodes" + childNodes.getLength());
			for (int i = 0; i < childNodes.getLength(); i++) {
				FlashModel app = new FlashModel();

				Node child = childNodes.item(i);

				if (child.getNodeType() == Node.ELEMENT_NODE) {
					Element element2 = (Element) child;

					app.setQuestion(getValue("question", element2));
					app.setAnswer(getValue("answer", element2));
					app.setHint(getValue("hint", element2));
					app.setBase64(getValue("image", element2));

				}
				model.add(app);

			}
			totalCards = model.size();

			Log.d("tag", "totalCards" + totalCards);
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
}