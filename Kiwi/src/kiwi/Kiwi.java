package kiwi;

import kiwi.core.Engine;

public class Kiwi {
	
	public static final Version
		VERSION = new Version("Kiwi", 1, 1, 2);
	
	public static void main(String[] args) {
		Engine engine = new Engine();
		engine.init();
	}
	
	public static class Version {
		public final String
			VERSION_NAME;
		public final int
			VERSION_ID,
			RELEASE_ID,
			PATCH_ID;
		
		public Version(
				String version_name,
				int version_id,
				int release_id,
				int patch_id
				) {
			this.VERSION_NAME = version_name;
			this.VERSION_ID = version_id;
			this.RELEASE_ID = release_id;
			this.PATCH_ID = patch_id;
		}
		
		@Override
		public String toString() {
			return
					this.VERSION_NAME + " " +
					this.VERSION_ID + "." +
					this.RELEASE_ID + "." +
					this.PATCH_ID;
		}
	}
}
