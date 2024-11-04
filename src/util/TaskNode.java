package util;

import model.Task;

import java.util.Objects;

public class TaskNode {
    private Task task;
    private TaskNode next;
    private TaskNode prev;

    public TaskNode(TaskNode prev, Task task, TaskNode next) {
        this.task = task;
        this.next = next;
        this.prev = prev;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public TaskNode getNext() {
        return next;
    }

    public void setNext(TaskNode next) {
        this.next = next;
    }

    public TaskNode getPrev() {
        return prev;
    }

    public void setPrev(TaskNode prev) {
        this.prev = prev;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaskNode taskNode = (TaskNode) o;
        return Objects.equals(task, taskNode.task) && Objects.equals(next, taskNode.next) && Objects.equals(prev, taskNode.prev);
    }

    @Override
    public int hashCode() {
        return Objects.hash(task, next, prev);
    }
}
