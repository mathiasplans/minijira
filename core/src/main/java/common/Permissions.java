package common;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.security.Permission;

/**
 * Enum of all the permissions in minijira
 */
public enum Permissions {
    /**
     * Users with NORIGTH permission have no rights to do anything
     */
    NORIGHT(0),

    /**
     * Users with SEE permission have the right to see all the tasks
     */
    SEE(1),

    /**
     * Users with CREATE permission have the right to create and modify new tasks
     * along with all the previous rights
     */
    CREATE(2),

    /**
     * Users with COMPLETE permission have the right to complete tasks
     * along with all the previous rights
     */
    COMPLETE(3),

    /**
     * Users with ALL permission have all the rights
     */
    ALL(4);

    private final int index;

    /**
     * Default constructor. Initializes index
     * @param index
     */
    Permissions(int index){
        this.index = index;
    }

    /**
     * Gets the index of the enum
     * @return index of the enum
     */
    @Contract(pure = true)
    public int getIndex(){
        return index;
    }

    /**
     * Gets the enum value by index
     * @param index search key
     * @return Enum object with given index. If object with given index does not exist then NORIGHT is returned
     */
    public static Permissions getPermission(int index){
        for(Permissions permission: Permissions.values()){
            if(permission.getIndex() == index)
                return permission;
        }

        return NORIGHT;
    }

}
