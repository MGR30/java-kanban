package util;

import service.*;

import java.io.File;

public class Managers {

    public static TaskManager getDefault() {
        return new InMemoryTaskManager(getDefaultHistory());
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static TaskManager getFileBackendTaskManager() {
        File file = new File("file.csv");
        return FileBackedTaskManager.loadFromFile(file);
    }
}
