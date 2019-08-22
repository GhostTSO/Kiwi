package _kiwi.core;

public class Engine implements Runnable {
	public static final Engine
		INSTANCE = new Engine();
	public static final int
		WINDOW_W = 640,
		WINDOW_H = 480,
		THREAD_FPS = 60,
		THREAD_TPS = 60;
	
	protected long
		fps,
		tps;
	
	private Engine() { }
	
	
	public static void init() {
		
	}
	
	public static void exit() {
		
	}
	
	public void onInit() {
		
	}
	
	public void onExit() {
		
	}

	@Override
	public void run() {
		try {
			
		} catch(Exception ex) {
			ex.printStackTrace();
		} finally {
			
		}
	}
}
