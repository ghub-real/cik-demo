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
    private final String baseUrl;

    public SECCikIndexFetcher(String baseUrl) {
        this.baseUrl = baseUrl;
    }
    public List<SECCikIndex> fetchSECCikData() throws IOException {
        List<SECCikIndex> dataItems = new ArrayList<>();
        String url = this.baseUrl + "files/company_tickers.json";
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(url);
            request.setHeader("User-Agent", System.getProperty("user.agent"));
            request.setHeader("Accept-Encoding", "gzip, deflate");
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode rootNode = mapper.readTree(response.getEntity().getContent());
                rootNode.forEach(node -> {
                    SECCikIndex cikIndex = new SECCikIndex();
                    cikIndex.setCikStr(node.get("cik_str").asText());
                    cikIndex.setTitle(node.get("title").asText());
                    cikIndex.setTicker(node.get("ticker").asText());
                    dataItems.add(cikIndex);
                });
            }
        }
        return dataItems;
    }
    // New method to save data to a pipe-delimited file
    public void saveDataToFile(List<SECCikIndex> dataItems, String filePath) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (SECCikIndex item : dataItems) {
                writer.write(item.getCikStr() + "|" + item.getTitle() + "|" + item.getTicker());
                writer.newLine();
            }
        }
    }
}
