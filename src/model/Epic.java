package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class Epic extends Task {
    private List<Subtask> subtasks;
    private LocalDateTime endTime;

    public Epic() {
        this.subtasks = new ArrayList<>();
    }

    public Epic(String name, String description, TaskStatus status, TaskType type, Duration duration, LocalDateTime startTime) {
        super(name, description, status, type, duration, startTime);
        this.subtasks = new ArrayList<>();
        endTime = LocalDateTime.MIN;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public List<Subtask> getSubtasks() {
        return subtasks;
    }

    public void setSubtasks(List<Subtask> subtasks) {
        this.subtasks = subtasks;
    }

    public void updateTimesParameters() {
        Optional<Subtask> earlySubtaskOptional = subtasks.stream()
                .min(Comparator.comparing(Task::getStartTime));
        earlySubtaskOptional.ifPresent(subtask -> this.setStartTime(subtask.getStartTime()));

        Optional<Subtask> olderSubtaskOptional = subtasks.stream()
                .max(Comparator.comparing(Task::getEndTime));
        olderSubtaskOptional.ifPresent(subtask -> endTime = subtask.getEndTime());

        setDuration(Duration.ZERO);
        subtasks.forEach(subtask -> setDuration(getDuration().plus(subtask.getDuration())));
    }

    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(subtasks, epic.subtasks);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subtasks);
    }
}
