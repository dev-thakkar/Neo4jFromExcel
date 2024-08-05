package com.learningtech.graphql_demo.repository;

import com.learningtech.graphql_demo.model.Pond;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface PondRepository extends Neo4jRepository<Pond, String> {
}
