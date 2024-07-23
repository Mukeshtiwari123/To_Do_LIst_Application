package To_Do_List;

import java.util.ArrayList;
import java.util.List;

public class ToDoList {
    private List<Task> tasks;

    public ToDoList() {
        tasks = new ArrayList<>();
    }

    public void addTask(Task task) {
        tasks.add(task);
    }

    public void markTaskAsComplete(int index) {
        tasks.get(index).setComplete(true);
    }

    public void removeTask(int index) {
        tasks.remove(index);
    }

    public List<Task> getTasks() {
        return tasks;
    }
}
