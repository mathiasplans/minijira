package Common;

import Common.Permissions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class User {
    private String name;
    final private long id;
    final private List<Long> projects;
    final private Map<Long, Permissions> permissions;

    public User(String name, long id) {
        this.name = name;
        this.id = id;

        projects = new ArrayList<>();
        permissions = new HashMap<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public List<Long> getProjects() {
        return projects;
    }

    public Map<Long, Permissions> getPermissions() {
        return permissions;
    }
}
