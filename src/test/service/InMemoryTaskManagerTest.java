package test.service;

import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.InMemoryHistoryManager;
import service.InMemoryTaskManager;

import static model.TaskStatus.NEW;

class InMemoryTaskManagerTest {
    private static InMemoryTaskManager taskManager;
    private Task task;
    private Epic epic;
    private Subtask subtask;

    @BeforeEach
    void setUpTaskManager() {
        taskManager = new InMemoryTaskManager(new InMemoryHistoryManager());
        task = new Task("name", "description", NEW);
        taskManager.createTask(task);
        epic = new Epic("epicName", "epicDescription", NEW);
        taskManager.createEpic(epic);
        subtask = new Subtask("subtaskName", "subtaskDescription", NEW);
        subtask.setEpicId(epic.getId());
        taskManager.createSubtask(subtask);
    }

    @Test
    void getAllTask() {
        Assertions.assertEquals(task, taskManager.getAllTask().getFirst());
    }

    @Test
    void getAllSubtask() {
        Assertions.assertEquals(subtask, taskManager.getAllSubtask().getFirst());
    }

    @Test
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
        Task expectedTask = new Task("name", "description", NEW);
        expectedTask.setId(task.getId());
        Assertions.assertEquals(expectedTask, task);
    }

    @Test
    void createSubtask() {
        Subtask expectedSubtask = new Subtask("subtaskName", "subtaskDescription", NEW);

        Assertions.assertEquals(NEW, epic.getStatus());

        Subtask subtask1 = new Subtask("subtaskName1", "subtaskName1", TaskStatus.IN_PROGRESS);
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
        Epic expectedEpic = new Epic("epicName", "epicDescription", NEW);
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
        Task expectedDeletedTask = new Task("expTaskName", "expTaskDescription", NEW);

        taskManager.createTask(expectedDeletedTask);
        Assertions.assertEquals(expectedDeletedTask, taskManager.getTaskById(expectedDeletedTask.getId()));

        taskManager.deleteTaskById(expectedDeletedTask.getId());
        Assertions.assertNull(taskManager.getTaskById(expectedDeletedTask.getId()));
    }

    @Test
    void deleteSubtaskById() {
        taskManager.deleteSubtaskById(subtask.getId());

        Assertions.assertNull(taskManager.getSubtaskById(subtask.getId()));
        Assertions.assertFalse(epic.getSubtasks().contains(subtask));
    }

    @Test
    void deleteEpicById() {
        Subtask subtaskForDelete = new Subtask("subName", "subDesc", NEW);
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
        Task task = new Task("name", "description", NEW);
        taskManager.createTask(task);

        Assertions.assertEquals(task.getName(), taskManager.getTaskById(1L).getName());
        Assertions.assertEquals(task.getDescription(), taskManager.getTaskById(1L).getDescription());
        Assertions.assertEquals(task.getStatus(), taskManager.getTaskById(1L).getStatus());
    }
}