package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TaskTest {

    @Test
    public void taskWithSameIdsAreSame() {
        Task task = new Task("name", "description", TaskStatus.NEW);
        Task otherTask = new Task("name", "description", TaskStatus.NEW);
        task.setId(1L);
        otherTask.setId(1L);

        assertEquals(task, otherTask);
    }
}