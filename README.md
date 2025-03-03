# Java Local Chatroom

A simple, no-nonsense chatroom application built purely in Java. Create chatrooms, join existing ones, send direct messages (soon™), and see who's online. No databases, no external servers—just pure local chat.

## Features

- **Chatrooms**: Create and join chatrooms with ease.
- **Direct Messaging**: One-on-one conversations (still in the works).
- **Active Users List**: See who's online.
- **MegaPhone**: Broadcast messages (coming soon).
- **Exit Option**: Because nobody wants to be stuck in a chatroom forever.

## Getting Started

### Prerequisites

- **Java 17+**  
- **Maven** (for building the project)

### Installation & Usage

1. **Clone the repository**  
   ```sh
   git clone https://github.com/dkoh2018/java_local_chatroom.git
   cd dkoh2018-java_local_chatroom
   ```

2. **Build the project**  
   ```sh
   mvn clean package
   ```

3. **Run the application**  
   ```sh
   java -cp target/chatapplication-1.0-SNAPSHOT.jar com.example.App
   ```

4. **Choose an option from the menu**  
   - Join or create chatrooms  
   - Attempt direct messaging (placeholder alert)  
   - Check active users  
   - Log out and disappear  

## Directory Structure

```
dkoh2018-java_local_chatroom/
├── Document_Design.txt  # Design details, data structures, and logic
├── pom.xml              # Maven build configuration
└── src/
    └── main/
        └── java/
            └── com/
                └── example/
                    └── chatapp/
                        └── App.java
```

## How It Works

- **User management**: Stored in a `HashMap` for quick lookups.
- **Chatrooms**: Also managed via a `HashMap`, ensuring fast access.
- **Message history**: Uses an `ArrayList` to maintain order.
- **Active users**: A `HashSet` ensures no duplicates.

For a deep dive, check `Document_Design.txt`.
