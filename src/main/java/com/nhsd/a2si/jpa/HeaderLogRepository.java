package com.nhsd.a2si.jpa;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HeaderLogRepository extends CrudRepository<HeaderLog, Long> {
}
