package service;

import model.Epic;
import model.Subtask;
import model.Task;

import java.util.List;

public interface TaskManager {
    //Получение всех задач
    List<Task> getAllTask();

    List<Subtask> getAllSubtask();

    List<Epic> getAllEpic();

    // Очистка всех задач
    void deleteAllTasks();

    void deleteAllSubtask();

    void deleteAllEpic();

    //Получение задачи по идентификатору
    Task getTaskById(Long taskId);

    Subtask getSubtaskById(Long subtaskId);

    Epic getEpicById(Long epicId);

    //Создание задачи
    void createTask(Task task);

    void createSubtask(Subtask newSubtask);

    void createEpic(Epic epic);

    void updateTask(Task task);

    void updateSubtask(Subtask updatableSubtask);

    void updateEpic(Epic epic);

    void deleteTaskById(Long taskId);

    void deleteSubtaskById(Long subtaskId);

    void deleteEpicById(Long epicId);

    List<Subtask> getSubtasksByEpicId(Long epicId);

    List<Task> getHistory();
}
