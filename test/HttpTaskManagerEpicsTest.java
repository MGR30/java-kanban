import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import model.Epic;
import model.Subtask;
import model.TaskStatus;
import model.TaskType;
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

public class HttpTaskManagerEpicsTest {
    TaskManager manager = Managers.getDefault();
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    Gson gson = HttpTaskServer.getGson();

    public HttpTaskManagerEpicsTest() {
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
    public void testEpic_post_success() throws IOException, InterruptedException {
        Epic epic = new Epic("Test 2", "Testing epic 2",
                TaskStatus.NEW, TaskType.EPIC, Duration.ofMinutes(5), LocalDateTime.now().plusMinutes(20));
        String epicJson = gson.toJson(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(201, response.statusCode());

        List<Epic> epicsFromManager = manager.getAllEpic();

        assertNotNull(epicsFromManager, "Задачи не возвращаются");
        Assertions.assertEquals(1, epicsFromManager.size());
        Assertions.assertEquals("Test 2", epicsFromManager.getFirst().getName(), "Некорректное имя задачи");
    }


    @Test
    public void testEpic_postUpdate() throws IOException, InterruptedException {
        Epic epic = new Epic("epic4", "description", TaskStatus.NEW, TaskType.EPIC, Duration.ZERO, LocalDateTime.now());
        manager.createEpic(epic);

        Epic epicUpdate = new Epic("epic4", "description", TaskStatus.NEW, TaskType.EPIC, Duration.ZERO, LocalDateTime.now());
        epicUpdate.setId(epic.getId());
        epicUpdate.setName("Test 3");
        String epicJson = gson.toJson(epicUpdate);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(201, response.statusCode());

        List<Epic> epicsFromManager = manager.getAllEpic();

        assertNotNull(epicsFromManager, "Задачи не возвращаются");
        Assertions.assertEquals(1, epicsFromManager.size());
        Assertions.assertEquals("Test 3", epicsFromManager.getFirst().getName(), "Некорректное имя задачи");
    }


    @Test
    public void testEpic_delete_success() throws IOException, InterruptedException {
        Epic epic = new Epic("Test 2", "Testing epic 2",
                TaskStatus.NEW, TaskType.EPIC, Duration.ofMinutes(5), LocalDateTime.now().plusMinutes(20));
        manager.createEpic(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics" + "/" + epic.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(201, response.statusCode());

        List<Epic> epicsFromManager = manager.getAllEpic();

        Assertions.assertEquals(0, epicsFromManager.size());
    }

    @Test
    public void testEpic_delete_notFoundException() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics" + "/" + 1);
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(404, response.statusCode());

        List<Epic> epicsFromManager = manager.getAllEpic();

        Assertions.assertEquals(0, epicsFromManager.size());
    }

    @Test
    public void testEpic_getById_success() throws IOException, InterruptedException {
        Epic epic = new Epic("epic4", "description", TaskStatus.NEW, TaskType.EPIC, Duration.ZERO, LocalDateTime.now());
        manager.createEpic(epic);
        Subtask subtask = new Subtask("Test 2", "Testing subtask 2",
                TaskStatus.NEW, TaskType.SUBTASK, Duration.ofMinutes(5), LocalDateTime.now().plusMinutes(20));
        subtask.setEpicId(epic.getId());
        manager.createSubtask(subtask);
        gson.toJson(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics" + "/" + epic.getId() + "/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode());

        List<Subtask> subtasksFromManager = manager.getSubtasksByEpicId(epic.getId());

        List<Subtask> subtasksExpected = List.of(subtask);
        List<Subtask> subtaskActual = gson.fromJson(response.body(), new HttpTaskManagerSubtasksTest.SubtaskListTypeToken().getType());
        Assertions.assertEquals(1, subtaskActual.size());
        Assertions.assertEquals(subtasksExpected, subtaskActual);
    }

    @Test
    public void testEpic_getById_notFoundException() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics" + "/" + 1);
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(404, response.statusCode());

        List<Epic> epicsFromManager = manager.getAllEpic();

        Assertions.assertEquals(0, epicsFromManager.size());
    }

    @Test
    public void testEpic_getAll_success() throws IOException, InterruptedException {
        Epic epic = new Epic("Test 2", "Testing epic 2",
                TaskStatus.NEW, TaskType.EPIC, Duration.ofMinutes(5), LocalDateTime.now().plusMinutes(20));
        manager.createEpic(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode());

        List<Epic> epicActual = gson.fromJson(response.body(), new HttpTaskManagerTasksTest.TaskListTypeToken().getType());
        Assertions.assertEquals(1, epicActual.size());
    }

    @Test
    public void testEpic_getSubtaskByEpicId() throws IOException, InterruptedException {
        Epic epic = new Epic("epic4", "description", TaskStatus.NEW, TaskType.EPIC, Duration.ZERO, LocalDateTime.now());
        manager.createEpic(epic);
        Subtask subtask = new Subtask("Test 2", "Testing subtask 2",
                TaskStatus.NEW, TaskType.SUBTASK, Duration.ofMinutes(5), LocalDateTime.now().plusMinutes(20));
        subtask.setEpicId(epic.getId());
        manager.createSubtask(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics" + "/" + 1 + "/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode());

        List<Subtask> subtasksExpected = manager.getAllSubtask();
        List<Subtask> subtaskActual = gson.fromJson(response.body(), new HttpTaskManagerSubtasksTest.SubtaskListTypeToken().getType());
        Assertions.assertEquals(1, subtaskActual.size());
        Assertions.assertTrue(subtasksExpected.containsAll(subtaskActual));
    }

    @Test
    public void testEpic_getSubtaskByEpicId_notFoundException() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics" + "/" + 1);
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(404, response.statusCode());

        List<Epic> epicsFromManager = manager.getAllEpic();

        Assertions.assertEquals(0, epicsFromManager.size());
    }

    static class EpicListTypeToken extends TypeToken<List<Epic>> {
    }

    static class SubtaskListTypeToken extends TypeToken<List<Subtask>> {
    }
}
