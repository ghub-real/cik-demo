package org.maullu.driver;

import lombok.extern.slf4j.Slf4j;
import org.maullu.model.SECCikIndex;
import org.maullu.service.SECCikFilingsProcessor;

import java.io.IOException;
import java.util.List;

@Slf4j
public class SECCikFilingsRetriever {
    public static void main(String[] args) {
        SECCikFilingsProcessor dataEnricher = new SECCikFilingsProcessor();
        try {
            List<SECCikIndex> dataItems = dataEnricher.readCIKIndex("output.txt");
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
}
