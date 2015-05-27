package org.buildmlearn.toolkit.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
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
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }


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
