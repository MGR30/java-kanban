import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import handlers.*;
import handlers.adapters.DurationTypeAdapter;
import handlers.adapters.LocalDateAdapter;
import model.*;
import service.TaskManager;
import util.Managers;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private HttpServer httpServer;
    private TaskManager taskManager;

    public HttpTaskServer(TaskManager taskManager) {
        this.taskManager = taskManager;

    }

    public static void main(String[] args) throws IOException {
        TaskManager taskManager = Managers.getDefault();
        HttpTaskServer httpTaskServer = new HttpTaskServer(taskManager);
        httpTaskServer.start();
    }

    public void start() throws IOException {
        //prepareTestData(taskManager); // только для тестирования
        Gson gson = getGson();
        httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks", new TasksRequestsHandler(taskManager, gson));
        httpServer.createContext("/subtasks", new SubtasksRequestsHandler(taskManager, gson));
        httpServer.createContext("/epics", new EpicRequestsHandler(taskManager, gson));
        httpServer.createContext("/history", new HistoryRequestsHandler(taskManager, gson));
        httpServer.createContext("/prioritized", new PrioritizedRequestsHandler(taskManager, gson));
        httpServer.start();
    }

    public void stop() {
        httpServer.stop(2);
    }

    public static Gson getGson() {
        return new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new LocalDateAdapter())
                .registerTypeAdapter(Duration.class, new DurationTypeAdapter())
                .setPrettyPrinting()
                .serializeNulls()
                .create();
    }

    private static void prepareTestData(TaskManager taskManager) {
        Task task1 = new Task("name1", "description", TaskStatus.NEW, TaskType.TASK, Duration.ZERO, LocalDateTime.now());
        Task task2 = new Task("name2", "description", TaskStatus.NEW, TaskType.TASK, Duration.ZERO, LocalDateTime.now().plusMinutes(1));
        Task task3 = new Task("name3", "description", TaskStatus.NEW, TaskType.TASK, Duration.ZERO, LocalDateTime.now().plusMinutes(2));
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createTask(task3);

        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task2.getId());
        taskManager.getTaskById(task3.getId());

        Epic epic1 = new Epic("epic4", "description", TaskStatus.NEW, TaskType.EPIC, Duration.ZERO, LocalDateTime.now().plusMinutes(3));
        Epic epic2 = new Epic("epic5", "description", TaskStatus.NEW, TaskType.EPIC, Duration.ZERO, LocalDateTime.now().plusMinutes(4));
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);

        taskManager.getEpicById(epic1.getId());
        taskManager.getEpicById(epic2.getId());

        Subtask task4 = new Subtask("subname8", "description", TaskStatus.NEW, TaskType.SUBTASK, Duration.ZERO, LocalDateTime.now().plusMinutes(5));
        Subtask task5 = new Subtask("subname9", "description", TaskStatus.NEW, TaskType.SUBTASK, Duration.ZERO, LocalDateTime.now().plusMinutes(6));
        task4.setEpicId(epic1.getId());
        task5.setEpicId(epic2.getId());
        taskManager.createSubtask(task4);
        taskManager.createSubtask(task5);
        epic1.getSubtasks().add(task4);
        epic2.getSubtasks().add(task5);

        taskManager.getSubtaskById(task4.getId());
        taskManager.getSubtaskById(task5.getId());
    }
}
