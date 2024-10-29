package org.maullu.service;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.maullu.model.SECCikIndex;

import java.io.IOException;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SECCikIndexFetcherTest {
    private static WireMockServer wireMockServer;

    @BeforeAll
    public static void setup() {
        wireMockServer = new WireMockServer(WireMockConfiguration.wireMockConfig().port(8080));
        wireMockServer.start();
        WireMock.configureFor("localhost", 8080);
    }

    @AfterAll
    public static void tearDown() {
        wireMockServer.stop();
    }

    @Test
    public void testFetchSECCikData() throws IOException {
        // Mock the SEC endpoint
        String jsonResponse = "[{\"cik_str\":\"0001550453\",\"title\":\"TriLinc Global Impact Fund LLC\",\"ticker\":\"TRLC\"}]";
        WireMock.stubFor(WireMock.get(urlEqualTo("/files/company_tickers.json"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(jsonResponse)));

        // Set the system property for user.agent
        System.setProperty("user.agent", "TestAgent");

        // Create an instance of SECCikIndexFetcher and call fetchSECCikData
        SECCikIndexFetcher fetcher = new SECCikIndexFetcher("http://localhost:8080/");
        List<SECCikIndex> dataItems = fetcher.fetchSECCikData();

        // Verify the results
        assertNotNull(dataItems);
        assertEquals(1, dataItems.size());
        assertEquals("0001550453", dataItems.get(0).getCikStr());
        assertEquals("TriLinc Global Impact Fund LLC", dataItems.get(0).getTitle());
        assertEquals("TRLC", dataItems.get(0).getTicker());
    }
}
