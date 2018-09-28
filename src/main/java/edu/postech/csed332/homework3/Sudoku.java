package edu.postech.csed332.homework3;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import edu.postech.csed332.homework3.Game.WrongGameException;

/**
 * 
 * An instance of this class represents a even/odd Sudoku puzzle. This class must be immutable. That
 * is, you should define a constructor to create a Sudoku object with all the necessary information
 * for a concrete even/odd Sudoku puzzle.
 * 
 * You can freely add member variables and methods to implement this class.
 * 
 */
public class Sudoku {
	private static Logger log = LoggerFactory.getLogger(Sudoku.class);
	private char[][] sudoku = null;
	private Set<Solution> solutions = null;

	/**
	 * Creates a new sudoku game from given file.
	 *
	 * @param fileName the name of the input file
	 * @throws IOException
	 */
	public Sudoku(String fileName) throws IOException {

		// NOTE: DO NOT check each character of board input in this part.
		// Rather, throw exception when functions using each characters on runtime.

		// Check file readability
		File inputFile = new File(fileName);
		if (inputFile.isFile() && inputFile.canRead()) {

			try {

				FileInputStream input = new FileInputStream(inputFile);

				try {

					log.info("Reading a game from {}.", fileName);

					// File read by line
					BufferedReader read = new BufferedReader(new InputStreamReader(input));
					String line;
					int i = 0;

					// Initialize
					this.sudoku = new char[9][];

					try {

						// Read by line
						while ((line = read.readLine()) != null) {

							log.debug("A line entered: {} [{}]", line, line.length());

							if (line.length() != 9)
								log.warn("Line length is not ⑨, YOU STUPID!");

							sudoku[i++] = line.toCharArray();

						}

						log.info("Successfully read.");

					} finally {

						read.close();

					}

				} finally {

					input.close();

				}

			} catch (IOException e) {

				log.error("Check the file and its format.");
				throw e;

			}

		} else {

			log.error("File is not readable.");
			throw new IOException();

		}

	}

