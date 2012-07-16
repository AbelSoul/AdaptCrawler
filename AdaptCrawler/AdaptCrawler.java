import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.parser.ParserDelegator;

/**
 * This is the main class for a web crawler program for Emerge Adapt
 * 
 * @author Robert Wilson
 *
 */
public class AdaptCrawler {

	/** URL objects as instance variables */
	private static URL testURL, siteURL;

	/** String representing a default URL */
	private static String defaultURL = "http://www.tyre-shopper.co.uk/";

	/** Buffered reader to read incoming HTML */
	private static BufferedReader reader;

	/** String reader to read line of HTML as String */
	private static StringReader aReader;

	/** String representing page title */
	private static String title;

	/** integer representing the HTTP status code */
	private static int stCode;

	/** List of Strings representing anchor links */
	private static List<String> links;

	/** StringBuilder for use with CVS file */
	private static StringBuilder CvsString;

	/**
	 * This is the main method for the web crawler which checks for
	 * a URL entered at the command line and uses either this or default
	 * URL to pass to further methods for processing
	 * 
	 * @param args - the arguments for the command line
	 * 
	 * @throws IOException 
	 * @throws BadLocationException 
	 */
	public static void main(String[] args) throws IOException,
			BadLocationException {

		// String representing the URL
		String input = "";

		// Check if argument added at command line
		if (args.length >= 1) {
			input = args[0];
		}

		// If no argument at command line use default
		else {
			input = defaultURL;
			System.out.println("\nNo argument entered so default of " + input
					+ " used. \n");
		}

		// create String builder object for CVS file and add input URL
		CvsString = new StringBuilder();
		CvsString.append(input);
		CvsString.append(",");
		System.out.println("URL: " + input);

		// call methods to process URL and retrieve title - throws exceptions
		processURL(input);
		parsePage(input);

		// pass to method to create CVS file
		generateCsvFile(CvsString);
		System.out.println("cvs = " + CvsString);
	}

	/**
	 * This method processes the URL and fetches the HTML
	 * 
	 * @param input - a String representing the URL
	 * 
	 * @throws IOException
	 * @throws BadLocationException
	 */
	public static void processURL(String input) throws IOException,
			BadLocationException {

		// create array list of strings to represent page anchor links
		links = new ArrayList<String>();

		// add original URL to List
		links.add(input);

		// input test URL and read from file input stream
		try {
			testURL = new URL(input);
			reader = new BufferedReader(new InputStreamReader(
					testURL.openStream()));

			// String variable to hold the returned content
			String line = "";

			// print content to console until no new lines of content
			while ((line = reader.readLine()) != null) {
				// System.out.println(line);

				// pass each line to method to parse anchor tags for links to
				// other pages
				HtmlParse(line);
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
			System.out.println("Exception thrown");
		}
		HttpURLConnection(testURL.toString());
	}

	/**
	 * This method retrieves the title of the web page
	 * 
	 * @throws BadLocationException 
	 * @throws IOException 
	 */
	public static void parsePage(String pageURL) throws IOException,
			BadLocationException {

		// create new HTML editor kit object
		HTMLEditorKit kit = new HTMLEditorKit();
		HTMLDocument doc = (HTMLDocument) kit.createDefaultDocument();
		doc.putProperty("IgnoreCharsetDirective", Boolean.TRUE);

		// create new input reader
		Reader HTMLReader = new InputStreamReader(testURL.openConnection()
				.getInputStream());
		kit.read(HTMLReader, doc, 0);

		// retrieve title and convert to String
		title = (String) doc.getProperty(Document.TitleProperty);
		System.out.println("title: " + title);

		// add to CSV file
		CvsString.append(title);
		CvsString.append(",");
	}

	/**
	 * This method extracts the the HTTP status code as an integer and the URL
	 * then appends them to a String for use as a CVS file
	 * 
	 * @throws IOException 
	 */
	protected static void HttpURLConnection(String URL) throws IOException {

		try {
			URL pageURL = new URL(URL);

			HttpURLConnection connection = (HttpURLConnection) pageURL
					.openConnection();
			stCode = connection.getResponseCode();
			System.out.println("HTTP Status code: " + stCode);

			// append to CVS string
			CvsString.append(stCode);
			CvsString.append("\n");

			// retrieve URL
			siteURL = connection.getURL();
			System.out.println(siteURL + " = URL");

			CvsString.append(siteURL);
			CvsString.append(",");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method adds an HTML parser to make more sense of the content
	 * and extract anchor tags
	 * 
	 * Swing HTML parser is apparently not thread safe and
	 * if used in a heavily threaded environment crashing can occur
	 * This program only runs one thread at a time
	 *
	 * @param line - a String representing a line of HTML
	 * @throws IOException 
	 */
	public static void HtmlParse(String line) throws IOException {

		// create new string reader object
		aReader = new StringReader(line);

		// create HTML parser object
		HTMLEditorKit.Parser parser = new ParserDelegator();

		// // create array list of strings to represent links //TODO remove if
		// not needed
		// final List<String> links = new ArrayList<String>();
		//
		// // add original URL to List
		// links.add(testURL.toString());

		// parse A anchor tags whilst handling start tag
		parser.parse(aReader, new HTMLEditorKit.ParserCallback() {
			// method to handle start tags
			public void handleStartTag(HTML.Tag t, MutableAttributeSet a,
					int pos) {
				// check if A tag
				if (t == HTML.Tag.A) {
					Object link = a.getAttribute(HTML.Attribute.HREF);
					if (link != null) {
						links.add(String.valueOf(link));

						// cast to string and pass to methods to get title,
						// status
						String pageURL = link.toString();
						try {
							parsePage(pageURL); // Title - To print URL, HTML
							// page title, and HTTP status
							HttpURLConnection(pageURL); // Status
							// pause for half a second between pages
							Thread.sleep(500);

						} catch (IOException e) {
							e.printStackTrace();
						} catch (BadLocationException e) {
							e.printStackTrace();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}, true);
		aReader.close();
	}

	/**
	 * This method generates a CSV file containing URL, HTML page title and HTTP status code
	 * 
	 * @throws IOException
	 */
	private static void generateCsvFile(StringBuilder c) throws IOException {

		try {
			// create new file writer
			FileWriter writer = new FileWriter("CVS.txt");

			// append String representing CVS file
			writer.append(c);
			writer.flush();
			writer.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	// TODO Code to perform JUnit tests
}
