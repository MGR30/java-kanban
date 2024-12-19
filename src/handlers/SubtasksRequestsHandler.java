package handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exceptions.NotFoundException;
import exceptions.OverlapInTimeException;
import model.Subtask;
import service.TaskManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

public class SubtasksRequestsHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;


    public SubtasksRequestsHandler(TaskManager taskManager, Gson gson) {
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
            List<Subtask> allSubtasks = taskManager.getAllSubtask();
            String response = gson.toJson(allSubtasks);
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
            Subtask subtaskById = taskManager.getSubtaskById(idFromPath.get());
            String jsonResponse = gson.toJson(subtaskById);
            writeResponse(exchange, jsonResponse, 200);
        } catch (NotFoundException e) {
            writeResponse(exchange, e.getMessage(), 404);
        } catch (Exception e) {
            writeResponse(exchange, e.getMessage(), 500);
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        String string = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Subtask subtaskFromRequest = gson.fromJson(string, Subtask.class);
        try {
            if (subtaskFromRequest.getId() != null) {
                taskManager.updateSubtask(subtaskFromRequest);
                writeResponse(exchange, "Задача обновлена", 201);
            } else {
                Subtask subtaskForSave = new Subtask(subtaskFromRequest.getName(),
                        subtaskFromRequest.getDescription(),
                        subtaskFromRequest.getStatus(),
                        subtaskFromRequest.getType(),
                        subtaskFromRequest.getDuration(),
                        subtaskFromRequest.getStartTime());
                subtaskForSave.setEpicId(subtaskFromRequest.getEpicId());
                taskManager.createSubtask(subtaskForSave);
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
            taskManager.deleteSubtaskById(idFromPath.get());
            writeResponse(exchange, "Задача удалена", 201);
        } catch (NotFoundException e) {
            writeResponse(exchange, e.getMessage(), 404);
        } catch (Exception e) {
            writeResponse(exchange, e.getMessage(), 500);
        }
    }
}
