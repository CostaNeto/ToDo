package com.costaneto.todo.recyclerviewadpter;

import android.content.Context;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.costaneto.todo.R;
import com.costaneto.todo.RecyclerViewInterface;
import com.costaneto.todo.recyclerviewmodel.TaskModel;

import java.util.ArrayList;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {

    private final RecyclerViewInterface recyclerViewInterface;
    Context context;
    ArrayList<TaskModel> taskModels;

    public TaskAdapter(Context context, ArrayList<TaskModel> taskModels, RecyclerViewInterface recyclerViewInterface) {
        this.context = context;
        this.taskModels = taskModels;
        this.recyclerViewInterface = recyclerViewInterface;
    }


    @NonNull
    @Override
    public TaskAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.task_layout, parent, false);
        return new TaskAdapter.ViewHolder(view, recyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskAdapter.ViewHolder holder, int position) {
        holder.taskTextView.setText(taskModels.get(position).getTask());
        Log.e("adapter >> DATABASE_TASK", "TASK_TEXT > " + taskModels.get(position).getTask() + "  |  TASK_STATUS > " + taskModels.get(position).getStatus());
        if (taskModels.get(position).getStatus() == 1) {
            holder.taskCheckBox.setChecked(true);
            holder.taskTextView.setPaintFlags(holder.taskTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            Log.e("adapter >> checkBox.isChecked()", Boolean.toString(holder.taskCheckBox.isChecked()));
        } else{
            holder.taskCheckBox.setChecked(false);
            holder.taskTextView.setPaintFlags(holder.taskTextView.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
            Log.e("adapter >> checkBox.isChecked()", Boolean.toString(holder.taskCheckBox.isChecked()));
        }

    }


    @Override
    public int getItemCount() {
        return taskModels.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        CheckBox taskCheckBox;
        TextView taskTextView;

        public ViewHolder (@NonNull View itemView, RecyclerViewInterface recyclerViewInterface) {
            super(itemView);

            taskCheckBox = itemView.findViewById(R.id.taskCheckBox);
            taskTextView = itemView.findViewById(R.id.taskTextView);

            // Set task as done/not done
            taskCheckBox.setOnClickListener(v -> {
                if (taskCheckBox.isChecked()) {
                    taskTextView.setPaintFlags(taskTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    recyclerViewInterface.updateTaskStatus(taskTextView.getText().toString(), 1);
                }
                else {
                    taskTextView.setPaintFlags(taskTextView.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
                    recyclerViewInterface.updateTaskStatus(taskTextView.getText().toString(), 0);
                }
            });

            // Deleting a task from the list
            itemView.setOnLongClickListener(v -> {
                if (recyclerViewInterface != null) {
                    int pos = getBindingAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION)
                        recyclerViewInterface.onItemLongClicked(pos, taskTextView.getText().toString());
                }
                return true;
            });
        }
    }
}
