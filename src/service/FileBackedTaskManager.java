package service;

import model.*;

import java.io.*;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private final File file;

    public FileBackedTaskManager(HistoryManager historyManager, File file) {
        super(historyManager);
        this.file = file;
        loadFromFile(file);
    }

    @Override
    public void createTask(Task task) {
        super.createTask(task);
        save();
    }

    @Override
    public void createSubtask(Subtask newSubtask) {
        super.createSubtask(newSubtask);
        save();
    }

    @Override
    public void createEpic(Epic epic) {
        super.createEpic(epic);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void removeTaskFromHistory(Long id) {
        super.removeTaskFromHistory(id);
        save();
    }

    @Override
    public void deleteEpicById(Long epicId) {
        super.deleteEpicById(epicId);
        save();
    }

    @Override
    public void deleteSubtaskById(Long subtaskId) {
        super.deleteSubtaskById(subtaskId);
        save();
    }

    @Override
    public void deleteTaskById(Long taskId) {
        super.deleteTaskById(taskId);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask updatableSubtask) {
        super.updateSubtask(updatableSubtask);
        save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllSubtask() {
        super.deleteAllSubtask();
        save();
    }

    @Override
    public void deleteAllEpic() {
        super.deleteAllEpic();
        save();
    }

    public File getFile() {
        return file;
    }

    public void save() {
        List<Task> allTask = super.getAllTask();
        List<Epic> allEpic = super.getAllEpic();
        List<Subtask> allSubtask = super.getAllSubtask();
        try (FileWriter csvWriter = new FileWriter(file); BufferedReader reader = new BufferedReader(new FileReader(file))) {
            int existString = reader.read();
            if (existString == -1) {
                csvWriter.write("id,name,status,description,type,epic\n");
            }
            for (Task task : allTask) {
                csvWriter.append(task.toString()).append("\n");
            }
            for (Epic epic : allEpic) {
                csvWriter.append(epic.toString()).append("\n");
            }
            for (Subtask subtask : allSubtask) {
                csvWriter.append(subtask.toString()).append("\n");
            }
        } catch (IOException e) {
            System.out.println("Произошла ошибка при записи файла");
        }
    }

    private void loadFromFile(File file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            while (reader.ready()) {
                String loadingString = reader.readLine();
                if (loadingString.startsWith("id")) {
                    continue;
                }
                String[] splitsString = loadingString.split(",");
                switch (splitsString[4]) {
                    case "SUBTASK":
                        Subtask subtask = new Subtask(splitsString[1], splitsString[2], TaskStatus.valueOf(splitsString[3]), TaskType.valueOf(splitsString[4]));
                        subtask.setId(Long.parseLong(splitsString[0]));
                        subtask.setEpicId(Long.parseLong(splitsString[5]));
                        super.saveSubtaskToStorage(subtask);
                        break;
                    case "EPIC":
                        Epic epic = new Epic(splitsString[1], splitsString[2], TaskStatus.valueOf(splitsString[3]), TaskType.valueOf(splitsString[4]));
                        epic.setId(Long.parseLong(splitsString[0]));
                        super.saveEpicToStorage(epic);
                        break;
                    default:
                        Task task = new Task(splitsString[1], splitsString[2], TaskStatus.valueOf(splitsString[3]), TaskType.valueOf(splitsString[4]));
                        task.setId(Long.parseLong(splitsString[0]));
                        super.saveTaskToStorage(task);
                        break;
                }
            }
            addLoadingSubtaskToEpic();
            System.out.println("Загрузка данных произошла успешно");
        } catch (IOException e) {
            System.out.println("Ошибка при чтении файла");
        }
    }

    private void addLoadingSubtaskToEpic() {
        List<Subtask> subtasks = super.getAllSubtask();
        List<Epic> epics = super.getAllEpic();
        for (Epic epic : epics) {
            long epicId = epic.getId();
            for (Subtask subtask : subtasks) {
                if (subtask.getEpicId() == epicId) {
                    epic.getSubtasks().add(subtask);
                }
            }
        }
    }
}
