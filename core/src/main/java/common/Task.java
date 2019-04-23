package common;

import data.RawTask;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;

/**
 * Class which defines the task
 */
public class Task {
    private final Map<Git, Set<String>> gitMap;
    private final long taskId;
    private boolean isCompleted = false;
    private String title;
    private String description;
    private int priority;
    private final User createdBy;
    private long deadlineMS;
    private final long dateCreatedMS;
    private long masterTaskId;
    private final Set<User> assignedEmployees = new HashSet<>();
    private final Set<Long> boards = new HashSet<>();

    /**
     * Main constructor. Creates a task with given name and ID
     * @param id ID of the task
     * @param name name of the task
     */
    public Task(long id, String name){
        this.title = name;
        dateCreatedMS = System.currentTimeMillis();
        gitMap = new HashMap<>();
        this.taskId = id;
        createdBy = null;
    }

    /**
     * Secondary constructor. Initializes all the fields
     * @param id ID of the task
     * @param name name of the task
     * @param description description of the task
     * @param board board where the task belongs
     * @param deadline deadline of the task, in MS form 1970
     * @param author author of the task
     * @param priority priority of the task
     */
    public Task(long id, String name, String description, long board, long deadline, User author, int priority){
        this.title = name;
        this.description = description;

        this.taskId = id;

        boards.add(board);

        createdBy = author;
        this.priority = priority;

        // Time
        dateCreatedMS = System.currentTimeMillis();
        deadlineMS = deadline;

        // GitHub integration
        gitMap = new HashMap<>();

    }

    /**
     * Secondary constructor. Constructs the task from RawTask.
     * @param source RawTask object
     */
    public Task(RawTask source){
        // GitHub integration
        gitMap = new HashMap<>();

        if(source != null) {
            createdBy = new User("temp", source.createdBy, new byte[]{}, new byte[]{});
            dateCreatedMS = source.dateCreatedMS;
            deadlineMS = source.deadlineMS;
            description = source.description;
            isCompleted = source.isCompleted;
            masterTaskId = source.masterTaskId;
            title = source.title;
            taskId = source.taskId;
            priority = source.priority;

            for (Long id : source.assignedEmployees) {
                assignedEmployees.add(new User("temp", id, new byte[]{}, new byte[]{}));
            }

            for (Long board : source.boards) {
                boards.add(board);
            }
        }else{
            dateCreatedMS = -1;
            taskId = -1;
            createdBy = null;
        }
    }

    /**
     * Method for converting the Task object into RawTask
     * @return RawTask object
     */
    public RawTask getRawTask(){
        RawTask out = new RawTask(
                taskId,
                isCompleted,
                title,
                description,
                priority, -1L,
                deadlineMS,
                dateCreatedMS,
                masterTaskId,
                new long[]{-1L},
                new long[]{-1L});

        if(createdBy != null)
            out.createdBy = createdBy.getId();

        out.assignedEmployees = new long[assignedEmployees.size()];
        List<User> assigneeList = new ArrayList<>(assignedEmployees);

        for (int i = 0; i < out.assignedEmployees.length; i++) {
            out.assignedEmployees[i] = assigneeList.get(i).getId();
        }

        out.boards = new long[boards.size()];
        List<Long> boardList = new ArrayList<>(boards);

        for (int i = 0; i < out.boards.length; i++) {
            out.boards[i] = boardList.get(i);
        }

        return out;
    }

    /**
     * Method for completing the task
     */
    public void complete(){
        isCompleted = true;
    }

    /**
     * Method for adding the task to a board
     * @param board ID of the board where the task is added
     */
    public void addBoard(long board){
        boards.add(board);
    }

    /**
     * Method for removing the task from a board
     * @param board ID of the board whence the task is to be removed
     */
    public void removeBoard(long board){
        boards.remove(board);
    }

    /**
     * Method for adding assignees to the task
     * @param assignee User object of the user to whom the task is assigned
     */
    public void addAssignee(User assignee){
        assignedEmployees.add(assignee);
    }

    /**
     * Method for removing assignees from the task
     * @param assignee User onbject of the user from who the task is unassigned
     */
    public void removeAssignee(User assignee){
        assignedEmployees.remove(assignee);
    }



    /**
     * Create a branch under the task
     * // TODO
     * @param repository
     * @param sourceBranch
     * @throws IOException
     */
    public void addBranch(String repository, String sourceBranch) throws IOException, GitAPIException {
        // TODO
    }

    /**
     * // TODO
     * @return
     */
    public Map<Git, Set<String>> getGitMap() {
        return gitMap;
    }

    /**
     * Mehthod for getting the ID of the task
     * @return ID of the task
     */
    public long getId() {
        return taskId;
    }

