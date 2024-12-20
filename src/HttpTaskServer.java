import com.sun.net.httpserver.HttpServer;
import handlers.*;
import service.TaskManager;
import util.Managers;

import java.io.IOException;
import java.net.InetSocketAddress;

import static util.GsonConfigurator.getGson;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private HttpServer httpServer;
    private final TaskManager taskManager;

    public HttpTaskServer(TaskManager taskManager) {
        this.taskManager = taskManager;

    }

    public static void main(String[] args) throws IOException {
        TaskManager taskManager = Managers.getDefault();
        HttpTaskServer httpTaskServer = new HttpTaskServer(taskManager);
        httpTaskServer.start();
    }

    public void start() throws IOException {
        httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks", new TasksRequestsHandler(taskManager, getGson()));
        httpServer.createContext("/subtasks", new SubtasksRequestsHandler(taskManager, getGson()));
        httpServer.createContext("/epics", new EpicRequestsHandler(taskManager, getGson()));
        httpServer.createContext("/history", new HistoryRequestsHandler(taskManager, getGson()));
        httpServer.createContext("/prioritized", new PrioritizedRequestsHandler(taskManager, getGson()));
        httpServer.start();
    }

    public void stop() {
        httpServer.stop(2);
    }
}
