import java.util.ArrayList;
public class BartenderBob {
    private static final String NAME = "BartenderBob";
    private final ArrayList<Task> STORAGE = new ArrayList<>();
    public BartenderBob() {
        System.out.println("Welcome back! I'm " + NAME + "\nHow's it going out there?");
    }

    public void leave() {
        System.out.println("Bye! Another round next time!");
    }

    public void echo(String userInput) {
        System.out.println(userInput);
    }

    public void store(Task task) {
        STORAGE.add(task);
        System.out.println("Added: " + task.getDescription());
    }

    public void list() {
        System.out.println("Here are the tasks in your list!");
        for (int i = 0; i < STORAGE.size(); i++) {
            int number = i + 1;
            System.out.println(number + ". " + STORAGE.get(i).show());
        }
    }

    public void markDone(String index) {
        int integerIndex = Integer.parseInt(index);
        Task task = STORAGE.get(integerIndex - 1);
        task.mark();
        System.out.println("Nice! I've marked this task as done:");
        System.out.println(task.show());
    }

    public void unmarkDone(String index) {
        int integerIndex = Integer.parseInt(index);
        Task task = STORAGE.get(integerIndex - 1);
        task.unmark();
        System.out.println("OK, I've marked this task as not done yet:");
        System.out.println(task.show());
    }
}
