package net.ion.nradon.restlet.representation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.nio.channels.FileChannel;
import java.util.Date;

import net.ion.framework.util.FileUtil;
import net.ion.framework.util.IOUtil;
import net.ion.nradon.restlet.MediaType;
import net.ion.nradon.restlet.data.Disposition;
import net.ion.nradon.restlet.util.IoUtils;

public class FileRepresentation extends Representation {

    /**
     * Indicates if this file should be automatically deleted on release of the
     * representation.
     */
    private volatile boolean autoDeleting;

    /** The file handle. */
    private volatile File file;

    public FileRepresentation(File file, MediaType mediaType) {
        this(file, mediaType, -1);
    }


    public FileRepresentation(File file, MediaType mediaType, int timeToLive) {
        super(mediaType);
        this.file = file;
        setModificationDate(new Date(file.lastModified()));

        if (timeToLive == 0) {
            setExpirationDate(null);
        } else if (timeToLive > 0) {
            setExpirationDate(new Date(System.currentTimeMillis()
                    + (1000L * timeToLive)));
        }

        setMediaType(mediaType);
        Disposition disposition = new Disposition();
        disposition.setFilename(file.getName());
        this.setDisposition(disposition);
    }

    public FileRepresentation(String path, MediaType mediaType) {
        this(new File(path), mediaType, -1);
    }


    @Override
    public FileChannel getChannel() throws IOException {
        try {
            return new FileInputStream(this.file).getChannel();
        } catch (FileNotFoundException fnfe) {
            throw new IOException("Couldn't get the channel. File not found");
        }
    }

    public File getFile() {
        return this.file;
    }

    @Override
    public Reader getReader() throws IOException {
        return new FileReader(this.file);
    }

    @Override
    public long getSize() {
        if (super.getSize() != UNKNOWN_SIZE) {
            return super.getSize();
        }

        return this.file.length();
    }

    @Override
    public FileInputStream getStream() throws IOException {
        try {
            return new FileInputStream(this.file);
        } catch (FileNotFoundException fnfe) {
            throw new IOException("Couldn't get the stream. File not found");
        }
    }

    @Override
    public String getText() throws IOException {
        return IoUtils.toString(getStream(), getCharacterSet());
    }

    @Deprecated
    public boolean isAutoDelete() {
        return autoDeleting;
    }


    public boolean isAutoDeleting() {
        return isAutoDelete();
    }

    /**
     * Releases the file handle.
     */
    @Override
    public void release() {
        if (isAutoDeleting() && getFile() != null) {
            try {
                FileUtil.deleteQuietly(getFile());
            } catch (Exception e) {
            }
        }

        setFile(null);
        super.release();
    }

    @Deprecated
    public void setAutoDelete(boolean autoDeleting) {
        this.autoDeleting = autoDeleting;
    }

    public void setAutoDeleting(boolean autoDeleting) {
        setAutoDelete(autoDeleting);
    }


    public void setFile(File file) {
        this.file = file;
    }

    @Override
    public void write(OutputStream outputStream) throws IOException {
        IOUtil.copy(getStream(), outputStream);
    }


    @Override
    public void write(Writer writer) throws IOException {
        IOUtil.copy(getReader(), writer);
    }

}
