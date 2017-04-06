package org.quizGen.shasha.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import org.quizGen.shasha.constant.Constants;
import org.quizGen.shasha.model.SavedProject;
import org.quizGen.shasha.model.Template;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;



public class DeepLinkerActivity extends Activity {
    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Uri data = getIntent().getData();
        File fXmlFile = new File(data.getPath());

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder;
        try {
            dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);
            doc.getDocumentElement().normalize();
            Log.d("Files", "Root element :" + doc.getDocumentElement().getAttribute("type"));
            SavedProject savedProject = new SavedProject(fXmlFile, fXmlFile.getName(), fXmlFile.lastModified(), doc.getDocumentElement().getAttribute("type"), fXmlFile.getAbsolutePath());
            Template[] templates = Template.values();
            for (int i = 0; i < templates.length; i++) {
                if (savedProject.getType().equals(getString(templates[i].getType()))) {
                    Intent intent = new Intent(this, TemplateEditor.class);
                    intent.putExtra(Constants.TEMPLATE_ID, i);
                    intent.putExtra(Constants.PROJECT_FILE_PATH, savedProject.getFullPath());
                    startActivity(intent);
                    finish();
                    return;
                }
            }
            Toast.makeText(this, "Invalid project file", Toast.LENGTH_SHORT).show();
            finish();
        } catch (ParserConfigurationException | IOException | SAXException e) {
            e.printStackTrace();
        }
    }


}
