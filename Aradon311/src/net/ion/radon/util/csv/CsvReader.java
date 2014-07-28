package net.ion.radon.util.csv;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;

// TODO: Add consuming so that log files may be read. these are flies in which the delimiter is any number of a certain character. ie) space, tab etc.

// alternative: Allow delimiter to be larger than one char.
// still doesnt really help though. Could use Regexps?

// maybe a second class. RegexpBlockReader....

public class CsvReader {

    // new String to stop internning
    static public String END_OF_LINE = new String("END_OF_LINE");

    private char field_delim = Csv.FIELD_DELIMITER;
    private char block_delim = Csv.BLOCK_DELIMITER;

    private Reader reader;
    private boolean newline;

    // should   bbb,,,ccc be considered to be two elements?
    // useful for log parsing.
    private boolean consume;

    public CsvReader(Reader rdr) {
        this.reader = rdr;
    }

    public void setFieldDelimiter(char ch) {
        field_delim = ch;
    }

    public void setBlockDelimiter(char ch) {
        block_delim = ch;
    }

    public void setConsuming(boolean b) {
        this.consume = b;
    }

    public boolean isConsuming() {
        return this.consume;
    }

    public String[] readLine() throws IOException {
        ArrayList<String> list = new ArrayList<String>();
        String str;

        while( true ) {
            str = readField();
            if(str == null) {
                break;
            }
            if(str == END_OF_LINE) {
                break;
            }
            list.add(str);
        }

        if(list.isEmpty()) {
            return null;
        }

        return list.toArray(new String[0]);
    }

    public String readField() throws IOException {
        if(this.newline) {
            this.newline = false;
            return END_OF_LINE;
        }

        StringBuffer buffer = new StringBuffer();
        boolean quoted = false;
        int last = -1;
        int ch = this.reader.read();

        if(ch == -1) {
            return null;
        }

        if(ch == '"') {
            quoted = true;
        } else 
        if(ch == block_delim) {
            return END_OF_LINE;
        } else
        if(ch == field_delim) {
            return "";
        } else {
            buffer.append((char)ch);
        }

        while( (ch = this.reader.read()) != -1) {
            if(ch == block_delim) {
                this.newline = true;
                break;
            }
            if(quoted) {
                if(ch == '"') {
                    if(last == '"') {
                        // forget about this quote and move on
                        last = -1;  
                        buffer.append('"');
                        continue;
                    }
                    last = '"';
                    continue;
                }
            }
            if(ch == field_delim) {
                if(quoted) {
                    if(last == '"') {
                        break;
                    }
                } else {
                    break;
                }
            }
            buffer.append((char)ch);
        }

        return buffer.toString();
    }

    public void close() throws IOException {
        this.reader.close();
    }

}
