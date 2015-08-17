package cl.buildersoft.framework.util;

import static org.junit.Assert.*;

import org.junit.Test;

public class BSUtilsTest {

	@Test
	public void testUnSplitString() {
		String[] array = { "Hola", "Mundo", "como", "estan" };
		String expect = "Hola,Mundo,como,estan";

		String received = BSUtils.unSplitString(array, ",");
		assertTrue(expect.equals(received));

	}

}
