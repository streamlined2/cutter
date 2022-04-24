package luxoft.ch.cutter;

import java.util.Arrays;
import java.util.Optional;

public class Board {

	enum IndexType {
		ROW, COLUMN;

		public static IndexType oppositeType(IndexType indexType) {
			return indexType == ROW ? COLUMN : ROW;
		}
	}

	record Index(int value, IndexType type) {
		public Index next() {
			return new Index(value + 1, type);
		}
	}

	private class Position {

		private int row;
		private int column;

		private Position(int row, int column) {
			this.row = row;
			this.column = column;
		}

		private boolean isValid() {
			return 0 <= row && row < height && 0 <= column && column < width;
		}

		private boolean isLessEqual(Index index) {
			return switch (index.type) {
			case ROW -> row <= index.value;
			case COLUMN -> column <= index.value;
			};
		}

		private boolean isLessThanLimit(IndexType indexType) {
			int limit = getLimit(indexType);
			return switch (indexType) {
			case ROW -> row < limit;
			case COLUMN -> column < limit;
			};
		}

		private Position getReflectedPosition(Index index) {
			return switch (index.type) {
			case ROW -> new Position(index.value + (index.value - 1 - row), column);
			case COLUMN -> new Position(row, index.value + (index.value - 1 - column));
			};
		}

		private Position nextPosition(Index index) {
			int nextColumn = column + 1;
			if (nextColumn >= lastColumn(index)) {
				return new Position(row + 1, 0);
			}
			return new Position(row, nextColumn);
		}

		private int lastColumn(Index index) {
			return index.type == IndexType.COLUMN ? index.value : width;
		}

		private Position turnClockwise(Position leftTop, int side, int times) {
			int count = times;
			Position position = this;
			do {
				int bottomRow = leftTop.row + side - 1;
				int rightColumn = leftTop.column + side - 1;
				int columnOffset = position.column - leftTop.column;
				int rowOffset = position.row - leftTop.row;
				if (isTopEdge(position, leftTop, side)) {
					position = new Position(leftTop.row + columnOffset, rightColumn);
				} else if (isRightEdge(position, leftTop, side)) {
					position = new Position(bottomRow, rightColumn - rowOffset);
				} else if (isBottomEdge(position, leftTop, side)) {
					position = new Position(bottomRow - (rightColumn - position.column), leftTop.column);
				} else {
					position = new Position(leftTop.row, leftTop.column + (bottomRow - position.row));
				}
			} while (--count > 0);
			return position;
		}

		private boolean isTopEdge(Position position, Position leftTop, int side) {
			return position.row == leftTop.row && position.column >= leftTop.column
					&& position.column < leftTop.column + side - 1;
		}

		private boolean isRightEdge(Position position, Position leftTop, int side) {
			return position.column == leftTop.column + side - 1 && position.row >= leftTop.row
					&& position.row < leftTop.row + side - 1;
		}

		private boolean isBottomEdge(Position position, Position leftTop, int side) {
			return position.row == leftTop.row + side - 1 && position.column > leftTop.column
					&& position.column < leftTop.column + side;
		}

		private Optional<Position> getTurnedReflectedPosition(Index index, int times) {
			Position position = getReflectedPosition(index);
			if (!position.isValid()) {
				return Optional.empty();
			}
			if (times == 0) {
				return Optional.of(position);
			}
			Position leftTop = getLeftTopPosition(index);
			int side = getSide(leftTop);
			if (side == 1) {
				return Optional.of(position);
			}
			return Optional.of(position.turnClockwise(leftTop, side, times));
		}

		private int getSide(Position leftTop) {
			return Math.min(getLimit(IndexType.ROW) - leftTop.row, getLimit(IndexType.COLUMN) - leftTop.column);
		}

		private Position getLeftTopPosition(Index index) {
			return switch (index.type) {
			case COLUMN -> new Position(0, index.value);
			case ROW -> new Position(index.value, 0);
			};
		}

	}

