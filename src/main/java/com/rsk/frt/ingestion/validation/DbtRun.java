package com.rsk.frt.ingestion.validation;

import org.springframework.stereotype.Service;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Service
public class DbtRun {

    public Map<String, String> executeDbtRun() throws IOException{
        Map<String, String> result = new HashMap<>();
        ProcessBuilder processBuilder = new ProcessBuilder("bash", "-c", "dbt run");
        processBuilder.directory(new File("/Users/Montgomery.baring/Documents/dbt_pipelines/dbt_cl_test"));
        Process process = processBuilder.start();
        String processLogs;
        System.out.println(getProcessLogs(process));
        try {
            process.waitFor();
            processLogs = "---> DBT execution Completed...";
        } catch (InterruptedException e) {
            processLogs = "---> Interrupted Exception: " + e.getMessage();
        }
        result.put("result_status", String.valueOf(process.exitValue() == 0));
        result.put("description", processLogs);
        return result;
    }

    private static String getProcessLogs(Process process) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String logsLine;
            while ((logsLine = reader.readLine()) != null) {
                stringBuilder.append(logsLine).append("\n");
            }
        }
        return stringBuilder.toString();
    }

}