package net.ion.radon.core.let;

import java.io.File;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import net.ion.nradon.restlet.FileMetaType;

public class FileResponseBuilder {

	private File file;
	private String type;

	public FileResponseBuilder(File file){
		this(file, FileMetaType.mediaType(file.getName())) ;
	}

	public FileResponseBuilder(File file, String mtype){
		this.file = file ;
		this.type = mtype ;
	}
	

	public Response build(){
		return Response.status(file.exists() ? Status.OK.getStatusCode() : Status.NOT_FOUND.getStatusCode())
				.type(type).entity(file).build() ;
	}
	
	
}


