import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class SudokuBoard {

    private static final int SIZE = 9;

    private int[][] board;

    public SudokuBoard() {
        this.board = new int[SIZE][SIZE];
    }

    private void parseBoard(Scanner sc) {
        for (int i = 0; i < SIZE; i++) {
            String line = sc.nextLine();
            for (int j = 0; j < SIZE; j++) {
                this.board[i][j] = Character.getNumericValue(line.charAt(j));
            }
        }
    }

    public static Map<String, SudokuBoard> readFromFile(String filename) throws FileNotFoundException {
        Map<String, SudokuBoard> boards = new HashMap<>();
        try (Scanner sc = new Scanner(new File(filename))) {
            while (sc.hasNextLine()) {
                String gridName = sc.nextLine();
                SudokuBoard board = new SudokuBoard();
                board.parseBoard(sc);
                boards.put(gridName, board);
            }
        }
        return boards;
    }

    public boolean isNumberValid(int row, int col, int number) {
        return isRowValid(row, number) && isColumnValid(col, number) && isBoxValid(row, col, number);
    }

    private boolean isRowValid(int row, int number) {
        for (int i = 0; i < SIZE; i++) {
            if (board[row][i] == number) {
                return false;
            }
        }
        return true;
    }

    private boolean isColumnValid(int col, int number) {
        for (int i = 0; i < SIZE; i++) {
            if (board[i][col] == number) {
                return false;
            }
        }
        return true;
    }

    private boolean isBoxValid(int row, int col, int number) {
        int boxRow = row - row % 3;
        int boxCol = col - col % 3;

        for (int i = boxRow; i < boxRow + 3; i++) {
            for (int j = boxCol; j < boxCol + 3; j++) {
                if (board[i][j] == number) {
                    return false;
                }
            }
        }
        return true;
    }

    public void printBoard() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                System.out.print(board[i][j] + " ");
            }
            System.out.println();
        }
    }

    public boolean solve() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                if (board[row][col] == 0) {
                    for (int number = 1; number <= SIZE; number++) {
                        if (isNumberValid(row, col, number)) {
                            board[row][col] = number;

                            if (solve()) {
                                return true;
                            } else {
                                board[row][col] = 0; // undo the move
                            }
                        }
                    }
                    return false; // no valid numbers can be placed in this cell
                }
            }
        }
        return true; // all cells are filled
    }

    public static void main(String[] args) {
        try {
            Map<String, SudokuBoard> boards = SudokuBoard.readFromFile("sudoku.txt");

            long startTime = System.nanoTime();

            for (Map.Entry<String, SudokuBoard> entry : boards.entrySet()) {
                System.out.println("Solving " + entry.getKey() + "...");

                if (entry.getValue().solve()) {
                    System.out.println("Solved " + entry.getKey() + ":");
                    entry.getValue().printBoard();
                } else {
                    System.out.println("No solution found for " + entry.getKey());
                }

                System.out.println();
            }

            long endTime = System.nanoTime();
            long duration = (endTime - startTime) / 1000000;  // divide by 1000000 to get milliseconds.

            System.out.println("Total time taken to solve all puzzles: " + duration + " ms");

        } catch (FileNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
