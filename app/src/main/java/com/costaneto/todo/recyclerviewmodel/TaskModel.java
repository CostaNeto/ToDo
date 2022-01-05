package com.costaneto.todo.recyclerviewmodel;

public class TaskModel {
    private String task;
    private int status;

    public TaskModel(String task, int status) {
        this.task = task;
        this.status = status;
    }

    public String getTask() {
        return task;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
