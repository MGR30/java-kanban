import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import model.Task;
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
import static util.GsonConfigurator.getGson;

public class HttpTaskManagerTasksTest {
    TaskManager manager = Managers.getDefault();
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    Gson gson = getGson();

    public HttpTaskManagerTasksTest() throws IOException {
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
    public void testTask_post_success() throws IOException, InterruptedException {
        Task task = new Task("Test 2", "Testing task 2",
                TaskStatus.NEW, TaskType.TASK, Duration.ofMinutes(5), LocalDateTime.now().plusMinutes(20));
        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(201, response.statusCode());

        List<Task> tasksFromManager = manager.getAllTask();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        Assertions.assertEquals(1, tasksFromManager.size());
        Assertions.assertEquals("Test 2", tasksFromManager.getFirst().getName(), "Некорректное имя задачи");
    }

    @Test
    public void testTask_post_overlapInTimeException() throws IOException, InterruptedException {
        Task task = new Task("Test 2", "Testing task 2",
                TaskStatus.NEW, TaskType.TASK, Duration.ofMinutes(5), LocalDateTime.now());
        manager.createTask(task);

        Task taskForRequest = new Task("Test 2", "Testing task 2",
                TaskStatus.NEW, TaskType.TASK, Duration.ofMinutes(5), LocalDateTime.now().plusMinutes(2));
        String taskJson = gson.toJson(taskForRequest);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(406, response.statusCode());

        List<Task> tasksFromManager = manager.getAllTask();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        Assertions.assertEquals(1, tasksFromManager.size());
    }

    @Test
    public void testTask_postUpdate() throws IOException, InterruptedException {
        Task task = new Task("Test 2", "Testing task 2",
                TaskStatus.NEW, TaskType.TASK, Duration.ofMinutes(5), LocalDateTime.now().plusMinutes(20));
        manager.createTask(task);
        task.setName("Test 3");
        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(201, response.statusCode());

        List<Task> tasksFromManager = manager.getAllTask();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        Assertions.assertEquals(1, tasksFromManager.size());
        Assertions.assertEquals("Test 3", tasksFromManager.getFirst().getName(), "Некорректное имя задачи");
    }

    @Test
    public void testTask_postUpdate_overlapInTimeException() throws IOException, InterruptedException {
        Task task = new Task("Test 2", "Testing task 2",
                TaskStatus.NEW, TaskType.TASK, Duration.ofMinutes(5), LocalDateTime.now());
        manager.createTask(task);

        Task taskForRequest = new Task("Test 3", "Testing task 3",
                TaskStatus.NEW, TaskType.TASK, Duration.ofMinutes(5), LocalDateTime.now().plusMinutes(20));
        manager.createTask(taskForRequest);
        taskForRequest.setName("Test 3");
        taskForRequest.setStartTime(LocalDateTime.now());
        String taskJson = gson.toJson(taskForRequest);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(406, response.statusCode());
    }

    @Test
    public void testTask_delete_success() throws IOException, InterruptedException {
        Task task = new Task("Test 2", "Testing task 2",
                TaskStatus.NEW, TaskType.TASK, Duration.ofMinutes(5), LocalDateTime.now().plusMinutes(20));
        manager.createTask(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks" + "/" + task.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(201, response.statusCode());

        List<Task> tasksFromManager = manager.getAllTask();

        Assertions.assertEquals(0, tasksFromManager.size());
    }

    @Test
    public void testTask_delete_notFoundException() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks" + "/" + 1);
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(404, response.statusCode());

        List<Task> tasksFromManager = manager.getAllTask();

        Assertions.assertEquals(0, tasksFromManager.size());
    }

    @Test
    public void testTask_getById_success() throws IOException, InterruptedException {
        Task task = new Task("Test 2", "Testing task 2",
                TaskStatus.NEW, TaskType.TASK, Duration.ofMinutes(5), LocalDateTime.now().plusMinutes(20));
        manager.createTask(task);
        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks" + "/" + task.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode());

        List<Task> tasksFromManager = manager.getAllTask();

        Assertions.assertEquals(1, tasksFromManager.size());
        Assertions.assertEquals(taskJson, response.body());
    }

    @Test
    public void testTask_getById_notFoundException() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks" + "/" + 1);
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(404, response.statusCode());

        List<Task> tasksFromManager = manager.getAllTask();

        Assertions.assertEquals(0, tasksFromManager.size());
    }

    @Test
    public void testTask_getAll_success() throws IOException, InterruptedException {
        Task task1 = new Task("Test 2", "Testing task 2",
                TaskStatus.NEW, TaskType.TASK, Duration.ofMinutes(5), LocalDateTime.now().plusMinutes(20));
        manager.createTask(task1);

        Task task2 = new Task("Test 3", "Testing task 3",
                TaskStatus.NEW, TaskType.TASK, Duration.ofMinutes(5), LocalDateTime.now().plusMinutes(26));
        manager.createTask(task2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode());

        List<Task> tasksExpected = manager.getAllTask();
        List<Task> taskActual = gson.fromJson(response.body(), new TaskListTypeToken().getType());
        Assertions.assertEquals(2, taskActual.size());
        Assertions.assertEquals(tasksExpected, taskActual);
    }

    static class TaskListTypeToken extends TypeToken<List<Task>> {
    }
}


