package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class Epic extends Task {
    private final List<Subtask> subtasks;
    private LocalDateTime endTime;

    public Epic(String name, String description, TaskStatus status, TaskType type, Duration duration, LocalDateTime startTime) {
        super(name, description, status, type, duration, startTime);
        this.subtasks = new ArrayList<>();
    }

    public LocalDateTime getEndTime() {
        endTime = getStartTime().plus(getDuration());
        return endTime;
    }

    public List<Subtask> getSubtasks() {
        return subtasks;
    }

    public void updateTimesParameters() {
        Optional<Subtask> earlySubtaskOptional = subtasks.stream()
                .min(Comparator.comparing(Task::getStartTime));
        earlySubtaskOptional.ifPresent(subtask -> this.setStartTime(subtask.getStartTime()));

        setDuration(Duration.ZERO);
        subtasks.forEach(subtask -> setDuration(getDuration().plus(subtask.getDuration())));
        endTime = getStartTime().plus(getDuration());
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
