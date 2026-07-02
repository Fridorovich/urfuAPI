package org.urfu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.urfu.entity.Institute;
import java.util.UUID;

@Repository
public interface InstituteRepository extends JpaRepository<Institute, UUID> {
}