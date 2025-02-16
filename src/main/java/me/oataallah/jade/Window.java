package me.oataallah.jade;

import me.oataallah.util.Time;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {

    private static Window instance = null;

    private final int WIDTH, HEIGHT;
    private final String TITLE;

    private long glfwWindow;

    public float r = 1, g = 1, b = 1, a = 1;
    private boolean fadeToBlack = false;

    private static Scene currentScene = null;

    private Window() {
        this.WIDTH = 1920;
        this.HEIGHT = 1080;
        this.TITLE = "Mario";
    }

    public static void changeScene(int newScene) {
        switch (newScene) {
            case 0:
                currentScene = new LevelEditorScene();
                // currentScene.init();
                break;
            case 1:
                currentScene = new LevelScene();
                // currentScene.init();
                break;
            default:
                assert false : "Unknown scene '" + newScene + "'";
                break;
        }
    }

    public static Window get() {
        if (instance == null) instance = new Window();
        return instance;
    }

    public void run() {
        System.out.println("Hello LWJGL " + Version.getVersion() + "!");

        try (GLFWErrorCallback errorCallback = GLFWErrorCallback.createPrint(System.err)) {
            glfwSetErrorCallback(errorCallback);
            init();
            loop();
        } finally {
            glfwFreeCallbacks(glfwWindow);
            glfwDestroyWindow(glfwWindow);
            glfwTerminate();
        }
    }


    private void init() {
        GLFWErrorCallback.createPrint(System.err).set();

        if (!glfwInit()) throw new IllegalStateException("Unable to initialize GLFW");

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

        glfwWindow = glfwCreateWindow(this.WIDTH, this.HEIGHT, this.TITLE, NULL, NULL);
        if ( glfwWindow == NULL ) throw new RuntimeException("Failed to create the GLFW window");

        glfwSetCursorPosCallback(glfwWindow, MouseListener::mousePosCallback);
        glfwSetMouseButtonCallback(glfwWindow, MouseListener::mouseButtonCallback);
        glfwSetScrollCallback(glfwWindow, MouseListener::mouseScrollCallback);
        glfwSetKeyCallback(glfwWindow, KeyListener::keyCallback);

        glfwMakeContextCurrent(glfwWindow);

        glfwSwapInterval(1);

        glfwShowWindow(glfwWindow);

        GL.createCapabilities();
        Window.changeScene(0);
    }

    private void loop() {
        float beginTime = Time.getTime();
        float endTime = Time.getTime();
        float dt = -1.0f;

        while ( !glfwWindowShouldClose(glfwWindow) ) {
            glfwPollEvents();

            glClearColor(r, g, b, a);
            glClear(GL_COLOR_BUFFER_BIT);

            if (dt >= 0) currentScene.update(dt);

            glfwSwapBuffers(glfwWindow);

            endTime = Time.getTime();
            dt = endTime - beginTime;
            beginTime = endTime;
        }
    }

}
