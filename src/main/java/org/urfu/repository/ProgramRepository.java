package org.urfu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.urfu.entity.Program;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProgramRepository extends JpaRepository<Program, UUID> {
    @Query("SELECT p FROM Program p LEFT JOIN FETCH p.modules LEFT JOIN FETCH p.institute LEFT JOIN FETCH p.head")
    List<Program> findAllWithModules();

    @Query("SELECT p FROM Program p LEFT JOIN FETCH p.modules LEFT JOIN FETCH p.institute LEFT JOIN FETCH p.head WHERE p.uuid = :uuid")
    Optional<Program> findByIdWithModules(@Param("uuid") UUID uuid);
}