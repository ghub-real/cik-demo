package org.maullu.driver;

import lombok.extern.slf4j.Slf4j;
import org.maullu.model.SECCikIndex;
import org.maullu.service.SECCikFilingsProcessor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class SECCikFilingsRetriever {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: java SECCikFilingsRetriever <command>");
            System.out.println("Commands: fetchFilings, checkMissingFiles");
            return;
        }

        String command = args[0];
        switch (command) {
            case "fetchFilings":
                fetchFilings("output.txt");
                break;
            case "checkMissingFiles":
                try {
                    List<String> missingFiles = checkMissingFiles("output.txt", "output_files");
                    missingFiles.forEach(log::info);
                } catch (IOException e) {
                    log.error("Failed to check missing files", e);
                }
                break;
            default:
                System.out.println("Unknown command: " + command);
                System.out.println("Commands: fetchFilings, checkMissingFiles");
        }


    }

    private static void fetchFilings(String filePath) {
        SECCikFilingsProcessor dataEnricher = new SECCikFilingsProcessor();
        try {
            List<SECCikIndex> dataItems = dataEnricher.readCIKIndex(filePath);
            int counter = 0;
            for (SECCikIndex item : dataItems) {
                dataEnricher.fetchSECCikAttributes(item.getCikStr());
                counter++;
                if (counter % 50 == 0) {
                    try {
                        Thread.sleep(5000); // Add a delay of 5 seconds
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        log.trace("Thread was interrupted", e);
                    }
                }
            }
        } catch (IOException e) {
            log.error("Failed to aggregate data from SEC", e);
        }
    }

    public static List<String> checkMissingFiles(String outputFilePath, String outputFolderPath) throws IOException {
        List<String> missingFiles = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(outputFilePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String cik = String.format("%010d", Integer.parseInt(line.split("\\|")[0]));
                File file = new File(outputFolderPath, "CIK" + cik + "_output.json");
                if (!file.exists()) {
                    missingFiles.add(cik);
                }
            }
        }
        return missingFiles;
    }
}
