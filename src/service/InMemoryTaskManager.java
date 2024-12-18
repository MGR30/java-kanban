package service;

import exceptions.NotFoundException;
import exceptions.OverlapInTimeException;
import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    private static final String NOT_FOUND_MESSAGE = "Задача с таким идентификатором не найдена";
    private static final String OVERLAP_IN_TIME_MESSAGE = "Задача пересекается по времени!";
    private final Map<Long, Task> taskStorage;
    private final Map<Long, Subtask> subtaskStorage;
    private final Map<Long, Epic> epicStorage;
    private final Set<Task> sortedTasksStorage;
    private final HistoryManager historyManager;
    private long nextId = 1L;

    public InMemoryTaskManager(HistoryManager historyManager) {
        taskStorage = new HashMap<>();
        subtaskStorage = new HashMap<>();
        epicStorage = new HashMap<>();
        sortedTasksStorage = new TreeSet<>();
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
        values.forEach(subtask -> {
            Epic epic = epicStorage.get(subtask.getEpicId());
            epic.setStatus(TaskStatus.NEW);
            epic.getSubtasks().clear();
            epic.updateTimesParameters();
        });
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
        if (task != null) {
            historyManager.add(task);
        } else {
            throw new NotFoundException(NOT_FOUND_MESSAGE);
        }
        return task;
    }

    @Override
    public Subtask getSubtaskById(Long subtaskId) {
        Subtask subtask = subtaskStorage.get(subtaskId);
        if (subtask != null) {
            historyManager.add(subtask);
        } else {
            throw new NotFoundException(NOT_FOUND_MESSAGE);
        }
        return subtask;
    }

    @Override
    public Epic getEpicById(Long epicId) {
        Epic epic = epicStorage.get(epicId);
        if (epic != null) {
            historyManager.add(epic);
        } else {
            throw new NotFoundException(NOT_FOUND_MESSAGE);
        }
        return epic;
    }

    //Создание задачи

    @Override
    public void createTask(Task task) {
        task.setId(nextId++);

        if (isNotIntersectionTime(task) || taskStorage.isEmpty()) {
            taskStorage.put(task.getId(), task);
        } else {
            System.out.println("Задача пересекается по времени!");
            throw new OverlapInTimeException(OVERLAP_IN_TIME_MESSAGE);
        }

        if (Objects.nonNull(task.getStartTime())) {
            sortedTasksStorage.add(task);
        }
    }

    @Override
    public void createSubtask(Subtask newSubtask) {
        newSubtask.setId(nextId++);
        Epic epic = epicStorage.get(newSubtask.getEpicId());

        if (isNotIntersectionTime(newSubtask)) {
            subtaskStorage.put(newSubtask.getId(), newSubtask);
            epic.getSubtasks().add(newSubtask);
            updateEpicStatus(epic);
            epic.updateTimesParameters();
        } else {
            System.out.println("Задача пересекается по времени!");
            throw new OverlapInTimeException(OVERLAP_IN_TIME_MESSAGE);
        }

        if (Objects.nonNull(newSubtask.getStartTime())) {
            sortedTasksStorage.add(newSubtask);
        }
    }

    @Override
    public void createEpic(Epic epic) {
        epic.setId(nextId++);
        epicStorage.put(epic.getId(), epic);
    }


    //Обновление данных по задаче
    @Override
    public void updateTask(Task task) {
        if (isNotIntersectionTime(task) && taskStorage.containsKey(task.getId())) {
            taskStorage.put(task.getId(), task);
        } else {
            System.out.println("Задача пересекается по времени!");
            throw new OverlapInTimeException(OVERLAP_IN_TIME_MESSAGE);
        }
    }

    @Override
    public void updateSubtask(Subtask updatableSubtask) {
        Epic epic = epicStorage.get(updatableSubtask.getEpicId());
        if (isNotIntersectionTime(updatableSubtask) && subtaskStorage.containsKey(updatableSubtask.getId())) {
            subtaskStorage.put(updatableSubtask.getId(), updatableSubtask);
            epic.getSubtasks().add(updatableSubtask);
            updateEpicStatus(epic);
            epic.updateTimesParameters();
        } else {
            System.out.println(OVERLAP_IN_TIME_MESSAGE);
            throw new OverlapInTimeException(OVERLAP_IN_TIME_MESSAGE);
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        epicStorage.put(epic.getId(), epic);
    }

    //Удаление задачи по идентификатору
    @Override
    public void deleteTaskById(Long taskId) {
        if (getTaskById(taskId) == null) {
            throw new NotFoundException(NOT_FOUND_MESSAGE);
        }
        taskStorage.remove(taskId);
    }

    @Override
    public void deleteSubtaskById(Long subtaskId) {
        if (getSubtaskById(subtaskId) == null) {
            throw new NotFoundException(NOT_FOUND_MESSAGE);
        }
        Subtask subtask = subtaskStorage.get(subtaskId);
        Epic epic = epicStorage.get(subtask.getEpicId());
        epic.getSubtasks().remove(subtask);
        updateEpicStatus(epic);
        epic.updateTimesParameters();
        subtaskStorage.remove(subtaskId);
    }

    @Override
    public void deleteEpicById(Long epicId) {
        if (getEpicById(epicId) == null) {
            throw new NotFoundException(NOT_FOUND_MESSAGE);
        }
        Epic epic = epicStorage.get(epicId);
        epic.getSubtasks().forEach(subtask -> subtaskStorage.remove(subtask.getId()));
        epicStorage.remove(epicId);
    }

    //Получение элементов конкретной коллекции
    @Override
    public List<Subtask> getSubtasksByEpicId(Long epicId) {
        if (getEpicById(epicId) == null) {
            throw new NotFoundException(NOT_FOUND_MESSAGE);
        }
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

    public void saveTaskToStorage(Task task) {
        taskStorage.put(task.getId(), task);
    }

    public void saveSubtaskToStorage(Subtask subtask) {
        subtaskStorage.put(subtask.getId(), subtask);
    }

    public void saveEpicToStorage(Epic epic) {
        epicStorage.put(epic.getId(), epic);
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return sortedTasksStorage.stream().toList();
    }

    private boolean isNotIntersectionTime(Task task) {
        long count = getPrioritizedTasks().stream()
                .filter(task1 -> task1.getEndTime().isAfter(task.getStartTime())
                        && (task1.getStartTime().isBefore(task.getStartTime()) || task1.getStartTime().isEqual(task.getStartTime()))
                        && !Objects.equals(task1.getId(), task.getId()))
                .count();
        return count == 0;
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
