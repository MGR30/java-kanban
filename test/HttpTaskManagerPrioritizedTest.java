import com.google.gson.Gson;
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

import static util.GsonConfigurator.getGson;

public class HttpTaskManagerPrioritizedTest {
    TaskManager manager = Managers.getDefault();
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    Gson gson = getGson();

    public HttpTaskManagerPrioritizedTest() {
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
    public void testPrioritized_get() throws IOException, InterruptedException {
        Task task = new Task("Task 1", "Testing task 2",
                TaskStatus.NEW, TaskType.TASK, Duration.ofMinutes(5), LocalDateTime.now().plusMinutes(20));
        manager.createTask(task);

        Epic epic = new Epic("epic4", "description", TaskStatus.NEW, TaskType.EPIC, Duration.ZERO, LocalDateTime.now());
        manager.createEpic(epic);

        Subtask subtask = new Subtask("subtask 2", "Testing subtask 2",
                TaskStatus.NEW, TaskType.SUBTASK, Duration.ofMinutes(5), LocalDateTime.now().plusMinutes(10));
        subtask.setEpicId(epic.getId());
        manager.createSubtask(subtask);

        Task task2 = new Task("Task 2", "Testing task 3",
                TaskStatus.NEW, TaskType.TASK, Duration.ofMinutes(5), LocalDateTime.now().plusMinutes(27));
        manager.createTask(task2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/prioritized");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode());


        List<Task> tasksActual = gson.fromJson(response.body(), new HttpTaskManagerTasksTest.TaskListTypeToken().getType());
        Assertions.assertEquals(3, tasksActual.size());
        Assertions.assertEquals("subtask 2", tasksActual.getFirst().getName());
        Assertions.assertEquals("Task 1", tasksActual.get(1).getName());
        Assertions.assertEquals("Task 2", tasksActual.get(2).getName());
    }
}
