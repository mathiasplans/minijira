package common;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import data.RawTask;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Class for containing and handling the tasks
 */
public class TaskContainer {
    final private List<Task> tasks;
    private static final Gson gson = new Gson();
    private Path inpath;

    private final ContainerHelper<Task> container;

    // Task creation
    private long order = 0;

    /**
     * Default constructor. Initializes the list of the tasks
     */
    public TaskContainer() throws IOException{
        this.tasks = new ArrayList<>();
        container = new ContainerHelper<>(tasks);
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
        container = new ContainerHelper<>(tasks);
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
        container = new ContainerHelper<>(tasks);
        importTasks(path);
    }

    /**
     * Imports thasks from given paths. The path
     * has to point to a directory/file. The tasks have to be in JSON
     * format.
     * @param path Path to the directory/file where the tasks are stored
     * @throws IOException If file IO fails
     */
    private void importTasks(String path) throws IOException {
        importTasks(Paths.get(path));
    }

    /**
     * Imports thasks from given paths. The path
     * has to point to a directory/file. The tasks have to be in JSON
     * format.
     * @param path Path to the directory/file where the tasks are stored
     * @throws IOException If file IO fails
     */
    private void importTasks(Path path) throws IOException {
        /* Fill the list */
        try{
            container.importItems(path, json -> new Task(gson.fromJson(json, RawTask.class)));
        }catch(JsonSyntaxException e){
            System.out.println("Failed to import tasks");
        }
        // Set the save path
        inpath = path;
    }

    /**
     * Export stored tasks into a directory, specified by the path
     * @param path path to the save directory
     * @throws IOException If file IO fails
     */
    public void saveTasks(Path path) throws IOException {
        container.exportItems(path, task -> gson.toJson(task.getRawTask(), RawTask.class));
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
        Task testTask = getTask(task.getId());
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
        removeTask(getTask(id));
    }

    /**
     * Method for creating a new task into the container
     * @param name name of the task
     * @return the created task
     */
    public Task newTask(long id, String name){
        Task newTask = new Task(id, name);
        tasks.add(newTask);
        return newTask;
    }

    /**
     * Method for creating a new task into the container
     * @param id task ID
     * @param name name of the task
     * @param description description of the task
     * @param board board ID where this task belongs
     * @param deadline deadline of the task
     * @param author author of the task
     * @param priority priority of the task
     * @return the created task
     */
    public Task newTask(long id, String name, String description, long board, long deadline, User author, int priority){
        Task newTask = new Task(id, name, description, board, deadline, author, priority);
        tasks.add(newTask);
        return newTask;
    }

    /**
     * Method for creating a new task into the container
     * @param task RawTask object, converted from JSON
     * @return the created task
     */
    public Task newTask(RawTask task){
        Task newTask = new Task(task);
        tasks.add(newTask);
        return newTask;
    }

    /**
     * Updates the given task. Replaces it with the new version
     * @param task the new and better task
     * @return the created task
     */
    public Task updateTask(RawTask task){
        // Remove the previous version from the list if it exists
        if(tasks.indexOf(getTask(task.taskId)) != -1)
            tasks.remove(getTask(task.taskId));

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
    public Task getTask(long id){
        for(Task task: tasks){
            if(task.getId() == id){
                return task;
            }
        }

        return null;
    }
}
