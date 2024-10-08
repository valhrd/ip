package yapper.components;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import yapper.exceptions.YapperException;
import yapper.exceptions.YapperFileFormatException;


/**
 * Handles storage of tasks by reading from and writing to a file.
 */
public class Storage {
    private String filePath;
    private File referenceFile;

    /**
     * Constructs a Storage object with the specified file path.
     * Initializes the file and reads its contents.
     *
     * @param filePath the path to the file where tasks are stored
     */
    public Storage(String filePath) {
        this.filePath = filePath;
        setReferenceFile(filePath);
        initialiseFile();
    }

    /**
     * Initializes the file by creating it if it doesn't exist and reading its contents.
     * Creates necessary directories if they do not exist.
     */
    private void initialiseFile() {
        assert this.filePath != null : "The path to the file should not be null";
        File directory = this.referenceFile.getParentFile();
        if (directory != null && !directory.exists()) {
            directory.mkdirs();
        }

        // Creates the data file if it doesn't exist
        try {
            if (!this.referenceFile.exists()) {
                this.referenceFile.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * Sets the reference file to be accessed
     * @param filePath the desired file path for storing data in the form of a string
     */
    public void setReferenceFile(String filePath) {
        this.referenceFile = new File(filePath);
    }

    /**
     * Reads the tasks from the file and returns them as a list.
     * Parses each line to create appropriate Task objects.
     *
     * @return an ArrayList of Task objects read from the file
     * @throws YapperException if there is an issue with file format or other reading errors
     */
    public ArrayList<Task> readFile() throws YapperException {
        assert this.referenceFile != null : "File should not be null and should exist";
        ArrayList<Task> taskList = new ArrayList<>();
        try (Scanner fileScanner = new Scanner(this.referenceFile)) {
            while (fileScanner.hasNextLine()) {
                String[] taskInfo = fileScanner.nextLine().split("\\|");
                if (taskInfo.length < 4) {
                    throw new YapperFileFormatException("File is corrupted in one way or another");
                }
                String taskType = taskInfo[1].trim();
                String taskStatus = taskInfo[2].trim();
                String taskDesc = taskInfo[3].trim();
                Task task = null;
                switch (taskType) {
                case "T":
                    task = new ToDo(taskDesc.trim());
                    break;
                case "D":
                    task = new Deadline(taskDesc.trim(), taskInfo[4].trim());
                    break;
                case "E":
                    String[] timeRange = taskInfo[4].trim().split("-----");
                    if (timeRange.length != 2) {
                        throw new YapperFileFormatException("Invalid event range detected");
                    }
                    task = new Event(taskDesc.trim(), timeRange[0].trim(), timeRange[1].trim());
                    break;
                default:
                    throw new YapperFileFormatException("Task type not recognised: " + taskType);
                }
                if ("X".equals(taskStatus)) {
                    task.mark();
                } else if (!taskStatus.isEmpty()) {
                    throw new YapperFileFormatException("Task status not recognised: " + taskStatus);
                }
                taskList.add(task);
            }
        } catch (FileNotFoundException e) {
            throw new YapperFileFormatException("File not found");
        }
        return taskList;
    }
}
