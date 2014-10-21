package util;

import java.io.File;

/**
 * .
 */
public class MkdirCommand extends Command {
    /**
     * @param cmd
     *            params
     */
    MkdirCommand(final String[] cmd) {
        super(cmd);
    }

    /**
     * .
     */
    @Override
    public final void run() {
        if (args.length != 2) {
            System.out.println("Wrong number of arguments");
            return;
        }
        File folder = new File(System.getProperty("user.dir"), args[1]);

        if (!folder.exists()) {
            try {
                folder.mkdir();
            } catch (SecurityException e) {
                System.out
                        .println("cannot create directory : Permission denied");
            }
        } else {
            System.out.println("cannot create directory : File exists");
        }
    }
}
