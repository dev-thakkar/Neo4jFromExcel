package com.learningtech.graphql_demo.service;

import com.learningtech.graphql_demo.model.Insurance;
import com.learningtech.graphql_demo.model.Pond;
import com.learningtech.graphql_demo.model.Section;
import com.learningtech.graphql_demo.repository.InsuranceRepository;
import com.learningtech.graphql_demo.repository.PondRepository;
import com.learningtech.graphql_demo.repository.SectionRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Slf4j
@Service
public class ExcelToNeo4jService {
    @Autowired
    private final Driver neo4jDriver;

    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private PondRepository pondRepository;

    @Autowired
    private InsuranceRepository insuranceRepository;

    @Autowired
    public ExcelToNeo4jService(Driver neo4jDriver) {
        this.neo4jDriver = neo4jDriver;
    }

    public void processExcel(InputStream excelFile) throws IOException {
        //FileInputStream excelFile = new FileInputStream(excelFilePath);

        //internally convert byte stream to java class  which is WORKBOOK
        Workbook workbook = new XSSFWorkbook(excelFile);

        try (Session session = neo4jDriver.session()) {
            processNodes(session, workbook);
        }

        workbook.close();
        excelFile.close();
    }

    private void processNodes(Session session, Workbook workbook) {
        Map<String, Section> sectionsMap = new HashMap<>();
        Map<String, Pond> pondsMap = new HashMap<>();
        Map<String, Insurance> insurancesMap = new HashMap<>();

        Sheet sheet = workbook.getSheet("Sheet1"); // Adjust sheet name as per your Excel file
        Iterator<Row> iterator = sheet.iterator();
        if (iterator.hasNext()) {
            iterator.next(); // Skip header row
        }

        //actual rows reading from excel
        while (iterator.hasNext()) {
            Row currentRow = iterator.next();

            String sectionName = currentRow.getCell(0).getStringCellValue();
            String pondId = String.valueOf(currentRow.getCell(1).getNumericCellValue());
            String policyNumber = currentRow.getCell(7).getStringCellValue();

            Section section = sectionsMap.computeIfAbsent(sectionName, Section::new);

            Pond pond = pondsMap.computeIfAbsent(sectionName + "_" + pondId, k -> {
                Pond newPond = new Pond();
                newPond.setPondId(pondId);
                newPond.setName(currentRow.getCell(2).getStringCellValue());
                newPond.setWSA(currentRow.getCell(3).getNumericCellValue());
                newPond.setAcresCovered(currentRow.getCell(4).getNumericCellValue());
                newPond.setStockingDensity(currentRow.getCell(5).getNumericCellValue());
                newPond.setTotalInputCost(currentRow.getCell(6).getNumericCellValue());
                return newPond;
            });

            Insurance insurance = insurancesMap.computeIfAbsent(policyNumber, k -> {
                Insurance newInsurance = new Insurance();
                newInsurance.setPolicyNumber(policyNumber);
                newInsurance.setPremium(currentRow.getCell(8).getNumericCellValue());
                newInsurance.setGST(currentRow.getCell(9).getNumericCellValue());
                newInsurance.setPremiumInclGST(currentRow.getCell(10).getNumericCellValue());
                newInsurance.setPaymentDate(currentRow.getCell(11).getDateCellValue().toString());
                return newInsurance;
            });

            // Create nodes and relationships in Neo4j
            createNodesAndRelationships(session, section, pond, insurance);
        }
    }

    private void createNodesAndRelationships(Session session, Section section, Pond pond, Insurance insurance) {
        // Create Section node if not already created
        if (section != null) {
            createSectionNode(session, section);
        }

        // Create Pond node if not already created
        if (pond != null) {
            createPondNode(session, section.getName(), pond);
        }

        // Create Insurance node if not already created
        if (insurance != null) {
            createInsuranceNode(session, insurance, pond.getPondId());
        }

        // Establish relationships
//        if (section != null && pond != null) {
//            createRelationship(session, section.getName(), pond.getPondId());
//        }
//
//        if (pond != null && insurance != null) {
//            createRelationship(session, pond.getPondId(), insurance.getPolicyNumber());
//        }
    }

    private void createSectionNode(Session session, Section section) {
        String query = "CREATE (:Section {name: $name})";
        session.writeTransaction(tx -> tx.run(query, Collections.singletonMap("name", section.getName())));
    }

