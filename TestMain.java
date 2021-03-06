import java.awt.Point;
import java.io.IOException;
import java.util.Vector;

import adm.ADM;
import cdc.CDC;
import entity.*;
import pem.PEM;
import sdm.SDM;

public class TestMain {
	
	static boolean state = true;

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		SDM.getInstance().readMap("./resource/Map/Map001.txt");
		MonsterInfo.getInstance().loadMonsterData("./resource/Data/Monster/Mode1/");
		
		MonsterInfo.getInstance().getRandomMonster(5).Print();
		 
	}
	
	static public void Finish() {
		System.out.println("Finish");
		Vector<String> v = CDC.getInstance().getUpdateInfo();
		for ( String str : v ) {
			System.out.println(str);
		}

	}
}

class PThread implements Runnable {

	private Thread _thread;
	private boolean isRunning = false;

	public PThread() {
		init();
	}
	
	private void init() {
		MonsterInfo.getInstance().loadMonsterData("./resource/Data/Monster/Mode1/");
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		init();
		int SleepTime = 1000/100;
		int Total = 3000;
		int Now = 0;
		
		int x = 0, y = 0;
		
		while (Now <= Total) {
			try {
				PEM.getInstance().tick();
				//PEM.getInstance().PrintState();
				Thread.sleep(SleepTime);
				Now += SleepTime;
				//TestMain.Finish();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				
			}
		}
		PEM.getInstance().PrintState();
	}
	
	public void start() {
		if ( isRunning ) 
			return;
		
		isRunning = true;
		_thread = new Thread(this);
		_thread.start();
	}
	
	public void stop() {
		if ( !isRunning ) 
			return;
		
		isRunning = false;
		try {
			_thread.join();
		} catch (InterruptedException event) {
			// TODO Auto-generated catch block
			event.printStackTrace();
		}
	}
}

class P1Thread implements Runnable {

	private Thread _thread;
	private boolean isRunning = false;

	public P1Thread () {
		init();
	}
	
	private void init() {
		
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		init();
		int SleepTime = 1000/500;
		int Total = 3000;
		int Now = 0;
		
		int x = 0, y = 0;
		
		while (Now <= Total) {
			try {
				Thread.sleep(SleepTime);
				Now += SleepTime;
				Vector<String> v = CDC.getInstance().getUpdateInfo();
				for ( String str : v ) {
					System.out.println(str);
				}
				System.out.println("=================");
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				
			}
		}
		PEM.getInstance().PrintState();
	}
	
	public void start() {
		if ( isRunning ) 
			return;
		
		isRunning = true;
		_thread = new Thread(this);
		_thread.start();
	}
	
	public void stop() {
		if ( !isRunning ) 
			return;
		
		isRunning = false;
		try {
			_thread.join();
		} catch (InterruptedException event) {
			// TODO Auto-generated catch block
			event.printStackTrace();
		}
	}
}