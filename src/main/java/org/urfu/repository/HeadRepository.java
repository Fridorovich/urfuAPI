package org.urfu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.urfu.entity.Head;

import java.util.UUID;

@Repository
public interface HeadRepository extends JpaRepository<Head, UUID> {
    boolean existsByFullname(String fullname);
}