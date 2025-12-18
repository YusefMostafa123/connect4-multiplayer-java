/*
this deals with the communication between the server and a client,
deals with playing the game, chat, putting client in games together,
and teh stats for the multiplayer
 */
package server;

import java.io.*;
import java.net.Socket;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.*;

public class HandleClient implements Runnable {
    private final Socket socket;
    private final Set<String> activeUsers;
    private String username;
    private BufferedReader in;
    private PrintWriter out;
    private HandleClient opponent;
    private int playerId = 0;
    private boolean inGame = false;
    private boolean waiting = false;
    private String reservedOpponent = null;
    private static final Map<String, HandleClient> rematchWait = new HashMap<>();
    private static final Set<String> blockedFromPublicQueue = new HashSet<>();
    private static HandleClient publicWaiting = null;

    public HandleClient(Socket s, Set<String> names) {
        this.socket = s;
        this.activeUsers = names;
    }

    public void run() {
        try {
            this.in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            this.out = new PrintWriter(this.socket.getOutputStream(), true);

            while(true) {
                this.send("ENTER_USERNAME");
                String proposed = this.in.readLine();
                if (proposed == null) return;

                synchronized(this.activeUsers) {
                    if (!this.activeUsers.add(proposed)) {
                        this.send("USERNAME_TAKEN");
                        continue;
                    }

                    File statsFile = new File("user_stats/" + proposed + ".txt");
                    if (!statsFile.exists()) {
                        statsFile.getParentFile().mkdirs();
                        PrintWriter writer = new PrintWriter(statsFile);
                        writer.println("WINS:0");
                        writer.println("LOSSES:0");
                        writer.println("DRAWS:0");
                        writer.close();
                    }

                    this.username = proposed;
                    this.send("LOGIN_SUCCESS");
                    Logger.log(this.username + " logged in");
                }

                while((proposed = this.in.readLine()) != null) {
                    if ("WAIT_FOR_GAME".equals(proposed)) {
                        Logger.log(username + " is waiting for a game");
                        synchronized(HandleClient.class) {
                            if (this.reservedOpponent != null) {
                                rematchWait.put(this.username, this);
                                this.waiting = true;
                                blockedFromPublicQueue.add(this.username);
                                HandleClient opp = rematchWait.get(this.reservedOpponent);
                                if (opp != null && opp.waiting && this.reservedOpponent.equals(opp.username) && this.username.equals(opp.reservedOpponent)) {
                                    opp.waiting = this.waiting = false;
                                    opp.inGame = this.inGame = true;
                                    opp.opponent = this;
                                    this.opponent = opp;
                                    opp.playerId = 1;
                                    this.playerId = 2;
                                    opp.send("START:1:" + opp.username + ":" + this.username);
                                    this.send("START:2:" + opp.username + ":" + this.username);
                                    Logger.log("rematch started between " + opp.username + " and " + this.username);

                                    rematchWait.remove(this.username);
                                    rematchWait.remove(opp.username);
                                    blockedFromPublicQueue.remove(this.username);
                                    blockedFromPublicQueue.remove(opp.username);
                                }
                            } else if (blockedFromPublicQueue.contains(this.username)) {
                                Logger.log("cant play in public server to reserve rematch: " + this.username);
                            } else if (publicWaiting != null && !publicWaiting.socket.isClosed() && !blockedFromPublicQueue.contains(publicWaiting.username)) {
                                HandleClient opp = publicWaiting;

                                opp.waiting = this.waiting = false;
                                opp.inGame = this.inGame = true;
                                opp.opponent = this;
                                this.opponent = opp;
                                opp.playerId = 1;
                                this.playerId = 2;
                                opp.send("START:1:" + opp.username + ":" + this.username);
                                this.send("START:2:" + opp.username + ":" + this.username);
                                Logger.log("Game started between " + opp.username + " and " + this.username);

                                publicWaiting = null;
                            } else {
                                publicWaiting = this;
                                this.waiting = true;
                            }
                        }
                    } else if ("CANCEL_WAIT".equals(proposed)) {
                        Logger.log(username + " canceled waiting");
                        synchronized(HandleClient.class) {
                            if (publicWaiting == this) {
                                publicWaiting = null;
                            }

                            rematchWait.remove(this.username);
                            this.waiting = false;
                            this.reservedOpponent = null;
                            blockedFromPublicQueue.remove(this.username);
                            if (this.opponent != null && this.opponent.reservedOpponent != null && this.opponent.reservedOpponent.equals(this.username)) {
                                this.opponent.reservedOpponent = null;
                                blockedFromPublicQueue.remove(this.opponent.username);
                            }
                        }

                        this.send("CANCEL_CONFIRMED");
                    } else if (proposed.startsWith("MOVE:")) {
                        if (this.inGame && this.opponent != null && !this.opponent.socket.isClosed()) {
                            this.opponent.send(proposed);
                            Logger.log(username + " played move " + proposed);
                        }
                    } else if (proposed.startsWith("CHAT:")) {
                        if (this.opponent != null && !this.opponent.socket.isClosed()) {
                            this.opponent.send(proposed);
                            Logger.log(username + " sent message: " + proposed);
                        }
                    } else if ("WIN".equals(proposed)) {
                        if (this.inGame && this.opponent != null && !this.opponent.socket.isClosed()) {
                            this.send("WIN");
                            this.opponent.send("LOSE");
                            Logger.log(username + " won against " + opponent.username);
                            this.updateStat(this.username, "WINS");
                            this.updateStat(this.opponent.username, "LOSSES");

                            if (this.opponent != null) {
                                this.reservedOpponent = this.opponent.username;
                                this.opponent.reservedOpponent = this.username;
                                blockedFromPublicQueue.add(this.username);
                                blockedFromPublicQueue.add(this.opponent.username);
                            }

                            Logger.log("rematch reserved between " + username + " and " + opponent.username);
                        }
                        this.inGame = false;
                        if (this.opponent != null) this.opponent.inGame = false;
                        this.opponent = null;
                        this.playerId = 0;
                        Logger.log(username + " game session clear");
                    } else if ("DRAW".equals(proposed)) {
                        if (this.inGame && this.opponent != null && !this.opponent.socket.isClosed()) {
                            this.send("DRAW");
                            this.opponent.send("DRAW");
                            this.updateStat(this.username, "DRAWS");
                            this.updateStat(this.opponent.username, "DRAWS");
                            Logger.log("Game between " + this.username + " and " + this.opponent.username + " ended in draw");
                        }

                        this.inGame = false;
                        if (this.opponent != null) this.opponent.inGame = false;
                        this.opponent = null;
                        this.playerId = 0;
                        Logger.log(username + " game session clear");
                    } else if ("QUIT_GAME".equals(proposed)) {
                        Logger.log(username + " quit the game");
                        if (this.opponent != null && !this.opponent.socket.isClosed()) {
                            this.opponent.send("QUIT_GAME");
                            this.opponent.reservedOpponent = null;
                            blockedFromPublicQueue.remove(this.opponent.username);
                            this.opponent.opponent = null;
                            this.opponent.inGame = false;
                        }

                        this.send("QUIT_GAME");

                        this.inGame = false;
                        if (this.opponent != null) this.opponent.inGame = false;
                        this.opponent = null;
                        this.playerId = 0;
                        Logger.log(username + " game session clear");

                        this.reservedOpponent = null;
                        blockedFromPublicQueue.remove(this.username);
                    } else if ("GET_STATS".equals(proposed)) {
                        try {
                            File file = new File("user_stats/" + this.username + ".txt");
                            int wins = 0;
                            int losses = 0;
                            int draws = 0;
                            if (file.exists()) {
                                for(String line : Files.readAllLines(file.toPath())) {
                                    if (line.startsWith("WINS:")) wins = Integer.parseInt(line.split(":" )[1]);
                                    else if (line.startsWith("LOSSES:")) losses = Integer.parseInt(line.split(":" )[1]);
                                    else if (line.startsWith("DRAWS:")) draws = Integer.parseInt(line.split(":" )[1]);
                                }
                            }

                            this.send("STATS:" + wins + ":" + losses + ":" + draws);
                            Logger.log("Stats sent for " + this.username + ": W:" + wins + " L:" + losses + " D:" + draws);
                        } catch (IOException e) {
                            this.send("STATS:0:0:0");
                            Logger.log("couldn't send stats for " + this.username);
                        }
                    } else if ("LOGOUT".equals(proposed)) {
                        Logger.log(username + " logged out");
                        return;
                    }
                }

                return;
            }
        } catch (IOException e) {
            Logger.log("problem with client communication for user: " + username);
        } finally {
            this.cleanup();
        }
    }

