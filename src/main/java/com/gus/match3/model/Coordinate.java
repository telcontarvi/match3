package com.gus.match3.model;

import java.util.Objects;

public class Coordinate {
	private int row;
	private int col;
	
	public Coordinate(int row, int col) {
		super();
		this.row = row;
		this.col = col;
	}

	public int getRow() {
		return row;
	}

	public int getCol() {
		return col;
	}

	@Override
	public int hashCode() {
		return Objects.hash(col, row);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Coordinate other = (Coordinate) obj;
		return col == other.col && row == other.row;
	}

	@Override
	public String toString() {
		return "Coordinate [row=" + row + ", col=" + col + "]";
	}
	
	
}
