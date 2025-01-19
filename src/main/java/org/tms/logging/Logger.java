package org.tms.logging;

import java.time.LocalDateTime;

public class Logger {

    public static void log(LogLevel loglevel ,String message) {

        String timeStamp = LocalDateTime.now().format();
        String lmessage = String.format("");
        System.out.println(message);
    }

}

