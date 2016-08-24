package org.buildmlearn.toolkit.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

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
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        TemplateAdapter mAdapter = new TemplateAdapter(this);
        RecyclerView mRecyclerView = (RecyclerView) findViewById(android.R.id.list);
        assert mRecyclerView != null;
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mAdapter.setOnClickListener(new TemplateAdapter.SetOnClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(getApplicationContext(), TemplateEditor.class);
                intent.putExtra(Constants.TEMPLATE_ID, position);
                startActivity(intent);
            }
        });
    }

}
