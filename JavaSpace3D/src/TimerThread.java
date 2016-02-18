
public class TimerThread implements Runnable {
	TimerThread(){}
	
	@Override
	
	public void run() {
		// TODO Auto-generated method stub
		while(true) {
			try {
			Thread.sleep(36);
			} catch (InterruptedException e) {
			}
			SpaceRun.doDraw = true;
		}
	}

}
