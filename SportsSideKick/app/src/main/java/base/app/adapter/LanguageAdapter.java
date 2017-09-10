package base.app.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import base.app.R;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Aleksandar Marinkovic on 05-Jun-17.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */


public class LanguageAdapter extends RecyclerView.Adapter<LanguageAdapter.ViewHolder> {

    private String currentLanguage;
    private String[] values;
    private LanguageOnClick languageOnClick;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public View view;
        @BindView(R.id.background)
        ImageView background;
        @BindView(R.id.icon)
        ImageView icon;
        @BindView(R.id.caption)
        TextView caption;


        ViewHolder(View v) {
            super(v);
            view = v;
            ButterKnife.bind(this, view);
        }
    }

    public LanguageAdapter(Context context, LanguageOnClick languageOnClick) {
        if (context != null) {
            values = context.getResources().getStringArray(R.array.language_names);
            currentLanguage = context.getString(R.string.this_language);
        }
        this.languageOnClick = languageOnClick;
    }

    @Override
    public LanguageAdapter.ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        final ViewHolder viewHolder;
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.language_item, parent, false);
        viewHolder = new ViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = viewHolder.getAdapterPosition();
                if (!currentLanguage.equals(values[position])) {
                    languageOnClick.languageChange(values[position], languageShortName.get(position));
                }
            }
        });
        return viewHolder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        String itemLanguage = values[position];
        holder.icon.setImageResource(languageIcons.get(position));
        holder.caption.setText(itemLanguage);
        if (itemLanguage.equals(currentLanguage)) {
            holder.background.setVisibility(View.VISIBLE);
        } else {
            holder.background.setVisibility(View.INVISIBLE);
        }
    }


    @Override
    public int getItemCount() {
        if (languageIcons != null){
            return languageIcons.size();
        }
        return 0;
    }


    private static final List<Integer> languageIcons = Collections.unmodifiableList(
        new ArrayList<Integer>() {{
            add(R.drawable.bengali);
            add(R.drawable.chinese);
            add(R.drawable.english);
            add(R.drawable.french);
            add(R.drawable.german);
            add(R.drawable.indonesian);
            add(R.drawable.italian);
            add(R.drawable.polish);
            add(R.drawable.portuguese);
            add(R.drawable.russian);
            add(R.drawable.spanish);
            add(R.drawable.arabic);
        }});


    public interface LanguageOnClick {
        void languageChange(String language,String  shortLanguage);
    }


    private static final List<String> languageShortName = Collections.unmodifiableList(
            new ArrayList<String>() {{
                add("bh");
                add("zh");
                add("en");
                add("fr");
                add("de");
                add("id");
                add("it");
                add("pl");
                add("pt");
                add("ru");
                add("es");
                add("ar");
            }});
}