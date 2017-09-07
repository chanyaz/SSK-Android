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

    private Context context;
    private String myLanguage;
    private String[] values;
    LanguageOnClick languageOnClick;

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
            this.context = context;
            values = context.getResources().getStringArray(R.array.language_names);
            myLanguage = context.getString(R.string.this_language);
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
                if (!myLanguage.equals(values[viewHolder.getAdapterPosition()])) {
                    languageOnClick.languageChange(values[viewHolder.getAdapterPosition()],short_language.get(viewHolder.getAdapterPosition()));
                }
            }
        });
        return viewHolder;
    }


    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.icon.setImageResource(languageImages[position]);
        holder.caption.setText(values[position]);
        if (context.getString(R.string.this_language).equals(values[position])) {
            holder.background.setVisibility(View.VISIBLE);
        } else {
            holder.background.setVisibility(View.INVISIBLE);
        }


    }


    @Override
    public int getItemCount() {
        if (languageImages == null)
            return 0;
        return languageImages.length;
    }


    public static final int[] languageImages = {
            R.drawable.bengali,
            R.drawable.chinese,
            R.drawable.english,
            R.drawable.french,
            R.drawable.german,
            R.drawable.indonesian,
            R.drawable.italian,
            R.drawable.polish,
            R.drawable.portuguese,
            R.drawable.russian,
            R.drawable.spanish,
    };


    public interface LanguageOnClick {
        void languageChange(String language,String  shortLanguage);
    }


    public static final List<String> short_language = Collections.unmodifiableList(
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
                // etc
            }});
}