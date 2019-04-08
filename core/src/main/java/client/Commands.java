package client;

import common.Task;
import common.TaskContainer;
import common.UserContainer;

import java.io.IOException;
import java.util.List;

/**
 * This class will take user input (as a string) and then handles it
 */
public class Commands {
    final private TaskContainer taskContainer;
    final private UserContainer userContainer;

    public Commands(TaskContainer taskContainer, UserContainer userContainer) {
        this.taskContainer = taskContainer;
        this.userContainer = userContainer;
    }

    private void taskSet(String[] tokens, int level) throws ArrayIndexOutOfBoundsException {
        Task subject = taskContainer.getById(Long.parseLong(tokens[level + 1]));
        switch (tokens[level]){
            case "title":
                // task set title <id> <title>
                subject.setTitle(tokens[level + 2]);
                break;

            case "description":
                // task set description <id> <title>
                subject.setDescription(tokens[level + 2]);
                break;

            case "deadline":
                // task set deadline <id> <deadline>
                // TODO: not in MS format
                subject.setDeadlineMS(Long.parseLong(tokens[level + 2]));
                break;

            case "priority":
                // task set priority <id> <priority>
                subject.setPriority(Integer.parseInt(tokens[level + 2]));
                break;

            case "mastertask":
                // task set mastertask <id> <mastertask id>
                subject.setMasterTaskId(Long.parseLong(tokens[level + 2]));
                break;

            default:
                System.out.println("Command does not exist: taskSet");
        }
    }

    private void taskAdd(String[] tokens, int level) throws ArrayIndexOutOfBoundsException {
        Task subject = taskContainer.getById(Long.parseLong(tokens[level + 1]));
        switch (tokens[level]){
            case "board":
                // task add board <id> <board id>
                subject.addBoard(Long.parseLong(tokens[level + 2]));
                break;

            case "assignee":
                // task add assignee <id> <user id>
                subject.addAssignee(userContainer.getById(Long.parseLong(tokens[level + 2])));
                break;

            default:
                System.out.println("Command does not exist: taskAdd");
        }
    }

    private void taskCommands(String[] tokens, int level){
        try {
            switch (tokens[level]) {
                case "create":
                    // Kui argumendiks on antud ainult nimi
                    taskContainer.addTask(new Task(tokens[level + 1]));
                    break;
                case "info":
                    System.out.println(taskContainer.getById(Long.parseLong(tokens[level + 1])).toString());
                    break;
                case "complete":
                    taskContainer.getById(Long.parseLong(tokens[level + 1])).complete();
                    break;
                case "set":
                    taskSet(tokens, level + 1);
                default:
                    System.out.println("Command does not exist: task");
            }
        }catch (ArrayIndexOutOfBoundsException e){
            System.out.println("Error: Command called without giving arguments");
        }catch (IllegalArgumentException e){
            System.out.println(e.getMessage());
        }
    }

    private void printTasks(List<Task> tasks){
        for(Task task: tasks){
            System.out.println(task.toString());
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

        // Debug: print out command
        for(String token: tokens){
            System.out.print(token + " ");
        }
        System.out.println();

        switch (tokens[0]){
            case "task":
                taskCommands(tokens, 1);
                break;

            case "board":
                switch (tokens[2]){
                    case "src/test/resources/tasks":
                        taskCommands(tokens, 3);
                        break;
                    case "list":
                        printTasks(taskContainer.getTasks(Long.parseLong(tokens[1])));
                        break;
                }
                break;

            case "save":
                taskContainer.saveTasks();
                break;

            default:
                System.out.println("Command does not exist: root");
        }
    }
}
