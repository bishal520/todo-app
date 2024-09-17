package com.bishal.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ToDoAdapter extends RecyclerView.Adapter<ToDoAdapter.ToDoViewHolder> {

    private List<ToDoItem> toDoItems;
    private OnItemCheckedChangeListener listener;

    public ToDoAdapter(List<ToDoItem> toDoItems, OnItemCheckedChangeListener listener) {
        this.toDoItems = toDoItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ToDoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.todo_item, parent, false);
        return new ToDoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ToDoViewHolder holder, int position) {
        ToDoItem item = toDoItems.get(position);

        holder.checkBox.setText(item.getTask());
        holder.checkBox.setChecked(item.isCompleted());

        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                item.setCompleted(isChecked); // Update the item's completion status
                if (listener != null) {
                    listener.onItemCheckedChanged(item); // Notify the listener
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return toDoItems.size();
    }

    public static class ToDoViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;

        public ToDoViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.checkBox);
        }
    }

    // Interface for item checked change callback
    public interface OnItemCheckedChangeListener {
        void onItemCheckedChanged(ToDoItem item);
    }
}
