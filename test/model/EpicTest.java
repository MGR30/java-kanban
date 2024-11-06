package model;

import model.Epic;
import model.TaskStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EpicTest {

    @Test
    public void epicWithSameIdsAreSame() {
        Epic epic = new Epic("name", "description", TaskStatus.NEW);
        Epic otherEpic = new Epic("name", "description", TaskStatus.NEW);
        epic.setId(1L);
        otherEpic.setId(1L);

        assertEquals(epic, otherEpic);
    }
}