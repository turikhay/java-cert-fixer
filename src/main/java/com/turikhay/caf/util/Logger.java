package com.turikhay.caf.util;

import java.io.PrintStream;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public interface Logger {
    void logMessage(String message);

    void logError(String message, Throwable error);

    static ConsumerBasedLogger.Builder builder() {
        return new ConsumerBasedLogger.Builder();
    }

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

        public static class Builder {
            private Consumer<String> logMessageConsumer;
            private BiConsumer<String, Throwable> logErrorConsumer;

            public Builder logMessage(Consumer<String> logMessageConsumer) {
                this.logMessageConsumer = logMessageConsumer;
                return this;
            }

            public Builder logError(BiConsumer<String, Throwable> logErrorConsumer) {
                this.logErrorConsumer = logErrorConsumer;
                return this;
            }

            public ConsumerBasedLogger build() {
                return new ConsumerBasedLogger(logMessageConsumer, logErrorConsumer);
            }
        }
    }

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
