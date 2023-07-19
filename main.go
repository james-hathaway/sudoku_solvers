package main

import (
	"bufio"
	"fmt"
	"os"
	"strconv"
	"time"
)

const SIZE = 9

type SudokuBoard struct {
	board [][]int
}

func NewSudokuBoard() *SudokuBoard {
	return &SudokuBoard{
		board: make([][]int, SIZE),
	}
}

func (b *SudokuBoard) ParseBoard(s *bufio.Scanner) {
	for i := 0; i < SIZE; i++ {
		s.Scan()
		line := s.Text()
		b.board[i] = make([]int, SIZE)
		for j := 0; j < SIZE; j++ {
			b.board[i][j], _ = strconv.Atoi(string(line[j]))
		}
	}
}

func ReadFromFile(filename string) (map[string]*SudokuBoard, error) {
	f, err := os.Open(filename)
	if err != nil {
		return nil, err
	}
	defer f.Close()

	boards := make(map[string]*SudokuBoard)
	s := bufio.NewScanner(f)

	for s.Scan() {
		gridName := s.Text()
		board := NewSudokuBoard()
		board.ParseBoard(s)
		boards[gridName] = board
	}

	return boards, nil
}

func (b *SudokuBoard) IsNumberValid(row, col, number int) bool {
	return b.IsRowValid(row, number) && b.IsColumnValid(col, number) && b.IsBoxValid(row, col, number)
}

func (b *SudokuBoard) IsRowValid(row, number int) bool {
	for i := 0; i < SIZE; i++ {
		if b.board[row][i] == number {
			return false
		}
	}
	return true
}

func (b *SudokuBoard) IsColumnValid(col, number int) bool {
	for i := 0; i < SIZE; i++ {
		if b.board[i][col] == number {
			return false
		}
	}
	return true
}

func (b *SudokuBoard) IsBoxValid(row, col, number int) bool {
	boxRow := row - row%3
	boxCol := col - col%3

	for i := boxRow; i < boxRow+3; i++ {
		for j := boxCol; j < boxCol+3; j++ {
			if b.board[i][j] == number {
				return false
			}
		}
	}
	return true
}

func (b *SudokuBoard) PrintBoard() {
	for i := 0; i < SIZE; i++ {
		for j := 0; j < SIZE; j++ {
			fmt.Printf("%d ", b.board[i][j])
		}
		fmt.Println()
	}
}

func (b *SudokuBoard) Solve() bool {
	for row := 0; row < SIZE; row++ {
		for col := 0; col < SIZE; col++ {
			if b.board[row][col] == 0 {
				for number := 1; number <= SIZE; number++ {
					if b.IsNumberValid(row, col, number) {
						b.board[row][col] = number

						if b.Solve() {
							return true
						} else {
							b.board[row][col] = 0
						}
					}
				}
				return false
			}
		}
	}
	return true
}

func main() {
	boards, err := ReadFromFile("sudoku.txt")
	if err != nil {
		fmt.Println("Error: ", err)
		return
	}

	startTime := time.Now()

	for k, v := range boards {
		fmt.Println("Solving ", k, "...")

		if v.Solve() {
			fmt.Println("Solved ", k, ":")
			v.PrintBoard()
		} else {
			fmt.Println("No solution found for ", k)
		}

		fmt.Println()
	}

	duration := time.Since(startTime)
	fmt.Println("Total time taken to solve all puzzles: ", duration.Milliseconds(), "ms")
}
