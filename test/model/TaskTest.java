package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TaskTest {

    @Test
    public void taskWithSameIdsAreSame() {
        Task task = new Task("name", "description", TaskStatus.NEW, TaskType.TASK);
        Task otherTask = new Task("name", "description", TaskStatus.NEW, TaskType.TASK);
        task.setId(1L);
        otherTask.setId(1L);

        assertEquals(task, otherTask);
    }
}