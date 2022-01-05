package com.costaneto.todo;

public interface RecyclerViewInterface {
    void onItemLongClicked(int position, String task);
    void updateTaskStatus(String task, int status);
//    void deleteTask(String task);
}
