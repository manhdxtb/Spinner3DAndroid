package com.app.spinner.activity.adslib;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.spinner.R;
import com.app.spinner.databinding.ActivityLanguageItemBinding;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Locale;


public class LanguageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<LanguageModel> listLanguage = new ArrayList<>();

    private LanguageAdapterListener languageAdapterListener;

    public int currentPosition = -69;

    public LanguageAdapter(LanguageAdapterListener languageAdapterListener, String currentLang) {
        listLanguage.clear();

        listLanguage.add(new LanguageModel(R.drawable.img_language_english, "English", "en"));
        listLanguage.add(new LanguageModel(R.drawable.img_language_german, "German", "de"));
        listLanguage.add(new LanguageModel(R.drawable.img_language_spain, "Spanish", "es"));
        listLanguage.add(new LanguageModel(R.drawable.img_language_france, "French", "fr"));
        listLanguage.add(new LanguageModel(R.drawable.img_language_hindi, "Hindi", "hi"));
        listLanguage.add(new LanguageModel(R.drawable.img_language_indonesia, "Indonesian", "in"));
        listLanguage.add(new LanguageModel(R.drawable.img_language_vietnam, "Vietnam", "vi"));
        listLanguage.add(new LanguageModel(R.drawable.img_language_italy, "Italy", "it"));
        listLanguage.add(new LanguageModel(R.drawable.img_language_malaysia, "Malaysia", "ms"));
        listLanguage.add(new LanguageModel(R.drawable.img_language_portugal, "Portuguese", "pt"));
        listLanguage.add(new LanguageModel(R.drawable.img_language_russia, "Russian", "ru"));
        listLanguage.add(new LanguageModel(R.drawable.img_language_thailand, "Thai", "th"));
        listLanguage.add(new LanguageModel(R.drawable.img_language_turkey, "Turkey", "tr"));

        this.languageAdapterListener = languageAdapterListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(ActivityLanguageItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        LanguageModel languageModel = listLanguage.get(position);

        Glide.with(holder.itemView.getContext()).load(languageModel.getLogoLanguage()).into(viewHolder.binding.imgLogo);
        viewHolder.binding.tvLanguage.setText(new Locale(languageModel.getLg()).getDisplayName());

        if (currentPosition == position) {
            viewHolder.binding.icSelect.setImageResource(R.drawable.ic_checked);
        } else {
            viewHolder.binding.icSelect.setImageResource(R.drawable.ic_uncheck);
        }
    }

    @Override
    public int getItemCount() {
        return this.listLanguage.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {

        private ActivityLanguageItemBinding binding;

        public ViewHolder(ActivityLanguageItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (getBindingAdapterPosition() < 0 || getBindingAdapterPosition() >= listLanguage.size())
                        return;

                    if (getBindingAdapterPosition() == currentPosition) {
                        return;
                    }

                    currentPosition = getBindingAdapterPosition();
                    if (languageAdapterListener != null) {
                        languageAdapterListener.onClick(listLanguage.get(currentPosition));
                    }
                    notifyDataSetChanged();
                }
            });
        }
    }

    public interface LanguageAdapterListener {
        void onClick(LanguageModel languageModel);
    }
}
