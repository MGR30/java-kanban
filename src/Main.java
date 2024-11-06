import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import service.InMemoryTaskManager;
import service.TaskManager;
import util.Managers;

import java.util.Collection;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");

        /*TaskManager inMemoryTaskManager = new InMemoryTaskManager();
        Task task1 = new Task("Сходить за хлебом", "Дойти до магазина, в котором продают хлеб", TaskStatus.NEW);
        Task task2 = new Task("Побрить ежа", "Я не знаю зачем", TaskStatus.NEW);
        inMemoryTaskManager.createTask(task1);
        inMemoryTaskManager.createTask(task2);

        Epic epic1 = new Epic("Починить автомобиль", "Крутит, но не заводится", TaskStatus.NEW);
        inMemoryTaskManager.createEpic(epic1);
        Subtask subtask1 = new Subtask("Найти причину поломки", "Возможно придется запачкаться", TaskStatus.NEW);
        Subtask subtask2 = new Subtask("Заказать детали", "Какие?", TaskStatus.NEW);
        subtask1.setEpicId(epic1.getId());
        subtask2.setEpicId(epic1.getId());
        inMemoryTaskManager.createSubtask(subtask1);
        inMemoryTaskManager.createSubtask(subtask2);
        epic1.getSubtasks().add(subtask1.getId());
        epic1.getSubtasks().add(subtask2.getId());

        Epic epic2 = new Epic("Написать план на день", "Все должно идти по плану", TaskStatus.NEW);
        inMemoryTaskManager.createEpic(epic2);
        Subtask subtask3 = new Subtask("Купить ежедневник", "Канцелярка за углом", TaskStatus.NEW);
        subtask3.setEpicId(epic2.getId());
        inMemoryTaskManager.createSubtask(subtask3);
        epic2.getSubtasks().add(subtask3.getId());
        inMemoryTaskManager.createSubtask(subtask3);*/

        TaskManager taskManager = Managers.getDefault();
        Task task1 = new Task("name1", "description", TaskStatus.NEW);
        Task task2 = new Task("name2", "description", TaskStatus.NEW);
        Task task3 = new Task("name3", "description", TaskStatus.NEW);
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createTask(task3);
        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task2.getId());
        taskManager.getTaskById(task3.getId());

        Epic epic1 = new Epic("epic4", "description", TaskStatus.NEW);
        Epic epic2 = new Epic("epic5", "description", TaskStatus.NEW);
        Epic epic3 = new Epic("epic6", "description", TaskStatus.NEW);
        Epic epic4 = new Epic("epic7", "description", TaskStatus.NEW);
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
        Subtask task4 = new Subtask("subname8", "description", TaskStatus.NEW);
        Subtask task5 = new Subtask("subname9", "description", TaskStatus.NEW);
        Subtask task6 = new Subtask("subname10", "description", TaskStatus.NEW);
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


        Task task11 = new Task("name11", "description", TaskStatus.NEW);
        taskManager.createTask(task11);
        taskManager.getTaskById(task11.getId());
        printHistory(taskManager.getHistory());
        System.out.println("#################");
        Task task12 = new Task("name12", "description", TaskStatus.NEW);
        taskManager.createTask(task12);

        taskManager.getTaskById(task12.getId());
        printHistory(taskManager.getHistory());
        System.out.println("#################");
        Task task13 = new Task("name13", "description", TaskStatus.NEW);
        taskManager.createTask(task13);

        taskManager.getTaskById(task13.getId());
        printHistory(taskManager.getHistory());
        System.out.println("###########");
        taskManager.getTaskById(task1.getId());
        printHistory(taskManager.getHistory());


        //Поверяем получение всех задач
        //checkGetAll(inMemoryTaskManager);

        //Проверяем обновление задачи
        //checkUpdateTask(task1, inMemoryTaskManager);

        //Проверяем обновление эпика
        //checkEpicUpdate(epic1, subtask1, subtask2, inMemoryTaskManager);

        //Проверка удаления
        //checkTaskDelete(inMemoryTaskManager, task2);
        //checkEpicDelete(inMemoryTaskManager, epic2);

        //Проверка получения списка подзадач по epicId
        //checkGetEpicSubtaskListById(inMemoryTaskManager.getSubtasksByEpicId(epic1.getId()));

        //Проверяем удаление
        //checkDeleteAllSubtask(taskManager);
        //checkDeleteAllEpic(taskManager);

        //subtask2.setStatus(TaskStatus.NEW);
        //checkDeleteSubtaskById(inMemoryTaskManager, subtask1);

    }

    private static void printHistory(List<Task> tasks) {
        for (Task task : tasks) {
            System.out.println(task.getName());
        }
    }

    private static void checkDeleteSubtaskById(TaskManager inMemoryTaskManager, Subtask subtask) {
        System.out.println("#### Проверяем удаление подзадачи по id ####");
        System.out.println("Выводим эпик до удаления подзадачи");
        Long epicId = subtask.getEpicId();
        System.out.println(inMemoryTaskManager.getEpicById(epicId));
        inMemoryTaskManager.deleteSubtaskById(subtask.getId());
        System.out.println("Выводим эпик после удаления, статус эпика NEW");
        System.out.println(inMemoryTaskManager.getEpicById(epicId));
        System.out.println("##############################################################");
    }

    private static void checkDeleteAllSubtask(InMemoryTaskManager inMemoryTaskManager) {
        System.out.println("#### Проверяем удаление всех подзадач ####");
        System.out.println("Выводим все эпики до удаления");
        System.out.println(inMemoryTaskManager.getAllEpic());
        System.out.println("Выводим все подзадачи");
        System.out.println(inMemoryTaskManager.getAllSubtask());
        inMemoryTaskManager.deleteAllSubtask();
        System.out.println("Выводим все эпики после удаления, статусы эпиков NEW");
        System.out.println(inMemoryTaskManager.getAllEpic());
        System.out.println("Выводим все подзадачи, должна быть пустая мапа");
        System.out.println(inMemoryTaskManager.getAllSubtask());
        System.out.println("##############################################################");
    }

    private static void checkDeleteAllEpic(InMemoryTaskManager inMemoryTaskManager) {
        System.out.println("#### Проверяем удаление всех эпиков ####");
        System.out.println("Выводим все эпики до удаления");
        System.out.println(inMemoryTaskManager.getAllEpic());
        System.out.println("Выводим все подзадачи");
        System.out.println(inMemoryTaskManager.getAllSubtask());
        inMemoryTaskManager.deleteAllEpic();
        System.out.println("Выводим все эпики после удаления, должна быть пустая мапа");
        System.out.println(inMemoryTaskManager.getAllEpic());
        System.out.println("Выводим все подзадачи, должна быть пустая мапа");
        System.out.println(inMemoryTaskManager.getAllSubtask());
        System.out.println("##############################################################");
    }

    private static void checkEpicDelete(TaskManager inMemoryTaskManager, Epic epic2) {
        System.out.println("#### Проверяем удаление эпика ####");
        inMemoryTaskManager.deleteEpicById(epic2.getId());
        System.out.println(inMemoryTaskManager.getAllEpic());
        System.out.println(inMemoryTaskManager.getAllSubtask());
        System.out.println("####################################################################");
        System.out.println();
    }

    private static void checkTaskDelete(TaskManager inMemoryTaskManager, Task task2) {
        System.out.println("#### Проверяем удаление задач ####");
        inMemoryTaskManager.deleteTaskById(task2.getId());
        System.out.println(inMemoryTaskManager.getAllTask());
        System.out.println("####################################################################");
        System.out.println();
    }

    private static void checkEpicUpdate(Epic epic1, Subtask subtask1, Subtask subtask2, TaskManager inMemoryTaskManager) {
        System.out.println("#### Выводим эпик с подзадачами до обновления статуса в подзадачах, эпик - NEW ####");
        System.out.println(epic1);
        System.out.println(subtask1);
        System.out.println(subtask2);
        System.out.println("#### Меняем статус в первой подзадаче на IN_PROGRESS ####");
        subtask1.setStatus(TaskStatus.IN_PROGRESS);
        inMemoryTaskManager.updateSubtask(subtask1);
        System.out.println("#### Выводим эпик с подзадачами, эпик - IN_PROGRESS  ####");
        System.out.println(inMemoryTaskManager.getEpicById(epic1.getId()));
        System.out.println(subtask1);
        System.out.println(subtask2);
        System.out.println("#### Меняем статус второй подзадачи на DONE ####");
        subtask2.setStatus(TaskStatus.DONE);
        System.out.println("#### Выводим эпик с подзадачами, эпик - IN_PROGRESS ####");
        System.out.println(subtask1);
        System.out.println(subtask2);
        inMemoryTaskManager.updateSubtask(subtask2);
        System.out.println(inMemoryTaskManager.getEpicById(epic1.getId()));
        System.out.println("#### Меняем статус первой подзадачи на DONE ####");
        subtask1.setStatus(TaskStatus.DONE);
        System.out.println("#### Выводим эпик с подзадачами, эпик - DONE ####");
        System.out.println(subtask1);
        System.out.println(subtask2);
        inMemoryTaskManager.updateSubtask(subtask1);
        System.out.println(inMemoryTaskManager.getEpicById(epic1.getId()));
        System.out.println("##############################################################");
        System.out.println();
    }

    private static void checkUpdateTask(Task task1, TaskManager inMemoryTaskManager) {
        System.out.println("#### Выводим таск до обновления ####");
        System.out.println(task1);
        task1.setStatus(TaskStatus.IN_PROGRESS);
        inMemoryTaskManager.updateTask(task1);
        System.out.println("#### Выводим таск после обновления ####");
        System.out.println(inMemoryTaskManager.getTaskById(task1.getId()));
        System.out.println("##############################################################");
        System.out.println();
    }

    private static void checkGetAll(TaskManager inMemoryTaskManager) {
        System.out.println("#### Получаем задачи ####");
        Collection<Task> allTask = inMemoryTaskManager.getAllTask();
        System.out.println(allTask);
        Collection<Subtask> allSubtask = inMemoryTaskManager.getAllSubtask();
        System.out.println(allSubtask);
        Collection<Epic> allEpic = inMemoryTaskManager.getAllEpic();
        System.out.println(allEpic);
        System.out.println("################################################################");
        System.out.println();
    }

    private static void checkGetEpicSubtaskListById(List<Subtask> subtasks) {
        System.out.println("####  ####");
        System.out.println(subtasks);
        System.out.println("####################################################################");
    }

}
