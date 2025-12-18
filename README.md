# Connect-4 Multiplayer (Java)

This is a fully featured Connect-4 game built in Java and has both **single-player (AI)** and **real time multiplayer** modes.  
This project has a custom **JavaFX GUI**, a **client server architecture**, persistent **player statistics**, and in-game **chat Feature**.

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

## Author
Yusef Mostafa

---
