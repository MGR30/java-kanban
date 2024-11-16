package model;

import model.Subtask;
import model.TaskStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class SubtaskTest {

    @Test
    public void subtaskWithSameIdsAreSame() {
        Subtask subtask = new Subtask("name", "description", TaskStatus.NEW, TaskType.SUBTASK);
        Subtask otherSubtask = new Subtask("name", "description", TaskStatus.NEW, TaskType.SUBTASK);
        subtask.setId(1L);
        otherSubtask.setId(1L);

        assertEquals(subtask, otherSubtask);
    }

    @Test
    public void thisSubtaskIdNotSettingInThisEpicId() {
        Subtask subtask = new Subtask("name", "description", TaskStatus.NEW, TaskType.SUBTASK);
        subtask.setId(1L);
        subtask.setEpicId(subtask.getId());

        assertNull(subtask.getEpicId());
    }
}