package common;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

class ContainerHelper<ContainerType> {
    private final List<ContainerType> container;

    /**
     * Primary constructor. Initializes the container field
     * @param container List object of type ContainerType
     */
    ContainerHelper(List<ContainerType> container){
        this.container = container;
    }

    /**
     * Method for importing items from files
     * @param path path to a file/directory whence items are imported
     * @param converterJSONToItem callback which converts a JSON String to ContainerType
     * @throws IOException If file IO fails
     */
    void importItems(Path path, Converter<String, ContainerType> converterJSONToItem) throws IOException {
        // Check if file exists
        if(!Files.exists(path))
            throw new IllegalArgumentException("Given path does not point to neither file nor directory");

        // If the File object points at directory
        if (Files.isDirectory(path)){
            try (var files = Files.walk(path)){
                files.filter(Files::isRegularFile).forEach((p) -> {
                    try {
                        container.add(converterJSONToItem.call(Files.readString(p)));
                    }catch (IOException e){
                        throw new RuntimeException(e);
                    }
                });
            }
        }

        // If the File object points at file
        else if(Files.isRegularFile(path)){
            List<String> lines = Files.readAllLines(path);
            for(String line: lines){
                if(!line.isBlank())
                    container.add(converterJSONToItem.call(line));
            }
        }
    }

    /**
     * Method for exporting items to files in JSON format
     * @param path path to the save file/directory where the items are exported
     * @param converterItemToJSON callback which converts ContainerType into JSON String
     * @throws IOException If file IO fails
     */
    void exportItems(Path path, Converter<ContainerType, String> converterItemToJSON) throws IOException {
        // Check if file exists
        if(!Files.exists(path))
            throw new IllegalArgumentException("Given path does not point to neither file nor directory");

        // If the File object points at directory
        if(Files.isDirectory(path)){
            for (ContainerType item: container) {
                Files.writeString(
                        path.resolve(String.valueOf(item.hashCode())),
                        converterItemToJSON.call(item)
                );
            }
        }

        // If the File object points at file
        else if(Files.isRegularFile(path)){
            StringBuilder builder = new StringBuilder();
            for(ContainerType item: container){
                builder.append(converterItemToJSON.call(item));
                builder.append("\n");
            }

            Files.writeString(path, builder.toString());
        }
    }
}
