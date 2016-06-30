package org.buildmlearn.toolkit.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListAdapter;

import org.buildmlearn.toolkit.R;
import org.buildmlearn.toolkit.adapter.TemplateAdapter;
import org.buildmlearn.toolkit.constant.Constants;

/**
 * @brief Template activity show the list of available templates in the toolkit. Templates are defined in Template.java enum file
 */
public class TemplateActivity extends AppCompatActivity {

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_template);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        ListAdapter mAdapter = new TemplateAdapter(this);
        AbsListView mListView = (AbsListView) findViewById(android.R.id.list);
        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(getApplicationContext(), TemplateEditor.class);
                intent.putExtra(Constants.TEMPLATE_ID, position);
                startActivity(intent);

            }
        });
    }

}
