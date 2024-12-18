package handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exceptions.NotFoundException;
import exceptions.OverlapInTimeException;
import model.Epic;
import model.Subtask;
import service.TaskManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

public class EpicRequestsHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;

    public EpicRequestsHandler(TaskManager taskManager, Gson gson) {
        super(gson);
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String requestMethod = exchange.getRequestMethod();
        String[] pathParts = path.split("/");

        switch (requestMethod) {
            case "GET": {
                if (pathParts.length == 2) {
                    handleGet(exchange);
                }
                if (pathParts.length == 3) {
                    handleGetById(exchange);
                }
                if (path.endsWith("subtasks")) {
                    handleGetSubtasksByEpicId(exchange);
                }
                break;
            }
            case "POST": {
                handlePost(exchange);
                break;
            }
            case "DELETE": {
                handleDelete(exchange);
                break;
            }
            default:
                writeResponse(exchange, "Такого эндпоинта не существует", 400);
        }
    }

    private void handleGet(HttpExchange exchange) throws IOException {
        try {
            List<Epic> allEpics = taskManager.getAllEpic();
            String response = gson.toJson(allEpics);
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
            Epic epicById = taskManager.getEpicById(idFromPath.get());
            String jsonResponse = gson.toJson(epicById);
            writeResponse(exchange, jsonResponse, 200);
        } catch (NotFoundException e) {
            writeResponse(exchange, e.getMessage(), 404);
        } catch (Exception e) {
            writeResponse(exchange, e.getMessage(), 500);
        }
    }

    private void handleGetSubtasksByEpicId(HttpExchange exchange) throws IOException {
        try {
            Optional<Long> idFromPath = getIdFromPath(exchange);
            if (idFromPath.isEmpty()) {
                writeResponse(exchange, NOT_CORRECT_ID, 400);
                return;
            }
            List<Subtask> subtasks = taskManager.getSubtasksByEpicId(idFromPath.get());
            String response = gson.toJson(subtasks);
            writeResponse(exchange, response, 200);
        } catch (NotFoundException e) {
            writeResponse(exchange, e.getMessage(), 404);
        } catch (Exception e) {
            writeResponse(exchange, e.getMessage(), 500);
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        String string = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Epic epic = gson.fromJson(string, Epic.class);
        try {
            if (epic.getId() != null) {
                taskManager.updateEpic(epic);
                writeResponse(exchange, "Задача обновлена", 201);
            } else {
                taskManager.createEpic(new Epic(epic.getName(),
                        epic.getDescription(),
                        epic.getStatus(),
                        epic.getType(),
                        epic.getDuration(),
                        epic.getStartTime()));
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
            taskManager.deleteEpicById(idFromPath.get());
            writeResponse(exchange, "Задача удалена", 201);
        } catch (NotFoundException e) {
            writeResponse(exchange, e.getMessage(), 404);
        } catch (Exception e) {
            writeResponse(exchange, e.getMessage(), 500);
        }
    }
}
