package com.inetum.rlibrarybackend.repository;

import com.inetum.rlibrarybackend.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PersonRepository extends JpaRepository<Person, Integer>, JpaSpecificationExecutor<Person> {
    Optional<Person> findByAuth0Id(String auth0Id);
}
