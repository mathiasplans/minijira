package common;

import com.google.gson.Gson;
import data.RawTask;
import org.jetbrains.annotations.NotNull;
import org.kohsuke.github.GitHub;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Class for containing and handling the tasks
 */
public class TaskContainer {
    final private List<Task> tasks;
    private static final Gson gson = new Gson();
    private String inpath;

    // Task creation
    private long order = 0;
    private final GitHub gitHub = null; // TODO
    /**
     * Default constructor. Initializes the list of the tasks
     */
    public TaskContainer() throws IOException{
        this.tasks = new ArrayList<>();
    }

    /**
     * Secondary constructor. Initializes the list of the tasks and then
     * fills it with tasks which are in a directory, pointed to be path.
     * The tasks in the directory have to be in JSON format
     * @param path Path to the directory of tasks
     * @throws IOException If file IO fails
     */
    public TaskContainer(String path) throws IOException {
        tasks = new ArrayList<>();
        importTasks(path);
    }

    /**
     * Same as secondary constructor, but instead of taking a stirng of the path as
     * the argument, a Path object is taken instead.
     * @param path Path object of the directory of tasks
     * @throws IOException If file IO failed
     */
    public TaskContainer(@NotNull Path path) throws IOException {
        tasks = new ArrayList<>();
        importTasks(path.toString());
    }

    /**
     * Imports thasks from given paths. The path
     * has to point to a directory/file. The tasks have to be in JSON
     * format.
     * @param path Path to the directory/file where the tasks are stored
     * @throws IOException If file IO fails
     */
    private void importTasks(String path) throws IOException {
        // Set the save path
        inpath = path;

        /* Fill the list */
        // File/Directory
        File dile = new File(path);

        // Check if file exists
        if(!dile.exists())
            throw new IllegalArgumentException("Given path does not point to neither file nor directory");

        // If the File object points at directory
        if (dile.isDirectory()){
            File[] files = dile.listFiles();

            if (files != null) {
                for (File file : files) {
                    newTask(gson.fromJson(Files.readString(file.toPath()), RawTask.class));
                }
            }

        }

        // If the File object points at file
        else if(dile.isFile()){
            List<String> lines = Files.readAllLines(dile.toPath(), StandardCharsets.UTF_8);
            for(String line: lines){
                if(!"".equals(line))
                    newTask(gson.fromJson(line, RawTask.class));
            }
        }

        /* Determine the order */
        // Get the biggest ID
        long biggestTaskId = -1;
        for(Task task: tasks){
            if(task.getTaskId() > biggestTaskId)
                biggestTaskId = task.getTaskId();
        }

        // Set new order. This ensures that old Task IDs don't get overwritten
        order = biggestTaskId + 1;
    }

    /**
     * Export stored tasks into a directory, specified by the path
     * @param path path to the save directory
     * @throws IOException If file IO fails
     */
    public void saveTasks(String path) throws IOException {
        File dile = new File(path);

        // Check if file exists
        if(!dile.exists())
            throw new IllegalArgumentException("Given path does not point to neither file nor directory");

        // If the File object points at directory
        if(dile.isDirectory()){
            for (Task task : tasks) {
                File newFile = new File(path, String.valueOf(task.getTaskId()));
                newFile.createNewFile();

                try {
                    Files.createFile(newFile.toPath());
                    Files.write(newFile.toPath(), gson.toJson(task.getRawTask(), RawTask.class).getBytes());
                } catch (FileAlreadyExistsException e) {
                    // ignore, if exists, overwrite
                }
            }
        }

        // If the File object points at file
        else if(dile.isFile()){
            StringBuilder builder = new StringBuilder();
            for(Task task: tasks){
                builder.append(gson.toJson(task, RawTask.class));
                builder.append("\n");
            }

            Files.write(dile.toPath(), builder.toString().getBytes());
        }
    }

    /**
     * Export stored tasks into directory. This variant can be called only if
     * tasks were originally imported. The tasks are saved into the directory whence
     * they were imported.
     * @throws IOException If file IO fails
     * @throws IllegalStateException If the tasks weren't originally imported.
     *                               TaskContainer doesn't know the path! Call saveUsers(String path) variant instead
     */
    public void saveTasks() throws IOException {
        if(inpath == null)
            throw new IllegalStateException(
                    "Can not save tasks: path to the save directory does not exist. Call saveUsers(String path) variant instead"
            );

        saveTasks(inpath);
    }

    /**
     * Method for adding a task to the container
     * @param task Task to be added to the container
     * @throws IllegalArgumentException If task with the given ID already exists
     */
    public void addTask(Task task) {
        // Check if task already exists!
        Task testTask = getById(task.getTaskId());
        if(testTask != null)
            throw new IllegalArgumentException("Task with given ID already exists");
        tasks.add(task);
    }

    /**
     * Method for removing a task from the container
     * @param task Task to be removed from the container
     */
    public void removeTask(Task task){
        tasks.remove(task);
    }

    /**
     * ID variant of the removeTask(Task task). Removes
     * the task with given ID.
     * @param id ID of the task to be removed
     */
    public void removeTask(long id){
        removeTask(getById(id));
    }

    /**
     * Method for creating a new task into the container
     * @param name name of the task
     * @return the created task
     */
    public Task newTask(String name){
        Task newTask = new Task(order++, name);
        tasks.add(newTask);
        return newTask;
    }

    /**
     * Method for creating a new task into the container
     * @param name name of the task
     * @param description description of the task
     * @param board board ID where this task belongs
     * @param deadline deadline of the task
     * @param author author of the task
     * @param priority priority of the task
     * @return the created task
     */
    public Task newTask(String name, String description, long board, long deadline, User author, int priority){
        Task newTask = new Task(order++, name, description, board, deadline, author, priority, gitHub);
        tasks.add(newTask);
        return newTask;
    }

    /**
     * Method for creating a new task into the container
     * @param task RawTask object, converted from JSON
     * @return the created task
     */
    public Task newTask(RawTask task){
        Task newTask = new Task(task, gitHub);
        tasks.add(newTask);
        return newTask;
    }

    /**
     * Updates the given task. Replaces it with the new version
     * @param task the new and better task
     * @return the created task
     */
    public Task updateTask(RawTask task){
        // Remove the previous version from the list
        tasks.remove(getById(task.taskId));

        // Replace it with the new version
        return newTask(task);
    }

    /**
     * Method for getting the list of the tasks
     * @return list of the tasks
     */
    public List<Task> getTasks(){
        return tasks;
    }

    /**
     * Board variant of the getTasks(). This method returns only the tasks
     * with given board ID
     * @param boardId which' boards tasks are to be returned
     * @return list of the tasks in given board
     */
    public List<Task> getTasks(long boardId){
        List<Task> newTasks = new ArrayList<>();

        for(Task task: tasks){
            for(long board: task.getBoards()){
                if(board == boardId){
                    newTasks.add(task);
                    break;
                }
            }
        }

        return newTasks;
    }

    /**
     * Method for acquiring a task with specific ID
     * @param id search key
     * @return the task with given ID. Is null when task with given ID does not exist
     */
    public Task getById(long id){
        for(Task task: tasks){
            if(task.getTaskId() == id){
                return task;
            }
        }

        return null;
    }
}
