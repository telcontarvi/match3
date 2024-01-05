package com.gus.match3.model;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Stage {
	private int rows;
	private int cols;
	private char[][] board;
	private Map<Character, Integer> pieceCountMap;

	private Stage stageFrom;
	private Swap swapFrom;

	public Stage(int rows, int cols) {
		super();
		this.rows = rows;
		this.cols = cols;
		this.board = new char[rows][cols];

		fillBoardEmpty();

		this.pieceCountMap = new HashMap<>();
	}

	public Stage(char[][] board) {
		this.rows = board.length;
		this.cols = board[0].length;
		this.board = new char[rows][];
		for (int row = 0; row < rows; row++) {
			this.board[row] = Arrays.copyOf(board[row], cols);
		}
		this.pieceCountMap = new HashMap<>();
		stabiliceBoard();
	}

	public Stage(Stage stageFrom, Swap swapFrom) {
		this(stageFrom.board);
		this.swap(swapFrom);

		this.pieceCountMap = new HashMap<>();
		this.stageFrom = stageFrom;
		this.swapFrom = swapFrom;
		stabiliceBoard();
	}

	public int getRows() {
		return rows;
	}

	public int getCols() {
		return cols;
	}

	public Stage getStageFrom() {
		return stageFrom;
	}

	public Swap getSwapFrom() {
		return swapFrom;
	}

	public boolean isDeadEnd() {
		return pieceCountMap.values().stream().anyMatch(v -> v > 0 && v < 3);
	}

	public boolean isSolved() {
		return pieceCountMap.values().stream().allMatch(v -> v == 0);
	}

	public Set<Swap> getPosibleSwaps() {
		Set<Swap> posibleSwaps = new HashSet<>();
		for (int row = 0; row < rows; row++) {
			for (int col = 0; col < cols; col++) {
				Coordinate coord = new Coordinate(row, col);
				posibleSwaps.addAll(getPosibleSwaps(coord));
			}
		}
		return posibleSwaps;
	}

	private void fillBoardEmpty() {
		for (char[] row : board) {
			Arrays.fill(row, Match3Constants.EMPTY_CHAR);
		}
	}

	private char getPiece(Coordinate coord) {
		try {
			return board[coord.getRow()][coord.getCol()];
		} catch (ArrayIndexOutOfBoundsException ex) {
			throw new ArrayIndexOutOfBoundsException(
					"Error accesing board of " + rows + " x " + cols + " with " + coord);
		}
	}

	private void setPiece(Coordinate coord, char piece) {
		try {
			board[coord.getRow()][coord.getCol()] = piece;
		} catch (ArrayIndexOutOfBoundsException ex) {
			throw new ArrayIndexOutOfBoundsException(
					"Error setting piece in a board of " + rows + " x " + cols + " with " + coord);
		}
	}

	private void emptyCoordinate(Coordinate coord) {
		this.setPiece(coord, Match3Constants.EMPTY_CHAR);
	}

	private void calcPieceCountMap() {
		pieceCountMap.clear();
		for (int row = 0; row < rows; row++) {
			for (int col = 0; col < cols; col++) {
				char piece = board[row][col];
				if (isPieceMovible(piece)) {
					pieceCountMap.merge(piece, 1, (v1, v2) -> v1 + v2);
				}
			}
		}
	}

	private void collapseBoard() {
		for (int col = 0; col < cols; col++) {
			int baseRow = rows - 1;
			for (int row = rows - 1; row >= 0; row--) {
				char piece = board[row][col];
				if (!isPieceEmpty(piece)) {
					if (!isPieceWall(piece) && baseRow > row) {
						board[baseRow][col] = piece;
						board[row][col] = Match3Constants.EMPTY_CHAR;
						baseRow = baseRow - 1;
					} else {
						baseRow = row - 1;
					}
				}
			}
		}
	}

	private void stabiliceBoard() {
		collapseBoard();
		banishBoad();
	}

	private void banishBoad() {
		Set<Coordinate> coordsInMatch3 = getCoordinatesInMatch3();
		if (coordsInMatch3.size() > 0) {
			coordsInMatch3.forEach(c -> emptyCoordinate(c));
			stabiliceBoard();
		} else {
			calcPieceCountMap();
		}
	}

	private Set<Coordinate> getCoordinatesInMatch3() {
		Set<Coordinate> coordsInMatch3 = new HashSet<>();
		for (int row = 0; row < rows; row++) {
			for (int col = 0; col < cols; col++) {
				Coordinate coord = new Coordinate(row, col);
				coordsInMatch3.addAll(getCoordinatesInMatch3ByStartingCoord(coord));
			}
		}
		return coordsInMatch3;
	}

	private Set<Coordinate> getCoordinatesInMatch3ByStartingCoord(Coordinate coord) {
		return getCoordinatesInMatch3ByStartingCoord(coord, false);
	}

	private Set<Coordinate> getCoordinatesInMatch3ByStartingCoord(Coordinate coord, boolean allDirections) {
		Set<Coordinate> coordsInMatch3 = new HashSet<>();
		char piece = getPiece(coord);
		if (isPieceMovible(piece)) {
			if (coord.getRow() < rows - 2) {
				addCoordsIfInMatch3(coord, Direction.DOWN, coordsInMatch3);
			}
			if (coord.getCol() < cols - 2) {
				addCoordsIfInMatch3(coord, Direction.RIGHT, coordsInMatch3);
			}
			if (allDirections) {
				if (coord.getRow() > 1) {
					addCoordsIfInMatch3(coord, Direction.UP, coordsInMatch3);
				}

				if (coord.getCol() > 1) {
					addCoordsIfInMatch3(coord, Direction.LEFT, coordsInMatch3);
				}
			}
		}
		return coordsInMatch3;
	}

	private void addCoordsIfInMatch3(Coordinate c1, Direction dir, Collection<Coordinate> coordsInMatch3) {
		Coordinate c2 = nextCoordinate(c1, dir);
		Coordinate c3 = nextCoordinate(c2, dir);
		if (c1 != null && c2 != null && c3 != null) {
			char piece = getPiece(c1);
			if (isPieceMovible(piece) && piece == getPiece(c2) && piece == getPiece(c3)) {
				coordsInMatch3.add(c1);
				coordsInMatch3.add(c2);
				coordsInMatch3.add(c3);
			}
		}

	}

	private Coordinate nextCoordinate(Coordinate coord, Direction dir) {
		if (coord == null) {
			return null;
		}
		switch (dir) {
		case DOWN:
			return coord.getRow() < rows - 1 ? new Coordinate(coord.getRow() + 1, coord.getCol()) : null;
		case RIGHT:
			return coord.getCol() < cols - 1 ? new Coordinate(coord.getRow(), coord.getCol() + 1) : null;
		case UP:
			return coord.getCol() > 0 ? new Coordinate(coord.getRow() - 1, coord.getCol()) : null;
		case LEFT:
			return coord.getCol() > 0 ? new Coordinate(coord.getRow(), coord.getCol() - 1) : null;
		default:
			return null;
		}
	}

	private Set<Swap> getPosibleSwaps(Coordinate coord) {
		Set<Swap> posibleSwaps = new HashSet<>();
		if (isPieceMovible(coord)) {
			if (testSwapable(coord, Direction.DOWN)) {
				posibleSwaps.add(new Swap(coord, Direction.DOWN));
			}
			if (testSwapable(coord, Direction.RIGHT)) {
				posibleSwaps.add(new Swap(coord, Direction.RIGHT));
			}
		}
		return posibleSwaps;
	}

	private boolean testSwapable(Coordinate c1, Direction dir) {
		boolean retorno = false;
		Coordinate c2 = nextCoordinate(c1, dir);
		if (isPieceMovible(c2)) {
			swap(c1, c2);
			retorno = getCoordinatesInMatch3ByStartingCoord(c1, true).size() > 0
					|| getCoordinatesInMatch3ByStartingCoord(c2, true).size() > 0;
			swap(c1, c2);
		}
		return retorno;
	}

	private boolean isPieceMovible(Coordinate coord) {
		return coord != null && isPieceMovible(getPiece(coord));
	}

	private void swap(Swap s) {
		Coordinate c1 = s.getCoord();
		Coordinate c2 = nextCoordinate(c1, s.getDir());
		swap(c1, c2);
	}

	private void swap(Coordinate c1, Coordinate c2) {
		char piece1 = getPiece(c1);
		char piece2 = getPiece(c2);
		setPiece(c1, piece2);
		setPiece(c2, piece1);
	}

	public void paint() {
		for (int row = 0; row < rows; row++) {
			System.out.println(new String(board[row]));
		}
		System.out.println("------");
		System.out.println(pieceCountMap);
		System.out.println("------------");
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.deepHashCode(board);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Stage other = (Stage) obj;
		return Arrays.deepEquals(board, other.board);
	}

	public static boolean isPieceEmpty(char piece) {
		return piece == Match3Constants.EMPTY_CHAR;
	}

	public static boolean isPieceWall(char piece) {
		return piece == Match3Constants.WALL_CHAR;
	}

	public static boolean isPieceMovible(char piece) {
		return !isPieceEmpty(piece) && !isPieceWall(piece);
	}
}
