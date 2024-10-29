package org.maullu.driver;

import org.maullu.model.SECCikIndex;
import org.maullu.service.SECCikIndexFetcher;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.List;

public class SECCIkIndexRetriever {
    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(SECCIkIndexRetriever.class);
    public static void main(String[] args) {
        SECCikIndexFetcher fetcher = new SECCikIndexFetcher();
        try {
            List<SECCikIndex> dataItems = fetcher.fetchSECCikData();
            fetcher.saveDataToFile(dataItems, "output.txt");
        } catch (IOException e) {
            logger.error("Failed to download data from SEC", e);
        }
    }
}
