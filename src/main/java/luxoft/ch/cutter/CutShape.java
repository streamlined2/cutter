package luxoft.ch.cutter;

public abstract class CutShape {

	protected final int width;
	protected final boolean singleCut;
	protected final int[] values;

	public abstract boolean cut();

	protected CutShape(int[] input, int width, boolean singleCut) {
		if (width <= 0) {
			throw new IllegalArgumentException("width should be positive");
		}
		if (input.length % width != 0) {
			throw new IllegalArgumentException(
					"size %d of passed array should be factor of %d".formatted(input.length, width));
		}
		this.values = input;
		this.width = width;
		this.singleCut = singleCut;
	}

	public int[] getValues() {
		return values;
	}

}