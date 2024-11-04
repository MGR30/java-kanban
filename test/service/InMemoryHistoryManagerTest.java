package service;

import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.InMemoryHistoryManager;

class InMemoryHistoryManagerTest {
    private static InMemoryHistoryManager historyManager;
    private static Task newTask;
    private static Task newTask2;

    @BeforeEach
    public void setUp() {
        historyManager = new InMemoryHistoryManager();
        newTask = new Task("name", "description", TaskStatus.NEW);
        newTask2 = new Task("name", "description", TaskStatus.NEW);
        newTask2.setId(1L);
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
}