package model;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private final List<Integer> subtasksIds;

    public Epic(String name, String description, TaskStatus status) {
        super(name, description, status);
        this.subtasksIds = new ArrayList<>();
    }

    public List<Integer> getSubtasks() {
        return subtasksIds;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "subtasksIds=" + subtasksIds +
                "} " + super.toString();
    }
}
