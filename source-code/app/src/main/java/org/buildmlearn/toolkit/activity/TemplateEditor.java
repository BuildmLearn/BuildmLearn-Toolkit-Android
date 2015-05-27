package org.buildmlearn.toolkit.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.buildmlearn.toolkit.R;
import org.buildmlearn.toolkit.constant.Constants;
import org.buildmlearn.toolkit.model.Template;
import org.buildmlearn.toolkit.model.TemplateInterface;

public class TemplateEditor extends AppCompatActivity {

    private ListView templateEdtiorList;
    private int templateId;
    private Template template;
    private TemplateInterface selectedTemplate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_template_editor);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        ActionBar actionBar = getSupportActionBar();
        templateEdtiorList = (ListView)findViewById(R.id.template_editor_listview);
        templateId = getIntent().getIntExtra(Constants.TEMPLATE_ID, -1);

        if(templateId != -1) {
            Template[] templates = Template.values();
            template = templates[templateId];
            Class templateClass = template.getTemplateClass();
            try {
                Object templateObject = templateClass.newInstance();
                selectedTemplate = (TemplateInterface) templateObject;
                Toast.makeText(this, selectedTemplate.onAttach(), Toast.LENGTH_LONG).show();
                if (actionBar == null) {
                    throw new AssertionError();
                }
                actionBar.setTitle(selectedTemplate.getTitle());
                populateListView(selectedTemplate.newTemplateEditorAdapter(this));
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }


        }
    }

    private void populateListView(BaseAdapter adapter) {
        if(templateEdtiorList != null) {
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View templateHeader = inflater.inflate(R.layout.listview_header_template, templateEdtiorList, false);
            templateEdtiorList.addHeaderView(templateHeader, null, false);
            templateEdtiorList.setAdapter(adapter);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_template_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
