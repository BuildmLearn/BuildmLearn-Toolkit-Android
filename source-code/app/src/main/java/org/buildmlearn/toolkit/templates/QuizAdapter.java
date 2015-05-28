package org.buildmlearn.toolkit.templates;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import org.buildmlearn.toolkit.R;
import org.buildmlearn.toolkit.views.TextViewPlus;

import java.util.ArrayList;

/**
 * Created by abhishek on 28/5/15.
 */
public class QuizAdapter extends BaseAdapter {


    private Context context;
    private ArrayList<QuizModel> quizData;

    public QuizAdapter(Context context, ArrayList<QuizModel> quizData) {
        this.context = context;
        this.quizData = quizData;
    }

    @Override
    public int getCount() {
        return quizData.size();
    }

    @Override
    public QuizModel getItem(int position) {
        return quizData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater mInflater;
        mInflater = LayoutInflater.from(context);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.quiz_item, parent, false);
        }
        Holder holder = new Holder();
        holder.question = (TextViewPlus) convertView.findViewById(R.id.question);
        holder.options = new ArrayList<>();
        holder.options.add((TextViewPlus) convertView.findViewById(R.id.answer1));
        holder.options.add((TextViewPlus) convertView.findViewById(R.id.answer2));
        holder.options.add((TextViewPlus) convertView.findViewById(R.id.answer3));
        holder.options.add((TextViewPlus) convertView.findViewById(R.id.answer4));
        holder.questionIcon = (TextViewPlus) convertView.findViewById(R.id.question_icon);

        QuizModel data = getItem(position);
        holder.question.setText(data.getQuestion());
        holder.questionIcon.setText(data.getQuestion().substring(0, 1));

        for (int i = 0; i < holder.options.size(); i++) {
            if (i < data.getOptions().size()) {
                int ascii = 65 + i;
                holder.options.get(i).setText(Character.toString((char)ascii) + ")  " + data.getOptions().get(i));
                holder.options.get(i).setVisibility(View.VISIBLE);
            } else {
                holder.options.get(i).setVisibility(View.INVISIBLE);
            }
        }

        holder.options.get(data.getCorrectAnswer()).setCustomFont(context, "roboto_medium.ttf");
        holder.options.get(data.getCorrectAnswer()).setTextColor(context.getResources().getColor(R.color.quiz_correct_answer));
        return convertView;
    }

    public class Holder {
        TextViewPlus question;
        TextViewPlus questionIcon;
        ArrayList<TextViewPlus> options;
    }
}
