use std::collections::HashMap;
use std::fs::File;
use std::io::{self, prelude::*, BufReader};
use std::time::Instant;

const SIZE: usize = 9;

struct SudokuBoard {
    board: [[i32; SIZE]; SIZE],
}

impl SudokuBoard {
    fn new() -> Self {
        Self {
            board: [[0; SIZE]; SIZE],
        }
    }

    fn parse_board(&mut self, reader: &mut BufReader<File>) -> io::Result<()> {
        for i in 0..SIZE {
            let mut line = String::new();
            reader.read_line(&mut line)?;
            for (j, c) in line.trim().chars().enumerate() {
                self.board[i][j] = c.to_digit(10).unwrap() as i32;
            }
        }
        Ok(())
    }

    fn is_number_valid(&self, row: usize, col: usize, number: i32) -> bool {
        self.is_row_valid(row, number)
            && self.is_column_valid(col, number)
            && self.is_box_valid(row, col, number)
    }

    fn is_row_valid(&self, row: usize, number: i32) -> bool {
        !self.board[row].contains(&number)
    }

    fn is_column_valid(&self, col: usize, number: i32) -> bool {
        for i in 0..SIZE {
            if self.board[i][col] == number {
                return false;
            }
        }
        true
    }

    fn is_box_valid(&self, row: usize, col: usize, number: i32) -> bool {
        let box_row = row - row % 3;
        let box_col = col - col % 3;

        for i in box_row..box_row + 3 {
            for j in box_col..box_col + 3 {
                if self.board[i][j] == number {
                    return false;
                }
            }
        }
        true
    }

    fn print_board(&self) {
        for i in 0..SIZE {
            for j in 0..SIZE {
                print!("{} ", self.board[i][j]);
            }
            println!();
        }
    }

    fn solve(&mut self) -> bool {
        for row in 0..SIZE {
            for col in 0..SIZE {
                if self.board[row][col] == 0 {
                    for number in 1..=SIZE as i32 {
                        if self.is_number_valid(row, col, number) {
                            self.board[row][col] = number;
                            if self.solve() {
                                return true;
                            } else {
                                self.board[row][col] = 0;
                            }
                        }
                    }
                    return false;
                }
            }
        }
        true
    }
}

fn read_from_file(filename: &str) -> io::Result<HashMap<String, SudokuBoard>> {
    let file = File::open(filename)?;
    let mut reader = BufReader::new(file);
    let mut boards = HashMap::new();

    loop {
        let mut grid_name = String::new();
        if reader.read_line(&mut grid_name)? == 0 {
            break;
        }
        let mut board = SudokuBoard::new();
        board.parse_board(&mut reader)?;
        boards.insert(grid_name.trim().to_string(), board);
    }

    Ok(boards)
}

fn main() -> io::Result<()> {
    let filename = "/Users/jameshathaway/Desktop/SudokuSolvers/sudoku_solver/sudoku.txt";
    let mut boards = read_from_file(filename)?;

    let start_time = Instant::now();

    for (name, board) in &mut boards {
        println!("Solving {}...", name);
        if board.solve() {
            println!("Solved {}:", name);
            board.print_board();
        } else {
            println!("No solution found for {}", name);
        }
        println!();
    }

    let duration = start_time.elapsed();

    println!(
        "Total time taken to solve all puzzles: {} ms",
        duration.as_millis()
    );

    Ok(())
}
