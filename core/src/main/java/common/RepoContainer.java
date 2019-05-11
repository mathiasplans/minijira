package common;

import org.eclipse.jgit.api.CreateBranchCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PushCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.internal.storage.dfs.DfsRepositoryDescription;
import org.eclipse.jgit.internal.storage.dfs.InMemoryRepository;
import org.eclipse.jgit.transport.RefSpec;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RepoContainer {
    private final Map<String, Git> repositories = new HashMap<>();

    private Git updateRepo(String repositoryURL) throws GitAPIException {
        Git git = repositories.get(repositoryURL);

        // Fetch
        git.fetch().setRemote(repositoryURL).setRefSpecs(new RefSpec("+refs/heads/*:refs/heads/*")).call();

        return git;
    }

    private Git initiateRepository(String repository) {
        Git git = new Git(new InMemoryRepository(new DfsRepositoryDescription()));
        repositories.put(repository, git);
        return git;
    }

    public void addBranch(String repositoryURL, String branchName, String source) throws GitAPIException {
        Git git = updateRepo(repositoryURL);

        CreateBranchCommand branch = git.branchCreate();

        // Create a branch
        branch.setStartPoint(source);
        branch.setName(branchName);
        branch.setUpstreamMode(CreateBranchCommand.SetupUpstreamMode.SET_UPSTREAM);
        branch.setForce(true);
        branch.call();

        // Push it
        PushCommand push = git.push();
        push.setRemote(repositoryURL);
        push.setRefSpecs(new RefSpec(branchName + ":" + branchName));
        push.call();
    }

    /**
     * https://stackoverflow.com/questions/45587631/how-to-checkout-a-remote-branch-without-knowing-if-it-exists-locally-in-jgit
     * @param repositoryURL
     * @param branchName
     * @return
     * @throws GitAPIException
     * @throws IOException
     */
    public boolean hasMerged(String repositoryURL, String branchName) throws GitAPIException, IOException {
        Git git = updateRepo(repositoryURL);
        return git.getRepository().exactRef("refs/heads/" + branchName) != null;
    }
}
