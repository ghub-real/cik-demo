package org.maullu.service;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.maullu.model.SECCikIndex;

import java.io.IOException;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SECCikIndexFetcherTest {
    @RegisterExtension
    static WireMockExtension wireMockExt = WireMockExtension.newInstance().options(WireMockConfiguration.wireMockConfig().dynamicPort()).build();

    @BeforeEach
    public void setUp() {
        System.clearProperty("user.agent");
        WireMock.configureFor("localhost", wireMockExt.getPort());
    }

    @Test
    public void testFetchSECCikData() throws IOException {
        // Mock the SEC endpoint
        String jsonResponse = "[{\"cik_str\":\"0001550453\",\"title\":\"TriLinc Global Impact Fund LLC\",\"ticker\":\"TRLC\"}]";
        WireMock.configureFor("localhost", wireMockExt.getPort());
        WireMock.stubFor(WireMock.get(urlEqualTo("/files/company_tickers.json"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(jsonResponse)));

        // Set the system property for user.agent
        System.setProperty("user.agent", "TestAgent");

        // Create an instance of SECCikIndexFetcher and call fetchSECCikData
        SECCikIndexFetcher fetcher = new SECCikIndexFetcher("http://localhost:" + wireMockExt.getPort() + "/");
        List<SECCikIndex> dataItems = fetcher.fetchSECCikData();

        // Verify the results
        assertNotNull(dataItems);
        assertEquals(1, dataItems.size());
        assertEquals("0001550453", dataItems.get(0).getCikStr());
        assertEquals("TriLinc Global Impact Fund LLC", dataItems.get(0).getTitle());
        assertEquals("TRLC", dataItems.get(0).getTicker());
    }
}
