package service;

import model.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    public Map<Long, Node> getTaskNodeMap() {
        return taskNodeMap;
    }

    private final Map<Long, Node> taskNodeMap;
    private Node firstNodeInMemory;
    private Node lastNodeInMemory;

    public InMemoryHistoryManager() {
        taskNodeMap = new HashMap<>();
    }

    @Override
    public void add(Task task) {
        if (task != null) {
            if (taskNodeMap.containsKey(task.getId())) {
                removeNode(task.getId());
            }
            taskNodeMap.put(task.getId(), linkLast(task));
        }
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    @Override
    public void remove(Long id) {
        removeNode(id);
        taskNodeMap.remove(id);
    }

    private void removeNode(Long id) {
        Node node = taskNodeMap.get(id);
        if (node != null) {
            Node prevNode = node.getPrev();
            Node nextNode = node.getNext();
            if (firstNodeInMemory != null && firstNodeInMemory.equals(node)) {
                firstNodeInMemory = nextNode;
            }

            if (prevNode != null) {
                prevNode.setNext(nextNode);
            }
            if (nextNode != null) {
                nextNode.setPrev(prevNode);
            }
        }
    }


    private Node linkLast(Task task) {
        final Node l = lastNodeInMemory;
        final Node newNode = new Node(l, task, null);
        lastNodeInMemory = newNode;
        if (firstNodeInMemory == null || l == null) {
            firstNodeInMemory = newNode;
        } else {
            l.setNext(newNode);
        }

        return newNode;
    }

    private ArrayList<Task> getTasks() {
        ArrayList<Task> tasks = new ArrayList<>();
        Node node = firstNodeInMemory;
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

    public static class Node {
        private Task task;
        private Node next;
        private Node prev;

        public Node(Node prev, Task task, Node next) {
            this.task = task;
            this.next = next;
            this.prev = prev;
        }

        public Task getTask() {
            return task;
        }

        public void setTask(Task task) {
            this.task = task;
        }

        public Node getNext() {
            return next;
        }

        public void setNext(Node next) {
            this.next = next;
        }

        public Node getPrev() {
            return prev;
        }

        public void setPrev(Node prev) {
            this.prev = prev;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Node node = (Node) o;
            return Objects.equals(task, node.task) && Objects.equals(next, node.next) && Objects.equals(prev, node.prev);
        }

        @Override
        public int hashCode() {
            return Objects.hash(task, next, prev);
        }
    }
}
