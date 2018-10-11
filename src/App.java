import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class App {
    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
    DistributedMap map;
    static final long serialVersionUID = 1L;

    public App() throws Exception {
        map = new DistributedMap();
    }

    public void runCommandLine() throws IOException {
        while (true) {
            System.out.print(">>>>>");
            String command = bufferedReader.readLine();
            if (command.startsWith("put")) {
                this.handlePutOperation(command);

            } else if (command.startsWith("rem")) {
                this.handleRemoveOperation(command);

            } else if (command.startsWith("get")) {
                this.handleGetOperation(command);

            } else if (command.startsWith("has")) {
                this.handleContainsOperation(command);

            } else {
                System.out.println("Zła operacja :( ");
            }
        }
    }




    private void handleRemoveOperation(String command) throws IOException {
        String[] splitTable = command.split(" ");
        String key = getKey(splitTable);
        String value = map.remove(key);
        if(value != null) {
            System.out.println("Removed value: " + value);
        } else {
            System.out.println("No such value!");
        }
    }

    private void handleContainsOperation(String command) throws IOException {
        String[] splitTable = command.split(" ");
        String key = getKey(splitTable);
        System.out.println("Map contains " + key + " : " +  map.containsKey(key));
    }

    private void handleGetOperation(String command) throws IOException {
        String[] splitTable = command.split(" ");
        String key = getKey(splitTable);
        String value = map.get(key);
        if(value !=null){
            System.out.println("This value : " + value);
        }
        else{
            System.out.println("There is not that key in map");
        }
    }

    private String getKey(String[] splitTable) throws IOException {
        String key;
        if(splitTable.length < 2){
            System.out.print("Enter key: ");
            key = bufferedReader.readLine();
        } else {
            key = splitTable[1];
        }
        return key;
    }

    private void handlePutOperation(String command) throws IOException {
        final String[] splitTable = command.split(" ");
        String key, value;
        if(splitTable.length == 1) {
            System.out.print("Enter key: ");
            key = bufferedReader.readLine();
            System.out.print("Enter Value: ");
            value = bufferedReader.readLine();
        }
        else if(splitTable.length == 2){
            System.out.println("Enter Value: ");
            key = splitTable[1];
            value = bufferedReader.readLine();
        }
        else {
            key = splitTable[1];
            value = splitTable[2];
        }
        this.map.put(key, value);
        System.out.println("Did");
    }

    public static void main(String[] args) throws Exception{

        App app =new App();
        app.runCommandLine();

    }


}