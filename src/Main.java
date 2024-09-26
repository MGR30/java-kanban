import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import service.TaskManager;

import java.util.Collection;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");

        TaskManager taskManager = new TaskManager();
        Task task1 = new Task("Сходить за хлебом", "Дойти до магазина, в котором продают хлеб", TaskStatus.NEW);
        Task task2 = new Task("Побрить ежа", "Я не знаю зачем", TaskStatus.NEW);
        taskManager.createTask(task1);
        taskManager.createTask(task2);

        Epic epic1 = new Epic("Починить автомобиль", "Крутит, но не заводится", TaskStatus.NEW);
        taskManager.createEpic(epic1);
        Subtask subtask1 = new Subtask("Найти причину поломки", "Возможно придется запачкаться", TaskStatus.NEW);
        Subtask subtask2 = new Subtask("Заказать детали", "Какие?", TaskStatus.NEW);
        subtask1.setEpicId(epic1.getId());
        subtask2.setEpicId(epic1.getId());
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        epic1.getSubtasks().add(subtask1.getId());
        epic1.getSubtasks().add(subtask2.getId());

        Epic epic2 = new Epic("Написать план на день", "Все должно идти по плану", TaskStatus.NEW);
        taskManager.createEpic(epic2);
        Subtask subtask3 = new Subtask("Купить ежедневник", "Канцелярка за углом", TaskStatus.NEW);
        subtask3.setEpicId(epic2.getId());
        taskManager.createSubtask(subtask3);
        epic2.getSubtasks().add(subtask3.getId());
        taskManager.createSubtask(subtask3);

        //Поверяем получение всех задач
        checkGetAll(taskManager);

        //Проверяем обновление задачи
        checkUpdateTask(task1, taskManager);

        //Проверяем обновление эпика
        checkEpicUpdate(epic1, subtask1, subtask2, taskManager);

        //Проверка удаления
        checkTaskDelete(taskManager, task2);
        checkEpicDelete(taskManager, epic2);

        //Проверка получения списка подзадач по epicId
        checkGetEpicSubtaskListById(taskManager.getSubtasksByEpicId(epic1.getId()));

        //Проверяем удаление
        //checkDeleteAllSubtask(taskManager);
        //checkDeleteAllEpic(taskManager);

        subtask2.setStatus(TaskStatus.NEW);
        checkDeleteSubtaskById(taskManager, subtask1);

    }

    private static void checkDeleteSubtaskById(TaskManager taskManager, Subtask subtask) {
        System.out.println("#### Проверяем удаление подзадачи по id ####");
        System.out.println("Выводим эпик до удаления подзадачи");
        Long epicId = subtask.getEpicId();
        System.out.println(taskManager.getEpicById(epicId));
        taskManager.deleteSubtaskById(subtask.getId());
        System.out.println("Выводим эпик после удаления, статус эпика NEW");
        System.out.println(taskManager.getEpicById(epicId));
        System.out.println("##############################################################");
    }

    private static void checkDeleteAllSubtask(TaskManager taskManager){
        System.out.println("#### Проверяем удаление всех подзадач ####");
        System.out.println("Выводим все эпики до удаления");
        System.out.println(taskManager.getAllEpic());
        System.out.println("Выводим все подзадачи");
        System.out.println(taskManager.getAllSubtask());
        taskManager.deleteAllSubtask();
        System.out.println("Выводим все эпики после удаления, статусы эпиков NEW");
        System.out.println(taskManager.getAllEpic());
        System.out.println("Выводим все подзадачи, должна быть пустая мапа");
        System.out.println(taskManager.getAllSubtask());
        System.out.println("##############################################################");
    }

    private static void checkDeleteAllEpic(TaskManager taskManager){
        System.out.println("#### Проверяем удаление всех эпиков ####");
        System.out.println("Выводим все эпики до удаления");
        System.out.println(taskManager.getAllEpic());
        System.out.println("Выводим все подзадачи");
        System.out.println(taskManager.getAllSubtask());
        taskManager.deleteAllEpic();
        System.out.println("Выводим все эпики после удаления, должна быть пустая мапа");
        System.out.println(taskManager.getAllEpic());
        System.out.println("Выводим все подзадачи, должна быть пустая мапа");
        System.out.println(taskManager.getAllSubtask());
        System.out.println("##############################################################");
    }

    private static void checkEpicDelete(TaskManager taskManager, Epic epic2) {
        System.out.println("#### Проверяем удаление эпика ####");
        taskManager.deleteEpicById(epic2.getId());
        System.out.println(taskManager.getAllEpic());
        System.out.println(taskManager.getAllSubtask());
        System.out.println("####################################################################");
        System.out.println();
    }

    private static void checkTaskDelete(TaskManager taskManager, Task task2) {
        System.out.println("#### Проверяем удаление задач ####");
        taskManager.deleteTaskById(task2.getId());
        System.out.println(taskManager.getAllTask());
        System.out.println("####################################################################");
        System.out.println();
    }

    private static void checkEpicUpdate(Epic epic1, Subtask subtask1, Subtask subtask2, TaskManager taskManager) {
        System.out.println("#### Выводим эпик с подзадачами до обновления статуса в подзадачах, эпик - NEW ####");
        System.out.println(epic1);
        System.out.println(subtask1);
        System.out.println(subtask2);
        System.out.println("#### Меняем статус в первой подзадаче на IN_PROGRESS ####");
        subtask1.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateSubtask(subtask1);
        System.out.println("#### Выводим эпик с подзадачами, эпик - IN_PROGRESS  ####");
        System.out.println(taskManager.getEpicById(epic1.getId()));
        System.out.println(subtask1);
        System.out.println(subtask2);
        System.out.println("#### Меняем статус второй подзадачи на DONE ####");
        subtask2.setStatus(TaskStatus.DONE);
        System.out.println("#### Выводим эпик с подзадачами, эпик - IN_PROGRESS ####");
        System.out.println(subtask1);
        System.out.println(subtask2);
        taskManager.updateSubtask(subtask2);
        System.out.println(taskManager.getEpicById(epic1.getId()));
        System.out.println("#### Меняем статус первой подзадачи на DONE ####");
        subtask1.setStatus(TaskStatus.DONE);
        System.out.println("#### Выводим эпик с подзадачами, эпик - DONE ####");
        System.out.println(subtask1);
        System.out.println(subtask2);
        taskManager.updateSubtask(subtask1);
        System.out.println(taskManager.getEpicById(epic1.getId()));
        System.out.println("##############################################################");
        System.out.println();
    }

    private static void checkUpdateTask(Task task1, TaskManager taskManager) {
        System.out.println("#### Выводим таск до обновления ####");
        System.out.println(task1);
        task1.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateTask(task1);
        System.out.println("#### Выводим таск после обновления ####");
        System.out.println(taskManager.getTaskById(task1.getId()));
        System.out.println("##############################################################");
        System.out.println();
    }

    private static void checkGetAll(TaskManager taskManager) {
        System.out.println("#### Получаем задачи ####");
        Collection<Task> allTask = taskManager.getAllTask();
        System.out.println(allTask);
        Collection<Subtask> allSubtask = taskManager.getAllSubtask();
        System.out.println(allSubtask);
        Collection<Epic> allEpic = taskManager.getAllEpic();
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
