package shopify.converter.util;


import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class FileUtil {

    public List<String> getFilePathsFromFolder(String folderPath, String fileType) {
        List<String> filePaths = new ArrayList<>();

        File folder = new File(folderPath);

        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles();

            if (files != null) {
                for (File file : files) {
                    if (file.isFile() && isFileTypeMatch(file, fileType)) {
                        filePaths.add(file.getAbsolutePath());
                    }
                }
            }
        }

        return filePaths;
    }

    private boolean isFileTypeMatch(File file, String fileType) {
        String filePath = file.getName();
        return filePath.contains(fileType);
    }

    public boolean checkFileConsistency(String folderPath1, String folderPath2) {
        // Создаем объекты для папок
        File folder1 = new File(folderPath1);
        File folder2 = new File(folderPath2);

        // Получаем список файлов в каждой папке
        File[] filesInFolder1 = folder1.listFiles();
        File[] filesInFolder2 = folder2.listFiles();

        // Проверяем, что оба массива файлов не равны null и их длины одинаковы
        if (filesInFolder1 != null && filesInFolder2 != null &&
                filesInFolder1.length > 0 && filesInFolder2.length > 0 &&
                filesInFolder1.length == filesInFolder2.length * 2) {
            return true;
        } else {
            return false;
        }
    }

}
