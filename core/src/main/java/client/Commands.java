package client;

import common.*;
import messages.ProtocolConnection;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

/**
 * This class will take user input (as a string) and then handles it
 */
class Commands {
    private final TaskContainer taskContainer;
    private final UserContainer userContainer;
    private final Boards boards;
    private final ProtocolConnection connection;
    private final Sync sync;
    private final ClientAuth auth;

    private boolean running = true;

    /**
     * Constructor, initializes task and user containers with given values
     * @param taskContainer TaskContainer object, is filled with tasks!
     * @param userContainer UserContainer object, is filled with users!
     */
    @Contract(pure = true)
    Commands(TaskContainer taskContainer, UserContainer userContainer, Boards boards, ProtocolConnection connection, Sync sync, ClientAuth auth) {
        this.taskContainer = taskContainer;
        this.userContainer = userContainer;
        this.boards = boards;
        this.connection = connection;
        this.sync = sync;
        this.auth = auth;
    }

    /**
     * Simple method for checking if user hasn't fired a quit signal
     * @return true if program needs to run still, false if program has to end
     */
    boolean isRunning(){
        return running;
    }

    private boolean checkArgumentLength(String scope, int length, int trueLength){
        if(length < trueLength){
            System.out.println(scope + ": Not enough arguments");
            return true;
        }

        if(length > trueLength){
            System.out.println(scope + ": Too many arguments");
            return true;
        }

        return false;
    }

    /**
     * Command parser for 'task set ___'
     * @param tokens parsed command split up
     * @param level level of the parse, which word is handled from the tokens
     * @exception IOException If communication with server fails
     */
    private void taskSet(@NotNull String[] tokens, int level) throws IOException {
        if(checkArgumentLength("Task Set", tokens.length, level + 3))
            return;

        // Get the task
        Task subject = taskContainer.getTask(Long.parseLong(tokens[level + 1]));

        // Check whether the task exists
        if(subject == null){
            System.out.println("Task with given ID does not exist");
            return;
        }

        // Handle next token
        switch (tokens[level]){
            case "title":
                // task set title <id> <title>
                subject.setTitle(tokens[level + 2]);
                break;

            case "description":
                StringBuilder builder = new StringBuilder();
                for(int i = level + 2; i < tokens.length; i++){
                    builder.append(tokens[i]);
                    builder.append(" ");
                }
                // task set description <id> <title>
                subject.setDescription(builder.toString());
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
                return;
        }

        // Update the subject in server
        sync.updateTask(subject);
    }

    /**
     * Command parser for 'task add ___'
     * @param tokens parsed command split up
     * @param level level of the parse, which word is handled from the tokens
     * @exception IOException If communication with server fails
     */
    private void taskAdd(@NotNull String[] tokens, int level) throws IOException {
        if(checkArgumentLength("Task Add", tokens.length, level + 3))
            return;

        // Get the task
        Task subject = taskContainer.getTask(Long.parseLong(tokens[level + 1]));

        // Check whether the task exists
        if(subject == null){
            System.out.println("Task Add: Task with given ID does not exist");
            return;
        }

        // Handle the next token
        switch (tokens[level]){
            case "board":
                // task add board <id> <board id>
                subject.addBoard(Long.parseLong(tokens[level + 2]));
                break;

            case "assignee":
                // task add assignee <id> <user id>
                subject.addAssignee(userContainer.getUser(Long.parseLong(tokens[level + 2])));
                break;

            default:
                System.out.println("Task Add: Command does not exist");
                return;
        }

        // Update the task in server
        sync.updateTask(subject);
    }

