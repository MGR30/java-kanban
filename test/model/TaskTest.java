package model;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TaskTest {

    @Test
    public void taskWithSameIdsAreSame() {
        Task task = new Task("name", "description", TaskStatus.NEW, TaskType.TASK, Duration.ZERO, LocalDateTime.now());
        Task otherTask = new Task("name", "description", TaskStatus.NEW, TaskType.TASK, Duration.ZERO, LocalDateTime.now());
        task.setId(1L);
        otherTask.setId(1L);

        assertEquals(task, otherTask);
    }
}