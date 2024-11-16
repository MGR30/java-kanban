package model;

import java.util.Objects;

public class Subtask extends Task {
    private Long epicId;

    public Subtask(String name, String description, TaskStatus status, TaskType type) {
        super(name, description, status, type);
    }

    public Long getEpicId() {
        return epicId;
    }

    public void setEpicId(Long epicId) {
        if (!Objects.equals(epicId, this.getId()) && this.epicId == null) {
            this.epicId = epicId;
        }
    }

    @Override
    public String toString() {
        return super.toString() + "," + epicId;
    }
}
