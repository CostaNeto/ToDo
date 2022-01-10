package com.costaneto.todo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.costaneto.todo.recyclerviewadpter.TaskAdapter;
import com.costaneto.todo.recyclerviewmodel.TaskModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements RecyclerViewInterface {


    private FloatingActionButton addNewTaskButton;
    private ConstraintLayout noTasksConstraintLayout;
    private RecyclerView tasksRecyclerView;
    private Button dialogAddTaskButton;
    private EditText dialogAddTaskEditText;
    private AlertDialog dialog;
    private ImageView closeDialogImageView;
    private ArrayList<TaskModel> taskModels = new ArrayList<>();
    private ArrayList<TaskModel> selectedList = new ArrayList<>();
    private TaskAdapter adapter;
    private DatabaseManager databaseManager;
    private Cursor cursor;
    private int databaseTaskCount = 0, taskStatus = 0, selectedItemsCount = 0;
    public int firstSelected = -1;
    private Toolbar deletionToolbar;
    private TextView selectedTasksCountTextView;
    private ImageButton closeDeleteButton;
    public boolean isActionMode = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Toolbar operations
        deletionToolbar = findViewById(R.id.deletionToolbar);
        setSupportActionBar(deletionToolbar);
        selectedTasksCountTextView = findViewById(R.id.selectedTasksCountTextView);
        closeDeleteButton = findViewById(R.id.closeDeleteButton);
        closeDeleteButton.setOnClickListener(v -> closeActionMode());

        // RecyclerView
        tasksRecyclerView = findViewById(R.id.tasksRecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        tasksRecyclerView.setLayoutManager(linearLayoutManager);
        adapter = new TaskAdapter(this, taskModels, this);
        tasksRecyclerView.setAdapter(adapter);
        noTasksConstraintLayout = findViewById(R.id.noTasksConstraintLayout);

        // Open database
        databaseManager = new DatabaseManager(this);
        try {
            databaseManager.open();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Get task from database
        // and pass it to RecyclerView
        cursor = databaseManager.fetchAllTasks();
        if (cursor.moveToFirst()) {
            do {
                String id = cursor.getString(cursor.getColumnIndexOrThrow((DatabaseHelper.TASK_ID)));
                String task = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.TASK_TEXT));
                int status = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.TASK_STATUS));
                Log.e("  <mainActivity> DATABASE_TASK", "[id] > " + id + " [text] > " + task + " [status] > " + status);
                taskModels.add(new TaskModel(task, status, false));
                databaseTaskCount++;
                adapter.notifyItemInserted(taskModels.size() - 1);
            } while (cursor.moveToNext());
        }
        Log.e("DatabaseCount", databaseTaskCount +  " [taskModel.size] > " + taskModels.size());
        // If there are no tasks,
        // show message saying so.
        setBackgroundMessage();

        // Builder for dialog prompting
        // content of new task
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Inflate the adding_new_task_layout
        // for prompt dialog
        View view = getLayoutInflater().inflate(R.layout.adding_new_task_layout, null);
        dialogAddTaskEditText = view.findViewById(R.id.dialogAddTaskEditText);
        dialogAddTaskButton = view.findViewById(R.id.dialogAddTaskButton);
        dialogAddTaskButton.setOnClickListener(v -> {
            // Get task from edit text
            String task = dialogAddTaskEditText.getText().toString();
            dialogAddTaskEditText.getText().clear();

            // Insert task into database and
            // from thence into RecyclerView
            setToDoTasks(task);

            // Close dialog
            dialog.dismiss();
        });

        // Set the inflated view to dialog builder
        builder.setView(view);

        // Create dialog
        dialog = builder.create();

        // Close dialog
        closeDialogImageView = view.findViewById(R.id.closeDialogImageView);
        closeDialogImageView.setOnClickListener(v -> dialog.hide());

        // Open the task prompting
        // dialog window using the fab
        addNewTaskButton = findViewById(R.id.addNewTaskButton);
        addNewTaskButton.setOnClickListener(v -> {
            dialog.show();
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        });
    }

    public void setBackgroundMessage() {
        if (taskModels.size() == 0)
            noTasksConstraintLayout.setVisibility(View.VISIBLE);
        else
            noTasksConstraintLayout.setVisibility(View.GONE);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.delete_task && selectedList.size() > 0)
            deleteTasks();
        else if (item.getItemId() == R.id.select_all_tasks)
            selectAllTasks();

        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void closeActionMode() {
        isActionMode = false;
        closeDeleteButton.setVisibility(View.GONE);
        selectedTasksCountTextView.setVisibility(View.GONE);
        selectedItemsCount = 0;
        selectedList.clear();
        deletionToolbar.getMenu().clear();
        adapter.notifyDataSetChanged();
    }

    private void setToDoTasks(String task) {
        // Insert task into database
        databaseManager.insertTask(task, taskStatus);

        // Pass task to RecyclerView
        cursor = databaseManager.fetchSingleTask();
        String databaseTask = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.TASK_TEXT));
        taskModels.add(new TaskModel(databaseTask, taskStatus, false));
        adapter.notifyItemInserted(taskModels.size() - 1);

    }

    @SuppressLint("NotifyDataSetChanged")
    private void selectAllTasks() {
        // If all tasks aren't selected
        if (selectedList.size() < taskModels.size()) {
            for (int i = 0; i < taskModels.size(); i++) {
                if (!taskModels.get(i).isSelected()) {
                    taskModels.get(i).setSelected(true);
                    selectedList.add(taskModels.get(i));
                }
                changeToolbarText(taskModels.size());
                adapter.notifyDataSetChanged();
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void deleteTasks() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if (selectedList.size() == 1)
            builder.setMessage("Delete 1 task?");
        else
            builder.setMessage("Delete " + selectedList.size() + " tasks?");
        builder.setPositiveButton("Delete", (dialog, which) -> {
            for (TaskModel model : selectedList) {
                databaseManager.deleteTask(model.getTask());
                taskModels.remove(model);
            }
            adapter.notifyDataSetChanged();
            closeActionMode();
            setBackgroundMessage();
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> {

        });
        builder.show();

    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void selectTask(int position) {
        // Check if task is already selected
        if (taskModels.get(position).isSelected()) {
            taskModels.get(position).setSelected(false);
            selectedList.remove(taskModels.get(position));
            selectedItemsCount--;
        }
        else {
            taskModels.get(position).setSelected(true);
            selectedList.add(taskModels.get(position));
            selectedItemsCount++;
        }
        changeToolbarText(selectedItemsCount);
        adapter.notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void startSelection(int position) {
        if (!isActionMode){
            isActionMode = true;
            taskModels.get(position).setSelected(true);
            selectedList.add(taskModels.get(position));
            closeDeleteButton.setVisibility(View.VISIBLE);
            selectedTasksCountTextView.setVisibility(View.VISIBLE);
            selectedItemsCount++;
            changeToolbarText(selectedItemsCount);
            firstSelected = position;
            deletionToolbar.inflateMenu(R.menu.delete_task_menu);
            adapter.notifyDataSetChanged();
        }
    }

    @SuppressLint("SetTextI18n")
    private void changeToolbarText(int selectedItemsCount) {
        if (selectedItemsCount == 0)
            selectedTasksCountTextView.setText("0 tasks selected");
        else if (selectedItemsCount == 1)
            selectedTasksCountTextView.setText("1 task selected");
        else
            selectedTasksCountTextView.setText(selectedItemsCount + " tasks selected");
    }


    @Override
    public void updateTaskStatus(String task, int status) {
        databaseManager.updateTaskStatus(task, status);
    }

}