package _kiwi.core;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

public class Input implements KeyListener, MouseListener, MouseWheelListener, MouseMotionListener {
	protected static final Input
		INSTANCE = new Input();
	public static final int
		NUM_KEYS = 65535,
		NUM_BTNS = 3;
	public static enum Key {
		DN_ACTION, DN,
		UP_ACTION, UP
	}
	public static enum Btn {
		DN_ACTION, DN,
		UP_ACTION, UP
	}
	
	protected final boolean[]
		key_buffer = new boolean[NUM_KEYS],
		btn_buffer = new boolean[NUM_BTNS];
	protected final Point
		mouse_buffer = new Point();
	protected float
		wheel_buffer;
	
	protected final Key[]
		keys = new Key[NUM_KEYS];
	protected final Btn[]
		btns = new Btn[NUM_BTNS];
	protected final Point
		mouse = new Point();
	protected float
		wheel;
	
	private Input() {
		for(int i = 0; i < NUM_KEYS; i ++)
			keys[i] = Key.UP;
		for(int i = 0; i < NUM_BTNS; i ++)
			btns[i] = Btn.UP;
	}
	
	public static void poll() {
		INSTANCE.pollMouse();
		INSTANCE.pollWheel();
		INSTANCE.pollKeys();
		INSTANCE.pollBtns();
	}
	
	public void pollMouse() {
		if(!mouse_buffer.equals(mouse)) {
			mouse.x = mouse_buffer.x;
			mouse.y = mouse_buffer.y;
			Engine.INSTANCE.onMouseMoved(mouse);
		}
	}
	
	public void pollWheel() {
		if(wheel_buffer != wheel) {
			wheel = wheel_buffer;
			if(wheel_buffer != 0)
				Engine.INSTANCE.onWheelMoved(wheel);
			wheel_buffer = 0f;
		}
	}
	
	public void pollKeys() {
		for(int i = 0; i < NUM_KEYS; i ++)
			switch(keys[i]) {
			case DN:
			case DN_ACTION:
				if(key_buffer[i]) {
					keys[i] = Key.DN;
				} else {
					keys[i] = Key.DN_ACTION;
					Engine.INSTANCE.onKeyDnAction(i);
				}
				break;
			case UP:
			case UP_ACTION:
				if(key_buffer[i]) {
					keys[i] = Key.UP_ACTION;
					Engine.INSTANCE.onKeyUpAction(i);
				} else {
					keys[i] = Key.UP;
				}
				break;			
			}
	}
	
	public void pollBtns() {
		for(int i = 0; i < NUM_BTNS; i ++)
			switch(btns[i]) {
			case DN:
			case DN_ACTION:
				if(btn_buffer[i]) {
					btns[i] = Btn.DN;
				} else {
					btns[i] = Btn.DN_ACTION;
					Engine.INSTANCE.onBtnDnAction(mouse, i);
				}
				break;
			case UP:
			case UP_ACTION:
				if(btn_buffer[i]) {
					btns[i] = Btn.UP_ACTION;
					Engine.INSTANCE.onBtnUpAction(mouse, i);
				} else {
					btns[i] = Btn.UP;
				}
				break;			
			}
	}

	@Override
	public void keyTyped(KeyEvent e) {
		//do nothing
	}

	@Override
	public void keyPressed(KeyEvent e) {
		key_buffer[e.getKeyCode()] = true;
	}

	@Override
	public void keyReleased(KeyEvent e) {
		key_buffer[e.getKeyCode()] = false;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		//do nothing
	}

	@Override
	public void mousePressed(MouseEvent e) {
		btn_buffer[e.getButton()] = true;
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		btn_buffer[e.getButton()] = false;
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		//do nothing
	}

	@Override
	public void mouseExited(MouseEvent e) {
		//do nothing
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		wheel_buffer = e.getWheelRotation();
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		mouse_buffer.x = e.getX();
		mouse_buffer.y = e.getY();
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		mouse_buffer.x = e.getX();
		mouse_buffer.y = e.getY();
	}
	
	public static Point getMouse() {
		return INSTANCE.mouse;
	}
	
	public static float getWheel() {
		return INSTANCE.wheel;
	}
	
	public static boolean isWheelUp() {
		return getWheel() < 0;
	}
	
	public static boolean isWheelDn() {
		return getWheel() > 0;
	}
	
	public static Key getKey(int i) {
		return INSTANCE.keys[i];
	}
	
	public static boolean isKeyDn(int i) {
		Key key = getKey(i);
		return 
				key == Key.DN ||
				key == Key.DN_ACTION;
	}
	
	public static boolean isKeyUp(int i) {
		Key key = getKey(i);
		return 
				key == Key.UP ||
				key == Key.UP_ACTION;
	}
	
	public static boolean isKeyDnAction(int i) {
		Key key = getKey(i);
		return key == Key.DN_ACTION;
	}
	
	public static boolean isKeyUpAction(int i) {
		Key key = getKey(i);
		return key == Key.UP_ACTION;
	}
	
	public static Btn getBtn(int i) {
		return INSTANCE.btns[i];
	}
	
	public static boolean isBtnDn(int i) {
		Btn btn = getBtn(i);
		return 
				btn == Btn.DN ||
				btn == Btn.DN_ACTION;
	}
	
	public static boolean isBtnUp(int i) {
		Btn btn = getBtn(i);
		return 
				btn == Btn.UP ||
				btn == Btn.UP_ACTION;
	}
	
	public static boolean isBtnDnAction(int i) {
		Btn btn = getBtn(i);
		return btn == Btn.DN_ACTION;
	}
	
	public static boolean isBtnUpAction(int i) {
		Btn btn = getBtn(i);
		return btn == Btn.UP_ACTION;
	}
}
