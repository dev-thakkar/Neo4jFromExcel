package com.learningtech.graphql_demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Insurance {

    @Id
    @GeneratedValue
    @JsonIgnore
    private Long id;

    private String policyNumber;
    private double premium;
    private double GST;
    private double premiumInclGST;
    private String paymentDate;

    public Insurance() {
    }

    public String getPolicyNumber() {
        return policyNumber;
    }

    public void setPolicyNumber(String policyNumber) {
        this.policyNumber = policyNumber;
    }

    public double getPremium() {
        return premium;
    }

    public void setPremium(double premium) {
        this.premium = premium;
    }

    public double getGST() {
        return GST;
    }

    public void setGST(double GST) {
        this.GST = GST;
    }

    public double getPremiumInclGST() {
        return premiumInclGST;
    }

    public void setPremiumInclGST(double premiumInclGST) {
        this.premiumInclGST = premiumInclGST;
    }

    public String getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(String paymentDate) {
        this.paymentDate = paymentDate;
    }

    public Insurance(String policyNumber, double premium, double GST, double premiumInclGST, String paymentDate) {
        this.policyNumber = policyNumber;
        this.premium = premium;
        this.GST = GST;
        this.premiumInclGST = premiumInclGST;
        this.paymentDate = paymentDate;
    }
}
