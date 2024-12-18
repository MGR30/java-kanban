package handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exceptions.NotFoundException;
import exceptions.OverlapInTimeException;
import model.Task;
import service.TaskManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

public class TasksRequestsHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;

    public TasksRequestsHandler(TaskManager taskManager, Gson gson) {
        super(gson);
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String requestMethod = exchange.getRequestMethod();
        String[] pathParts = exchange.getRequestURI().getPath().split("/");

        switch (requestMethod) {
            case "GET": {
                if (pathParts.length == 2) {
                    handleGet(exchange);
                }
                if (pathParts.length == 3) {
                    handleGetById(exchange);
                }
                break;
            }
            case "DELETE": {
                handleDelete(exchange);
                break;
            }
            case "POST": {
                handlePost(exchange);
                break;
            }
            default:
                writeResponse(exchange, "Такого эндпоинта не существует", 400);
        }
    }

    private void handleGet(HttpExchange exchange) throws IOException {
        try {
            List<Task> allTask = taskManager.getAllTask();
            String response = gson.toJson(allTask);
            writeResponse(exchange, response, 200);
        } catch (Exception e) {
            writeResponse(exchange, e.getMessage(), 500);
        }
    }

    private void handleGetById(HttpExchange exchange) throws IOException {
        try {
            Optional<Long> idFromPath = getIdFromPath(exchange);
            if (idFromPath.isEmpty()) {
                writeResponse(exchange, NOT_CORRECT_ID, 400);
                return;
            }
            Task taskById = taskManager.getTaskById(idFromPath.get());
            String jsonResponse = gson.toJson(taskById);
            writeResponse(exchange, jsonResponse, 200);
        } catch (NotFoundException e) {
            writeResponse(exchange, e.getMessage(), 404);
        } catch (Exception e) {
            writeResponse(exchange, e.getMessage(), 500);
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        String string = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Task task = gson.fromJson(string, Task.class);
        try {
            if (task.getId() != null) {
                taskManager.updateTask(task);
                writeResponse(exchange, "Задача обновлена", 201);
            } else {
                taskManager.createTask(new Task(task.getName(),
                        task.getDescription(),
                        task.getStatus(),
                        task.getType(),
                        task.getDuration(),
                        task.getStartTime()));
                writeResponse(exchange, "Задача создана", 201);
            }
        } catch (OverlapInTimeException e) {
            writeResponse(exchange, e.getMessage(), 406);
        } catch (Exception e) {
            writeResponse(exchange, e.getMessage(), 500);
        }
    }

    private void handleDelete(HttpExchange exchange) throws IOException {
        Optional<Long> idFromPath = getIdFromPath(exchange);
        try {
            if (idFromPath.isEmpty()) {
                writeResponse(exchange, NOT_CORRECT_ID, 400);
                return;
            }
            taskManager.deleteTaskById(idFromPath.get());
            writeResponse(exchange, "Задача удалена", 201);
        } catch (NotFoundException e) {
            writeResponse(exchange, e.getMessage(), 404);
        } catch (Exception e) {
            writeResponse(exchange, e.getMessage(), 500);
        }
    }
}