package service;

import exceptions.NotFoundException;
import model.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static model.TaskStatus.NEW;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @BeforeEach
    void setUpTaskManager() {
        taskManager = new InMemoryTaskManager(new InMemoryHistoryManager());
        super.setUp(taskManager);
    }

    void getAllTask() {
        Assertions.assertEquals(task, taskManager.getAllTask().getFirst());
    }

    void getAllSubtask() {
        Assertions.assertEquals(subtask, taskManager.getAllSubtask().getFirst());
    }

    void getAllEpic() {
        Assertions.assertEquals(epic, taskManager.getAllEpic().getFirst());
    }

    @Test
    void deleteAllTasks() {
        taskManager.deleteAllTasks();
        Assertions.assertTrue(taskManager.getAllTask().isEmpty());
    }

    @Test
    void deleteAllSubtask() {
        taskManager.deleteAllSubtask();
        Assertions.assertTrue(taskManager.getAllSubtask().isEmpty());
        Assertions.assertTrue(epic.getSubtasks().isEmpty());
    }

    @Test
    void deleteAllEpic() {
        taskManager.deleteAllEpic();
        Assertions.assertTrue(taskManager.getAllEpic().isEmpty());
        Assertions.assertTrue(taskManager.getAllSubtask().isEmpty());
    }

    @Test
    void getTaskById() {
        Assertions.assertEquals(task, taskManager.getTaskById(task.getId()));
    }

    @Test
    void getSubtaskById() {
        Assertions.assertEquals(subtask, taskManager.getSubtaskById(subtask.getId()));
    }

    @Test
    void getEpicById() {
        Assertions.assertEquals(epic, taskManager.getEpicById(epic.getId()));
    }

    @Test
    void createTask() {
        Task expectedTask = new Task("name", "description", NEW, TaskType.TASK, Duration.ZERO, LocalDateTime.now());
        expectedTask.setId(task.getId());
        Assertions.assertEquals(expectedTask, task);
    }

    @Test
    void createSubtask() {
        Subtask expectedSubtask = new Subtask("subtaskName", "subtaskDescription", NEW, TaskType.TASK, Duration.ZERO, LocalDateTime.now().plusHours(1));

        Assertions.assertEquals(NEW, epic.getStatus());

        Subtask subtask1 = new Subtask("subtaskName1", "subtaskName1", TaskStatus.IN_PROGRESS, TaskType.SUBTASK, Duration.ZERO, LocalDateTime.now().plusHours(2));
        subtask1.setEpicId(epic.getId());
        taskManager.createSubtask(subtask1);
        epic.getSubtasks().add(subtask1);

        expectedSubtask.setId(subtask.getId());
        expectedSubtask.setEpicId(epic.getId());

        taskManager.createSubtask(expectedSubtask);
        Assertions.assertEquals(expectedSubtask, subtask);
        Assertions.assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus());
    }

    @Test
    void createEpic() {
        Epic expectedEpic = new Epic("epicName", "epicDescription", NEW, TaskType.EPIC, Duration.ZERO, LocalDateTime.now());
        expectedEpic.getSubtasks().add(subtask);

        taskManager.createEpic(expectedEpic);
        expectedEpic.setId(epic.getId());
        Assertions.assertEquals(NEW, expectedEpic.getStatus());
        Assertions.assertEquals(expectedEpic, taskManager.getEpicById(expectedEpic.getId()));
    }

    @Test
    void updateTask() {
        String expectedDescription = "anotherTaskDescription";
        task.setDescription(expectedDescription);

        taskManager.updateTask(task);
        Assertions.assertEquals(task, taskManager.getTaskById(task.getId()));
        Assertions.assertEquals(expectedDescription, taskManager.getTaskById(task.getId()).getDescription());
    }

    @Test
    void updateSubtask() {
        String expectedDescription = "anotherSubtaskDescription";

        subtask.setDescription(expectedDescription);
        Assertions.assertEquals(NEW, epic.getStatus());

        subtask.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateSubtask(subtask);
        Assertions.assertEquals(TaskStatus.IN_PROGRESS, taskManager.getEpicById(epic.getId()).getStatus());
        Assertions.assertEquals(TaskStatus.IN_PROGRESS, taskManager.getSubtaskById(subtask.getId()).getStatus());
        Assertions.assertEquals(expectedDescription, taskManager.getSubtaskById(subtask.getId()).getDescription());
    }

    @Test
    void updateEpic() {
        String expectedEpicDescription = "anotherEpicDescription";
        epic.setDescription(expectedEpicDescription);

        taskManager.updateEpic(epic);
        Assertions.assertEquals(expectedEpicDescription, taskManager.getEpicById(epic.getId()).getDescription());
    }

    @Test
    void deleteTaskById() {
        Task expectedDeletedTask = new Task("expTaskName", "expTaskDescription", NEW, TaskType.TASK, Duration.ZERO, LocalDateTime.now().plusHours(3));

        taskManager.createTask(expectedDeletedTask);
        Assertions.assertEquals(expectedDeletedTask, taskManager.getTaskById(expectedDeletedTask.getId()));

        taskManager.deleteTaskById(expectedDeletedTask.getId());
        Assertions.assertThrows(NotFoundException.class, () -> taskManager.getTaskById(expectedDeletedTask.getId()));
    }

    @Test
    void deleteSubtaskById() {
        taskManager.deleteSubtaskById(subtask.getId());

        Assertions.assertThrows(NotFoundException.class, () -> taskManager.getSubtaskById(subtask.getId()));
        Assertions.assertFalse(epic.getSubtasks().contains(subtask));
    }

    @Test
    void deleteEpicById() {
        Subtask subtaskForDelete = new Subtask("subName", "subDesc", NEW, TaskType.SUBTASK, Duration.ZERO, LocalDateTime.now().plusHours(2));
        subtaskForDelete.setEpicId(epic.getId());
        epic.getSubtasks().add(subtaskForDelete);
        taskManager.createSubtask(subtaskForDelete);
        Assertions.assertEquals(2, taskManager.getAllSubtask().size());

        taskManager.deleteEpicById(epic.getId());
        Assertions.assertTrue(taskManager.getAllSubtask().isEmpty());
        Assertions.assertTrue(taskManager.getAllEpic().isEmpty());
    }

    @Test
    void getSubtasksByEpicId() {
        Assertions.assertEquals(subtask, taskManager.getSubtasksByEpicId(epic.getId()).getFirst());
    }

    @Test
    void getHistory() {
        taskManager.getEpicById(epic.getId());
        taskManager.getTaskById(task.getId());
        taskManager.getSubtaskById(subtask.getId());

        Assertions.assertEquals(3, taskManager.getHistory().size());
    }

    @Test
    void notMutableTaskOnCreateTest() {
        Task task = new Task("name", "description", NEW, TaskType.TASK, Duration.ZERO, LocalDateTime.now());
        taskManager.createTask(task);

        Assertions.assertEquals(task.getName(), taskManager.getTaskById(1L).getName());
        Assertions.assertEquals(task.getDescription(), taskManager.getTaskById(1L).getDescription());
        Assertions.assertEquals(task.getStatus(), taskManager.getTaskById(1L).getStatus());
    }

    @Override
    @Test
    void epicStatusUpdateTest_AllSubtasksNew() {
        super.epicStatusUpdateTest_AllSubtasksNew();
    }

    @Override
    @Test
    void epicStatusUpdateTest_AllSubtasksDone() {
        super.epicStatusUpdateTest_AllSubtasksDone();
    }

    @Override
    @Test
    void epicStatusUpdateTest_SubtasksDoneAndNew() {
        super.epicStatusUpdateTest_SubtasksDoneAndNew();
    }

    @Override
    @Test
    void epicStatusUpdateTest_SubtasksInProgress() {
        super.epicStatusUpdateTest_SubtasksInProgress();
    }

    @Override
    @Test
    void checkNotIntersectionTimeCorrect_TaskWithIntersectionTime() {
        super.checkNotIntersectionTimeCorrect_TaskWithIntersectionTime();
    }

    @Override
    @Test
    void checkNotIntersectionTimeCorrect_TaskWithoutIntersectionTime() {
        super.checkNotIntersectionTimeCorrect_TaskWithoutIntersectionTime();
    }
}