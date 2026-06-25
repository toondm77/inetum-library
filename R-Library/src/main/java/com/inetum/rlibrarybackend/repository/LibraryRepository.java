package com.inetum.rlibrarybackend.repository;

import com.inetum.rlibrarybackend.model.Library;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LibraryRepository extends JpaRepository<Library, Integer>, JpaSpecificationExecutor<Library> {
    Optional<Library> findByName(String name);
}
