package common;

/**
 * Enum of all the permissions in minijira
 */
public enum Permissions {
    /**
     * Users with NORIGTH permission have no rights to do anything
     */
    NORIGTHT,

    /**
     * Users with SEE permission have the right to see all the tasks
     */
    SEE,

    /**
     * Users with CREATE permission have the right to create and modify new tasks
     * along with all the previous rights
     */
    CREATE,

    /**
     * Users with COMPLETE permission have the right to complete tasks
     * along with all the previous rights
     */
    COMPLETE,

    /**
     * Users with ALL permission have all the rights
     */
    ALL
}
