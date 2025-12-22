import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

class Message {
    private int id;
    private String user;
    private String content;
    private String timestamp;
    
    public Message(int id, String user, String content) {
        this.id = id;
        this.user = user;
        this.content = content;
        this.timestamp = LocalDateTime.now()
            .format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }
    
    public int getId() { return id; }
    public String getUser() { return user; }
    public String getContent() { return content; }
    public String getTimestamp() { return timestamp; }
    
    @Override
    public String toString() {
        return String.format("[ID:%d] %s (%s): %s", id, user, timestamp, content);
    }
}

class ChatRoom {
    private Queue<Message> messageQueue;
    private int messageIdCounter;
    private String roomName;
    
    public ChatRoom(String roomName) {
        this.roomName = roomName;
        this.messageQueue = new LinkedList<>();
        this.messageIdCounter = 0;
    }
    
    public Message sendMessage(String user, String content) {
        Message msg = new Message(messageIdCounter++, user, content);
        messageQueue.offer(msg);
        return msg;
    }
    
    public List<Message> getMessagesSince(int sinceId) {
        List<Message> result = new ArrayList<>();
        for (Message msg : messageQueue) {
            if (msg.getId() > sinceId) {
                result.add(msg);
            }
        }
        return result;
    }
    
    public int getQueueSize() {
        return messageQueue.size();
    }
    
    public void displayAllMessages() {
        System.out.println("\n" + "=".repeat(70));
        System.out.println("Room: " + roomName + " | Total Messages: " + messageQueue.size());
        System.out.println("=".repeat(70));
        
        if (messageQueue.isEmpty()) {
            System.out.println("No messages in queue");
        } else {
            for (Message msg : messageQueue) {
                System.out.println(msg);
            }
        }
        System.out.println("=".repeat(70) + "\n");
    }
}

public class messagequeue {
    
    public static void main(String[] args) {
        System.out.println("\n" + "=".repeat(70));
        System.out.println("    QUEUE-BASED CHAT SYSTEM - DSA DEMONSTRATION");
        System.out.println("=".repeat(70));
        System.out.println("Concept: Queue (FIFO) ensures messages arrive in exact order");
        System.out.println("=".repeat(70) + "\n");
        
        ChatRoom room = new ChatRoom("General");
        
        // Scenario 1: Normal messaging
        System.out.println("SCENARIO 1: Normal Chat\n");
        
        Message m1 = room.sendMessage("Alice", "Hello everyone!");
        System.out.println("Sent: " + m1);
        
        Message m2 = room.sendMessage("Bob", "Hi Alice!");
        System.out.println("Sent: " + m2);
        
        Message m3 = room.sendMessage("Charlie", "Hey team!");
        System.out.println("Sent: " + m3);
        
        room.displayAllMessages();
        
        // Scenario 2: User goes offline and reconnects
        System.out.println("\nSCENARIO 2: Offline User Sync\n");
        
        int charlieLastSeen = 2;
        System.out.println("Charlie goes OFFLINE (last seen: ID " + charlieLastSeen + ")\n");
        
        Message m4 = room.sendMessage("Alice", "Charlie disconnected");
        System.out.println("Sent: " + m4);
        
        Message m5 = room.sendMessage("Bob", "He'll be back");
        System.out.println("Sent: " + m5);
        
        System.out.println("\nCharlie RECONNECTS! Syncing...\n");
        
        List<Message> missed = room.getMessagesSince(charlieLastSeen);
        System.out.println("Charlie receives " + missed.size() + " missed messages in ORDER:");
        for (Message msg : missed) {
            System.out.println("  -> " + msg);
        }
        
        room.displayAllMessages();
        
        System.out.println("\n" + "=".repeat(70));
        System.out.println("KEY CONCEPTS DEMONSTRATED");
        System.out.println("=".repeat(70));
        System.out.println("✓ Queue (FIFO): Messages in exact order");
        System.out.println("✓ Enqueue O(1): Fast message addition");
        System.out.println("✓ Sequential Access: Reliable sync");
        System.out.println("✓ LinkedList: Java Queue implementation");
        System.out.println("=".repeat(70) + "\n");
    }
}