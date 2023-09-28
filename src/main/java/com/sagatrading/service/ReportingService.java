package com.sagatrading.service;

import com.sagatrading.model.LogType;
import org.springframework.stereotype.Service;

@Service
public class ReportingService {
    public void queueLog(LogType logType, Object logMessage) {
        System.out.println(logType + " " + logMessage);
    }
}
