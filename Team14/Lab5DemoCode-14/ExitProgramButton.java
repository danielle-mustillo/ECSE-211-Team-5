/* Danielle Mustillo, Nathaniel Anthonisen
 * 260533476, 260470621
 * ECSE 211 - Design Principles and Methods
 * October 22, 2013
 */
/*
 *ExitProgramButton.java
 */
import lejos.nxt.Button;
import lejos.nxt.ButtonListener;

public class ExitProgramButton implements ButtonListener {
	/* Exit the program no matter its current state */
	@Override
	public void buttonPressed(Button b) {
		System.exit(0);
	}

	@Override
	public void buttonReleased(Button b) {
	}

}
