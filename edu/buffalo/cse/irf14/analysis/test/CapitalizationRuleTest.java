	/**
 * 
 */
package edu.buffalo.cse.irf14.analysis.test;

import static org.junit.Assert.*;

import org.junit.Test;

import edu.buffalo.cse.irf14.analysis.TokenFilterType;
import edu.buffalo.cse.irf14.analysis.TokenizerException;

/**
 * @author nikhillo
 * 
 */
public class CapitalizationRuleTest extends TFRuleBaseTest {

	@Test
	public void testRule() {
			try {
					assertArrayEquals(new String[] { "this", "is", "a", "test." },
							runTest(TokenFilterType.CAPITALIZATION, "This is a test."));
					assertArrayEquals(new String[] {"the", "city", "San Francisco", "is",
							"in", "California." },
							runTest(TokenFilterType.CAPITALIZATION, "The city San Francisco is in California."));
					assertArrayEquals(
							new String[] {"some", "bodily", "fluids,", "such",
									"as", "saliva", "and", "tears,", "do", "not",
									"transmit", "HIV" },
							runTest(TokenFilterType.CAPITALIZATION, "Some bodily fluids, such as saliva and tears, do not transmit HIV"));
					assertArrayEquals(
							new String[] { "a", "runs", "Apple's", "iOS",
									"mobile", "operating", "system," },
							runTest(TokenFilterType.CAPITALIZATION, "A runs Apple's iOS mobile operating system,"));
					assertArrayEquals(
							new String[] { "my", "name", "is", "mimanshu,"},
							runTest(TokenFilterType.CAPITALIZATION, "MY NAME IS MIMANSHU,"));
			} catch (TokenizerException e) {
				fail("Exception thrown when not expected!");
			}
	}

}
