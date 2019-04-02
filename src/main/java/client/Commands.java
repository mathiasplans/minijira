package client;

import common.Task;
import common.TaskContainer;

import java.io.IOException;
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
        try {
            switch (tokens[level]) {
                case "create":
                    // Kui argumendiks on antud ainult nimi
                    container.addTask(new Task(tokens[level + 1]));
                    break;
                case "info":
                    printTask(Long.parseLong(tokens[level + 1]));
                    break;
                case "complete":
                    container.getById(Long.parseLong(tokens[level + 1])).complete();
                    break;
                default:
                    System.out.println("Command does not exist");
            }
        }catch (ArrayIndexOutOfBoundsException e){
            System.out.println("Error: Command called without giving arguments");
        }catch (IllegalArgumentException e){
            System.out.println(e.getMessage());
        }
    }

    private void printTask(long id) throws IllegalArgumentException {
        Task task = container.getById(id);

        if(task == null)
            throw new IllegalArgumentException("Task with given ID does not exist!");

        // Temporary
        System.out.println("ID: " + task.getTaskId() + "\n" +
                "Title: " + task.getTitle() + "\n" +
                "Description: " + task.getDescription() + "\n" +
                "Reported " + task.getDateCreatedMS() +
                " by " + task.getCreatedBy() + "\n" +
                "Completed: " + task.isCompleted());
    }

    private void printTasks(List<Task> tasks){
        System.out.printf("%-30.30s\t%-30.30s", "ID", "Title");
        for(Task task: tasks){
            System.out.printf("%-30.30d\t%-30.30s", task.getTaskId(), task.getTitle());
        }
    }

    public void handle(String command) throws IllegalArgumentException, IOException {
        /*
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

            case "save":
                container.saveTasks();
                break;

            default:
                System.out.println("Command does not exist");
        }
    }
}
