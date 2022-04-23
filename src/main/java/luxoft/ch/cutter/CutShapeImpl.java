package luxoft.ch.cutter;

import java.util.Optional;
import luxoft.ch.cutter.Board.Index;
import luxoft.ch.cutter.Board.IndexType;;

public class CutShapeImpl implements CutShape {

	private final boolean singleCut;
	private final Board board;

	public CutShapeImpl(int[] input, int width, boolean singleCut) {
		this.singleCut = singleCut;
		this.board = new Board(input, width);
	}

	@Override
	public int[] getValues() {
		return board.getValues();
	}

	@Override
	public boolean cut() {
		Optional<Index> index = board.getSplitIndex(IndexType.COLUMN);
		if (index.isEmpty()) {
			return false;
		}
		Index splitIndex = index.get().next();
		boolean reflectionIdentical = board.isReflectionSame(splitIndex);
		if (reflectionIdentical) {
			board.split(splitIndex);
		}
		return reflectionIdentical;
	}

}
