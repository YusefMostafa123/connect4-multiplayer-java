/*
this is where the AI's logic is. this helps the AI make the best move to try to win.
while blocking teh other player and playing safe moves if needed
 */
package client;

import java.util.*;

public class Connect4Logic {

    public static int getBestMove(char[][] brd, char aiTkn, char optTkn) {
        List<Integer> vCols = new ArrayList<>();
        for (int c = 0; c < 7; c++) {
            if (brd[0][c] == '\0') vCols.add(c);
        }

        for (int c : vCols) {
            char[][] cpy = new char[6][7];
            for (int r = 0; r < 6; r++) System.arraycopy(brd[r], 0, cpy[r], 0, 7);
            for (int r = 5; r >= 0; r--) {
                if (cpy[r][c] == '\0') {
                    cpy[r][c] = aiTkn;
                    boolean win = false;
                    outer:
                    for (int i = 0; i < 6; i++) {
                        for (int j = 0; j < 7; j++) {
                            if (j + 3 < 7 && aiTkn == cpy[i][j] && aiTkn == cpy[i][j + 1] && aiTkn == cpy[i][j + 2] && aiTkn == cpy[i][j + 3]) {
                                win = true; break outer;
                            }
                            if (i + 3 < 6 && aiTkn == cpy[i][j] && aiTkn == cpy[i + 1][j] && aiTkn == cpy[i + 2][j] && aiTkn == cpy[i + 3][j]) {
                                win = true; break outer;
                            }
                            if (i + 3 < 6 && j + 3 < 7 && aiTkn == cpy[i][j] && aiTkn == cpy[i + 1][j + 1] && aiTkn == cpy[i + 2][j + 2] && aiTkn == cpy[i + 3][j + 3]) {
                                win = true; break outer;
                            }
                            if (i - 3 >= 0 && j + 3 < 7 && aiTkn == cpy[i][j] && aiTkn == cpy[i - 1][j + 1] && aiTkn == cpy[i - 2][j + 2] && aiTkn == cpy[i - 3][j + 3]) {
                                win = true; break outer;
                            }
                        }
                    }
                    if (win) return c;
                    break;
                }
            }
        }

        for (int c : vCols) {
            char[][] cpy = new char[6][7];
            for (int r = 0; r < 6; r++) System.arraycopy(brd[r], 0, cpy[r], 0, 7);
            for (int r = 5; r >= 0; r--) {
                if (cpy[r][c] == '\0') {
                    cpy[r][c] = optTkn;
                    boolean win = false;
                    outer:
                    for (int i = 0; i < 6; i++) {
                        for (int j = 0; j < 7; j++) {
                            if (j + 3 < 7 && optTkn == cpy[i][j] && optTkn == cpy[i][j + 1] && optTkn == cpy[i][j + 2] && optTkn == cpy[i][j + 3]) {
                                win = true; break outer;
                            }
                            if (i + 3 < 6 && optTkn == cpy[i][j] && optTkn == cpy[i + 1][j] && optTkn == cpy[i + 2][j] && optTkn == cpy[i + 3][j]) {
                                win = true; break outer;
                            }
                            if (i + 3 < 6 && j + 3 < 7 && optTkn == cpy[i][j] && optTkn == cpy[i + 1][j + 1] && optTkn == cpy[i + 2][j + 2] && optTkn == cpy[i + 3][j + 3]) {
                                win = true; break outer;
                            }
                            if (i - 3 >= 0 && j + 3 < 7 && optTkn == cpy[i][j] && optTkn == cpy[i - 1][j + 1] && optTkn == cpy[i - 2][j + 2] && optTkn == cpy[i - 3][j + 3]) {
                                win = true; break outer;
                            }
                        }
                    }
                    if (win) return c;
                    break;
                }
            }
        }

        List<Integer> sCols = new ArrayList<>();
        for (int c : vCols) {
            char[][] cpy = new char[6][7];
            for (int r = 0; r < 6; r++) System.arraycopy(brd[r], 0, cpy[r], 0, 7);
            for (int r = 5; r >= 0; r--) {
                if (cpy[r][c] == '\0') {
                    cpy[r][c] = aiTkn;
                    break;
                }
            }
            boolean bad = false;
            for (int oC = 0; oC < 7; oC++) {
                char[][] oppCpy = new char[6][7];
                for (int r = 0; r < 6; r++) System.arraycopy(cpy[r], 0, oppCpy[r], 0, 7);
                for (int r = 5; r >= 0; r--) {
                    if (oppCpy[r][oC] == '\0') {
                        oppCpy[r][oC] = optTkn;
                        boolean win = false;
                        outer:
                        for (int i = 0; i < 6; i++) {
                            for (int j = 0; j < 7; j++) {
                                if (j + 3 < 7 && optTkn == oppCpy[i][j] && optTkn == oppCpy[i][j + 1] && optTkn == oppCpy[i][j + 2] && optTkn == oppCpy[i][j + 3]) {
                                    win = true; break outer;
                                }
                                if (i + 3 < 6 && optTkn == oppCpy[i][j] && optTkn == oppCpy[i + 1][j] && optTkn == oppCpy[i + 2][j] && optTkn == oppCpy[i + 3][j]) {
                                    win = true; break outer;
                                }
                                if (i + 3 < 6 && j + 3 < 7 && optTkn == oppCpy[i][j] && optTkn == oppCpy[i + 1][j + 1] && optTkn == oppCpy[i + 2][j + 2] && optTkn == oppCpy[i + 3][j + 3]) {
                                    win = true; break outer;
                                }
                                if (i - 3 >= 0 && j + 3 < 7 && optTkn == oppCpy[i][j] && optTkn == oppCpy[i - 1][j + 1] && optTkn == oppCpy[i - 2][j + 2] && optTkn == oppCpy[i - 3][j + 3]) {
                                    win = true; break outer;
                                }
                            }
                        }
                        if (win) {
                            bad = true;
                        }
                        break;
                    }
                }
                if (bad) break;
            }
            if (!bad) sCols.add(c);
        }

        if (sCols.contains(3)) return 3;

        if (!sCols.isEmpty()) Collections.shuffle(sCols);
        else sCols = vCols;
        if (sCols.isEmpty()) {
            return -1;
        } else {
            return sCols.get(0);
        }
    }
}
