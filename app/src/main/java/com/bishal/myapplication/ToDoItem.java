package com.bishal.myapplication;

import androidx.annotation.NonNull;

public class ToDoItem {
    private String task;
    private boolean completed;

    public ToDoItem(String task, boolean completed) {
        this.task = task;
        this.completed = completed;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    @NonNull
    @Override
    public String toString() {
        return task + (completed ? " [completed]" : " [not completed]");
    }
}
