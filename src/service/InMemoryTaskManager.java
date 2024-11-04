package service;

import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    private final Map<Long, Task> taskStorage;
    private final Map<Long, Subtask> subtaskStorage;
    private final Map<Long, Epic> epicStorage;
    private final HistoryManager historyManager;
    private long nextId = 1L;

    public InMemoryTaskManager(HistoryManager historyManager) {
        taskStorage = new HashMap<>();
        subtaskStorage = new HashMap<>();
        epicStorage = new HashMap<>();
        this.historyManager = historyManager;
    }

    //Получение всех задач
    @Override
    public List<Task> getAllTask() {
        return new ArrayList<>(taskStorage.values());
    }

    @Override
    public List<Subtask> getAllSubtask() {
        return new ArrayList<>(subtaskStorage.values());
    }

    @Override
    public List<Epic> getAllEpic() {
        return new ArrayList<>(epicStorage.values());
    }

    // Очистка всех задач
    @Override
    public void deleteAllTasks() {
        taskStorage.clear();
    }

    @Override
    public void deleteAllSubtask() {
        Collection<Subtask> values = subtaskStorage.values();
        for (Subtask subtask : values) {
            Epic epic = epicStorage.get(subtask.getEpicId());
            epic.setStatus(TaskStatus.NEW);
            epic.getSubtasks().clear();
        }
        subtaskStorage.clear();
    }

    @Override
    public void deleteAllEpic() {
        subtaskStorage.clear();
        epicStorage.clear();
    }

    //Получение задачи по идентификатору
    @Override
    public Task getTaskById(Long taskId) {
        Task task = taskStorage.get(taskId);
        historyManager.add(task);
        return task;
    }

    @Override
    public Subtask getSubtaskById(Long subtaskId) {
        Subtask subtask = subtaskStorage.get(subtaskId);
        historyManager.add(subtask);
        return subtask;
    }

    @Override
    public Epic getEpicById(Long epicId) {
        Epic epic = epicStorage.get(epicId);
        historyManager.add(epic);
        return epic;
    }

    //Создание задачи

    @Override
    public void createTask(Task task) {
        task.setId(nextId++);
        taskStorage.put(task.getId(), task);
    }

    @Override
    public void createSubtask(Subtask newSubtask) {
        newSubtask.setId(nextId++);
        Epic epic = epicStorage.get(newSubtask.getEpicId());
        subtaskStorage.put(newSubtask.getId(), newSubtask);
        epic.getSubtasks().add(newSubtask);
        updateEpicStatus(epic);
    }

    @Override
    public void createEpic(Epic epic) {
        epic.setId(nextId++);
        epicStorage.put(epic.getId(), epic);
    }


    //Обновление данных по задаче
    @Override
    public void updateTask(Task task) {
        taskStorage.put(task.getId(), task);
    }

    @Override
    public void updateSubtask(Subtask updatableSubtask) {
        subtaskStorage.put(updatableSubtask.getId(), updatableSubtask);
        Epic epic = epicStorage.get(updatableSubtask.getEpicId());
        updateEpicStatus(epic);
    }

    @Override
    public void updateEpic(Epic epic) {
        epicStorage.put(epic.getId(), epic);
    }

    //Удаление задачи по идентификатору
    @Override
    public void deleteTaskById(Long taskId) {
        taskStorage.remove(taskId);
    }

    @Override
    public void deleteSubtaskById(Long subtaskId) {
        Subtask subtask = subtaskStorage.get(subtaskId);
        Epic epic = epicStorage.get(subtask.getEpicId());
        epic.getSubtasks().remove(subtask);
        updateEpicStatus(epic);
        subtaskStorage.remove(subtaskId);
    }

    @Override
    public void deleteEpicById(Long epicId) {
        Epic epic = epicStorage.get(epicId);
        List<Subtask> subtasksIds = epic.getSubtasks();
        for (Subtask subtask : subtasksIds) {
            subtaskStorage.remove(subtask.getId());
        }
        epicStorage.remove(epicId);
    }

    //Получение элементов конкретной коллекции
    @Override
    public List<Subtask> getSubtasksByEpicId(Long epicId) {
        Epic epic = epicStorage.get(epicId);

        return epic.getSubtasks();
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    public void removeTaskFromHistory(Long id) {
        historyManager.remove(id);
    }

    private void updateEpicStatus(Epic epic) {
        int countNew = 0;
        int countDone = 0;
        int countInProgress = 0;

        for (Subtask subtask : epic.getSubtasks()) {
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
