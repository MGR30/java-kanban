package service;

import model.Task;
import util.TaskNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private final ModifiedLinkedList viewHistory;

    public Map<Long, TaskNode> getTaskNodeMap() {
        return taskNodeMap;
    }

    private final Map<Long, TaskNode> taskNodeMap;

    public InMemoryHistoryManager() {
        viewHistory = new ModifiedLinkedList();
        taskNodeMap = new HashMap<>();
    }

    @Override
    public void add(Task task) {
        if (task != null) {
            if (taskNodeMap.containsKey(task.getId())) {
                removeNode(task.getId());
            }
            taskNodeMap.put(task.getId(), viewHistory.linkLast(task));
        }
    }

    @Override
    public List<Task> getHistory() {
        return viewHistory.getTasks();
    }

    @Override
    public void remove(Long id) {
        removeNode(id);
        taskNodeMap.remove(id);
    }

    public void removeNode(Long id) {
        TaskNode node = taskNodeMap.get(id);
        if (node != null) {
            TaskNode prevNode = node.getPrev();
            TaskNode nextNode = node.getNext();
            if (viewHistory.first != null && viewHistory.first.equals(node)) {
                viewHistory.first = nextNode;
            }

            if (prevNode != null) {
                prevNode.setNext(nextNode);
            }
            if (nextNode != null) {
                nextNode.setPrev(prevNode);
            }
        }
    }

    public static class ModifiedLinkedList {
        TaskNode first;
        TaskNode last;

        public TaskNode linkLast(Task task) {
            final TaskNode l = last;
            final TaskNode newNode = new TaskNode(l, task, null);
            last = newNode;
            if (first == null || l == null) {
                first = newNode;
            } else {
                l.setNext(newNode);
            }

            return newNode;
        }

        public ArrayList<Task> getTasks() {
            ArrayList<Task> tasks = new ArrayList<>();
            TaskNode node = first;
            while (true) {
                if (node == null) {
                    break;
                }
                if (node.getNext() == null) {
                    tasks.add(node.getTask());
                    break;
                }
                tasks.add(node.getTask());
                node = node.getNext();
            }
            return tasks;
        }
    }
}
