package org.maullu.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.maullu.model.SECCikAttributes;
import org.maullu.model.SECCikIndex;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class SECCikFilingsProcessor {

    public List<SECCikIndex> readCIKIndex(String filePath) throws IOException {
        List<SECCikIndex> dataItems = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length >= 2) {
                    SECCikIndex data = new SECCikIndex();
                    data.setCikStr(String.format("%010d", Integer.parseInt(parts[0])));
                    data.setTitle(parts[1]);
                    dataItems.add(data);
                }
            }
        }
        return dataItems;
    }

    public void fetchSECCikAttributes(String id) {
        try {
            String outputFileName = "output_files/CIK" + id + "_output.json";
            File outputFile = new File(outputFileName);
            if (!outputFile.exists()) {
                String url = "https://data.sec.gov/submissions/CIK" + id + ".json";
                this.saveFilingsToFile(url, outputFileName);
                log.info("File saved successfully.");
            } else {
                log.info("File already exists: {}", outputFileName);
            }
        } catch (IOException e) {
            log.error("Failed to download CIK attributes from SEC", e);
        }
    }

    private void saveFilingsToFile(String url, String filePath) throws IOException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(url);
            request.setHeader("User-Agent", System.getProperty("user.agent"));
            request.setHeader("Accept-Encoding", "gzip, deflate");

            try (CloseableHttpResponse response = httpClient.execute(request);
                 InputStream inputStream = response.getEntity().getContent();
                 FileOutputStream outputStream = new FileOutputStream(new File(filePath))) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            }
        }
    }

    public SECCikAttributes readAndParseJsonFile(String filePath) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode jsonNode = objectMapper.readTree(new File(filePath));
            return parseJson(jsonNode);
        } catch (IOException e) {
            log.error("Failed to read or parse JSON file", e);
        }
        return null;
    }

    private SECCikAttributes parseJson(JsonNode jsonNode) {
        SECCikAttributes attributes = new SECCikAttributes();
        attributes.setCik(jsonNode.get("cik").asText());
        attributes.setCompanyName(jsonNode.get("name").asText());
        attributes.setIncorporationStateOrCountry(jsonNode.path("stateOfIncorporation").asText());
        attributes.setBusinessStateOrCountry(jsonNode.path("addresses").path("business").path("stateOrCountry").asText());
        attributes.setEin(jsonNode.path("ein").asText());
        return attributes;
    }

    public void writeAttributesToFile(List<SECCikAttributes> attributesList, String filePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write("CIK|Company_Name|Incorporation_State_or_Country|Business_State_or_Country|EIN");
            writer.newLine();
            for (SECCikAttributes attributes : attributesList) {
                writer.write(String.join("|",
                        attributes.getCik(),
                        attributes.getCompanyName(),
                        attributes.getIncorporationStateOrCountry(),
                        attributes.getBusinessStateOrCountry(),
                        attributes.getEin()));
                writer.newLine();
            }
            log.info("Attributes written to file successfully.");
        } catch (IOException e) {
            log.error("Failed to write attributes to file", e);
        }
    }

}
