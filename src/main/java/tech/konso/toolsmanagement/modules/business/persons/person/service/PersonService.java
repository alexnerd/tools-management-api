package tech.konso.toolsmanagement.modules.business.persons.person.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.konso.toolsmanagement.modules.business.persons.label.service.LabelService;
import tech.konso.toolsmanagement.modules.business.persons.person.controller.dto.PersonFilterInfo;
import tech.konso.toolsmanagement.modules.business.persons.person.controller.dto.PersonFilterResponse;
import tech.konso.toolsmanagement.modules.business.persons.person.controller.dto.PersonInfo;
import tech.konso.toolsmanagement.modules.business.persons.person.controller.dto.PersonRequest;
import tech.konso.toolsmanagement.modules.business.persons.person.persistence.dao.Person;
import tech.konso.toolsmanagement.modules.business.persons.person.persistence.repository.PersonRepository;
import tech.konso.toolsmanagement.modules.business.persons.person.persistence.specification.PersonSpecification;
import tech.konso.toolsmanagement.modules.business.persons.person.service.mappers.PersonsDtoMapper;
import tech.konso.toolsmanagement.modules.business.persons.role.service.RoleService;
import tech.konso.toolsmanagement.system.commons.specification.AbstractSpecification;
import tech.konso.toolsmanagement.system.commons.exceptions.BPException;

import java.util.Optional;
import java.util.UUID;

import static tech.konso.toolsmanagement.system.commons.specification.AbstractSpecification.specBuilder;

/**
 * Service layer for working with persons.
 */
@Service
public class PersonService {

    @Autowired
    private RoleService roleService;

    @Autowired
    private LabelService labelService;

    @Autowired
    private PersonRepository repository;

    private PersonsDtoMapper personsDtoMapper;

    @PostConstruct
    public void init() {
        personsDtoMapper = new PersonsDtoMapper();
    }

    /**
     * Find person in database by unique id. Person must exist in database
     * <p>
     * Example:
     * <pre>
     *     Person person = findById(2L);
     * </pre>
     *
     * @param id of person, must exist in database
     * @return person from database
     * @throws BPException if person not exists in database
     */
    public PersonInfo findById(Long id) {
        return repository.findById(id).map(personsDtoMapper::mapToPersonInfo).orElseThrow(() -> new BPException("Person not found id: " + id));
    }

    /**
     * Finds persons by person specification and returns it in pageable format.
     * By default, result set sorts by create date from newer to older and without archived persons.
     * <p>
     * Example:
     * <pre>
     *     Specification&lt;Person> spec = specBuilder(sortSpec("name,desc")).build();
     *     PersonFilterResponse foundedPersons = service.findAll(0, 100, spec);
     * </pre>
     *
     * @param page number of returned result set
     * @param size of the returned page
     * @param spec set of person specification
     * @return {@link PersonFilterResponse} object for resulting dataset in pageable format
     * @see PersonSpecification person specifications
     */
    public Page<PersonFilterInfo> findAll(int page, int size, Specification<Person> spec) {
        AbstractSpecification.SpecBuilder<Person> builder = specBuilder(Person.class);
        Pageable pageable = PageRequest.of(page, size);
        return repository.findAll(builder.and(spec).build(), pageable).map(personsDtoMapper::mapToPersonFilterInfo);
    }

    /**
     * Save new person to database or update existing.
     * Run under transaction.
     * <p>
     * Example:
     * <pre>
     *     PersonRequest rq = new PersonRequest(null, "new_person", null, null, false);
     *     Person savedPerson = service.save(rq);
     * </pre>
     *
     * @param rq {@link PersonRequest} object for creating person
     * @return {@link Person} saved object
     */
    @Transactional
    public Person save(PersonRequest rq) {
        return Optional.ofNullable(rq.id())
                .map(id -> repository.findById(rq.id())
                        .orElseThrow(() -> new BPException("Person not found id: " + id))
                ).map(person -> toEntity(person, rq))
                .orElseGet(() ->
                        repository.save(toEntity(new Person(), rq))
                );
    }

    /**
     * Converts {@link PersonRequest} to {@link Person} object.
     * <p>
     * Example:
     * <pre>
     *     toEntity(new Person(), rq);
     * </pre>
     *
     * @param person {@link Person} object for save to database or update existing
     * @param rq {@link PersonRequest} object for converting to {@link Person}
     * @return {@link Person} saved object
     */
    private Person toEntity(Person person, PersonRequest rq) {
        if (person.getId() == null) {
            person.setUuid(UUID.randomUUID());
        }
        person.setPhoneNumber(rq.phoneNumber());
        person.setCompanyUuid(rq.companyUuid());
        person.setSurname(rq.surname());
        person.setName(rq.name());
        person.setPatronymic(rq.patronymic());
        person.setJobTitle(rq.jobTitle());
        person.setIsArchived(rq.isArchived());
        person.setIsUnregistered(rq.isUnregistered());

        person.removeLabels();
        rq.labels().stream().map(labelId -> labelService.getReference(labelId)).forEach(person::addLabel);

        person.removeRoles();
        rq.roles().stream().map(roleId -> roleService.getReference(roleId)).forEach(person::addRole);

        return person;
    }
}
