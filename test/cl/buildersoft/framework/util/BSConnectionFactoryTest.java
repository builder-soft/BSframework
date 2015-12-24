package cl.buildersoft.framework.util;

import static org.junit.Assert.fail;

import java.sql.Connection;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class BSConnectionFactoryTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testGetConnectionString() {
		BSConnectionFactory cf = new BSConnectionFactory();

		Connection conn = cf.getConnection("NOT_VALID");
		if (conn != null) {
			Assert.assertTrue(true);
		} else {
			fail("Not yet implemented");
		}
	}

	@Test
	public void testGetConnectionHttpServletRequest() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetConnection() {
		BSConnectionFactory cf = new BSConnectionFactory();

		Connection conn = cf.getConnection();
		if (conn != null) {
			Assert.assertTrue(true);
		} else {
			fail("Not yet implemented");
		}
	}

}
