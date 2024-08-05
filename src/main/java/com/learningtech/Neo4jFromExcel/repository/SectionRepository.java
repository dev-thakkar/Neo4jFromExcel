package com.learningtech.graphql_demo.repository;

import com.learningtech.graphql_demo.model.Section;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface SectionRepository extends Neo4jRepository<Section, Long> {
}
