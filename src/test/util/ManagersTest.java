package test.util;

import org.junit.jupiter.api.Test;
import service.HistoryManager;
import service.TaskManager;
import util.Managers;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {

    @Test
    void getDefault() {
        TaskManager taskManager = Managers.getDefault();
        assertNotNull(taskManager);
    }

    @Test
    void getDefaultHistory() {
        HistoryManager defaultHistory = Managers.getDefaultHistory();
        assertNotNull(defaultHistory);
    }
}