
package com.software.cloudtunnelgateway.loadtest;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LoadTestRepository
        extends MongoRepository<LoadTestDocument, String> {

    Optional<LoadTestDocument> findByTestId(String testId);

}
