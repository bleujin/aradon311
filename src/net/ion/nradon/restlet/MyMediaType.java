package net.ion.nradon.restlet;

import javax.ws.rs.core.MediaType;

public class MyMediaType extends MediaType {

    public static final String TEXT_PLAIN_UTF8 = "text/plain; charset=utf-8";
    public static final String APPLICATION_XML_UTF8 = "application/xml; charset=utf-8";
    public static final String TEXT_XML_UTF8 = "text/xml; charset=utf-8";
    public static final String TEXT_HTML_UTF8 = "text/html; charset=utf-8";
    public static final String APPLICATION_JSON_UTF8 = "application/json; charset=utf-8";
    
    
    public static final String IMAGE_PNG = "image/png" ;
    public static final String IMAGE_TIFF = "image/tiff" ;
    public static final String IMAGE_JPEG = "image/jpeg" ;
    public static final String IMAGE_GIF = "image/gif" ;

    public static MediaType IMAGE_PNG_TYPE = new MediaType("image", "png") ;
    public static MediaType IMAGE_TIFF_TYPE = new MediaType("image", "tiff") ;
    public static MediaType IMAGE_JPG_TYPE = new MediaType("image", "jpeg") ;
    public static MediaType IMAGE_GIF_TYPE = new MediaType("image", "gif") ;


    public static MediaType guessImageType(String mediaType) {
        if("png".equalsIgnoreCase(mediaType)) {
            return IMAGE_PNG_TYPE ;
        } else if("jpg".equalsIgnoreCase(mediaType) || "jpeg".equalsIgnoreCase(mediaType)) {
            return IMAGE_JPG_TYPE ;
        } else if("tiff".equalsIgnoreCase(mediaType)) {
            return IMAGE_TIFF_TYPE ;
        } else if("gif".equalsIgnoreCase(mediaType)) {
            return IMAGE_GIF_TYPE ;
        } else {
            return MediaType.APPLICATION_OCTET_STREAM_TYPE ;
        }
    }



}
