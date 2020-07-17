package com.ochodek.server.repository;

import com.ochodek.server.entity.AnotherCityName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnotherCityRepository extends JpaRepository<AnotherCityName, Long> {
}
