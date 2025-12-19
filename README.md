# Connect-4 Multiplayer (Java)

This is a fully featured Connect-4 game built in Java and has both **single-player (AI)** and **real time multiplayer** modes using server-client.  
This project has a custom **JavaFX GUI**, a **client server architecture**, persistent **player statistics**, and in game **chat Feature**.

---

## Features

### User Login
- Username login system
- Persistent player identity across sessions

### Multiplayer Mode
- Real-time two player Connect-4 over a network
- There is client and server communication using sockets
- There is turn synchronization and move validation
- Game chat between players
- Waiting room until a player joins

### Single Player (AI)
- Play against an AI opponent
- there is turn based logic and win detection
- There is a separation between logic and the UI

### Player Statistics
- This tracks wins, losses, ties and games played
- Stats screen accessible from the home menu

### Graphical User Interface
- Built with JavaFX
- Multiple scenes:
  - Login
  - Home menu
  - Waiting room
  - Multiplayer game
  - Single player game
  - Game Over screen
  - Stats screen
- Clean layout, intuitive controls, and visual turn indicators

---

## How to Run

### Prerequisites
- **Java 17+**
- **Maven**
- IntelliJ IDEA was used in build but can any Java IDE

---

### First Run the Server
cd server
mvn clean compile exec:java

### Second Run the Client
cd client
mvn clean compile exec:java

NOTE: You can run multiple clients to try multiplayer mode the same way.

## Some Screenshots
<img width="946" height="696" alt="Home Page" src="https://github.com/user-attachments/assets/c9ed216a-496d-40d8-aa77-0731f150204e" />

<img width="946" height="696" alt="Multiplayer" src="https://github.com/user-attachments/assets/25253405-e1f1-4d35-b914-31e94724275f" />

<img width="946" height="696" alt="GameOver" src="https://github.com/user-attachments/assets/50b302f4-3905-455c-a0c9-f4c82bcd12b2" />

<img width="946" height="696" alt="Stats" src="https://github.com/user-attachments/assets/7cd2fa0c-e8b7-474c-b364-186e6c744514" />

## Author
Yusef Mostafa

---
