package enums;

import java.time.LocalDate;
import java.time.LocalTime;

public enum Commands {
    UPPER("upper"){
        @Override
        public String runCommand(String msg) {
            return msg.toUpperCase();
        }
    },
    DATE("date") {
        @Override
        public String runCommand(String msg) {
            return LocalDate.now().toString();
        }
    },
    TIME("time") {
        @Override
        public String runCommand(String msg) {
            return LocalTime.now().toString();
        }
    },
    REVERSE("reverse") {
        @Override
        public String runCommand(String msg) {
            StringBuilder sb = new StringBuilder(msg);
            return sb.reverse().toString();
        }
    };

    private final String command;

    Commands(String command) {
        this.command = command;
    }

    public String getCommand() {
        return command;
    }

    public static Commands findByValue(String value) {
        for (Commands c : Commands.values()) {
            if (c.getCommand().equalsIgnoreCase(value)) {
                return c;
            }
        }
        return null;
    }

    public abstract String runCommand(String msg);
}
