package org.buildmlearn.toolkit.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.ColorRes;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.buildmlearn.toolkit.R;
import org.buildmlearn.toolkit.model.Template;

/**
 * @brief Adapter used for showing Templates available in the toolkit
 *
 * Created by Abhishek on 23-05-2015.
 */
public class TemplateAdapter extends BaseAdapter {

    private Context context;
    private int count;

    private Template[] templates = Template.values();
    private ListColor[] colors = ListColor.values();

    public TemplateAdapter(Context context, int count) {
        this.context = context;
        this.count = count;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getCount() {
        return templates.length;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Template getItem(int i) {
        return templates[i];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getItemId(int i) {
        return i;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        LayoutInflater mInflater;
        mInflater = LayoutInflater.from(context);

        if (i % 2 == 0) {
            view = mInflater.inflate(R.layout.item_template_right, viewGroup, false);
        } else {
            view = mInflater.inflate(R.layout.item_template_left, viewGroup, false);
        }

        Template template = getItem(i);

        ((TextView) view.findViewById(R.id.title)).setText(template.getTitle());
        ((TextView) view.findViewById(R.id.description)).setText(template.getDescription());
        ((ImageView) view.findViewById(R.id.image)).setImageResource(template.getImage());

        int color = colors[i % colors.length].getColor();
        ((CardView) view.findViewById(R.id.card_view)).setCardBackgroundColor(color);

        return view;
    }

    private enum ListColor {
        BLUE("#29A6D4"),
        GREEN("#1C7D6C"),
        ORANGE("#F77400"),
        RED("#F53B3C"),
        GRAYISH("#78909C");

        private
        @ColorRes
        int color;

        ListColor(String colorCode) {
            this.color = Color.parseColor(colorCode);
        }

        public int getColor() {
            return color;
        }
    }
}