    private void createPondNode(Session session, String sectionName, Pond pond) {
        String query = "MATCH (s:Section {name: $sectionName}) " +
                "CREATE (s)-[:HAS_POND]->(:Pond {pondId: $pondId, name: $name, " +
                "WSA: $WSA, acresCovered: $acresCovered, stockingDensity: $stockingDensity, " +
                "totalInputCost: $totalInputCost})";

        Map<String, Object> params = new HashMap<>();
        params.put("sectionName", sectionName);
        params.put("pondId", pond.getPondId());
        params.put("name", pond.getName());
        params.put("WSA", pond.getWSA());
        params.put("acresCovered", pond.getAcresCovered());
        params.put("stockingDensity", pond.getStockingDensity());
        params.put("totalInputCost", pond.getTotalInputCost());

        session.writeTransaction(tx -> tx.run(query, params));
    }

    private void createInsuranceNode(Session session, Insurance insurance, String pondId) {
        String query = "Match (s:Pond {pondId: $pondId}) " +
                "CREATE (s)-[:HAS_INSURANCE]->(:Insurance {policyNumber: $policyNumber, " +
                "premium: $premium, GST: $GST, premiumInclGST: $premiumInclGST, " +
                "paymentDate: $paymentDate})";

        Map<String, Object> params = new HashMap<>();
        params.put(("pondId"), pondId);
        params.put("policyNumber", insurance.getPolicyNumber());
        params.put("premium", insurance.getPremium());
        params.put("GST", insurance.getGST());
        params.put("premiumInclGST", insurance.getPremiumInclGST());
        params.put("paymentDate", insurance.getPaymentDate());

        session.writeTransaction(tx -> tx.run(query, params));
    }

//    private void createRelationship(Session session, String startNode, String endNode) {
//        String query = "MATCH (start), (end) WHERE start.name = $startNode AND end.id = $endNode " +
//                "CREATE (start)-[:HAS_RELATIONSHIP]->(end)";
//
//        Map<String, Object> params = new HashMap<>();
//        params.put("startNode", startNode);
//        params.put("endNode", endNode);
//
//        session.writeTransaction(tx -> tx.run(query, params));
//    }

    public List<Object> getAll() {
        try (Session session = neo4jDriver.session()) {
            // custom query and convert result to list
            Result result = session.run("MATCH (s:Section)-->(p:Pond)-->(i:Insurance)" +
                    " RETURN s.name," +
                    " p.pondId, p.name, p.WSA, p.acresCovered, p.stockingDensity, p.totalInputCost," +
                    " i.policyNumber, i.premium, i.GST, i.premiumInclGST, i.paymentDate");
            List<Object> sectionList = new ArrayList<>();
            while (result.hasNext()) {
                Record record = result.next();
                //converting neo4j node in java obj and adding it to list
                Section section = new Section(record.get("s.name").asString());
                Pond pond = new Pond(record.get("p.pondId").asString(),
                        record.get("p.name").asString(),
                        record.get("p.WSA").asDouble(),
                        record.get("p.acresCovered").asDouble(),
                        record.get("p.stockingDensity").asDouble(),
                        record.get("p.totalInputCost").asDouble());
                Insurance insurance = new Insurance(record.get("i.policyNumber").asString(),
                        record.get("i.premium").asDouble(),
                        record.get("i.GST").asDouble(),
                        record.get("i.premiumInclGST").asDouble(),
                        record.get("i.paymentDate").asString());
                sectionList.add(section);
                sectionList.add(pond);
                sectionList.add(insurance);
            }
            return sectionList;
            // return (List<Section>)sectionRepository.findAll();
        }
    }

    public List<Map<String, Object>> getSumPremium() {
        try (Session session = neo4jDriver.session()) {
            return session.readTransaction(tx -> {
                Result result = tx.run("MATCH (p:Pond)-->(i:Insurance) " +
                        "RETURN p.pondId AS pondId, SUM(i.premium) AS TotalCost");

                List<Map<String, Object>> resultList = new ArrayList<>();

                while (result.hasNext()) {
                    Record record = result.next();

                    Map<String, Object> resultMap = new HashMap<>();
                    resultMap.put("pondId", record.get("pondId").asString());
                    resultMap.put("TotalCost", record.get("TotalCost").asDouble());

                    resultList.add(resultMap);
                }

                return resultList;
            });
        }
    }
}

//    public List<Object> getSumPremium(){
//            try (Session session = neo4jDriver.session()) {
//
//                return session.readTransaction(tx -> {
//                    Result result = tx.run("match(p:Pond)-->(i:Insurance) \n" +
//                            "return p.pondId, SUM(i.premium) AS TotalCost");
//                    List<Object> sectionList = new ArrayList<>();
//                    while (result.hasNext()) {
//                        Record record = result.next();
//                        //converting neo4j node in java obj and adding it to list
//                        String pond = record.get("p.pondId").asString();
//                        Double TotalCost =record.get("TotalCost").asDouble();
//                        sectionList.add(pond);
//                        sectionList.add(TotalCost);
//                    }
//                    return sectionList;
//                });
//            }
//        }
//}
