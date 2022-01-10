package com.costaneto.todo.recyclerviewmodel;

public class TaskModel {
    private String task;
    private int status;
    private boolean isSelected = false;

    public TaskModel(String task, int status, boolean isSelected) {
        this.task = task;
        this.status = status;
        this.isSelected = isSelected;
    }

    public String getTask() {
        return task;
    }

    public int getStatus() {
        return status;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
