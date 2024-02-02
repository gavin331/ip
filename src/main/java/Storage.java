import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Storage {
    private String filePath; //eg "./data/tasks.txt"
    private Ui ui = new Ui();
//    private ArrayList<Task> storage;

    public Storage(String filePath) {
        this.filePath = filePath;
//        this.storage = new ArrayList<>();
    }
    public ArrayList<Task> load() throws BartenderBobException {
        try {
            ArrayList<Task> taskArray = new ArrayList<>();
            Path path = Paths.get(filePath); //Operating system independent
            if (Files.exists(path)) {
                List<String> tasks = Files.readAllLines(path);
                for (String taskString : tasks) {
                    String[] split = taskString.split(" \\| ");
                    String taskType = split[0];
                    String taskStatus = split[1];
                    boolean isDone = taskStatus.equals("X");
                    String description = split[2];

                    if (taskType.equals("T")) {
                        taskArray.add(new Task(description, isDone));
                    } else if (taskType.equals("D")) {
                        String dueDate = split[3];
                        dueDate = convertDateFormat(dueDate);
                        taskArray.add(new Deadline(description, dueDate, isDone));
                    } else if (taskType.equals("E")) {
                        String fromDate = split[3];
                        fromDate = convertDateFormat(fromDate);
                        String toDate = split[4];
                        toDate = convertDateFormat(toDate);
                        taskArray.add(new Event(description, fromDate, toDate, isDone));
                    }
                }
            } else {
                //Create data directory
                //if path is "./data/tasks.txt", then path.getParent()
                //gives data.
                Files.createDirectories(path.getParent());
                //Create tasks.txt
                Files.createFile(path);
            }
            return taskArray;
        } catch (IOException e) {
            throw new BartenderBobException();
//            System.out.println("Error occurred in storage->load() method: " + e.getMessage());
        }
    }
//    public void addTask(Task task) {
//        //[T][X] read book = T | X | read book
//        //[D][ ] return book (by: June 6th) = D |  | return book | June 6th
//        //[E][ ] project meeting (from: Aug 6th 2pm to: 4pm)
//        // = E |  | project meeting | Aug 6th 2pm | 4pm
//////        storage.add(task);
////        Path path = Paths.get(filePath);
//        saveTask(task);
//    }
    public void saveChanges(ArrayList<Task> tasks) { //for marking or unmarking tasks
        try {
            Path path = Paths.get(filePath);
            Files.write(path, "".getBytes(), StandardOpenOption.TRUNCATE_EXISTING);
            for (Task task : tasks) {
                saveTask(task);
            }
        } catch (IOException e) {
            ui.showSaveChangesError();
        }

    }

//    public int getSize() {
//        return storage.size();
//    }
//    public Task getTask(int index) {
//        return storage.get(index);
//    }
//    public void removeTask(int index) {
//        storage.remove(index);
//    }
    private String convertDateFormat(String oldDateFormat) {
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("MMM dd yyyy");

        // Parse the original string to LocalDate
        LocalDate localDate = LocalDate.parse(oldDateFormat, inputFormatter);

        // Define the formatter for the output pattern
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // Format the LocalDate to the desired output pattern
        return localDate.format(outputFormatter);
    }

    public void saveTask(Task task) {
        try {
            Path path = Paths.get(filePath);
            String taskString = task.show();
            String typeOfTask = taskString.substring(1, 2); //T
            String taskStatus = taskString.substring(4, 5); //X
            String taskDescription = "";
            String saveEntry = "";
            if (typeOfTask.equals("T")) {
                taskDescription = taskString.substring(7);
                saveEntry = typeOfTask + " | " + taskStatus + " | " + taskDescription;
            } else if (typeOfTask.equals("D")) {
                int startIndex = taskString.indexOf("(by: ");
                int endIndex = taskString.indexOf(")");
                taskDescription = taskString.substring(7, startIndex - 1);
                String deadline = taskString.substring(startIndex + 5, endIndex);
                saveEntry = typeOfTask + " | " + taskStatus + " | " + taskDescription
                        + " | " + deadline;
            } else if (typeOfTask.equals("E")) {
                int startIndex = taskString.indexOf("(from: ");
                taskDescription = taskString.substring(7, startIndex - 1);
                int endIndex = taskString.indexOf(" to:");
                String fromDate = taskString.substring(startIndex + 7, endIndex);
                startIndex = taskString.indexOf(")");
                String toDate = taskString.substring(endIndex + 5, startIndex);
                saveEntry = typeOfTask + " | " + taskStatus + " | " + taskDescription
                        + " | " + fromDate + " | " + toDate;
            }
            Files.write(path, (saveEntry + System.lineSeparator()).getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            ui.showSaveTasksError();
        }
    }
}
