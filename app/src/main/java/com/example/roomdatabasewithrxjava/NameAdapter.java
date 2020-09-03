package com.example.roomdatabasewithrxjava;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class NameAdapter extends RecyclerView.Adapter<NameViewHolder> {

    private List<String> names;

    public void setNames(List<String> names) {
        this.names = names;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NameViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NameViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull NameViewHolder holder, int position) {
        holder.setName(names.get(position));
    }

    @Override
    public int getItemCount() {
        if (names != null)
            return names.size();
        return 0;
    }
}
