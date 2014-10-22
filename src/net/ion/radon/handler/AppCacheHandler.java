package net.ion.radon.handler;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.concurrent.ScheduledExecutorService;
import java.util.logging.Logger;

import net.ion.framework.util.FileUtil;
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

	private File homeDir;
	private StringBuilder cacheContent = new StringBuilder();
	private WildcardFileFilter targetFilter = new WildcardFileFilter(new String[] { "*.jpg", "*.gif", "*.png", "*.css", "*.js" });
	private String prefixPath = "";
	private Logger logger = Logger.getLogger(getClass().getCanonicalName()) ;
	
	public AppCacheHandler(File homeDir) throws IOException {
		this.homeDir = homeDir;
		this.cacheContent = makeCacheResource();
	}

	@Override
	public void onEvent(EventType event, Radon radon) {
	}

	public AppCacheHandler resourceExtenstion(String... extNames) {
		this.targetFilter = new WildcardFileFilter(extNames);
		return this;
	}

	public AppCacheHandler startMonitor(ScheduledExecutorService ses) throws Exception {

		FileAlterationObserver fo = new FileAlterationObserver(homeDir);
		final FileAlterationMonitor fam = new FileAlterationMonitor(3000, ses, fo);

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
				logger.fine(file + " " + event + " ! " + this);
				AppCacheHandler.this.cacheContent = makeCacheResource();
			}
		};
		fo.addListener(listener);
		fam.start();
		return this;
	}
	
	public AppCacheHandler prefixPath(String prefixPath){
		this.prefixPath = prefixPath ;
		return this ;
	}

	StringBuilder makeCacheResource() {
		StringBuilder result = new StringBuilder(512);
		try {
			File[] found = FileUtil.findFiles(homeDir, targetFilter, true);

			result.append("CACHE MANIFEST\n\n");
			for (File find : found) {
				result.append(prefixPath +  StringUtil.replace(StringUtil.substringAfter(find.getCanonicalPath(), homeDir.getCanonicalPath()), "\\", "/") + "\n");
			}
			result.append("\n");
			result.append("NETWORK:\n");
			result.append("*\n");

			result.append("\n");
			result.append("FALLBACK:\n");
			result.append("#" + new Date());
		} catch (IOException ex) {
			System.err.println(ex);
		}
		return result;
	}

	@Override
	public int order() {
		return 0;
	}

	@Override
	public void handleHttpRequest(HttpRequest request, HttpResponse response, HttpControl control) throws Exception {
		response.content(cacheContent.toString()).header(HttpHeaderNames.CONTENT_TYPE, "text/cache-manifest").end();
	}

}