package com.turikhay.caf.util;

import java.io.PrintStream;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Simple logger interface
 */
public interface Logger {
    /**
     * Logs informational message
     * @param message message to log
     */
    void logMessage(String message);

    /**
     * Logs error message
     * @param message message to log
     * @param error error to report
     */
    void logError(String message, Throwable error);

    /**
     * Creates the builder for consumer-based logger
     * @return builder
     */
    static ConsumerBasedLogger.Builder builder() {
        return new ConsumerBasedLogger.Builder();
    }

    /**
     * Dependency injection-based logger
     */
    class ConsumerBasedLogger implements Logger {
        private final Consumer<String> logMessageConsumer;
        private final BiConsumer<String, Throwable> logErrorConsumer;

        private ConsumerBasedLogger(Consumer<String> logMessageConsumer, BiConsumer<String, Throwable> logErrorConsumer) {
            if (logMessageConsumer == null) {
                throw new NullPointerException("logMessageConsumer");
            }
            if (logErrorConsumer == null) {
                throw new NullPointerException("logErrorConsumer");
            }
            this.logMessageConsumer = logMessageConsumer;
            this.logErrorConsumer = logErrorConsumer;
        }

        @Override
        public void logMessage(String message) {
            logMessageConsumer.accept(message);
        }

        @Override
        public void logError(String message, Throwable error) {
            logErrorConsumer.accept(message, error);
        }

        /**
         * Consumer-based logger builder
         */
        public static class Builder {
            private Consumer<String> logMessageConsumer;
            private BiConsumer<String, Throwable> logErrorConsumer;

            /**
             * Sets consumer for informational messages
             * @param logMessageConsumer consumer that logs informational messages
             * @return the same Builder
             */
            public Builder logMessage(Consumer<String> logMessageConsumer) {
                this.logMessageConsumer = logMessageConsumer;
                return this;
            }

            /**
             * Sets consumer for error messages messages
             * @param logErrorConsumer consumer that logs error messages
             * @return the same Builder
             */
            public Builder logError(BiConsumer<String, Throwable> logErrorConsumer) {
                this.logErrorConsumer = logErrorConsumer;
                return this;
            }

            /**
             * Creates ConsumerBasedLogger
             * @return ConsumerBasedLogger
             */
            public ConsumerBasedLogger build() {
                return new ConsumerBasedLogger(logMessageConsumer, logErrorConsumer);
            }
        }
    }

    /**
     * Logger that writes to {@link java.io.PrintStream}
     * 
     * @see java.lang.System#out
     * @see java.lang.System#err
     */
    class PrintLogger implements Logger {
        private final PrintStream messageStream, errorStream;

        /**
         * Creates new PrintLogger
         * @param messageStream informational messages stream
         * @param errorStream error messages stream
         */
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

        /**
         * Returns PrintLogger that writes to stdout and stderr.
         * @return singleton PrintLogger
         */
        public static PrintLogger ofSystem() {
            if (SYSTEM == null) {
                SYSTEM = new PrintLogger(System.out, System.err);
            }
            return SYSTEM;
        }
    }
}
