package luxoft.ch.cutter;

public abstract class CutShape {

	private final int width;
	private final boolean singleCut;
	private final int[] values;

	public abstract boolean cut();

	protected CutShape(int[] input, int width, boolean singleCut) {
		this.values = input;
		this.width = width;
		this.singleCut = singleCut;
	}

	public int[] getValues() {
		return values;
	}

	public int getWidth() {
		return width;
	}

	public boolean isSingleCut() {
		return singleCut;
	}

}