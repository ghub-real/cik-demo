package org.maullu.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.maullu.model.SECCikIndex;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SECCikIndexFetcher {
    public List<SECCikIndex> fetchSECCikData() throws IOException {
        List<SECCikIndex> dataItems = new ArrayList<>();
        String url = "https://www.sec.gov/files/company_tickers.json";
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(url);
            request.setHeader("User-Agent", System.getProperty("user.agent"));
            request.setHeader("Accept-Encoding", "gzip, deflate");
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode rootNode = mapper.readTree(response.getEntity().getContent());
                rootNode.forEach(node -> {
                    SECCikIndex ticker = new SECCikIndex();
                    ticker.setCikStr(node.get("cik_str").asText());
                    ticker.setTitle(node.get("title").asText());
                    dataItems.add(ticker);
                });
            }
        }
        return dataItems;
    }
    // New method to save data to a pipe-delimited file
    public void saveDataToFile(List<SECCikIndex> dataItems, String filePath) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (SECCikIndex item : dataItems) {
                writer.write(item.getCikStr() + "|" + item.getTitle());
                writer.newLine();
            }
        }
    }
}
