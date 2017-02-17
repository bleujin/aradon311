package net.ion.framework.cloader;

public interface AfterReLoader {

	public final static AfterReLoader BLANK = new AfterReLoader() {
		@Override
		public void onReload(ClassLoader cl) {
		}
	};
	
	public void onReload(ClassLoader classloader) ;
}
