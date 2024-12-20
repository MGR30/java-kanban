package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Subtask extends Task {
    private Long epicId;

    public Subtask() {

    }

    public Subtask(String name, String description, TaskStatus status, TaskType type, Duration duration, LocalDateTime startTime) {
        super(name, description, status, type, duration, startTime);
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
