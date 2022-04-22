package luxoft.ch.cutter;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class CutShapeTest {
	
	@Test
	void test3() {
		int[] values = {1, 1, 1};
		CutShape cutter = new CutShapeImpl(values, 3, false);
		assertFalse(cutter.cut());
	}

	@Test
	void test4() {
		int[] values = {1, 1, 1, 1};
		int[] expectedResult = {1, 1, 2, 2};
		CutShape cutter = new CutShapeImpl(values, 4, true);
		assertTrue(cutter.cut());
		assertArrayEquals(expectedResult, cutter.getValues());
	}
	
	@Test
	void test6() {
		int[] values = {0, 1, 1, 1,
						1, 1, 0, 1};
		int[] expectedResult = {0, 1, 2, 2,
							  1, 1, 0, 2};
		CutShape cutter = new CutShapeImpl(values, 4, true);
		assertTrue(cutter.cut());
		assertArrayEquals(expectedResult, cutter.getValues());
	}
	
	@Test
	void test8() {
		int[] values = {0, 1, 1, 0,
						1, 1, 1, 1,
						1, 0, 0, 1};
		int[] expectedResult = {0, 1, 2, 0,
							  1, 1, 2, 2,
							  1, 0, 0, 2};
		CutShape cutter = new CutShapeImpl(values, 4, true);
		assertTrue(cutter.cut());
		assertArrayEquals(expectedResult, cutter.getValues());
	}
	
	@Test
	void testDisconnectedOnly() {
		int[] values = {1, 0, 1, 1,
						0, 0, 0, 0,
						0, 0, 0, 1};
		int[] expectedResult = {1, 0, 1, 2,
								0, 0, 0, 0,
								0, 0, 0, 2};
		CutShape cutter = new CutShapeImpl(values, 4, true);
		assertFalse(cutter.cut());
		cutter = new CutShapeImpl(values, 4, false);
		assertTrue(cutter.cut());
		assertArrayEquals(expectedResult, cutter.getValues());
	}
	
	@Test
	void testDisconnectedOnly2() {
		int[] values = { 0, 0, 0, 1, 0, 0, 
						 0, 1, 1, 1, 1, 1, 
						 1, 1, 1, 1, 1, 1, 
						 1, 0, 1, 0, 0, 0 };
		int[] expectedResult = { 0, 0, 0, 1, 0, 0, 
								0, 1, 2, 1, 1, 1, 
								2, 2, 2, 1, 2, 1, 
								2, 0, 2, 0, 0, 0 };
		CutShape cutter = new CutShapeImpl(values, 6, true);
		assertFalse(cutter.cut());
		cutter = new CutShapeImpl(values, 6, false);
		assertTrue(cutter.cut());
		assertArrayEquals(expectedResult, cutter.getValues());
	}
	
	@Test
	void testConnectedOnly() {
		int[] values = { 0, 0, 1, 1, 1, 
						 1, 1, 1, 1, 1, 
						 1, 1, 1, 1, 0,
						 1, 1, 1, 1, 0 };
		int[] expectedResult = { 0, 0, 1, 1, 2, 
								 1, 1, 1, 2, 2, 
								 1, 1, 2, 2, 0,
								 1, 2, 2, 2, 0 };
		CutShape cutter = new CutShapeImpl(values, 5, true);
		assertTrue(cutter.cut());
		assertArrayEquals(expectedResult, cutter.getValues());
	}
	
}
