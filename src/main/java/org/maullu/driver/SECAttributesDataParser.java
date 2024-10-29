package org.maullu.driver;

import lombok.extern.slf4j.Slf4j;
import org.maullu.model.SECCikAttributes;
import org.maullu.service.SECCikFilingsProcessor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class SECAttributesDataParser {
    public static void main(String[] args) {
        SECCikFilingsProcessor dataEnricher = new SECCikFilingsProcessor();
        File folder = new File("output_files");
        File[] listOfFiles = folder.listFiles((dir, name) -> name.endsWith(".json"));
        List<SECCikAttributes> attributesList = new ArrayList<>();
        if (listOfFiles != null) {
            for (File file : listOfFiles) {
                if (file.isFile()) {
                    SECCikAttributes attributes = dataEnricher.readAndParseJsonFile(file.getPath());
                    if (attributes != null) {
                        attributesList.add(attributes);
                    } else {
                        log.error("Failed to parse CIK Attributes from {}", file.getName());
                    }
                }
            }
        } else {
            log.error("No files found in the output_files directory.");
        }

        dataEnricher.writeAttributesToFile(attributesList, "output_files/parsed_attributes.txt");
    }
}
