import java.lang.System;
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

    static private JTextArea textArea = new JTextArea(50, 10);
    static private JButton enterButton = new JButton("ENTER");
    static private PrintStream standardOut = System.out;
    static private PrintStream printStream = new PrintStream(new CustomOutputStream(textArea));
    private static final String key = "ENTER";
    static private Semaphore semaphore = new Semaphore(0);

    /**
     * Action wrapper for mapping key with JButton
     */
    static private Action wrapper = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent ae) {
            enterButton.doClick();
        }
    };

    static {
        JFrame frame = new JFrame();
        frame.add( new JScrollPane( textArea )  );
        frame.pack();
        frame.setVisible( true );
        textArea.setEditable( false );
        frame.setSize(800,600);


        KeyStroke keyStroke = KeyStroke.getKeyStroke(key);
        Object actionKey = textArea.getInputMap(
                JComponent.WHEN_FOCUSED).get(keyStroke);
        textArea.getActionMap().put(actionKey, wrapper);


        enterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                semaphore.release();
            }
        });
    }


    /**
     * Turns logging on
     */
    static void logginOn() { Utils.loggingOn = true; }

    /**
     * Turns logging on
     */
    static void logginOff() { Utils.loggingOn = false; }

    static private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss:SS");

    /**
     * If {@link Utils#loggingOn} is set to true, logs message to the console
     *
     * @param  msg  the message to log
     */
    static void log(String msg) {
        Utils.log(msg, false);
    }

    /**
     * If loggingOn is set to true, logs message to the console.
     * Optionally adds error tah to the message
     *
     * @param  msg  the message to log
     * @param  error  if true the message is printed as an error
     */
    static void log(String msg, boolean error) {
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
    static void step(String msg) {
        Utils.log(msg, false);
        Utils.log("Click ENTER to continue..." , false);
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
