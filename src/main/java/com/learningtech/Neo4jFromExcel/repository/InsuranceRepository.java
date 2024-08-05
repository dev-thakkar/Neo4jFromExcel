package com.learningtech.graphql_demo.repository;

import com.learningtech.graphql_demo.model.Insurance;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface InsuranceRepository extends Neo4jRepository<Insurance, Long> {
}
