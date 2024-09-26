package service;

import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;

import java.util.*;

public class TaskManager {
    private final Map<Long, Task> taskStorage;
    private final Map<Long, Subtask> subtaskStorage;
    private final Map<Long, Epic> epicStorage;
    private long nextId = 1L;

    public TaskManager() {
        taskStorage = new HashMap<>();
        subtaskStorage = new HashMap<>();
        epicStorage = new HashMap<>();
    }

    //Получение всех задач
    public List<Task> getAllTask() {
        return new ArrayList<>(taskStorage.values());
    }

    public List<Subtask> getAllSubtask() {
        return new ArrayList<>(subtaskStorage.values());
    }

    public List<Epic> getAllEpic() {
        return new ArrayList<>(epicStorage.values());
    }

    // Очистка всех задач
    public void deleteAllTasks() {
        taskStorage.clear();
    }

    public void deleteAllSubtask() {
        Collection<Subtask> values = subtaskStorage.values();
        for (Subtask subtask : values) {
            Epic epic = epicStorage.get(subtask.getEpicId());
            epic.setStatus(TaskStatus.NEW);
            epic.getSubtasks().clear();
        }
        subtaskStorage.clear();
    }

    public void deleteAllEpic() {
        subtaskStorage.clear();
        epicStorage.clear();
    }

    //Получение задачи по идентификатору
    public Task getTaskById(Long taskId) {
        return taskStorage.get(taskId);
    }

    public Subtask getSubtaskById(Long subtaskId) {
        return subtaskStorage.get(subtaskId);
    }

    public Epic getEpicById(Long epicId) {
        return epicStorage.get(epicId);
    }

    //Создание задачи
    public void createTask(Task task) {
        task.setId(nextId++);
        taskStorage.put(task.getId(), task);
    }

    public void createSubtask(Subtask newSubtask) {
        newSubtask.setId(nextId++);
        Epic epic = epicStorage.get(newSubtask.getEpicId());
        subtaskStorage.put(newSubtask.getId(), newSubtask);
        updateEpicStatus(epic);
    }

    public void createEpic(Epic epic) {
        epic.setId(nextId++);
        epicStorage.put(epic.getId(), epic);
    }

    //Обновление данных по задаче

    public void updateTask(Task task) {
        taskStorage.put(task.getId(), task);
    }

    public void updateSubtask(Subtask updatableSubtask) {
        subtaskStorage.put(updatableSubtask.getId(), updatableSubtask);
        Epic epic = epicStorage.get(updatableSubtask.getEpicId());
        updateEpicStatus(epic);
    }

    public void updateEpic(Epic epic) {
        epicStorage.put(epic.getId(), epic);
    }

    //Удаление задачи по идентификатору

    public void deleteTaskById(Long taskId) {
        taskStorage.remove(taskId);
    }

    public void deleteSubtaskById(Long subtaskId) {
        Subtask subtask = subtaskStorage.get(subtaskId);
        Epic epic = epicStorage.get(subtask.getEpicId());
        epic.getSubtasks().remove(subtask.getId());
        updateEpicStatus(epic);
        subtaskStorage.remove(subtaskId);
    }

    public void deleteEpicById(Long epicId) {
        Epic epic = epicStorage.get(epicId);
        List<Long> subtasksIds = epic.getSubtasks();
        for (Long subtaskId : subtasksIds) {
            subtaskStorage.remove(subtaskId);
        }
    }

    //Получение элементов конкретной коллекции

    public List<Subtask> getSubtasksByEpicId(Long epicId) {
        Epic epic = epicStorage.get(epicId);
        List<Long> subtasksIds = epic.getSubtasks();
        List<Subtask> subtasks = new ArrayList<>();

        for (Long subtasksId : subtasksIds) {
            subtasks.add(subtaskStorage.get(subtasksId));
        }

        return subtasks;
    }


    private void updateEpicStatus(Epic epic) {
        int countNew = 0;
        int countDone = 0;
        int countInProgress = 0;

        for (Long subtaskId : epic.getSubtasks()) {
            Subtask subtask = subtaskStorage.get(subtaskId);
            switch (subtask.getStatus()) {
                case NEW:
                    countNew++;
                    break;
                case DONE:
                    countDone++;
                    break;
                case IN_PROGRESS:
                    countInProgress++;
                    break;
            }
        }

        if (countInProgress > 0) {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        } else if (countNew > 0 && countDone == 0) {
            epic.setStatus(TaskStatus.NEW);
        } else if (countDone > 0 && countNew == 0) {
            epic.setStatus(TaskStatus.DONE);
        }
    }
}
