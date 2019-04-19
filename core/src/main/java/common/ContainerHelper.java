package common;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

public class ContainerHelper<ContainerType> {
    private final List<ContainerType> container;

    /**
     * Primary constructor. Initializes the container field
     * @param container List object of type ContainerType
     */
    public ContainerHelper(List<ContainerType> container){
        this.container = container;
    }

    /**
     * Method for importing items from files
     * @param path path to a file/directory whence items are imported
     * @param callbackJSONToItem callback which converts a JSON String to ContainerType
     * @throws IOException If file IO fails
     */
    public void importItems(String path, Callback<String, ContainerType> callbackJSONToItem) throws IOException {
        // File/Directory
        File dile = new File(path);

        // Check if file exists
        if(!dile.exists())
            throw new IllegalArgumentException("Given path does not point to neither file nor directory");

        // If the File object points at directory
        if (dile.isDirectory()){
            File[] files = dile.listFiles();

            if (files != null) {
                for (File file : files) {
                    container.add(callbackJSONToItem.call(Files.readString(file.toPath())));
                }
            }
        }

        // If the File object points at file
        else if(dile.isFile()){
            List<String> lines = Files.readAllLines(dile.toPath(), StandardCharsets.UTF_8);
            for(String line: lines){
                if(!"".equals(line))
                    container.add(callbackJSONToItem.call(line));
            }
        }
    }

    /**
     * Method for exporting items to files in JSON format
     * @param path path to the save file/directory where the items are exported
     * @param callbackItemToJSON callback which converts ContainerType into JSON String
     * @throws IOException If file IO fails
     */
    public void exportItems(String path, Callback<ContainerType, String> callbackItemToJSON) throws IOException {
        File dile = new File(path);

        // Check if file exists
        if(!dile.exists())
            throw new IllegalArgumentException("Given path does not point to neither file nor directory");

        // If the File object points at directory
        if(dile.isDirectory()){
            for (ContainerType item: container) {
                File newFile = new File(path, String.valueOf(item.hashCode()));
                Files.writeString(newFile.toPath(), callbackItemToJSON.call(item), StandardCharsets.UTF_8);
            }
        }

        // If the File object points at file
        else if(dile.isFile()){
            StringBuilder builder = new StringBuilder();
            for(ContainerType item: container){
                builder.append(callbackItemToJSON.call(item));
                builder.append("\n");
            }

            Files.writeString(dile.toPath(), builder.toString(), StandardCharsets.UTF_8);
        }
    }
}