    /**
     * Method for checking if the task is completed
     * @return true if completed, false if incomplete
     */
    public boolean isCompleted() {
        return isCompleted;
    }

    /**
     * Method for setting the completed field
     * @param completed task completion status
     */
    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    /**
     * Mehtod for getting the title of the task
     * @return title of the task
     */
    public String getTitle() {
        return title;
    }

    /**
     * Method for setting the title of the task
     * @param title title of the task
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Method for getting the description of the task
     * @return description of the task
     */
    public String getDescription() {
        return description;
    }

    /**
     * Method for setting the description of the task
     * @param description description of the task
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Method for getting the priority of the task
     * @return priority of the task
     */
    public int getPriority() {
        return priority;
    }

    /**
     * Method for setting the priority of the task
     * @param priority priority of the task
     */
    public void setPriority(int priority) {
        this.priority = priority;
    }

    /**
     * Method for getting the author of the task
     * @return User object of the author of the task
     */
    public User getCreatedBy() {
        return createdBy;
    }

    /**
     * Method for getting the deadline of the task
     * @return deadline of the task
     */
    public long getDeadlineMS() {
        return deadlineMS;
    }

    /**
     * Method for setting the deadline of the task
     * @param deadlineMS deadiline of the task
     */
    public void setDeadlineMS(long deadlineMS) {
        this.deadlineMS = deadlineMS;
    }

    /**
     * Mehtod for getting the creation time of the taks
     * @return creation time of the task
     */
    public long getDateCreatedMS() {
        return dateCreatedMS;
    }

    /**
     * Method for getting the master task of the task
     * @return master task ID of the task
     */
    public long getMasterTaskId() {
        return masterTaskId;
    }

    /**
     * Method for setting the mastet task of the task
     * @param masterTaskId master task ID of the task
     */
    public void setMasterTaskId(long masterTaskId) {
        this.masterTaskId = masterTaskId;
    }

    /**
     * Method for acquiring the set of the assignees
     * @return list of the assignees of the task
     */
    public Set<User> getAssignedEmployees() {
        return assignedEmployees;
    }

    /**
     * Method for getting the set of the boards
     * @return list of the boards where this task belongs
     */
    public Set<Long> getBoards() {
        return boards;
    }

    /**
     * Method for formatting the description of the task
     * @param width width of the info box in characters
     * @return description part of the info box
     */
    @NotNull
    @Contract("_ -> new")
    private String displayDescription(int width){
        // Split the description into 43 character sections
        if(this.description == null)
            return String.format("| %-41s |\n", "No description given");

        String[] lines = this.description.split("(?<=\\G.{" + (width - 4) + "})");
        String endResult = new String("");
        StringBuilder builder = new StringBuilder(this.description.length() + 100);
        for(String line: lines){
            builder.append("| ");
            builder.append(line);
            builder.append(" ".repeat(width - 3 - line.length()));
            builder.append("|\n");
        }

        return new String(builder);
    }

    /**
     * Method for formatting the assignees part of the info box
     * @param width width of the info box in characters
     * @return assignees part of the info box
     */
    @NotNull
    @Contract("_ -> new")
    private String displayAssignees(int width){
        // Split the description into 43 character sections
        StringBuilder builder = new StringBuilder(this.assignedEmployees.size() + 100);

        // Header
        builder.append(
                String.format(
                        "| Assigned to: %-28s |\n",
                        ""
                )
        );

        if(assignedEmployees.isEmpty())
            builder.append(String.format("|       No users assigned to this task %-4s |\n", ""));

        else{
            for (User user : assignedEmployees) {
                builder.append(
                        String.format(
                                "|       %-28s %5s> |\n",
                                this.createdBy.getName(),
                                "<" + this.createdBy.getId()
                        )
                );
            }
        }

        return new String(builder);
    }

    /**
     * Override of the toString(). Formats a beautiful info box form the task
     * @return String of the info box. Print it!
     */
    @Override
    public String toString() {
        return "_____________________________________________\n" + // 45 _
                String.format("| %-4d  %-35s |\n", this.taskId, this.title) + // 45
                String.format("|%-43s|\n", "") +
                displayDescription(45) +
                String.format("|%-43s|\n", "") +
                String.format("| Reported: %-31d |\n", this.dateCreatedMS) +
                String.format("| Deadline: %-31d |\n", this.deadlineMS) +
                String.format("|%-43s|\n", "") +
                String.format("| Reported by: %-21s %5s> |\n",
                        (this.createdBy != null ? this.createdBy.getName() : "Unknown"),
                        "<" + (this.createdBy != null ? this.createdBy.getId() : "")) +
                displayAssignees(45) +
                String.format("|%-43s|\n", "") +
                "|___________________________________________|\n";

    }
}
