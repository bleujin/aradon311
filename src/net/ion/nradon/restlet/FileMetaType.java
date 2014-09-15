package net.ion.nradon.restlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

import net.ion.framework.util.MapUtil;
import net.ion.framework.util.StringUtil;

public class FileMetaType {

	private static Map<String, String> extToTypeMap = load();

	private static Map<String, String> load() {
		BufferedReader reader = new BufferedReader(new InputStreamReader(FileMetaType.class.getResourceAsStream("./minetype.txt")));

		Map<String, String> result = MapUtil.newMap() ;
		try {
			while (true) {
				String line = reader.readLine();
				if (StringUtil.isBlank(line))
					break;
				
				String[] values = StringUtil.split(line, ' ') ;
				result.put(values[0], StringUtil.trim(values[1])) ;
			}
		} catch (IOException ex) {
			throw new IllegalStateException(ex);
		}
		return result ;
	}
	
	public static String mediaType(String fileName){
		String ext = StringUtil.substringAfter(fileName, ".") ;
		return StringUtil.defaultIfEmpty(extToTypeMap.get(ext), "application/octet-stream") ;
	}

	public static MediaType mediaType2(String fileName){
		return MediaType.valueOf(mediaType(fileName)) ;
	}
}
