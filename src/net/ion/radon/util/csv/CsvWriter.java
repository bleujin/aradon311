package net.ion.radon.util.csv;

import java.io.IOException;
import java.io.Writer;

//import org.apache.commons.lang.StringUtils;

public class CsvWriter {

    private char field_delim = Csv.FIELD_DELIMITER;
    private char block_delim = Csv.BLOCK_DELIMITER;

    private Writer writer;
    private boolean written;

    public CsvWriter(Writer wtr) {
        this.writer = wtr;
    }

    public Writer getWriter() {
        return this.writer;
    }

    public void setFieldDelimiter(char ch) {
        field_delim = ch;
    }

    public void setBlockDelimiter(char ch) {
        block_delim = ch;
    }

    public void writeField(String field) throws IOException {
        if(written) {
            writer.write(field_delim);
            written = false;
        }

        int idx = field.indexOf(field_delim) + field.indexOf(block_delim);
        if(idx != -1) {
        	field = replace(field,"\r\n","\n");
            field = "\""+replace(field,"\"","\"\"")+"\"";
        }

        writer.write(field);
        written = true;
    }

    public void endBlock() throws IOException {
        writer.write(block_delim);
        written = false;
    }

    public void writeLine(String[] strs) throws IOException {
        int sz = strs.length;
        for(int i=0;i<sz;i++) {
            writeField(strs[i]);
        }
        endBlock();
    }

    public void close() throws IOException {
        this.writer.close();
    }

    // from Commons.Lang.StringUtils
    private static String replace(String text, String repl, String with) {   
        int max = -1;
        if (text == null || repl == null || with == null || repl.length() == 0 || max == 0) {
            return text;
        }
        
        StringBuilder buf = new StringBuilder(text.length());
        int start = 0, end = 0;
        while ((end = text.indexOf(repl, start)) != -1) {
            buf.append(text.substring(start, end)).append(with);
            start = end + repl.length();
            
            if (--max == 0) {
                break;
            }
        }
        buf.append(text.substring(start));
        return buf.toString();
    }

}
