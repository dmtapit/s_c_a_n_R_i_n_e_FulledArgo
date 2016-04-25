
import org.lwjgl.input.Keyboard;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import static org.lwjgl.opengl.GL11.*;

/**
 * Just a test class. Not needed for the project.
 * @author DTapit
 */
public class TestTransform {
	public void start() {
		try{
			createWindow();
			initGL();
			render();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	private void createWindow() throws Exception {
		Display.setFullscreen(false);
		Display.setDisplayMode(new DisplayMode(640, 480));
		Display.setTitle("CS445 - PROGRAM 2");
		Display.create();
	}
	private void initGL() {
		glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glOrtho(-640/2, 640/2, -480/2, 480/2, 1, -1);
		glMatrixMode(GL_MODELVIEW);
		//glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
		glDisable(GL_DEPTH_TEST);
	}
	private void render() throws LWJGLException {
		while(!Display.isCloseRequested()){
			try{
				glClear(GL_COLOR_BUFFER_BIT);
				glLoadIdentity();
				glPointSize(1);
				
				////Polygon 1
				glColor3f(1.0f, 0.0f, 0.0f);
				
//				glScalef(0.5f, 1.5f, 0);				
//				glRotatef(30f, 0f, 0f, 1f);
//				glTranslatef(100, -75, 0);

				glBegin(GL_LINE_LOOP);
					glVertex2f(80, 10);
					glVertex2f(80, 30);
					glVertex2f(230, 37);
					glVertex2f(280, 30);
					glVertex2f(280, 10);
				glEnd();
				
				glLoadIdentity(); // Resets the drawing perspective for next polygon
				
				////Polygon 2				
				glColor3f(0.1f, 0.8f, 0.3f);

//				glRotatef(-45f, 0f, 0f, 1f);
//				glTranslatef(30, 30, 0);
//				glScalef(2.0f, 0.2f, 0);
//				glRotatef(90f, 0f, 0f, 1f);
				
				glBegin(GL_LINE_LOOP);
					glVertex2f(10, 10);
					glVertex2f(10, 80);
					glVertex2f(80, 60);
					glVertex2f(210, 10);
					glVertex2f(210, 80);
					glVertex2f(150, 10);
				glEnd();
				
				
				Display.update();
				Display.sync(60);
			} catch (Exception e) {
			}
			Keyboard.create();
			if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
				System.exit(0);
			}
		} // end while loop;
		Keyboard.destroy();
		Display.destroy();
	}
	
	
	public static void main(String[] args) throws LWJGLException {
		TestTransform testTransform = new TestTransform();
		testTransform.start();
	}
}