    private void send(String s) {
        this.out.println(s);
        this.out.flush();
    }

    private void cleanup() {
        try {
            synchronized(this.activeUsers) {
                this.activeUsers.remove(this.username);
            }
            synchronized(HandleClient.class) {
                if (publicWaiting == this) {
                    publicWaiting = null;
                }
                rematchWait.remove(this.username);
                blockedFromPublicQueue.remove(this.username);
            }
            if (this.opponent != null && !this.opponent.socket.isClosed()) {
                this.opponent.send("QUIT_GAME");
                this.opponent.reservedOpponent = null;
                this.opponent.opponent = null;
                this.opponent.inGame = false;
                blockedFromPublicQueue.remove(this.opponent.username);
            }
            this.socket.close();
        } catch (IOException ignored) {
        }
        Logger.log("Connection closed for " + this.username);
    }

    private void updateStat(String user, String stat) {
        try {
            File file = new File("user_stats/" + user + ".txt");
            List<String> lines = new ArrayList<>(Files.readAllLines(file.toPath()));
            for (int i = 0; i < lines.size(); ++i) {
                if (lines.get(i).startsWith(stat)) {
                    String[] parts = lines.get(i).split(":" );
                    int count = Integer.parseInt(parts[1]) + 1;
                    lines.set(i, stat + ":" + count);
                }
            }
            Files.write(file.toPath(), lines, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
            FileChannel channel = FileChannel.open(file.toPath(), StandardOpenOption.WRITE);
            channel.force(true);
            channel.close();
            Logger.log("updated stats for " + user + " -> " + stat);
        } catch (IOException e) {
            Logger.log("couldn't update stats for " + user);
        }
    }
}