	private static final int EMPTY_BLOCK = 0;
	private static final int FIRST_PIECE = 1;
	private static final int SECOND_PIECE = 2;
	private static final int TURN_COUNT = 3;

	protected final int[] values;
	private final int width;
	private final int height;

	public Board(int[] input, int width) {
		if (width <= 0) {
			throw new IllegalArgumentException("width should be positive");
		}
		if (input.length % width != 0) {
			throw new IllegalArgumentException(
					"size %d of passed array should be factor of %d".formatted(input.length, width));
		}
		this.values = input;
		this.width = width;
		this.height = input.length / width;
	}

	public int[] getValues() {
		return values;
	}

	private int getIndex(int row, int col) {
		return row * width + col;
	}

	private int getValue(int row, int col) {
		return values[getIndex(row, col)];
	}

	private int getValue(Index index1, int index2) {
		return switch (index1.type) {
		case ROW -> getValue(index1.value, index2);
		case COLUMN -> getValue(index2, index1.value);
		};
	}

	private void setValue(Position position, int value) {
		values[getIndex(position.row, position.column)] = value;
	}

	private boolean isCellOccupied(Index index1, int index2) {
		return getValue(index1, index2) == FIRST_PIECE;
	}

	private boolean isCellOccupied(Position position) {
		return getValue(position.row, position.column) == FIRST_PIECE;
	}

	private int getLimit(IndexType indexType) {
		return indexType == IndexType.ROW ? height : width;
	}

	public Optional<Index> getSplitIndex(IndexType indexType) {
		int[] grades = getGrades(indexType);
		int total = grades[grades.length - 1];
		if (total % 2 == 0) {
			int half = total / 2;
			int index = Arrays.binarySearch(grades, half);
			return Optional.of(new Index(index, indexType));
		}
		return Optional.empty();
	}

	private int[] getGrades(IndexType indexType) {
		int[] grades = new int[getLimit(indexType)];
		int total = 0;
		for (int k = 0; k < grades.length; k++) {
			total += calculateTotal(new Index(k, indexType));
			grades[k] = total;
		}
		return grades;
	}

	private int calculateTotal(Index index) {
		int sum = 0;
		int limit = getLimit(IndexType.oppositeType(index.type));
		for (int k = 0; k < limit; k++) {
			if (isCellOccupied(index, k)) {
				sum++;
			}
		}
		return sum;
	}

	public Optional<Integer> getTurnCountForSameReflection(Index divisionIndex) {
		for (int turns = 0; turns < TURN_COUNT; turns++) {
			if (isTurnedReflectionSame(divisionIndex, turns)) {
				return Optional.of(Integer.valueOf(turns));
			}
		}
		return Optional.empty();
	}

	private boolean isTurnedReflectionSame(Index divisionIndex, int turns) {
		for (Position position = start(); hasNext(position,
				divisionIndex); position = position.nextPosition(divisionIndex)) {
			boolean occupied = isCellOccupied(position);
			Optional<Position> reflectedPosition = position.getTurnedReflectedPosition(divisionIndex, turns);
			if (reflectedPosition.isPresent()) {
				boolean reflectedOccupied = isCellOccupied(reflectedPosition.get());
				if (occupied != reflectedOccupied) {
					return false;
				}
			}
		}
		return true;
	}

	public void splitInTwoParts(Index divisionIndex, int turns) {
		for (Position position = start(); hasNext(position,
				divisionIndex); position = position.nextPosition(divisionIndex)) {
			Optional<Position> turnedReflectedPosition = position.getTurnedReflectedPosition(divisionIndex, turns);
			if (turnedReflectedPosition.isPresent() && isCellOccupied(turnedReflectedPosition.get())) {
				setValue(turnedReflectedPosition.get(), SECOND_PIECE);
			}
		}
	}

	private boolean hasNext(Position position, Index index) {
		if (!position.isLessThanLimit(IndexType.oppositeType(index.type))) {
			return false;
		}
		return position.isLessEqual(index);
	}

	private Position start() {
		return new Position(0, 0);
	}

}
