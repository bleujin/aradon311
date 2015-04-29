package net.ion.nradon.restlet.representation;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import net.ion.framework.parse.gson.GsonBuilder;
import net.ion.framework.parse.gson.JsonArray;
import net.ion.framework.parse.gson.JsonElement;
import net.ion.framework.parse.gson.JsonNull;
import net.ion.framework.parse.gson.JsonObject;
import net.ion.framework.parse.gson.JsonParser;
import net.ion.nradon.restlet.MediaType;
import net.ion.nradon.restlet.data.CharacterSet;

public class JsonObjectRepresentation extends WriterRepresentation {

	private boolean indenting;
	private JsonElement jsonElement = JsonNull.INSTANCE;

	public JsonObjectRepresentation(JsonElement jsonElement) {
		super(MediaType.APPLICATION_JSON, -1);
		init(jsonElement);
	}

	public JsonObjectRepresentation(Map<String, ? extends Object> map) {
		this(JsonParser.fromMap(map));
	}

	public JsonObjectRepresentation(Object bean) {
		this(JsonParser.fromObject(bean));
	}

	public JsonObjectRepresentation(String jsonString) {
		this(JsonParser.fromString(jsonString)) ;
	}

	public JsonArray getJsonArray() {
		return this.jsonElement.getAsJsonArray();
	}

	public JsonObject getJsonObject() {
		return this.jsonElement.getAsJsonObject() ;
	}

	private String getJsonText() {
		if (isIndenting()){
			return new GsonBuilder().setPrettyPrinting().create().toJson(jsonElement) ;
		} else {
			return jsonElement.toString() ;
		}
	}

	@Override
	public long getSize() {
		return super.getSize();
	}

	private void init(JsonElement jsonElement) {
		setCharacterSet(CharacterSet.UTF_8);
		this.jsonElement = jsonElement;
		this.indenting = false;
	}

	public boolean isIndenting() {
		return this.indenting;
	}

	public JsonObjectRepresentation setIndenting(boolean indenting) {
		this.indenting = indenting;
		return this ;
	}


	@Override
	public void write(Writer writer) throws IOException {
		writer.write(getJsonText());
	}

}
