package shopify.converter.util;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Component
public class FileCleanupScheduler {

    private final List<String> filePaths = new ArrayList<>();

    public void addFilePath(String filePath) {
        filePaths.add(filePath);
    }

    @Scheduled(fixedDelay = 120000)
    public void deleteFilesAfterDelay() {
        for (String filePath : filePaths) {
            File fileToDelete = new File(filePath);
            if (fileToDelete.exists()) {
                boolean isDeleted = fileToDelete.delete();
                if (isDeleted) {
                    System.out.println("File deleted successfully: " + filePath);
                } else {
                    System.out.println("Failed to delete the file: " + filePath);
                }
            }
        }

        filePaths.clear();
    }
}