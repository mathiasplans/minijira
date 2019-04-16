package client;

import common.Task;
import common.TaskContainer;
import common.UserContainer;
import messages.JiraMessageHandler;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

/**
 * This class will take user input (as a string) and then handles it
 */
class Commands {
    final private TaskContainer taskContainer;
    final private UserContainer userContainer;

    /**
     * Constructor, initializes task and user containers with given values
     * @param taskContainer TaskContainer object, is filled with tasks!
     * @param userContainer UserContainer object, is filled with users!
     */
    public Commands(TaskContainer taskContainer, UserContainer userContainer) {
        this.taskContainer = taskContainer;
        this.userContainer = userContainer;
    }

    /**
     * Command parser for 'task set ___'
     * @param tokens parsed command split up
     * @param level level of the parse, which word is handled from the tokens
     */
    private void taskSet(@NotNull String[] tokens, int level) {
        if(tokens.length < level + 3) {
            System.out.println("Task Set: Not enough arguments");
            return;
        }
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
                System.out.println("Task Set: Command does not exist");
        }
    }

    /**
     * Command parser for 'task add ___'
     * @param tokens parsed command split up
     * @param level level of the parse, which word is handled from the tokens
     */
    private void taskAdd(@NotNull String[] tokens, int level) {
        if(tokens.length < level + 3){
            System.out.println("Task Add: Not enough arguments");
            return;
        }

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
                System.out.println("Task Add: Command does not exist");
        }
    }

    /**
     * Command parser for 'task ___'
     * @param tokens parsed command split up
     * @param level level of the parse, which word is handled from the tokens
     */
    private void taskCommands(@NotNull String[] tokens, int level){
        if(tokens.length < level + 2){
            System.out.println("Task: Not enough arguments");
            return;
        }

        // Update the task which is queried
        switch (tokens[level]) {
            case "create":
                // Kui argumendiks on antud ainult nimi
                taskContainer.newTask(tokens[level + 1]);
                break;
            case "info":
                Task infoTask = taskContainer.getById(Long.parseLong(tokens[level + 1]));
                if(infoTask != null)
                    System.out.println(infoTask.toString());
                else
                    System.out.println("Task with this ID does not exist");
                break;
            case "complete":
                Task completeTask = taskContainer.getById(Long.parseLong(tokens[level + 1]));
                if(completeTask != null)
                    completeTask.complete();
                else
                    System.out.println("Task with this ID does not exist");
                break;
            case "set":
                taskSet(tokens, level + 1);
                break;
            default:
                System.out.println("Task: Command does not exist");
        }
    }

    /**
     * Command for printing out multiple tasks
     * @param tasks list of tasks to be prited out
     */
    private void printTasks(@NotNull List<Task> tasks){
        for(Task task: tasks){
            System.out.println(task.toString());
        }
    }

    /**
     * Method for printing out the manual of the program
     * // TODO: manual is empty at the moment
     */
    private void printManual(){
        System.out.println("manual");
    }

    /**
     * The root of the parser.
     * @param command command to be parsed (command line input)
     * @throws IllegalArgumentException If given command is empty
     * @throws IOException If IO fails
     */
    public void handle(@NotNull String command) throws IOException {
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
            case "quit":
                // TODO: "Do you want to save?" dialog
                System.exit(0);
                break;
            case "man":
                printManual();
                break;
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
