import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;

public class GL {
    public static void main(String[] args) {
        try {
            Display.setDisplayMode(new DisplayMode(800, 600));
            Display.create();

            System.out.println("OpenGL Version: " + GL11.glGetString(GL11.GL_VERSION));

            Display.destroy();
        } catch (LWJGLException e) {
            e.printStackTrace();
        }
    }
}
