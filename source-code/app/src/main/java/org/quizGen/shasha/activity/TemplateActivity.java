package org.quizGen.shasha.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import org.quizGen.shasha.R;
import org.quizGen.shasha.adapter.TemplateAdapter;
import org.quizGen.shasha.constant.Constants;


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
