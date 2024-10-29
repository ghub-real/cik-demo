package org.maullu.model;

import lombok.Data;

import java.util.List;

@Data
public class SECCikAttributes {
    private String cik;
    private String companyName;
    private String incorporationStateOrCountry;
    private String businessStateOrCountry;
    private String ein;
}
