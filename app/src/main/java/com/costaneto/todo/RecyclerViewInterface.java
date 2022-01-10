package com.costaneto.todo;

public interface RecyclerViewInterface {

    // Checkbox actions
    void updateTaskStatus(String task, int status);

    // Selecting items for deletion
    void startSelection(int position);
    void selectTask(int position);


}
