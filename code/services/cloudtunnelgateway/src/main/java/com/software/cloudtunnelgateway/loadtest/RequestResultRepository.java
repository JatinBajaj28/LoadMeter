
package com.software.cloudtunnelgateway.loadtest;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RequestResultRepository
        extends MongoRepository<RequestResultDocument, String> {

    List<RequestResultDocument>
    findByTestIdOrderByRequestNumberAsc(String testId);

    Page<RequestResultDocument>
    findByTestIdOrderByRequestNumberAsc(
            String testId,
            Pageable pageable
    );

    long deleteByTestId(String testId);

    long countByTestId(String testId);
}
