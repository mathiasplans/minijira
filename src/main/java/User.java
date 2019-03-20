public class User {
    private String name;
    private int id;
    private Permissions permissions;

    public User(String name, int id, Permissions permissions) {
        this.name = name;
        this.id = id;
        this.permissions = permissions;
    }
}
