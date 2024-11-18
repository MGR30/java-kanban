import model.*;
import service.TaskManager;
import util.Managers;

import java.io.IOException;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException {
        System.out.println("Поехали!");

        TaskManager taskManager = Managers.getFileBackendTaskManager();
        System.out.println(taskManager.getAllTask());
        System.out.println("###");
        System.out.println(taskManager.getAllSubtask());
        System.out.println("###");
        for (Epic epic : taskManager.getAllEpic()) {
            System.out.println(epic.toString() + epic.getSubtasks());
        }
        Task task1 = new Task("name1", "description", TaskStatus.NEW, TaskType.TASK);
        Task task2 = new Task("name2", "description", TaskStatus.NEW, TaskType.TASK);
        Task task3 = new Task("name3", "description", TaskStatus.NEW, TaskType.TASK);
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createTask(task3);
        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task2.getId());
        taskManager.getTaskById(task3.getId());

        Epic epic1 = new Epic("epic4", "description", TaskStatus.NEW, TaskType.EPIC);
        Epic epic2 = new Epic("epic5", "description", TaskStatus.NEW, TaskType.EPIC);
        Epic epic3 = new Epic("epic6", "description", TaskStatus.NEW, TaskType.EPIC);
        Epic epic4 = new Epic("epic7", "description", TaskStatus.NEW, TaskType.EPIC);
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);
        taskManager.createEpic(epic3);
        taskManager.createEpic(epic4);
        taskManager.getEpicById(epic1.getId());
        taskManager.getEpicById(epic2.getId());
        taskManager.getEpicById(epic3.getId());
        taskManager.getEpicById(epic4.getId());
        printHistory(taskManager.getHistory());
        System.out.println("#############");
        Subtask task4 = new Subtask("subname8", "description", TaskStatus.NEW, TaskType.SUBTASK);
        Subtask task5 = new Subtask("subname9", "description", TaskStatus.NEW, TaskType.SUBTASK);
        Subtask task6 = new Subtask("subname10", "description", TaskStatus.NEW, TaskType.SUBTASK);
        task4.setEpicId(epic1.getId());
        task5.setEpicId(epic2.getId());
        task6.setEpicId(epic3.getId());
        taskManager.createSubtask(task4);
        taskManager.createSubtask(task5);
        taskManager.createSubtask(task6);
        epic1.getSubtasks().add(task4);
        epic2.getSubtasks().add(task5);
        epic3.getSubtasks().add(task6);

        taskManager.getSubtaskById(task4.getId());
        taskManager.getSubtaskById(task5.getId());
        taskManager.getSubtaskById(task6.getId());


        Task task11 = new Task("name11", "description", TaskStatus.NEW, TaskType.TASK);
        taskManager.createTask(task11);
        taskManager.getTaskById(task11.getId());
        printHistory(taskManager.getHistory());
        System.out.println("#################");
        Task task12 = new Task("name12", "description", TaskStatus.NEW, TaskType.TASK);
        taskManager.createTask(task12);

        taskManager.getTaskById(task12.getId());
        printHistory(taskManager.getHistory());
        System.out.println("#################");
        Task task13 = new Task("name13", "description", TaskStatus.NEW, TaskType.TASK);
        taskManager.createTask(task13);
        Task task14 = new Task("name15", "description", TaskStatus.NEW, TaskType.TASK);
        taskManager.createTask(task14);
        Task task15 = new Task("name16", "description", TaskStatus.NEW, TaskType.TASK);
        taskManager.createTask(task15);
        Task task16 = new Task("name17", "description", TaskStatus.NEW, TaskType.TASK);
        taskManager.createTask(task16);
        taskManager.getTaskById(task13.getId());
        printHistory(taskManager.getHistory());
        System.out.println("###########");
        taskManager.getTaskById(task1.getId());
        printHistory(taskManager.getHistory());
        //taskManager.deleteAllTasks();

    }

    private static void printHistory(List<Task> tasks) {
        for (Task task : tasks) {
            System.out.println(task.getName());
        }
    }
}
