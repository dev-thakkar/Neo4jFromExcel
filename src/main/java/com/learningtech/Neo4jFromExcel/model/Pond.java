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
public class Pond {

    @Id
    @GeneratedValue
    @JsonIgnore
    private Long id;

    private String pondId;
    private String name; // Pond name
    private double WSA;
    private double acresCovered;
    private double stockingDensity;
    private double totalInputCost;
    public Pond() {

    }
    public String getPondId() {
        return pondId;
    }

    public void setPondId(String pondId) {
        this.pondId = pondId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getWSA() {
        return WSA;
    }

    public void setWSA(double WSA) {
        this.WSA = WSA;
    }

    public double getAcresCovered() {
        return acresCovered;
    }

    public void setAcresCovered(double acresCovered) {
        this.acresCovered = acresCovered;
    }

    public double getStockingDensity() {
        return stockingDensity;
    }

    public void setStockingDensity(double stockingDensity) {
        this.stockingDensity = stockingDensity;
    }

    public double getTotalInputCost() {
        return totalInputCost;
    }

    public void setTotalInputCost(double totalInputCost) {
        this.totalInputCost = totalInputCost;
    }

    public Pond(String pondId, String name, double WSA, double acresCovered, double stockingDensity, double totalInputCost) {
        this.pondId = pondId;
        this.name = name;
        this.WSA = WSA;
        this.acresCovered = acresCovered;
        this.stockingDensity = stockingDensity;
        this.totalInputCost = totalInputCost;
    }
}