    /**
     * Command parser for 'task ___'
     * @param tokens parsed command split up
     * @param level level of the parse, which word is handled from the tokens
     */
    private void taskCommands(@NotNull String[] tokens, int level) throws IOException {
        /* Commands with no arguments */
        switch (tokens[level]){
            case "list":
                printBrief(taskContainer.getTasks());
                return;
        }

        /* commands with arguments */
        if(checkArgumentLength("Task", tokens.length, level + 2))
            return;

        // Update the task which is queried
        switch (tokens[level]) {
            case "create":
                Task createTask = new Task(-1, tokens[level + 1]);
                sync.createTask(createTask);
                break;
            case "info":
                Task infoTask = taskContainer.getTask(Long.parseLong(tokens[level + 1]));
                if(infoTask != null)
                    System.out.println(infoTask.toString());
                else
                    System.out.println("Task with this ID does not exist");
                break;
            case "complete":
                Task completeTask = taskContainer.getTask(Long.parseLong(tokens[level + 1]));
                if(completeTask != null) {
                    // Set completion status
                    completeTask.complete();

                    // Update the task in server
                    sync.updateTask(completeTask);
                }
                else
                    System.out.println("Task with this ID does not exist");
                break;
            case "set":
                taskSet(tokens, level + 1);
                break;

            case "add":
                taskAdd(tokens, level + 1);
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
     * Print a brief task overview with Task ID and Task's title
     * @param tasks what tasks are to be printed out
     */
    private void printBrief(@NotNull List<Task> tasks){
        for(Task task: tasks){
            System.out.printf(" %4d %s\n", task.getId(), task.getTitle());
        }
    }

    /**
     * Method for printing out the manual of the program
     */
    private void printManual() throws IOException {
        InputStream fin = this.getClass().getClassLoader().getResourceAsStream("manual");
        if(fin != null){
            byte[] file = fin.readAllBytes();
            System.out.println(new String(file, StandardCharsets.UTF_8));
        }else{
            throw new FileNotFoundException("Manual not found");
        }
    }

    /**
     * Command parser for 'board ___'
     * @param tokens parsed command split up
     * @param level level of the parse, which word is handled from the tokens
     */
    private void boardCommands(@NotNull String[] tokens, int level) throws IOException {
        switch (tokens[level]) {
            case "create":
                if(checkArgumentLength("Board", tokens.length, level + 3))
                    return;
                long createID = Long.parseLong(tokens[level + 1]);
                String createName = tokens[level + 2];
                boards.registerBoard(createID, createName);
                sync.createBoard(createID, createName);
                break;

            case "add":
                if(checkArgumentLength("Board", tokens.length, level + 3))
                    return;
                long addTaskID = Long.parseLong(tokens[level + 1]);
                long addBoardID = Long.parseLong(tokens[level + 2]);
                Task addTask = taskContainer.getTask(addTaskID);
                addTask.addBoard(addBoardID);
                sync.updateTask(addTask);
                break;

            case "pull":
                if(tokens.length == level + 2) {
                    long pullID = Long.parseLong(tokens[level + 1]);
                    sync.getBoardTasks(pullID);
                }else{
                    sync.getBoards();
                    sync.getTasks(boards.getIDSet());
                }
                break;

            case "list":
                Set<Long> boardList = boards.getIDSet();
                for(long boardID: boardList){
                    System.out.printf(" %4d %s\n", boardID, boards.getBoardName(boardID));
                }
                break;

            default:
                System.out.println("Board: Command does not exist");
        }
    }

    private void userCommands(@NotNull String[] tokens, int level) throws IOException {
        switch (tokens[level]) {
            case "login":
                checkArgumentLength("User", tokens.length, level + 3);
                String username = tokens[level + 1];
                String password = tokens[level + 2];
                auth.loginRequest(username, password);
                break;

            case "register":
                checkArgumentLength("User", tokens.length, level + 3);
                String newUserName = tokens[level + 1];
                String newUserPassword = tokens[level + 2];
                auth.registerRequest(newUserName, newUserPassword);
                break;

            case "setEmail":
                checkArgumentLength("User", tokens.length, level+2);
                String email = tokens[level + 1];
                sync.addEmail(email);
                break;

            case "setPermissions":
                checkArgumentLength("User", tokens.length, level+4);
                String affectedUserName = tokens[level + 2];
                long projectID = Long.parseLong(tokens[level + 3]);
                Permissions permission = Permissions.NORIGHT;

                switch(tokens[level + 4]){
                    case "noright":
                        permission = Permissions.NORIGHT;
                        break;
                    case "see":
                        permission = Permissions.SEE;
                        break;
                    case "create":
                        permission = Permissions.CREATE;
                        break;
                    case "complete":
                        permission = Permissions.COMPLETE;
                        break;
                    case "all":
                        permission = Permissions.ALL;
                        break;
                }
                sync.setProjectPermission(projectID, permission, affectedUserName);
                break;

            default:
                System.out.println("User: Command does not exist");
        }
    }

    /**
     * The root of the parser.
     * @param scin scanner object to parse commands
     * @throws IllegalArgumentException If given command is empty
     * @throws IOException If IO fails
     */
    void handle(@NotNull Scanner scin) throws IOException {
        /*
         * Command structure
         * [area] [operation] [argument(s)]
         */
        String command = scin.nextLine();

        // If string is empty, ignore
        if(command.isBlank())
            return;

        // Tokenize the command
        String[] tokens = command.split(" ");

        switch (tokens[0]){
            case "quit":
                running = false;
                break;

            case "man":
                printManual();
                break;

            case "task":
                taskCommands(tokens, 1);
                break;

            case "board":
                boardCommands(tokens, 1);
                break;

            case "save":
                taskContainer.saveTasks();
                userContainer.saveUsers();
                boards.saveBoards();
                break;

            case "pull":
                sync.getTasks(boards.getIDSet());
                break;

            case "user":
                userCommands(tokens, 1);
                break;

            default:
                System.out.println("Command does not exist: root");
        }
    }
}
