package hardwareAbstraction;

public class LinePoller {

	private boolean rightSensorOnLine;
	private boolean leftSensorOnLine;
	
	public int getFilteredData(boolean rightSensor) {
		return 1;
	}
	
	public boolean onLine(boolean rightSensor) {
		return (rightSensor) ? rightSensorOnLine : leftSensorOnLine;
	}
	
	public boolean enteringLine(boolean rightSensor) {
		return true;
	}
	
}
