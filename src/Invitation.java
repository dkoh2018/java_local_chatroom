package src;

public class Invitation implements Comparable<Invitation> {
    private String fromUser;
    private String toUser;
    private long timestamp;

    public Invitation(String fromUser, String toUser, long timestamp) {
        this.fromUser = fromUser;
        this.toUser = toUser;
        this.timestamp = timestamp;
    }

    public String getFromUser() {
        return fromUser;
    }

    public String getToUser() {
        return toUser;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public int compareTo(Invitation other) {
        return Long.compare(this.timestamp, other.timestamp); // Sort by timestamp
    }
}
