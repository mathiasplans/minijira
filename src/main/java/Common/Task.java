package Common;

import data.RawTask;
import org.kohsuke.github.GHBranch;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;

import java.io.IOException;
import java.util.*;

public class Task {
    final private Map<GHRepository, List<GHBranch>> gitMap;
    final private long taskId;
    private boolean isCompleted = false;
    private String title;
    private String description;
    private int priority;
    final private User createdBy;
    private long deadlineMS;
    final private long dateCreatedMS;
    private long masterTaskId;
    final private List<User> assignedEmployees;
    final private List<Long> boards;

    // Increments with every construct
    static int order = 0;

    // Time getter
    static Date commonTime = new Date();

    // GitHub
    final GitHub gitHub;

    public Task(String name){
        this.title = name;
        dateCreatedMS = commonTime.getTime();
        gitHub = null;
        gitMap = new HashMap<GHRepository, List<GHBranch>>();
        this.taskId = order;
        order++;
        createdBy = null;
        assignedEmployees = new ArrayList<>();
        boards = new ArrayList<>();

    }

    public Task(String name, String description, long board, long deadline, User author, int priority, GitHub gitHub){
        this.title = name;
        this.description = description;

        this.taskId = order;
        order++;

        boards = new ArrayList<>();
        assignedEmployees = new ArrayList<>();

        boards.add(board);

        createdBy = author;
        this.priority = priority;

        // Time
        dateCreatedMS = commonTime.getTime();
        deadlineMS = deadline;

        // GitHub integration
        gitMap = new HashMap<GHRepository, List<GHBranch>>();

        this.gitHub = gitHub;
    }

    public Task(RawTask source, GitHub gitHub){
        // GitHub integration
        gitMap = new HashMap<GHRepository, List<GHBranch>>();

        this.gitHub = gitHub;

        boards = new ArrayList<>();

        assignedEmployees = new ArrayList<>();

        if(source != null) {
            createdBy = new User("temp", source.createdBy);
            dateCreatedMS = source.dateCreatedMS;
            deadlineMS = source.deadlineMS;
            description = source.description;
            isCompleted = source.isCompleted;
            masterTaskId = source.masterTaskId;
            title = source.title;
            taskId = source.taskId;
            priority = source.priority;

            for (Long id : source.assignedEmployees) {
                assignedEmployees.add(new User("temp", id));
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

    public RawTask getRawTask(){
        RawTask out = new RawTask();

        if(createdBy != null)
            out.createdBy = createdBy.getId();
        else
            out.createdBy = -1L;

        out.dateCreatedMS = dateCreatedMS;
        out.deadlineMS = deadlineMS;
        out.description = description;
        out.isCompleted = isCompleted;
        out.masterTaskId = masterTaskId;
        out.title = title;
        out.taskId = taskId;
        out.priority = priority;

        if(assignedEmployees != null) {
            out.assignedEmployees = new long[assignedEmployees.size()];

            for (int i = 0; i < out.assignedEmployees.length; i++) {
                out.assignedEmployees[i] = assignedEmployees.get(i).getId();
            }
        }else
            out.assignedEmployees = new long[]{-1L};

        if(boards !=null) {
            out.boards = new long[boards.size()];

            for (int i = 0; i < out.boards.length; i++) {
                out.boards[i] = boards.get(i);
            }
        }else
            out.boards = new long[]{-1L};

        return out;
    }

    public void complete(){
        isCompleted = true;
    }

    public void addBoard(long board){
        boards.add(board);
    }

    public void removeBoard(long board){
        boards.remove(board);
    }

    public void addAssignee(User assignee){
        assignedEmployees.add(assignee);
    }

    public void removeAssignee(User assignee){
        assignedEmployees.remove(assignee);
    }


    public void addBranch(String repository, String sourceBranch) throws IOException {
        GHRepository repo = gitHub.getRepository(repository);
        GHBranch branch = repo.getBranch(sourceBranch);
        // TODO
    }

    public Map<GHRepository, List<GHBranch>> getGitMap() {
        return gitMap;
    }

    public long getTaskId() {
        return taskId;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public long getDeadlineMS() {
        return deadlineMS;
    }

    public void setDeadlineMS(long deadlineMS) {
        this.deadlineMS = deadlineMS;
    }

    public long getDateCreatedMS() {
        return dateCreatedMS;
    }

    public long getMasterTaskId() {
        return masterTaskId;
    }

    public void setMasterTaskId(long masterTaskId) {
        this.masterTaskId = masterTaskId;
    }

    public List<User> getAssignedEmployees() {
        return assignedEmployees;
    }

    public List<Long> getBoards() {
        return boards;
    }

    public static int getOrder() {
        return order;
    }

    public static void setOrder(int order) {
        Task.order = order;
    }

    public static Date getCommonTime() {
        return commonTime;
    }

    public static void setCommonTime(Date commonTime) {
        Task.commonTime = commonTime;
    }

    public GitHub getGitHub() {
        return gitHub;
    }
}
