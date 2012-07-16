import static org.junit.Assert.fail;

import java.io.IOException;

import javax.swing.text.BadLocationException;

import org.junit.Test;

public class AdaptCrawlerTest extends AdaptCrawler {

	/** Instance variables */
	private String testURL = "http://abelsoul.co.uk";

	AdaptCrawler classToTest = new AdaptCrawler();

	@Test
	public void testMain() {
		fail("Not yet implemented");
	}

	@Test
	public void testProcessURL() {
		// classToTest.processURL(testURL);
		// assertEquals("Result", actuals);
	}

	@Test
	public void testParsePage() throws IOException, BadLocationException {
		classToTest.parsePage(testURL);
	}

	@Test
	public void testHttpURLConnection() throws IOException {
		classToTest.HttpURLConnection(testURL);
		// assertEquals("Result", actuals );
	}

	@Test
	public void testHtmlParse() throws IOException {
		classToTest.HtmlParse(testURL);
	}

}
