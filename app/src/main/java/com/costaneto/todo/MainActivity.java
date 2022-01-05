package com.costaneto.todo;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.costaneto.todo.recyclerviewadpter.TaskAdapter;
import com.costaneto.todo.recyclerviewmodel.TaskModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements RecyclerViewInterface {

    FloatingActionButton addNewTaskButton;
    Button dialogAddTaskButton;
    EditText dialogAddTaskEditText;
    AlertDialog dialog;
    ImageView closeDialogImageView;
    RecyclerView tasksRecyclerView;
    TaskAdapter adapter;
    DatabaseManager databaseManager;
    Cursor cursor;
    ArrayList<TaskModel> taskModels = new ArrayList<>();
    int taskStatus = 0; // not done

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tasksRecyclerView = findViewById(R.id.tasksRecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        tasksRecyclerView.setLayoutManager(linearLayoutManager);
        adapter = new TaskAdapter(this, taskModels, this);
        tasksRecyclerView.setAdapter(adapter);


        // Open database
        databaseManager = new DatabaseManager(this);
        try {
            databaseManager.open();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Get task from database
        // and pass it to RecyclerView
        cursor = databaseManager.fetchAll();
        if (cursor.moveToFirst()) {
            do {
                String task = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.TASK_TEXT));
                int status = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.TASK_STATUS));
                Log.e("mainActi >> DATABASE_TASK", "TASK_TEXT > " + task + "  |  TASK_STATUS > " + status);
                taskModels.add(new TaskModel(task, status));
                adapter.notifyItemInserted(taskModels.size() - 1);
            } while (cursor.moveToNext());
        }

        // Button to add a new task
        addNewTaskButton = findViewById(R.id.addNewTaskButton);

        // Dialog prompting content of new task
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Inflate the view needed
        View view = getLayoutInflater().inflate(R.layout.adding_new_task_layout, null);
        dialogAddTaskEditText = view.findViewById(R.id.dialogAddTaskEditText);
        dialogAddTaskButton = view.findViewById(R.id.dialogAddTaskButton);
        dialogAddTaskButton.setOnClickListener(v -> {
            // Get task from edit text
            String task = dialogAddTaskEditText.getText().toString();
            dialogAddTaskEditText.getText().clear();

            // Insert task into database and
            // pass it to RecyclerView
            setToDoTasks(task);

            // Close dialog
            dialog.dismiss();
        });

        // Set the view to dialog
        builder.setView(view);

        // Create dialog
        dialog = builder.create();

        // Close dialog
        closeDialogImageView = view.findViewById(R.id.closeDialogImageView);
        closeDialogImageView.setOnClickListener(v -> dialog.hide());

        // Adding a new task
        addNewTaskButton.setOnClickListener(v -> {
            dialog.show();
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        });
    }

    private void setToDoTasks(String task) {
        // Insert task into database
        databaseManager.insert(task, taskStatus);

        // Pass task to RecyclerView
        cursor = databaseManager.fetchSingle();
        String databaseTask = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.TASK_TEXT));
        taskModels.add(new TaskModel(databaseTask, taskStatus));
        adapter.notifyItemInserted(taskModels.size() - 1);

    }

    @Override
    public void onItemLongClicked(int position, String task) {
        taskModels.remove(position);
        adapter.notifyItemRemoved(position);
        adapter.notifyItemRangeChanged(position, taskModels.size());
        databaseManager.delete(task);
    }

    @Override
    public void updateTaskStatus(String task, int status) {
        databaseManager.updateStatus(task, status);
    }

//    @Override
//    public void deleteTask(String task) {
//        databaseManager.delete(task);
//    }
}