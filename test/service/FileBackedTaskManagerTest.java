package service;

import model.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static model.TaskStatus.NEW;

class FileBackedTaskManagerTest {
    private FileBackedTaskManager taskManager;
    private Task task;
    private Epic epic;
    private Subtask subtask;

    @BeforeEach
    void setUpTaskManager() {
        try {
            taskManager = new FileBackedTaskManager(new InMemoryHistoryManager(), File.createTempFile("tmpFile", ".csv"));
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
        Task expectedTask = new Task("name", "description", NEW, TaskType.TASK);
        expectedTask.setId(task.getId());
        Assertions.assertEquals(expectedTask, task);
        Assertions.assertTrue(loadStateFromFile().contains(task.toString()));
    }

    @Test
    void createSubtask() {
        Subtask expectedSubtask = new Subtask("subtaskName", "subtaskDescription", NEW, TaskType.TASK);

        Assertions.assertEquals(NEW, epic.getStatus());

        Subtask subtask1 = new Subtask("subtaskName1", "subtaskName1", TaskStatus.IN_PROGRESS, TaskType.SUBTASK);
        subtask1.setEpicId(epic.getId());
        taskManager.createSubtask(subtask1);
        epic.getSubtasks().add(subtask1);

        expectedSubtask.setId(subtask.getId());
        expectedSubtask.setEpicId(epic.getId());

        taskManager.createSubtask(expectedSubtask);
        Assertions.assertEquals(expectedSubtask, subtask);
        Assertions.assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus());
        Assertions.assertTrue(loadStateFromFile().contains(task.toString()));
    }

    @Test
    void createEpic() {
        Epic expectedEpic = new Epic("epicName", "epicDescription", NEW, TaskType.EPIC);
        expectedEpic.getSubtasks().add(subtask);

        taskManager.createEpic(expectedEpic);
        expectedEpic.setId(epic.getId());
        Assertions.assertEquals(NEW, expectedEpic.getStatus());
        Assertions.assertEquals(expectedEpic, taskManager.getEpicById(expectedEpic.getId()));
        Assertions.assertTrue(loadStateFromFile().contains(task.toString()));
    }

    @Test
    void updateTask() {
        String expectedDescription = "anotherTaskDescription";
        task.setDescription(expectedDescription);

        taskManager.updateTask(task);
        Assertions.assertEquals(task, taskManager.getTaskById(task.getId()));
        Assertions.assertEquals(expectedDescription, taskManager.getTaskById(task.getId()).getDescription());
        Assertions.assertTrue(loadStateFromFile().contains(task.toString()));
    }

    @Test
    void deleteEpicById() {
        Subtask subtaskForDelete = new Subtask("subName", "subDesc", NEW, TaskType.SUBTASK);
        subtaskForDelete.setEpicId(epic.getId());
        epic.getSubtasks().add(subtaskForDelete);
        taskManager.createSubtask(subtaskForDelete);
        Assertions.assertEquals(2, taskManager.getAllSubtask().size());

        taskManager.deleteEpicById(epic.getId());
        Assertions.assertTrue(taskManager.getAllSubtask().isEmpty());
        Assertions.assertTrue(taskManager.getAllEpic().isEmpty());
        Assertions.assertFalse(loadStateFromFile().contains(epic.toString()));
    }

    @Test
    void deleteSubtaskById() {
        taskManager.deleteSubtaskById(subtask.getId());

        Assertions.assertNull(taskManager.getSubtaskById(subtask.getId()));
        Assertions.assertFalse(epic.getSubtasks().contains(subtask));
        Assertions.assertFalse(loadStateFromFile().contains(subtask.toString()));
    }

    @Test
    void deleteTaskById() {
        Task expectedDeletedTask = new Task("expTaskName", "expTaskDescription", NEW, TaskType.TASK);

        taskManager.createTask(expectedDeletedTask);
        Assertions.assertEquals(expectedDeletedTask, taskManager.getTaskById(expectedDeletedTask.getId()));

        taskManager.deleteTaskById(expectedDeletedTask.getId());
        Assertions.assertNull(taskManager.getTaskById(expectedDeletedTask.getId()));
        Assertions.assertFalse(loadStateFromFile().contains(expectedDeletedTask.toString()));
    }

    @Test
    void updateEpic() {
        String expectedEpicDescription = "anotherEpicDescription";
        epic.setDescription(expectedEpicDescription);

        taskManager.updateEpic(epic);
        Assertions.assertEquals(expectedEpicDescription, taskManager.getEpicById(epic.getId()).getDescription());
        Assertions.assertTrue(loadStateFromFile().contains(epic.toString()));
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
        Assertions.assertTrue(loadStateFromFile().contains(subtask.toString()));
    }

    @Test
    void deleteAllTasks() {
        taskManager.deleteAllTasks();
        Assertions.assertTrue(taskManager.getAllTask().isEmpty());
        Assertions.assertFalse(loadStateFromFile().contains(task.toString()));
    }

    @Test
    void deleteAllSubtask() {
        taskManager.deleteAllSubtask();
        Assertions.assertTrue(taskManager.getAllSubtask().isEmpty());
        Assertions.assertTrue(epic.getSubtasks().isEmpty());
        Assertions.assertFalse(loadStateFromFile().contains(subtask.toString()));
    }

    @Test
    void deleteAllEpic() {
        taskManager.deleteAllEpic();
        Assertions.assertTrue(taskManager.getAllEpic().isEmpty());
        Assertions.assertTrue(taskManager.getAllSubtask().isEmpty());
        Assertions.assertFalse(loadStateFromFile().contains(epic.toString()));
    }

    @Test
    void save() {
        Task expectedTaskInFile = new Task("expectedTask", "expected description", NEW, TaskType.TASK);
        taskManager.createTask(expectedTaskInFile);
        Assertions.assertTrue(loadStateFromFile().contains(expectedTaskInFile.toString()));
    }

    private List<String> loadStateFromFile() {
        List<String> savedTasks = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(taskManager.getFile()))) {
            while (reader.ready()) {
                savedTasks.add(reader.readLine());
            }
        } catch (IOException e) {
            System.out.println("Произошла ошибка при чтении файла");
        }
        return savedTasks;
    }
}