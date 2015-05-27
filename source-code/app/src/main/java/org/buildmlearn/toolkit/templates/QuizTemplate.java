package org.buildmlearn.toolkit.templates;

import android.R;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Toast;

import org.buildmlearn.toolkit.fragment.dummy.DummyContent;
import org.buildmlearn.toolkit.fragment.dummy.DummyContent.DummyItem;
import org.buildmlearn.toolkit.model.TemplateInterface;

/**
 * Created by abhishek on 27/5/15.
 */
public class QuizTemplate implements TemplateInterface {


    transient private ArrayAdapter<DummyItem> mAdapter;
    private int count;

    public QuizTemplate() {
        count = 0;
    }

    @Override
    public BaseAdapter newTemplateEditorAdapter(Context context) {
        mAdapter = new ArrayAdapter<>(context,
                android.R.layout.simple_list_item_1, R.id.text1, DummyContent.ITEMS);
        return mAdapter;
    }

    @Override
    public BaseAdapter currentTemplateEditorAdapter() {
        return mAdapter;
    }

    @Override
    public String onAttach() {
        count++;
        return "This is Quiz Template, Count: " + count;
    }

    @Override
    public String getTitle() {
        return "Quiz Template";
    }

    @Override
    public void addItem(final Context context) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

        // set title
        alertDialogBuilder.setTitle("Add new item to Quiz Template?");

        // set dialog message
        alertDialogBuilder
                .setMessage("Click yes to add!")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(context, "Lets add some items", Toast.LENGTH_LONG).show();
                        DummyContent.ITEMS.add(new DummyItem("1", "Abhishek Batra"));
                        mAdapter.notifyDataSetChanged();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

}
