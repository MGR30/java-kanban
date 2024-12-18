package handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.Task;
import service.TaskManager;

import java.io.IOException;
import java.util.List;

public class HistoryRequestsHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;

    public HistoryRequestsHandler(TaskManager taskManager, Gson gson) {
        super(gson);
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (exchange.getRequestMethod().equals("GET")) {
            try {
                List<Task> history = taskManager.getHistory();
                String response = gson.toJson(history);
                writeResponse(exchange, response, 200);
            } catch (Exception e) {
                writeResponse(exchange, e.getMessage(), 500);
            }
        } else {
            writeResponse(exchange, "Такого эндпоинта не существует", 400);
        }
    }
}
