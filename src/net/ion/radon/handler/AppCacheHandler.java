package net.ion.radon.handler;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.logging.Logger;

import net.ion.framework.util.Debug;
import net.ion.framework.util.FileUtil;
import net.ion.framework.util.ListUtil;
import net.ion.framework.util.StringBuilderWriter;
import net.ion.framework.util.StringUtil;
import net.ion.nradon.HttpControl;
import net.ion.nradon.HttpHandler;
import net.ion.nradon.HttpRequest;
import net.ion.nradon.HttpResponse;
import net.ion.nradon.Radon;
import net.ion.nradon.handler.event.ServerEvent.EventType;
import net.ion.radon.cload.monitor.AbstractListener;
import net.ion.radon.cload.monitor.FileAlterationMonitor;

import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.jboss.resteasy.util.HttpHeaderNames;

public class AppCacheHandler implements HttpHandler {

	private StringBuilder cacheContent = new StringBuilder();
//	private WildcardFileFilter targetFilter = new WildcardFileFilter(new String[] { "*.jpg", "*.gif", "*.png", "*.css", "*.js" });
	private Logger logger = Logger.getLogger(getClass().getCanonicalName()) ;
	private List<DirConfig> dconfigs = ListUtil.newList() ;
	private boolean started = false;
	
	public AppCacheHandler() throws IOException {
	}

	@Override
	public void onEvent(EventType event, Radon radon) {
	}

	public AppCacheHandler appendDir(String prefixPath, File targetDir, String... extNames){
		if (started) throw new IllegalStateException("already monitor started ") ;
		dconfigs.add(new DirConfig(prefixPath, targetDir, extNames)) ;
		
		return this ;
	}

	public AppCacheHandler referenceDir(String prefixPath, File targetDir, String... extNames){
		if (started) throw new IllegalStateException("already monitor started ") ;
		dconfigs.add(new DirConfig(prefixPath, targetDir, extNames).referenceFile(true)) ;
		
		return this ;
	}

	
	public AppCacheHandler start(){
		this.cacheContent = makeCacheResource();
		
		return this ;
	}
	
	public AppCacheHandler startMonitor(ScheduledExecutorService ses) throws Exception {

		List<FileAlterationObserver> observers = ListUtil.newList() ;
		for (DirConfig dconfig : dconfigs) {
			FileAlterationObserver newObserver = new FileAlterationObserver(dconfig.targetDir);
			final WildcardFileFilter targetFilter = new WildcardFileFilter(dconfig.extNames);
			AbstractListener listener = new AbstractListener() {
				@Override
				public void onFileDelete(File file) {
					reload(file, " file delete") ;
				}
				@Override
				public void onFileCreate(File file) {
					reload(file, " file create") ;
				}
				@Override
				public void onFileChange(File file) {
					reload(file, " file change") ;
				}
				private void reload(File file, String event) {
					if (! targetFilter.accept(file)) return ;
					Debug.line(file + " " + event + " ! " + this);
					AppCacheHandler.this.cacheContent = makeCacheResource();
				}
			};
			newObserver.addListener(listener);
			observers.add(newObserver) ;
		}
		final FileAlterationMonitor fam = new FileAlterationMonitor(3000, ses, observers.get(0), observers.subList(1, observers.size()).toArray(new FileAlterationObserver[0]));
		
		fam.start();
		this.started = true ;
		return this;
	}
	
	StringBuilder makeCacheResource() {
		StringBuilderWriter result = new StringBuilderWriter();
		try {
			result.appendLine("CACHE MANIFEST\n");
			result.appendLine("#" + new Date());
			result.appendLine("");
			
			result.appendLine("CACHE:");
			for (DirConfig dconfig : dconfigs) {
				if (dconfig.isReferenceFile) continue ;
				final WildcardFileFilter targetFilter = new WildcardFileFilter(dconfig.extNames);
				File[] found = FileUtil.findFiles(dconfig.targetDir, targetFilter, true);
				for (File find : found) {
					result.appendLine(dconfig.prefixPath +  StringUtil.replace(StringUtil.substringAfter(find.getCanonicalPath(), dconfig.targetDir.getCanonicalPath()), "\\", "/"));
				}
			}
			result.appendLine("");
			result.appendLine("NETWORK:");
			result.appendLine("/outer.htm");
			result.appendLine("*");
			
			result.appendLine("");
			result.appendLine("FALLBACK:");
			
		} catch (IOException ex) {
			System.err.println(ex);
		}
		return result.getStringBuilder();
	}

	@Override
	public int order() {
		return 0;
	}

	@Override
	public void handleHttpRequest(HttpRequest request, HttpResponse response, HttpControl control) throws Exception {
		if ("/cache.appcache".equals(request.uri())) {
			response
				.header(HttpHeaderNames.CACHE_CONTROL, "").header(HttpHeaderNames.EXPIRES, "0")
				.content(cacheContent.toString()).header(HttpHeaderNames.CONTENT_TYPE, "text/cache-manifest").end();
		} else if ("/cache.appcache.reload".equals(request.uri())){
			cacheContent = makeCacheResource() ;
			response
				.header(HttpHeaderNames.CACHE_CONTROL, "no-store, no-cache").header(HttpHeaderNames.EXPIRES, "0")
				.content(cacheContent.toString()).header(HttpHeaderNames.CONTENT_TYPE, "text/cache-manifest").end();
			
		} else control.nextHandler(); 
	}

}


class DirConfig {
	String prefixPath ;
	File targetDir ;
	String[] extNames ;
	boolean isReferenceFile ;
	
	
	DirConfig(String prefixPath, File targetDir, String[] extNames){
		this.prefixPath = prefixPath ;
		this.targetDir = targetDir ;
		this.extNames = extNames ;
	}
	
	DirConfig referenceFile(boolean is){
		this.isReferenceFile = is;
		return this ;
	}
	
}
