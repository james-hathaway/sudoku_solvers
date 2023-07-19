import numpy as np
import time

class SudokuBoard:
    SIZE = 9

    def __init__(self):
        self.board = np.zeros((self.SIZE, self.SIZE), dtype=int)

    def _parse_board(self, lines):
        for i in range(self.SIZE):
            line = lines[i]
            for j in range(self.SIZE):
                self.board[i, j] = int(line[j])

    @staticmethod
    def read_from_file(filename):
        boards = {}
        with open(filename, 'r') as file:
            lines = file.read().splitlines()
            i = 0
            while i < len(lines):
                grid_name = lines[i]
                board_lines = lines[i+1:i+1+SudokuBoard.SIZE]
                board = SudokuBoard()
                board._parse_board(board_lines)
                boards[grid_name] = board
                i += SudokuBoard.SIZE + 1
        return boards

    def _is_number_valid(self, row, col, number):
        return self._is_row_valid(row, number) and self._is_column_valid(col, number) and self._is_box_valid(row, col, number)

    def _is_row_valid(self, row, number):
        return not number in self.board[row, :]

    def _is_column_valid(self, col, number):
        return not number in self.board[:, col]

    def _is_box_valid(self, row, col, number):
        box_row = row - row % 3
        box_col = col - col % 3
        return not number in self.board[box_row:box_row+3, box_col:box_col+3]

    def print_board(self):
        for row in self.board:
            print(' '.join(map(str, row)))
        print()

    def solve(self):
        for row in range(self.SIZE):
            for col in range(self.SIZE):
                if self.board[row, col] == 0:
                    for number in range(1, self.SIZE + 1):
                        if self._is_number_valid(row, col, number):
                            self.board[row, col] = number

                            if self.solve():
                                return True
                            else:
                                self.board[row, col] = 0
                    return False
        return True
    
    import time

if __name__ == "__main__":
    try:
        boards = SudokuBoard.read_from_file("sudoku.txt")

        start_time = time.time()

        for grid_name, board in boards.items():
            print(f"Solving {grid_name}...")

            if board.solve():
                print(f"Solved {grid_name}:")
                board.print_board()
            else:
                print(f"No solution found for {grid_name}")

        end_time = time.time()
        duration = (end_time - start_time) * 1000

        print(f"Total time taken to solve all puzzles: {duration:.2f} ms")

    except FileNotFoundError as e:
        print(f"Error: {e}")

