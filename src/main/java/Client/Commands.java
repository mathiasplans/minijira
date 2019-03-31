package Client;

import Common.Task;
import Common.TaskContainer;

import java.util.List;

/**
 * This class will take user input (as a string) and then handles it
 */
public class Commands {
    final private TaskContainer container;

    public Commands(TaskContainer container) {
        this.container = container;
    }

    private void taskCommands(String[] tokens, int level){
        switch (tokens[level]){
            case "create":
                break;
            case "info":
                break;
            case "complete":
                break;
        }
    }

    private void printTask(long id){
        Task task = container.getById(id);
        // Temporary
        System.out.println("ID: " + task.getTaskId() +
                            "Title: " + task.getTitle() +
                            "Description: " + task.getDescription() +
                            "Reported " + task.getDateCreatedMS() +
                            " by " + task.getCreatedBy());
    }

    private void printTasks(List<Task> tasks){
        System.out.printf("%-30.30s\t%-30.30s", "ID", "Title");
        for(Task task: tasks){
            System.out.printf("%-30.30d\t%-30.30s", task.getTaskId(), task.getTitle());
        }
    }

    public void handle(String command) throws IllegalArgumentException {
        /**
         * Command structure
         * [area] [operation] [argument(s)]
         */

        // If string is empty
        if("".equals(command))
            throw new IllegalArgumentException();

        // Tokenize the command
        String[] tokens = command.split(" ");

        switch (tokens[0]){
            case "task":
                taskCommands(tokens, 1);
                break;

            case "board":
                switch (tokens[2]){
                    case "tasks":
                        taskCommands(tokens, 3);
                        break;
                    case "list":
                        printTasks(container.getTasks(Long.parseLong(tokens[1])));
                        break;
                }
                break;
        }
    }
}
