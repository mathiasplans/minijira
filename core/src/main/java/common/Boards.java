package common;

import data.RawProject;
import data.RawProjectNameList;
import data.RawTask;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Boards {
    private final Map<Long, String> boardNames = new HashMap<>();
    private final TaskContainer tasks;
    private String boardpath;

    /**
     * Main constructor. Initializes the TaskContainer
     * @param tasks container of tasks
     */
    public Boards(TaskContainer tasks){
        this.tasks = tasks;
    }

    /**
     * Secondary constructor. Initializes the TaskContainer and imports
     * board information from path, which points to .csv file with first
     * column being board's ID and second it's name
     * @param tasks container of tasks
     * @param path path to the .csv file
     * @throws IOException If file IO fails
     */
    public Boards(TaskContainer tasks, Path path) throws IOException {
        this.tasks = tasks;
        importBoards(path);
    }

    /**
     * Secondary constructor. Initializes the TaskContainer and imports
     * board information from path, which points to .csv file with first
     * column being board's ID and second it's name
     * @param tasks container of tasks
     * @param path path to the .csv file
     * @throws IOException If file IO fails
     */
    public Boards(TaskContainer tasks, String path) throws IOException {
        this.tasks = tasks;
        importBoards(path);
    }

    /**
     * Method for setting the names of boards. Input path has to point at a
     * .csv file with first column being the board's ID and second it's name
     * @param path path to the .csv file
     */
    public void importBoards(String path) throws IOException {
        /* Fill the list */
        // File/Directory
        File file = new File(path);

        // Check if file exists and
        // if the File object points at file
        if(file.exists() && file.isFile()){
            List<String> lines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);

            for(String line: lines) {
                if (!"".equals(line))
                    boardNames.put(Long.parseLong(line.split(",")[0].replaceAll("[^0-9]", "")), line.split(" ")[1]);
            }
        }

        // If file does not exist or it's not a file
        else
            throw new IllegalArgumentException("Given path does not point to a file");

        // Set the inport path
        boardpath = path;
    }

    /**
     * Method for setting the names of boards. Input path has to point at a
     * .csv file with first column being the board's ID and second it's name
     * @param path path to the .csv file
     */
    public void importBoards(Path path) throws IOException {
        importBoards(path.toString());
    }

    /**
     * Method for saving board names to the file whence they were imported
     * @throws IOException If file IO fails
     */
    public void saveBoards() throws IOException {
        // StringBuilder object for building the contets of the CSV file
        StringBuilder builder = new StringBuilder();

        // Object of the file
        File file = new File(boardpath);

        // Build the contents of the file
        for(long boardID: boardNames.keySet()){
            builder.append(boardID);
            builder.append(",");
            builder.append(boardNames.get(boardID));
            builder.append("\n");
        }

        // Get the contents of the file
        String fileLines = builder.toString();

        // Write it to file
        Files.write(file.toPath(), fileLines.getBytes());
    }

    /**
     * Save the board ID and name. Can also be used for renaming boards
     * @param id ID of the board
     * @param name name of the board
     */
    public void registerBoard(long id, String name){
        boardNames.put(id, name);
    }

    /**
     * Save the board ID and name. Can also be used for renaming boards
     * @param boards directory of board IDs and board Names
     */
    public void registerBoard(Map<Long, String> boards){
        for(long boardID: boards.keySet())
            boardNames.put(boardID, boards.get(boardID));
    }

    /**
     * Save the board ID and name. Can also be used for renaming boards
     * @param projectNames RawProjectNameList object
     */
    public void registerBoard(RawProjectNameList projectNames){
        for(int i = 0; i < projectNames.projectIds.length; i++){
            boardNames.put(projectNames.projectIds[i], projectNames.projectNames[i]);
        }
    }

    /**
     * Save the board ID and name. Can also be used for renaming boards.
     * This variant also updates all the tasks referenced in RawProject object
     * @param project RawProject object
     */
    public void registerBoard(RawProject project){
        // Register the board
        boardNames.put(project.projectId, project.projectName);

        // Update the tasks
        for(RawTask task: project.tasks){
            tasks.updateTask(task);
        }
    }

    /**
     * Remove the board from existance
     * @param id
     */
    public void removeBoard(long id){
        // Remove from map
        boardNames.remove(id);

        // Remove from tasks themselves
        List<Task> taskList = tasks.getTasks(id);
        if(taskList != null)
            for(Task task: taskList)
                task.removeBoard(id);
    }

    /**
     * Mehtid for getting the name of a board
     * @param id ID of the baord which' name is needed
     * @return name of the baord which' ID was given
     */
    public String getBoardName(long id){
        return boardNames.get(id);
    }

    /**
     * Method for constructing RawProjectNameList object from this class
     * @return RawProjectNameList object
     */
    public RawProjectNameList getRawProjectNameList(){
        int listLength = boardNames.size();
        RawProjectNameList out = new RawProjectNameList(new String[listLength], new long[listLength]);
        int index = 0;
        for(long boardID: boardNames.keySet()){
            out.projectIds[index] = boardID;
            out.projectNames[index] = boardNames.get(boardID);
            index++;
        }

        return out;
    }

    /**
     * Method for constructiong RawProject object from this class
     * @param id ID of the project which will be converted to RawProject
     * @return RawProject object
     * @return RawProject object
     */
    public RawProject getRawProject(long id){
        // Get the list of tasks in board
        List<Task> boardTasks = tasks.getTasks(id);

        // Fill the array of tasks
        RawTask[] rawTasks = new RawTask[boardTasks.size()];
        for(int i = 0; i < boardTasks.size(); i++){
            rawTasks[i] = boardTasks.get(i).getRawTask();
        }

        return new RawProject(id, rawTasks, boardNames.get(id), "");
    }
}
