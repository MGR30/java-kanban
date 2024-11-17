package service;

import model.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import util.Managers;

import java.io.File;
import java.io.IOException;

import static model.TaskStatus.NEW;

class FileBackedTaskManagerTest {
    private FileBackedTaskManager taskManager;
    private Task task;
    private Epic epic;
    private Subtask subtask;
    private File tmpFile;

    @BeforeEach
    void setUpTaskManager() {
        try {
            tmpFile = File.createTempFile("tmpFile", ".csv");
            taskManager = new FileBackedTaskManager(Managers.getDefaultHistory(), tmpFile);
        } catch (IOException e) {
            System.out.println("Ошибка при создании файла");
        }
        task = new Task("name", "description", NEW, TaskType.TASK);
        taskManager.createTask(task);
        epic = new Epic("epicName", "epicDescription", NEW, TaskType.EPIC);
        taskManager.createEpic(epic);
        subtask = new Subtask("subtaskName", "subtaskDescription", NEW, TaskType.SUBTASK);
        subtask.setEpicId(epic.getId());
        taskManager.createSubtask(subtask);

    }

    @Test
    void createTask() {
        FileBackedTaskManager fileBackedTaskManager = FileBackedTaskManager.loadFromFile(tmpFile);
        Task expectedTask = fileBackedTaskManager.getTaskById(task.getId());
        expectedTask.setId(task.getId());

        Assertions.assertEquals(expectedTask, task);
    }

    @Test
    void createSubtask() {
        FileBackedTaskManager fileBackedTaskManager = FileBackedTaskManager.loadFromFile(tmpFile);
        Subtask expectedSubtask = fileBackedTaskManager.getSubtaskById(subtask.getId());

        Assertions.assertEquals(NEW, epic.getStatus());
        Assertions.assertEquals(expectedSubtask, subtask);
    }

    @Test
    void createEpic() {
        FileBackedTaskManager fileBackedTaskManager = FileBackedTaskManager.loadFromFile(tmpFile);
        Epic expectedEpic = fileBackedTaskManager.getEpicById(epic.getId());

        Assertions.assertEquals(expectedEpic, epic);

    }

    @Test
    void updateTask() {
        String expectedDescription = "anotherTaskDescription";
        task.setDescription(expectedDescription);
        taskManager.updateTask(task);
        FileBackedTaskManager fileBackedTaskManager = FileBackedTaskManager.loadFromFile(tmpFile);

        Assertions.assertEquals(task, fileBackedTaskManager.getTaskById(task.getId()));
        Assertions.assertEquals(expectedDescription, fileBackedTaskManager.getTaskById(task.getId()).getDescription());
    }

    @Test
    void deleteEpicById() {
        Subtask subtaskForDelete = new Subtask("subName", "subDesc", NEW, TaskType.SUBTASK);
        subtaskForDelete.setEpicId(epic.getId());
        epic.getSubtasks().add(subtaskForDelete);
        taskManager.createSubtask(subtaskForDelete);

        Assertions.assertEquals(2, taskManager.getAllSubtask().size());
        taskManager.deleteEpicById(epic.getId());

        FileBackedTaskManager fileBackedTaskManager = FileBackedTaskManager.loadFromFile(tmpFile);
        Assertions.assertTrue(fileBackedTaskManager.getAllSubtask().isEmpty());
        Assertions.assertTrue(fileBackedTaskManager.getAllEpic().isEmpty());
    }

    @Test
    void deleteSubtaskById() {
        taskManager.deleteSubtaskById(subtask.getId());
        FileBackedTaskManager fileBackedTaskManager = FileBackedTaskManager.loadFromFile(tmpFile);
        Assertions.assertNull(fileBackedTaskManager.getSubtaskById(subtask.getId()));
        Assertions.assertFalse(epic.getSubtasks().contains(subtask));
    }

