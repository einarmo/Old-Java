
public class TimerThread implements Runnable {
	TimerThread(){}
	
	@Override
	
	public void run() {
		// TODO Auto-generated method stub
		while(true) {
			if(System.currentTimeMillis()%17==0) {
				SpaceRun.doDraw = true;
			}
		}
	}

}
