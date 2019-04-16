package common;

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
    static long order = 0;

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

        if(assignedEmployees != null) {
            out.assignedEmployees = new long[assignedEmployees.size()];

            for (int i = 0; i < out.assignedEmployees.length; i++) {
                out.assignedEmployees[i] = assignedEmployees.get(i).getId();
            }
        }

        if(boards !=null) {
            out.boards = new long[boards.size()];

            for (int i = 0; i < out.boards.length; i++) {
                out.boards[i] = boards.get(i);
            }
        }

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

    public static long getOrder() {
        return order;
    }

    public static void setOrder(long order) {
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
            builder.append(" |");
        }

        return new String(builder);
    }

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
