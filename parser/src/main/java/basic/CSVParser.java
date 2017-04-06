package basic;

import java.io.*;

/**
 * A simple CSV Parser.
 */
public class CSVParser {
    /**
     * The file reader object.
     */
    private final BufferedReader br;
    /**
     * The current line split in elements.
     */
    private String[] currentItems;

    /**
     * Constructor.
     *
     * @param filePath
     *            The path to the file, that is to be read.
     * @throws FileNotFoundException
     */
    public CSVParser(String filePath) throws FileNotFoundException {
        this.br = new BufferedReader(new FileReader(filePath));
        currentItems = new String[0];
    }

    /**
     * Reads the next line of the file.
     * @return false when the last line was read, true otherwise.
     *
     * @throws IOException
     */
    public boolean readNextLine() throws IOException {
        String line = br.readLine();
        // Skip empty lines.
        while (line != null && line.length() == 0) {
            line = br.readLine();
        }
        // Check if end of file is reached.
        if (line == null) {
            return false;
        }
        // Split the line at ",", but also consider empty items (the split function
        // of the String class doesn't). Furthermore we assume the item-substrings
        // will have the following form: <item> | "<item>", where <item> does not
        // contain a ",".
        int itemCounter = 1;
        for (int i = 0; i < line.length(); i++) {
            if (line.charAt(i) == ',') {
                itemCounter++;
            }
        }
        currentItems = new String[itemCounter];
        itemCounter = 0;
        int apostrophePos0 = -1;
        int lastCommaPos = -1;
        for (int i = 0; i < line.length(); i++) {
            if (line.charAt(i) == '"') {
                if (apostrophePos0 == -1) {
                    apostrophePos0 = i;
                } else {
                    // => Second apostrophe found.
                    currentItems[itemCounter] = line.substring(apostrophePos0 + 1, i);
                    // Remove spaces.
                    currentItems[itemCounter] = currentItems[itemCounter].trim();
                    itemCounter++;
                }
            } else if (line.charAt(i) == ',') {
                if (apostrophePos0 == -1) {
                    // => There were no apostrophes.
                    currentItems[itemCounter] = line.substring(lastCommaPos + 1, i);
                    // Remove spaces.
                    currentItems[itemCounter] = currentItems[itemCounter].trim();
                    itemCounter++;
                }
                lastCommaPos = i;
                apostrophePos0 = -1;
            }
        }
        // The last item is not finished with a ",".
        if (apostrophePos0 == -1) {
            currentItems[itemCounter] = line.substring(lastCommaPos + 1,
                    line.length());
            currentItems[itemCounter] = currentItems[itemCounter].trim();
        }

        return true;
    }

    /**
     * Returns the item in column i of the current line.
     *
     * @param i
     *            the column index starting with 0.
     * @return the string in the current line at index i.
     * @throws EOFException
     */
    public String getItem(int i) throws EOFException {
        assert i >= 0 && i < currentItems.length;
        return currentItems[i];
    }

    /**
     * Returns the number of columns.
     */
    public int getNumColumns() {
        return currentItems.length;
    }
}