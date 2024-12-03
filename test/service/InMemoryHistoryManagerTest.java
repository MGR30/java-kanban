package service;

import model.Task;
import model.TaskStatus;
import model.TaskType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.InMemoryHistoryManager;

import java.time.Duration;
import java.time.LocalDateTime;

class InMemoryHistoryManagerTest {
    private static InMemoryHistoryManager historyManager;
    private static Task newTask;
    private static Task newTask2;
    private static Task newTask3;

    @BeforeEach
    public void setUp() {
        historyManager = new InMemoryHistoryManager();
        newTask = new Task("name", "description", TaskStatus.NEW, TaskType.TASK, Duration.ofMinutes(15), LocalDateTime.now());
        newTask2 = new Task("name2", "description", TaskStatus.NEW, TaskType.TASK, Duration.ofMinutes(15), LocalDateTime.now().plusMinutes(20));
        newTask3 = new Task("name3", "description", TaskStatus.NEW, TaskType.TASK, Duration.ofMinutes(15), LocalDateTime.now().plusMinutes(40));
        newTask.setId(1L);
        newTask2.setId(2L);
        newTask3.setId(3L);
    }

    @Test
    void add() {
        historyManager.add(newTask);
        Assertions.assertEquals(1, historyManager.getHistory().size());
    }

    @Test
    void addSameTask() {
        historyManager.add(newTask);
        historyManager.add(newTask);
        Assertions.assertEquals(1, historyManager.getHistory().size());
    }

    @Test
    void addSameAndAnotherTask() {
        historyManager.add(newTask);
        historyManager.add(newTask);
        historyManager.add(newTask2);
        Assertions.assertEquals(2, historyManager.getHistory().size());
    }

    @Test
    void getHistory() {
        historyManager.add(newTask);
        Assertions.assertEquals(newTask, historyManager.getHistory().getFirst());
    }

    @Test
    void removeOneRecordFromSingletonList() {
        historyManager.add(newTask);
        Assertions.assertEquals(1, historyManager.getTaskNodeMap().size());
        historyManager.remove(newTask.getId());
        Assertions.assertEquals(0, historyManager.getTaskNodeMap().size());
    }

    @Test
    void removeRecordFromHeadOfHistory() {
        historyManager.add(newTask);
        historyManager.add(newTask2);
        historyManager.add(newTask3);

        historyManager.remove(newTask.getId());
        System.out.println(historyManager.getHistory());
        Assertions.assertTrue(historyManager.getHistory().contains(newTask2));
        Assertions.assertTrue(historyManager.getHistory().contains(newTask3));
        Assertions.assertFalse(historyManager.getHistory().contains(newTask));
    }
    @Test
    void removeRecordFromMidlOfHistory() {
        historyManager.add(newTask);
        historyManager.add(newTask2);
        historyManager.add(newTask3);

        historyManager.remove(newTask2.getId());
        System.out.println(historyManager.getHistory());
        Assertions.assertTrue(historyManager.getHistory().contains(newTask));
        Assertions.assertTrue(historyManager.getHistory().contains(newTask3));
        Assertions.assertFalse(historyManager.getHistory().contains(newTask2));
    }
    @Test
    void removeRecordFromEndOfHistory() {
        historyManager.add(newTask);
        historyManager.add(newTask2);
        historyManager.add(newTask3);

        historyManager.remove(newTask3.getId());
        System.out.println(historyManager.getHistory());
        Assertions.assertTrue(historyManager.getHistory().contains(newTask));
        Assertions.assertTrue(historyManager.getHistory().contains(newTask2));
        Assertions.assertFalse(historyManager.getHistory().contains(newTask3));
    }

}