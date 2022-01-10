package com.costaneto.todo.recyclerviewadpter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.costaneto.todo.MainActivity;
import com.costaneto.todo.R;
import com.costaneto.todo.RecyclerViewInterface;
import com.costaneto.todo.recyclerviewmodel.TaskModel;

import java.util.ArrayList;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {

    private final RecyclerViewInterface recyclerViewInterface;
    private Context context;
    private ArrayList<TaskModel> taskModels;

    // Necessary for item selection when deleting
    private MainActivity mainActivity;

    public TaskAdapter(Context context, ArrayList<TaskModel> taskModels, RecyclerViewInterface recyclerViewInterface) {
        this.context = context;
        this.taskModels = taskModels;
        this.recyclerViewInterface = recyclerViewInterface;
        this.mainActivity = (MainActivity) context;
    }

    @NonNull
    @Override
    public TaskAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.task_layout, parent, false);
        return new TaskAdapter.ViewHolder(view, mainActivity);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskAdapter.ViewHolder holder, int position) {
        holder.taskCardView.setCardElevation(5);
        holder.taskTextView.setText(taskModels.get(position).getTask());
        if (taskModels.get(position).getStatus() == 1) {
            holder.taskCheckBox.setChecked(true);
            holder.taskTextView.setPaintFlags(holder.taskTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else{
            holder.taskCheckBox.setChecked(false);
            holder.taskTextView.setPaintFlags(holder.taskTextView.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
        }

        mainActivity.setBackgroundMessage();

        // Check what model is first selected
        // and then reset firstSelected position.
        // This way, the next item that consequently
        // gets the same position won't be considered
        // as first selected.
        if (mainActivity.firstSelected == position) {
            taskModels.get(position).setSelected(true);
            mainActivity.firstSelected = -1;
        }

        if (mainActivity.isActionMode) {
            if (taskModels.get(position).isSelected()){
                holder.taskCardView.setCardBackgroundColor(Color.parseColor("#FFEBEBEB"));
                holder.taskCardView.setCardElevation(2);
            }
            else {
                holder.taskCardView.setCardBackgroundColor(Color.WHITE);
                holder.taskCardView.setCardElevation(5);
            }
            if (taskModels.get(position).getStatus() == 1) {
                holder.taskTextView.setPaintFlags(holder.taskTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                holder.taskCheckBox.setChecked(true);
                taskModels.get(position).setStatus(1);
            }
            else {
                holder.taskTextView.setPaintFlags(holder.taskTextView.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
                holder.taskCheckBox.setChecked(false);
                taskModels.get(position).setStatus(0);
            }

            Log.e("BINDING", "[position] > " + position + " [status] > " + taskModels.get(position).getStatus());
            Log.e("BINDING", "[position_is_selected] > " + taskModels.get(position).isSelected());
        }
        else {
            holder.taskCardView.setCardBackgroundColor(Color.WHITE);
            taskModels.get(position).setSelected(false);
        }

        applyClickEvents(holder, position);
    }

    @Override
    public int getItemCount() {
        return taskModels.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        CardView taskCardView;
        CheckBox taskCheckBox;
        TextView taskTextView;
        MainActivity mainActivity;


        public ViewHolder (@NonNull View itemView, MainActivity mainActivity) {
            super(itemView);

            taskCheckBox = itemView.findViewById(R.id.taskCheckBox);
            taskTextView = itemView.findViewById(R.id.taskTextView);
            taskCardView = itemView.findViewById(R.id.taskCardView);
            this.mainActivity = mainActivity;
        }
    }

    private void applyClickEvents(@NonNull ViewHolder holder, int position) {
        // Set task as done/not done
        holder.taskCheckBox.setOnClickListener(v -> {
            if (holder.taskCheckBox.isChecked()) {
                holder.taskTextView.setPaintFlags(holder.taskTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                recyclerViewInterface.updateTaskStatus(holder.taskTextView.getText().toString(), 1);
                taskModels.get(position).setStatus(1);
            }
            else {
                holder.taskTextView.setPaintFlags(holder.taskTextView.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
                recyclerViewInterface.updateTaskStatus(holder.taskTextView.getText().toString(), 0);
                taskModels.get(position).setStatus(0);
            }
        });

        // Selection event
        holder.taskCardView.setOnClickListener(view -> {
            if (mainActivity.isActionMode)
                recyclerViewInterface.selectTask(position);
        });

        holder.taskCardView.setOnLongClickListener(view -> {
            recyclerViewInterface.startSelection(position);
            return true;
        });
    }


}
