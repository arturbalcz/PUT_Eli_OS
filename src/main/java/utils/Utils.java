package utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Semaphore;

/**
 * Utility methods used across all classes
 */
public class Utils {
    /**
     * If set to true, logs are printed
     */
    static private boolean logging = true;
    static private boolean stepping = false;

    private static final JFrame frame = new JFrame();
    private static final JTextArea textArea = new JTextArea(50, 10);
    private static final String KEY = "ENTER";
    private static final JButton enterButton = new JButton(KEY);
    private static final PrintStream printStream = new PrintStream(new CustomOutputStream(textArea));
    private static final Semaphore semaphore = new Semaphore(0);
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static String lastLogTime = "00:00:00";

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
        textArea.setFont(new Font("Lucida Console", Font.PLAIN,18));
        textArea.setEditable( false );
        frame.setSize(800,600);
        frame.setAlwaysOnTop(true  );

        KeyStroke keyStroke = KeyStroke.getKeyStroke(KEY);
        Object actionKey = textArea.getInputMap(
                JComponent.WHEN_FOCUSED).get(keyStroke);
        textArea.getActionMap().put(actionKey, wrapper);


        enterButton.addActionListener(ae -> semaphore.release());
    }

    /**
     * Closes second window
     */
    public static void closeLogs() { frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING)); }

    /**
     * Turns logging on
     */
    public static void loggingOn() { Utils.logging = true; }

    /**
     * Turns logging off
     */
    public static void loggingOff() { Utils.logging = false; }

    /**
     * Turns step work on
     */
    public static void stepOn() { Utils.stepping = true; }

    /**
     * Turns step work off
     */
    public static void stepOff() { Utils.stepping = false; }

    /**
     * If {@link Utils#logging} is set to true, logs message to the console
     *
     * @param  msg  the message to log
     */
    public static void log(String msg) {
        Utils.log(msg, false);
    }

    /**
     * If {@link Utils#logging} is set to true, logs message to the console.
     * Optionally adds error tah to the message
     *
     * @param  msg  the message to log
     * @param  error  if true the message is printed as an error
     */
    public static void log(String msg, boolean error) {
        if(logging) {
            if(error) msg = "ERR: " + msg;
            String time = formatter.format(LocalDateTime.now());

            if (lastLogTime.equals(time)) time = "        ";
            else lastLogTime = time;
            printStream.println(time + " " + msg);
        }
    }

    /**
     * If logging is set to true, logs message to the console and
     * wait for user to click ENTER
     * @param msg the message to log
     */
    public static void step(String msg) {
        boolean log = logging;
        loggingOn();
        Utils.log(msg, false);
        if (stepping) {
            Utils.log("Click ENTER to continue..." , false);
            try {
                semaphore.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Utils.log("Continued", false);
        }
        logging = log;
    }

    public static boolean isStepping() {
        return stepping;
    }
}
