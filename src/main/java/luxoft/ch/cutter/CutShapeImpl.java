package luxoft.ch.cutter;

import java.util.Arrays;

public class CutShapeImpl extends CutShape {

	private static final int EMPTY_BLOCK = 0;
	private static final int FIRST_PIECE = 1;
	private static final int SECOND_PIECE = 2;

	enum IndexType {
		ROW, COLUMN;

		public static IndexType oppositeIndexType(IndexType indexType) {
			return indexType == ROW ? COLUMN : ROW;
		}
	}

	private final int height;

	public CutShapeImpl(int[] input, int width, boolean singleCut) {
		super(input, width, singleCut);
		this.height = input.length / width;
	}

	private int getIndex(int row, int col) {
		int index = col;
		if (row > 0) {
			index += (row - 1) * width;
		}
		return index;
	}

	private int getValue(int row, int col) {
		return values[getIndex(row, col)];
	}

	private int getValue(int index1, int index2, IndexType index1Type) {
		return index1Type == IndexType.ROW ? getValue(index1, index2) : getValue(index2, index1);
	}

	private void setValue(int row, int col, int value) {
		values[getIndex(row, col)] = value;
	}

	private int sumValues(int index, IndexType indexType) {
		int sum = 0;
		int limit = getLimit(IndexType.oppositeIndexType(indexType));
		for (int k = 0; k < limit; k++) {
			if (getValue(index, k, indexType) == FIRST_PIECE) {
				sum++;
			}
		}
		return sum;
	}

	private int getLimit(IndexType indexType) {
		return indexType == IndexType.ROW ? height : width;
	}

	private int[] getGrades(IndexType indexType) {
		int[] grades = new int[getLimit(indexType)];
		int total = 0;
		for (int k = 0; k < grades.length; k++) {
			total += sumValues(k, indexType);
			grades[k] = total;
		}
		return grades;
	}

	private boolean isEven(int value) {
		return value % 2 == 0;
	}

	@Override
	public boolean cut() {
		int[] grades = getGrades(IndexType.COLUMN);
		int total = grades[grades.length - 1];
		if (isEven(total)) {
			int half = total / 2;
			int index = Arrays.binarySearch(grades, half);
			return index >= 0;
		}
		return false;
	}

	public static void main(String... args) {
		int[] testData = { 1, 1, 1 };
		System.out.println(new CutShapeImpl(testData, 3, true).cut());
	}

}
