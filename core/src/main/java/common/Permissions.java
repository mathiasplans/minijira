package common;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.security.Permission;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Enum of all the permissions in minijira
 */
public enum Permissions/* implements Comparable<Permissions>*/{
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
    //private static Map map = new HashMap();

    /**
     * Default constructor. Initializes index
     * @param index
     */
    Permissions(int index){
        this.index = index;
    }

    static Map<Integer, Permissions> buildPermissionMap(){
        for(common.Permissions permission: common.Permissions.values()){
            map.put(permission.index, permission);
        }
        return  map;
    }

    private static final Map<Integer, Permissions> map = Collections.unmodifiableMap(buildPermissionMap());


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


    public static Permissions valueOf(int i){
        return (Permissions) map.get(i);
    }

    /*public static int getValue(Permissions permission){
        return index;
    }*/

    /*int compareTo(Permissions permission){
        return index - permission.getIndex();
    }*/

}
