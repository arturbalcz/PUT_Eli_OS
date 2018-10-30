package utils;

import java.awt.*;
import java.io.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.concurrent.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utility methods used across all classes
 */
public class Utils {
    /**
     * If set to true, logs are printed
     */
    static private boolean loggingOn = true;

    private static final JFrame frame = new JFrame();
    private static final JTextArea textArea = new JTextArea(50, 10);
    private static final String KEY = "ENTER";
    private static final JButton enterButton = new JButton(KEY);
    private static final PrintStream printStream = new PrintStream(new CustomOutputStream(textArea));
    private static final Semaphore semaphore = new Semaphore(0);
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss:SS");

    /**
     * Action wrapper for mapping KEY with JButton
     */
    static private Action wrapper = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent ae) {
            enterButton.doClick();
        }
    };

    static {
        frame.add( new JScrollPane( textArea )  );
        frame.pack();
        frame.setVisible( true );
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        textArea.setFont(new Font("Courier", Font.PLAIN,20));
        textArea.setEditable( false );
        frame.setSize(800,600);

        KeyStroke keyStroke = KeyStroke.getKeyStroke(KEY);
        Object actionKey = textArea.getInputMap(
                JComponent.WHEN_FOCUSED).get(keyStroke);
        textArea.getActionMap().put(actionKey, wrapper);


        enterButton.addActionListener(ae -> semaphore.release());
    }

    public static void closeLogs() {
        frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
    }

    /**
     * Turns logging on
     */
    public static void logginOn() { Utils.loggingOn = true; }

    /**
     * Turns logging on
     */
    public static void logginOff() { Utils.loggingOn = false; }

    /**
     * If {@link Utils#loggingOn} is set to true, logs message to the console
     *
     * @param  msg  the message to log
     */
    public static void log(String msg) {
        Utils.log(msg, false);
    }

    /**
     * If loggingOn is set to true, logs message to the console.
     * Optionally adds error tah to the message
     *
     * @param  msg  the message to log
     * @param  error  if true the message is printed as an error
     */
    public static void log(String msg, boolean error) {
        if(loggingOn) {
            if(error) msg = "ERR: " + msg;
            String time = formatter.format(LocalDateTime.now());
            printStream.println(time + " - " + msg);
        }
    }

    /**
     * If loggingOn is set to true, logs message to the console and
     * wait for user to click ENTER
     * @param msg the message to log
     */
    public static void step(String msg) {
        Utils.log(msg, false);
        Utils.log("Click ENTER to continue..." , false);
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
