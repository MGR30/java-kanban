import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import model.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.TaskManager;
import util.Managers;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HttpTaskManagerSubtasksTest {
    TaskManager manager = Managers.getDefault();
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    Gson gson = HttpTaskServer.getGson();

    public HttpTaskManagerSubtasksTest() {
    }

    @BeforeEach
    public void setUp() throws IOException {
        manager.deleteAllTasks();
        manager.deleteAllSubtask();
        manager.deleteAllEpic();
        taskServer.start();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    @Test
    public void testSubtasks_post_success() throws IOException, InterruptedException {
        Epic epic = new Epic("epic4", "description", TaskStatus.NEW, TaskType.EPIC, Duration.ZERO, LocalDateTime.now());
        manager.createEpic(epic);
        Subtask subtask = new Subtask("Test 2", "Testing subtask 2",
                TaskStatus.NEW, TaskType.SUBTASK, Duration.ofMinutes(5), LocalDateTime.now().plusMinutes(20));
        subtask.setEpicId(epic.getId());
        String subtaskJson = gson.toJson(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(subtaskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(201, response.statusCode());

        List<Subtask> subtasksFromManager = manager.getAllSubtask();

        assertNotNull(subtasksFromManager, "Задачи не возвращаются");
        Assertions.assertEquals(1, subtasksFromManager.size());
        Assertions.assertEquals("Test 2", subtasksFromManager.getFirst().getName(), "Некорректное имя задачи");
    }

    @Test
    public void testSubtask_post_overlapInTimeException() throws IOException, InterruptedException {
        Task task1 = new Task("Test 2", "Testing task 2",
                TaskStatus.NEW, TaskType.TASK, Duration.ofMinutes(5), LocalDateTime.now().plusMinutes(20));
        manager.createTask(task1);
        Epic epic = new Epic("epic4", "description", TaskStatus.NEW, TaskType.EPIC, Duration.ZERO, LocalDateTime.now());
        manager.createEpic(epic);
        Subtask subtask = new Subtask("Test 3", "Testing subtask 3",
                TaskStatus.NEW, TaskType.SUBTASK, Duration.ofMinutes(5), LocalDateTime.now().plusMinutes(20));
        subtask.setEpicId(epic.getId());
        String subtaskJson = gson.toJson(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(subtaskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(406, response.statusCode());

        List<Subtask> subtasksFromManager = manager.getAllSubtask();

        assertNotNull(subtasksFromManager, "Задачи не возвращаются");
        Assertions.assertEquals(0, subtasksFromManager.size());
    }

    @Test
    public void testSubtask_postUpdate() throws IOException, InterruptedException {
        Epic epic = new Epic("epic4", "description", TaskStatus.NEW, TaskType.EPIC, Duration.ZERO, LocalDateTime.now());
        manager.createEpic(epic);
        Subtask subtask = new Subtask("Test 2", "Testing subtask 2",
                TaskStatus.NEW, TaskType.SUBTASK, Duration.ofMinutes(5), LocalDateTime.now().plusMinutes(20));
        subtask.setEpicId(epic.getId());
        manager.createSubtask(subtask);
        subtask.setName("Test 3");
        String subtaskJson = gson.toJson(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(subtaskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(201, response.statusCode());

        List<Subtask> subtasksFromManager = manager.getAllSubtask();

        assertNotNull(subtasksFromManager, "Задачи не возвращаются");
        Assertions.assertEquals(1, subtasksFromManager.size());
        Assertions.assertEquals("Test 3", subtasksFromManager.getFirst().getName(), "Некорректное имя задачи");
    }

    @Test
    public void testSubtask_postUpdate_overlapInTimeException() throws IOException, InterruptedException {
        Task task = new Task("Test 2", "Testing task 2",
                TaskStatus.NEW, TaskType.TASK, Duration.ofMinutes(5), LocalDateTime.now());
        manager.createTask(task);
        Epic epic = new Epic("epic4", "description", TaskStatus.NEW, TaskType.EPIC, Duration.ZERO, LocalDateTime.now());
        manager.createEpic(epic);
        Subtask subtask = new Subtask("Test 2", "Testing subtask 2",
                TaskStatus.NEW, TaskType.SUBTASK, Duration.ofMinutes(5), LocalDateTime.now().plusMinutes(20));
        subtask.setEpicId(epic.getId());
        manager.createSubtask(subtask);
        subtask.setName("Test 3");
        subtask.setStartTime(LocalDateTime.now());
        String subtaskJson = gson.toJson(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(subtaskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(406, response.statusCode());
    }

    @Test
    public void testSubtask_delete_success() throws IOException, InterruptedException {
        Epic epic = new Epic("epic4", "description", TaskStatus.NEW, TaskType.EPIC, Duration.ZERO, LocalDateTime.now());
        manager.createEpic(epic);
        Subtask subtask = new Subtask("Test 2", "Testing subtask 2",
                TaskStatus.NEW, TaskType.SUBTASK, Duration.ofMinutes(5), LocalDateTime.now().plusMinutes(20));
        subtask.setEpicId(epic.getId());
        manager.createSubtask(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks" + "/" + subtask.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(201, response.statusCode());

        List<Subtask> subtasksFromManager = manager.getAllSubtask();

        Assertions.assertEquals(0, subtasksFromManager.size());
    }

    @Test
    public void testSubtask_delete_notFoundException() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks" + "/" + 2);
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(404, response.statusCode());

        List<Subtask> subtasksFromManager = manager.getAllSubtask();

        Assertions.assertEquals(0, subtasksFromManager.size());
    }

    @Test
    public void testSubtask_getById_success() throws IOException, InterruptedException {
        Epic epic = new Epic("epic4", "description", TaskStatus.NEW, TaskType.EPIC, Duration.ZERO, LocalDateTime.now());
        manager.createEpic(epic);
        Subtask subtask = new Subtask("Test 2", "Testing subtask 2",
                TaskStatus.NEW, TaskType.SUBTASK, Duration.ofMinutes(5), LocalDateTime.now().plusMinutes(20));
        subtask.setEpicId(epic.getId());
        manager.createSubtask(subtask);
        String subtaskJson = gson.toJson(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks" + "/" + subtask.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode());

        List<Subtask> subtasksFromManager = manager.getAllSubtask();

        Assertions.assertEquals(1, subtasksFromManager.size());
        Assertions.assertEquals(subtaskJson, response.body());
    }

    @Test
    public void testSubtask_getById_notFoundException() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks" + "/" + 1);
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(404, response.statusCode());

        List<Subtask> subtasksFromManager = manager.getAllSubtask();

        Assertions.assertEquals(0, subtasksFromManager.size());
    }

    @Test
    public void testSubtask_getAll_success() throws IOException, InterruptedException {
        Epic epic = new Epic("epic4", "description", TaskStatus.NEW, TaskType.EPIC, Duration.ZERO, LocalDateTime.now());
        manager.createEpic(epic);
        Subtask subtask = new Subtask("Test 2", "Testing subtask 2",
                TaskStatus.NEW, TaskType.SUBTASK, Duration.ofMinutes(5), LocalDateTime.now().plusMinutes(20));
        subtask.setEpicId(epic.getId());
        manager.createSubtask(subtask);
        String subtaskJson = gson.toJson(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode());

        List<Subtask> subtasksExpected = manager.getAllSubtask();
        List<Subtask> subtaskActual = gson.fromJson(response.body(), new HttpTaskManagerSubtasksTest.SubtaskListTypeToken().getType());
        Assertions.assertEquals(1, subtaskActual.size());
        Assertions.assertEquals(subtasksExpected, subtaskActual);
    }

    static class SubtaskListTypeToken extends TypeToken<List<Subtask>> {
    }
}
