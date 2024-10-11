package service;

import model.Task;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final List<Task> viewHistory;

    public InMemoryHistoryManager() {
        viewHistory = new LinkedList<>();
    }

    @Override
    public void add(Task task) {
        if (viewHistory.size() < 10) {
            viewHistory.add(task);
        } else {
            viewHistory.removeFirst();
            viewHistory.add(task);
        }
    }

    @Override
    public List<Task> getHistory() {
        return viewHistory;
    }
}