	/**
	 * Returns a set of Solution instance for the given sudoku puzzle.
	 * 
	 * @return the set of solutions
	 * @throws WrongGameException
	 */
	public Set<Solution> solve() throws WrongGameException {

		// NOTE: In general cases, there may VERY FEW number of wrong boards are existing.
		// So, it is good to have more focus on "Solving" rather than "Error detection". This why
		// "Error Detection" part is devided in two 'for' loops.

		log.info("Solving the game...");

		if (this.solutions == null) {

			HashSet<List<Integer>> disjunctions = new HashSet<List<Integer>>();
			HashSet<Solution> solutions = new HashSet<Solution>();

			/* All possibilities for empty cells (N^2 + 2N) */
			log.debug("[1] Calculating all possibilities for empty cells...");

			// Initialize impossibles (N)
			List<List<HashSet<Integer>>> impossibles = new ArrayList<List<HashSet<Integer>>>();
			for (int i = 0; i < 9; i++) {

				List<HashSet<Integer>> row = new ArrayList<HashSet<Integer>>();

				for (int j = 0; j < 9; j++) {

					row.add(new HashSet<Integer>());

				}

				impossibles.add(row);

			}

			// Inquiry all impossibles (N^2)
			// HACK: Check wrong board size and OoR number in this part!
			for (int i = 0; i < 9; i++) {

				for (int j = 0; j < 9; j++) {

					int cell;

					try {

						cell = Character.getNumericValue(sudoku[i][j]);

					} catch (IndexOutOfBoundsException e) {

						log.error("Wrong board! Lack of row or column.");
						throw new WrongGameException();

					}

					if (cell > 0 && cell < 10) {

						// Row & Column
						for (int k = 0; k < 9; k++) {

							log.debug(
									"Propagate impossible {} from ({}, {}) to ({}, {}) and ({}, {}).",
									cell, i + 1, j + 1, k + 1, j + 1, i + 1, k + 1);

							impossibles.get(k).get(j).add(cell);
							impossibles.get(i).get(k).add(cell);

						}

						// Sub-grid
						for (int k = (i / 3) * 3; k < (i / 3 + 1) * 3; k++) {

							for (int l = (j / 3) * 3; l < (j / 3 + 1) * 3; l++) {

								log.debug("Propagate impossible {} from ({}, {}) to ({}, {}).",
										cell, i + 1, j + 1, k + 1, l + 1);

								impossibles.get(k).get(l).add(cell);

							}

						}

					} else if (cell > 9) {

						log.error("Wrong predefined number! Out of range ({}).", cell);
						throw new WrongGameException();

					}

				}

			}

			// Add disjunctions based on impossibles (N)
			// HACK: Check bad character at empty cell in this part!
			// NOTE: Hardcoded!
			for (int i = 0; i < 9; i++) {

				List<HashSet<Integer>> row = impossibles.get(i);

				for (int j = 0; j < 9; j++) {

					if (Character.getNumericValue(sudoku[i][j]) < 0) {

						HashSet<Integer> cell = row.get(j);
						List<Integer> possibles = new ArrayList<Integer>();

						if (sudoku[i][j] == '*') {

							// Even
							if (!cell.contains(2)) {
								possibles.add(2 + 10 * (j + 1) + 100 * (i + 1));
							} else {
								disjunctions.add(
										Arrays.asList(-1 * (2 + 10 * (j + 1) + 100 * (i + 1))));
							}
							if (!cell.contains(4)) {
								possibles.add(4 + 10 * (j + 1) + 100 * (i + 1));
							} else {
								disjunctions.add(
										Arrays.asList(-1 * (4 + 10 * (j + 1) + 100 * (i + 1))));
							}
							if (!cell.contains(6)) {
								possibles.add(6 + 10 * (j + 1) + 100 * (i + 1));
							} else {
								disjunctions.add(
										Arrays.asList(-1 * (6 + 10 * (j + 1) + 100 * (i + 1))));
							}
							if (!cell.contains(8)) {
								possibles.add(8 + 10 * (j + 1) + 100 * (i + 1));
							} else {
								disjunctions.add(
										Arrays.asList(-1 * (8 + 10 * (j + 1) + 100 * (i + 1))));
							}

							disjunctions.add(possibles);
							disjunctions
									.add(Arrays.asList(-1 * (1 + 10 * (j + 1) + 100 * (i + 1))));
							disjunctions
									.add(Arrays.asList(-1 * (3 + 10 * (j + 1) + 100 * (i + 1))));
							disjunctions
									.add(Arrays.asList(-1 * (5 + 10 * (j + 1) + 100 * (i + 1))));
							disjunctions
									.add(Arrays.asList(-1 * (7 + 10 * (j + 1) + 100 * (i + 1))));
							disjunctions
									.add(Arrays.asList(-1 * (9 + 10 * (j + 1) + 100 * (i + 1))));

							log.debug("Impossible numbers for even cell ({}, {}) are {}.", i + 1,
									j + 1, StringUtils.join(Arrays.asList(cell), ", "));
							log.debug("Possible numbers for even cell ({}, {}) are {}.", i + 1,
									j + 1, StringUtils.join(possibles, ", "));

						} else if (sudoku[i][j] == '.') {

							// Odd
							if (!cell.contains(1)) {
								possibles.add(1 + 10 * (j + 1) + 100 * (i + 1));
							} else {
								disjunctions.add(
										Arrays.asList(-1 * (1 + 10 * (j + 1) + 100 * (i + 1))));
							}
							if (!cell.contains(3)) {
								possibles.add(3 + 10 * (j + 1) + 100 * (i + 1));
							} else {
								disjunctions.add(
										Arrays.asList(-1 * (3 + 10 * (j + 1) + 100 * (i + 1))));
							}
							if (!cell.contains(5)) {
								possibles.add(5 + 10 * (j + 1) + 100 * (i + 1));
							} else {
								disjunctions.add(
										Arrays.asList(-1 * (5 + 10 * (j + 1) + 100 * (i + 1))));
							}
							if (!cell.contains(7)) {
								possibles.add(7 + 10 * (j + 1) + 100 * (i + 1));
							} else {
								disjunctions.add(
										Arrays.asList(-1 * (7 + 10 * (j + 1) + 100 * (i + 1))));
							}
							if (!cell.contains(9)) {
								possibles.add(9 + 10 * (j + 1) + 100 * (i + 1));
							} else {
								disjunctions.add(
										Arrays.asList(-1 * (9 + 10 * (j + 1) + 100 * (i + 1))));
							}

							disjunctions.add(possibles);
							disjunctions
									.add(Arrays.asList(-1 * (2 + 10 * (j + 1) + 100 * (i + 1))));
							disjunctions
									.add(Arrays.asList(-1 * (4 + 10 * (j + 1) + 100 * (i + 1))));
							disjunctions
									.add(Arrays.asList(-1 * (6 + 10 * (j + 1) + 100 * (i + 1))));
							disjunctions
									.add(Arrays.asList(-1 * (8 + 10 * (j + 1) + 100 * (i + 1))));

							log.debug("Impossible numbers for odd cell ({}, {}) are {}.", i + 1,
									j + 1, StringUtils.join(Arrays.asList(cell), ", "));
							log.debug("Possible numbers for odd cell ({}, {}) are {}.", i + 1,
									j + 1, StringUtils.join(possibles, ", "));

						} else {

							log.error("Wrong character detected. Stopping the solving.");
							throw new WrongGameException();

						}

					}

				}

			}

			/* Add basic Sudoku rules (1) */
			log.debug("[2] Add Basic Sudoku Rules...");

			// Row/Column Level
			// XXX: i/j = column or row, k = number
			for (int i = 0; i < 9; i++) {

				for (int k = 1; k < 10; k++) {

					List<Integer> disjunctionRow = new ArrayList<Integer>();
					List<Integer> disjunctionColumn = new ArrayList<Integer>();

					for (int j = 0; j < 9; j++) {

						disjunctionRow.add(k + 10 * (j + 1) + 100 * (i + 1));
						disjunctionColumn.add(k + 10 * (i + 1) + 100 * (j + 1));

					}

					disjunctions.add(disjunctionRow);
					disjunctions.add(disjunctionColumn);

				}

			}

			// Sub-grid Level
			// XXX: i/j = grid position, k = number
			for (int k = 1; k < 10; k++) {

				for (int i = 0; i < 3; i++) {

					for (int j = 0; j < 3; j++) {

						disjunctions.add(Arrays
								.asList(new Integer[] {k + 10 * (1 + 3 * j) + 100 * (1 + 3 * i),
										k + 10 * (1 + 3 * j) + 100 * (2 + 3 * i),
										k + 10 * (1 + 3 * j) + 100 * (3 + 3 * i),
										k + 10 * (2 + 3 * j) + 100 * (1 + 3 * i),
										k + 10 * (2 + 3 * j) + 100 * (2 + 3 * i),
										k + 10 * (2 + 3 * j) + 100 * (3 + 3 * i),
										k + 10 * (3 + 3 * j) + 100 * (1 + 3 * i),
										k + 10 * (3 + 3 * j) + 100 * (2 + 3 * i),
										k + 10 * (3 + 3 * j) + 100 * (3 + 3 * i)}));

					}

				}

			}

			/* Convert disjunctions to SAT4J form (N) */
			log.debug("[3] Convert to Sat4j form...");


			/* Solve CNF */
			log.debug("[4] Solving CNF...");


			/* Convert CNF solutions to Solution form */
			log.debug("[5] Change to Solution form...");


		}

		log.info("{} solution(s) found!", this.solutions.size());

		return this.solutions;

	}

