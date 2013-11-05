/* Danielle Mustillo, Nathaniel Anthonisen
 * 260533476, 260470621
 * ECSE 211 - Design Principles and Methods
 * October 08, 2013
 */
/*
 *ExitProgramButton.java
 */
import lejos.nxt.Button;
import lejos.nxt.ButtonListener;
import lejos.nxt.Sound;

/* Exit the program no matter its position */
public class ExitProgramButton implements ButtonListener {

	@Override
	public void buttonPressed(Button b) {
		System.exit(0);
	}

	@Override
	public void buttonReleased(Button b) {
	}

}
