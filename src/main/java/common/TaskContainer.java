package common;

import com.google.gson.Gson;
import data.RawTask;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class TaskContainer {
    final private List<Task> tasks;
    static final Gson gson = new Gson();
    private String inpath;

    public TaskContainer() {
        this.tasks = new ArrayList<>();
    }

    public TaskContainer(String path) throws IOException {
        inpath = path;
        tasks = new ArrayList<>();

        File directory = new File(path);

        File[] files = directory.listFiles();

        for(File file: files){
            tasks.add(new Task(gson.fromJson(Files.readString(file.toPath()), RawTask.class), null /* for now */));
        }
    }

    public TaskContainer(Path path) throws IOException {
        inpath = path.toString();
        tasks = new ArrayList<>();

        File directory = new File(path.toString());

        File[] files = directory.listFiles();

        for(File file: files){
            tasks.add(new Task(gson.fromJson(Files.readString(file.toPath()), RawTask.class), null /* for now */));
        }
    }

    public void saveTasks() throws IOException {
        for(Task task: tasks){
            File newFile = new File(inpath, String.valueOf(task.getTaskId()));
            newFile.createNewFile();

            try {
                Files.createFile(newFile.toPath());
                Files.write(newFile.toPath(), gson.toJson(task.getRawTask(), RawTask.class).getBytes());
            }catch(FileAlreadyExistsException e){
                // ignore, if exists, overwrite
            }
        }
    }

    public void saveTasks(String path) throws IOException {
        for(Task task: tasks){
            File newFile = new File(path + "/" + task.getTaskId());
            newFile.createNewFile();

            Files.write(newFile.toPath(), gson.toJson(task.getRawTask(), RawTask.class).getBytes());
        }
    }

    public void addTask(Task task){
        tasks.add(task);
    }

    public void removeTask(Task task){
        tasks.remove(task);
    }

    public List<Task> getTasks(){
        return tasks;
    }

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

    public Task getById(long id){
        for(Task task: tasks){
            if(task.getTaskId() == id){
                return task;
            }
        }

        return null;
    }
}
