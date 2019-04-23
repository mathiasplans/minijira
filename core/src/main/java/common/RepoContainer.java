package common;

import org.eclipse.jgit.api.CreateBranchCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PushCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.internal.storage.dfs.DfsRepositoryDescription;
import org.eclipse.jgit.internal.storage.dfs.InMemoryRepository;
import org.eclipse.jgit.lib.Repository;
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

    private Git createRepository(String repository) {
//        String repoName = repository.split("/")[repository.split("/").length - 1].split(".")[0];
//        File gitFile = new File(Paths.get("repositories", "repoName", ".git").toString());
//        Git out;
//        if(gitFile.exists()){
//            FileRepositoryBuilder builder = new FileRepositoryBuilder();
//            return out = new Git(builder.setGitDir(gitFile).readEnvironment().findGitDir().setMustExist(true).build());
//        }else{
//            return out = Git.cloneRepository().setURI(repository).setDirectory(gitFile.getParentFile()).setCloneAllBranches(true).call();
//        }
        return new Git(new InMemoryRepository(new DfsRepositoryDescription()));
    }

    public void addRepo(String repositroyURL){
        repositories.put(repositroyURL,createRepository(repositroyURL));
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
}