    @Test
    void deleteTaskById() {
        Task expectedDeletedTask = new Task("expTaskName", "expTaskDescription", NEW, TaskType.TASK);

        taskManager.createTask(expectedDeletedTask);
        Assertions.assertEquals(expectedDeletedTask, taskManager.getTaskById(expectedDeletedTask.getId()));

        taskManager.deleteTaskById(expectedDeletedTask.getId());
        FileBackedTaskManager fileBackedTaskManager = FileBackedTaskManager.loadFromFile(tmpFile);
        Assertions.assertNull(taskManager.getTaskById(expectedDeletedTask.getId()));
        Assertions.assertNull(fileBackedTaskManager.getTaskById(expectedDeletedTask.getId()));
    }

    @Test
    void updateEpic() {
        String expectedEpicDescription = "anotherEpicDescription";
        epic.setDescription(expectedEpicDescription);

        taskManager.updateEpic(epic);
        FileBackedTaskManager fileBackedTaskManager = FileBackedTaskManager.loadFromFile(tmpFile);
        Assertions.assertEquals(expectedEpicDescription, taskManager.getEpicById(epic.getId()).getDescription());
        Assertions.assertEquals(expectedEpicDescription, fileBackedTaskManager.getEpicById(epic.getId()).getDescription());
    }

    @Test
    void updateSubtask() {
        String expectedDescription = "anotherSubtaskDescription";

        subtask.setDescription(expectedDescription);
        Assertions.assertEquals(NEW, epic.getStatus());

        subtask.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateSubtask(subtask);
        FileBackedTaskManager fileBackedTaskManager = FileBackedTaskManager.loadFromFile(tmpFile);
        Assertions.assertEquals(TaskStatus.IN_PROGRESS, taskManager.getEpicById(epic.getId()).getStatus());
        Assertions.assertEquals(TaskStatus.IN_PROGRESS, taskManager.getSubtaskById(subtask.getId()).getStatus());
        Assertions.assertEquals(expectedDescription, taskManager.getSubtaskById(subtask.getId()).getDescription());
        Assertions.assertEquals(expectedDescription, fileBackedTaskManager.getSubtaskById(subtask.getId()).getDescription());
    }

    @Test
    void deleteAllTasks() {
        taskManager.deleteAllTasks();
        FileBackedTaskManager fileBackedTaskManager = FileBackedTaskManager.loadFromFile(tmpFile);
        Assertions.assertTrue(taskManager.getAllTask().isEmpty());
        Assertions.assertTrue(fileBackedTaskManager.getAllTask().isEmpty());
    }

    @Test
    void deleteAllSubtask() {
        taskManager.deleteAllSubtask();
        FileBackedTaskManager fileBackedTaskManager = FileBackedTaskManager.loadFromFile(tmpFile);
        Assertions.assertTrue(taskManager.getAllSubtask().isEmpty());
        Assertions.assertTrue(epic.getSubtasks().isEmpty());
        Assertions.assertTrue(fileBackedTaskManager.getAllSubtask().isEmpty());
    }

    @Test
    void deleteAllEpic() {
        taskManager.deleteAllEpic();
        FileBackedTaskManager fileBackedTaskManager = FileBackedTaskManager.loadFromFile(tmpFile);
        Assertions.assertTrue(taskManager.getAllEpic().isEmpty());
        Assertions.assertTrue(taskManager.getAllSubtask().isEmpty());
        Assertions.assertTrue(fileBackedTaskManager.getAllEpic().isEmpty());
        Assertions.assertTrue(fileBackedTaskManager.getAllSubtask().isEmpty());
    }

    @Test
    void save() {
        Task expectedTaskInFile = new Task("expectedTask", "expected description", NEW, TaskType.TASK);
        taskManager.createTask(expectedTaskInFile);
        FileBackedTaskManager fileBackedTaskManager = FileBackedTaskManager.loadFromFile(tmpFile);
        Assertions.assertEquals(expectedTaskInFile, fileBackedTaskManager.getTaskById(expectedTaskInFile.getId()));
    }
}