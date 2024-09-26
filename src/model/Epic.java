package model;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private final List<Long> subtasksIds;

    public Epic(String name, String description, TaskStatus status) {
        super(name, description, status);
        this.subtasksIds = new ArrayList<>();
    }

    public List<Long> getSubtasks() {
        return subtasksIds;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "subtasksIds=" + subtasksIds +
                "} " + super.toString();
    }
}
