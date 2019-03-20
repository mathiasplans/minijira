import org.kohsuke.github.GitHub;

import java.util.ArrayList;
import java.util.Date;

public class Test {
    public static void main(String[] args) throws Exception {
        ArrayList<ProgressReport> reports = new ArrayList<>();

        Comment testComment = new Comment(
                10,
                20,
                "AAA",
                new Date(),
                new User(
                        "Mathias",
                        20,
                        Permissions.USER)
        );

        reports.add(testComment);

        Task testTask = new Task(
                "Create minijira",
                "OOP project",
                "JIRA1",
                GitHub.connect(),
                Task.Type.FEATURE
        );
    }
}
