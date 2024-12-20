package service;

import exceptions.OverlapInTimeException;
import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskType;
import org.junit.jupiter.api.Assertions;

import java.time.Duration;
import java.time.LocalDateTime;

import static model.TaskStatus.*;

public abstract class TaskManagerTest<T extends TaskManager> {
    protected T taskManager;
    protected Task task;
    protected Epic epic;
    protected Subtask subtask;

    void setUp(T taskManager) {
        this.taskManager = taskManager;
        task = new Task("name", "description", NEW, TaskType.TASK, Duration.ofMinutes(2), LocalDateTime.now().plusMinutes(1));
        taskManager.createTask(task);
        epic = new Epic("epicName", "epicDescription", NEW, TaskType.EPIC, Duration.ZERO, LocalDateTime.now());
        taskManager.createEpic(epic);
        subtask = new Subtask("subtaskName", "subtaskDescription", NEW, TaskType.SUBTASK, Duration.ZERO, LocalDateTime.now().plusMinutes(21));
        subtask.setEpicId(epic.getId());
        taskManager.createSubtask(subtask);
    }

    void epicStatusUpdateTest_AllSubtasksNew() {
        Long epicId = epic.getId();
        Subtask subtask1 = new Subtask("subtask1", "description", NEW, TaskType.SUBTASK, Duration.ZERO, LocalDateTime.now().plusMinutes(28));
        subtask1.setEpicId(epicId);
        taskManager.createSubtask(subtask1);

        Assertions.assertEquals(NEW, epic.getStatus());
    }

    void epicStatusUpdateTest_AllSubtasksDone() {
        Long epicId = epic.getId();
        subtask.setStatus(DONE);
        taskManager.updateSubtask(subtask);
        Subtask subtask1 = new Subtask("subtask1", "description", DONE, TaskType.SUBTASK, Duration.ZERO, LocalDateTime.now().plusMinutes(28));
        subtask1.setEpicId(epicId);
        taskManager.createSubtask(subtask1);

        Assertions.assertEquals(DONE, epic.getStatus());
    }

    void epicStatusUpdateTest_SubtasksDoneAndNew() {
        Long epicId = epic.getId();
        Subtask subtask1 = new Subtask("subtask1", "description", DONE, TaskType.SUBTASK, Duration.ZERO, LocalDateTime.now().plusMinutes(29));
        subtask1.setEpicId(epicId);
        taskManager.createSubtask(subtask1);

        Assertions.assertEquals(NEW, epic.getStatus());
    }

    void epicStatusUpdateTest_SubtasksInProgress() {
        Long epicId = epic.getId();
        Subtask subtask1 = new Subtask("subtask1", "description", IN_PROGRESS, TaskType.SUBTASK, Duration.ZERO, LocalDateTime.now().plusMinutes(55));
        subtask1.setEpicId(epicId);
        taskManager.createSubtask(subtask1);

        Assertions.assertEquals(IN_PROGRESS, epic.getStatus());
    }

    void checkNotIntersectionTimeCorrect_TaskWithIntersectionTime() {
        Task newTask = new Task("taskName", "taskDescription", NEW, TaskType.TASK, Duration.ofMinutes(15), LocalDateTime.now().plusMinutes(1));
        System.out.println(taskManager.getAllTask());
        Assertions.assertThrows(OverlapInTimeException.class, () -> taskManager.createTask(newTask));
        Assertions.assertFalse(taskManager.getAllTask().contains(newTask));
    }

    void checkNotIntersectionTimeCorrect_TaskWithoutIntersectionTime() {
        Task newTask = new Task("taskName", "taskDescription", NEW, TaskType.TASK, Duration.ofMinutes(15), LocalDateTime.now().plusHours(2));
        taskManager.createTask(newTask);
        System.out.println(taskManager.getAllTask());
        Assertions.assertTrue(taskManager.getAllTask().contains(newTask));
    }
}
