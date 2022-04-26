package com.turikhay.caf.util;

import java.io.PrintStream;

public interface Logger {
    void logMessage(String message);

    void logError(String message, Throwable error);

    class PrintLogger implements Logger {
        private final PrintStream messageStream, errorStream;

        public PrintLogger(PrintStream messageStream, PrintStream errorStream) {
            this.messageStream = messageStream;
            this.errorStream = errorStream;
        }

        @Override
        public void logMessage(String message) {
            messageStream.println(message);
        }

        @Override
        public void logError(String message, Throwable error) {
            errorStream.println(error);
            error.printStackTrace(errorStream);
        }

        private static PrintLogger SYSTEM;

        public static PrintLogger ofSystem() {
            if (SYSTEM == null) {
                SYSTEM = new PrintLogger(System.out, System.err);
            }
            return SYSTEM;
        }
    }
}
