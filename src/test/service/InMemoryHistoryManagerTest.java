package test.service;

import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import service.InMemoryHistoryManager;

class InMemoryHistoryManagerTest {
    private static InMemoryHistoryManager historyManager;
    private static Task newTask;

    @BeforeAll
    public static void setUp() {
        historyManager = new InMemoryHistoryManager();
        newTask = new Task("name", "description", TaskStatus.NEW);
    }

    @Test
    void add() {
        historyManager.add(newTask);
        Assertions.assertEquals(1, historyManager.getHistory().size());
    }

    @Test
    void getHistory() {
        Assertions.assertEquals(newTask, historyManager.getHistory().getFirst());
    }
}