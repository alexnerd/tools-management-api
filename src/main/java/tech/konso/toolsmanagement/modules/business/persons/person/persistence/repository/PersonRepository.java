package tech.konso.toolsmanagement.modules.business.persons.person.persistence.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import tech.konso.toolsmanagement.modules.business.persons.person.persistence.dao.Person;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository for person entity.
 * <p>Supports CRUD operations, searching by specification and pageable format.
 * <p>By default, uses read only transactions, if you want another mode, you must directly specify it on your method.
 */
@Repository
@Transactional(readOnly = true)
public interface PersonRepository extends CrudRepository<Person, Long>, JpaSpecificationExecutor<Person> {
    @Override
    @EntityGraph(attributePaths = {"roles", "labels"},
            type = EntityGraph.EntityGraphType.FETCH)
    Optional<Person> findById(Long id);

    @Override
    @EntityGraph(attributePaths = {"roles", "labels"},
            type = EntityGraph.EntityGraphType.FETCH)
    Page<Person> findAll(@Nullable Specification<Person> spec, Pageable pageable);

    @Query("SELECT p.photoUuid FROM Person p WHERE p.id = :id")
    Optional<UUID> findPhotoUuidByPersonId(Long id);
}
