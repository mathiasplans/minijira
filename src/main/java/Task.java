import org.kohsuke.github.GHBranch;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;

import java.io.IOException;
import java.util.*;

public class Task {
    private int id;
    private String name;
    private String description;
    private List<ProgressReport> reports;
    private List<String> boards;
    private double changeTime;
    private Map<GHRepository, List<GHBranch>> gitMap;

    // Increments with every construct
    static int order = 0;

    // Time getter
    static Date commonTime = new Date();

    // GitHub
    GitHub gitHub;

    enum Status{
        OPEN,
        IN_PROGRESS,
        IN_REVIEW,
        DONE,
        CLOSED,
        ABANDONED,
        WONT_DO;
    }

    enum Type{
        FEATURE,
        BUGFIX,
        TEST,
        RESEARCH,
        EPIC,
        ORDER;
    }

    private Status taskStatus;
    private Type taskType;

    public Task(String name, String description, String board, GitHub gitHub, Type taskType){
        this.name = name;
        this.description = description;

        this.id = order;
        order++;

        reports = new ArrayList<>();
        boards = new ArrayList<>();

        boards.add(board);
        gitMap = new HashMap<GHRepository, List<GHBranch>>();

        this.gitHub = gitHub;

        this.taskType = taskType;

        // By default, new Tasks are open
        taskStatus = Status.OPEN;
    }

    public void setName(String newName){
        changeTime = commonTime.getTime();
        this.name = newName;
    }

    public String getName(){
        return name;
    }

    public void setDescription(String newDescription){
        changeTime = commonTime.getTime();
        this.description = newDescription;
    }

    public String getDescription(){
        return description;
    }

    public int getID(){
        return id;
    }

    public void addReport(ProgressReport report){
        reports.add(report);
    }

    public List<ProgressReport> getReports(){
        return reports;
    }

    public List<String> getBoards(){
        return boards;
    }

    public double getChangeTime(){
        return changeTime;
    }

    public void addBranch(String repository, String sourceBranch) throws IOException {
        GHRepository repo = gitHub.getRepository(repository);
        GHBranch branch = repo.getBranch(sourceBranch);
        // TODO
    }

    public Status getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(Status taskStatus) {
        this.taskStatus = taskStatus;
    }

    public Type getTaskType() {
        return taskType;
    }

    public void setTaskType(Type taskType) {
        this.taskType = taskType;
    }

    public double getTimeSpent(){
        double sum = 0;
        for(ProgressReport report: reports){
            sum += report.getDuration();
        }

        return sum;
    }
}
