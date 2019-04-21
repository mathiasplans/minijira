package server;

import common.Task;
import common.TaskContainer;
import org.jetbrains.annotations.NotNull;

public class Order {
    private final TaskContainer tasks;
    private long order = 0;

    public Order(@NotNull TaskContainer tasks) {
        this.tasks = tasks;

        /* Determine the order */
        // Get the biggest ID
        long biggestTaskId = -1;
        for(Task task: tasks.getTasks()){
            if(task.getId() > biggestTaskId)
                biggestTaskId = task.getId();
        }

        // Set new order. This ensures that old Task IDs don't get overwritten
        order = biggestTaskId + 1;
    }

    synchronized public long getID(){
        return order++;
    }
}
