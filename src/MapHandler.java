import java.io.Serializable;

public class MapHandler implements Serializable {
    private String key;
    private String value;
    private OperationType operationType;

    public MapHandler(String key, String value) {
        this.key = key;
        this.value = value;
        this.operationType = OperationType.PUT;
    }

    public MapHandler(String key) {
        this.key = key;
        this.operationType = OperationType.REMOVE;
    }

    public enum OperationType {
        PUT, REMOVE
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public OperationType getOperationType() {
        return operationType;
    }
}