	/**
	 * Returns true if a given cell must contain an even number.
	 * 
	 * @param row    row number
	 * @param column column number
	 * @return true if a cell (row, column) must be filled by an even number; otherwise, return
	 *         false.
	 * @throws WrongGameException
	 */
	public Boolean isEven(int row, int column) throws WrongGameException {

		try {

			int cellValue = Character.getNumericValue(sudoku[row][column]);

			if ((cellValue < 10 && cellValue > 0 && cellValue % 2 == 0)
					|| (sudoku[row][column] == '*')) {

				return true;

			} else if ((cellValue < 10 && cellValue > 0 && cellValue % 2 == 1)
					|| sudoku[row][column] == '.') {

				return false;

			}

		} catch (IndexOutOfBoundsException e) {

			log.error("Wrong board! Lack of row or column.");
			throw new WrongGameException();

		}

		log.error("Wrong board! Inappropriate character detected.");
		throw new WrongGameException();

	}

	/**
	 * Return the initial value of a cell, given an even/odd Sudoku puzzle. If there is no value to
	 * get (e.g. out of range, or empty), return null.
	 * 
	 * @param row    row number
	 * @param column column number
	 * @return A value of a cell, if it is non-empty
	 */
	public Integer getValue(int row, int column) {

		try {

			return Character.getNumericValue(sudoku[row][column]);

		} catch (IndexOutOfBoundsException e) {

			return null;

		}

	}

	/**
	 * Returns raw game board.
	 * 
	 * @return "sudoku" local variable
	 */
	public char[][] getRawBoard() {

		return sudoku;

	}

}
