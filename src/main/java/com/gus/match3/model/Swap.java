package com.gus.match3.model;

import java.util.Objects;

public class Swap {
	private Coordinate coord;
	private Direction dir;
	
	public Swap(Coordinate coord, Direction dir) {
		super();
		this.coord = coord;
		this.dir = dir;
	}

	public Coordinate getCoord() {
		return coord;
	}

	public Direction getDir() {
		return dir;
	}

	@Override
	public int hashCode() {
		return Objects.hash(coord, dir);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Swap other = (Swap) obj;
		return Objects.equals(coord, other.coord) && dir == other.dir;
	}

	@Override
	public String toString() {
		return "Swap [coord=" + coord + ", dir=" + dir + "]";
	}
	
	
}
