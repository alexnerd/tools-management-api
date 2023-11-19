package tech.konso.toolsmanagement.modules.business.persons.person.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import tech.konso.toolsmanagement.modules.business.persons.person.controller.dto.*;
import tech.konso.toolsmanagement.modules.business.persons.person.persistence.dao.Person;
import tech.konso.toolsmanagement.modules.business.persons.person.persistence.repository.PersonRepository;
import tech.konso.toolsmanagement.modules.business.persons.person.persistence.specification.PersonSpecification;
import tech.konso.toolsmanagement.modules.business.persons.person.service.mappers.PersonsDtoMapper;
import tech.konso.toolsmanagement.modules.business.persons.person.service.mappers.PersonsEntityMapper;
import tech.konso.toolsmanagement.modules.integration.facade.FileStorageFacade;
import tech.konso.toolsmanagement.modules.integration.facade.FileType;
import tech.konso.toolsmanagement.modules.integration.facade.dto.UploadResponse;
import tech.konso.toolsmanagement.system.commons.exceptions.BPException;
import tech.konso.toolsmanagement.system.commons.specification.AbstractSpecification;

import java.util.Optional;
import java.util.UUID;

import static tech.konso.toolsmanagement.system.commons.specification.AbstractSpecification.specBuilder;

/**
 * Service layer for working with persons.
 */
@Service
public class PersonService {

    @Autowired
    private PersonRepository repository;

    @Autowired
    private PersonsEntityMapper entityMapper;

    @Autowired
    @Qualifier("file-storage-facade-impl")
    private FileStorageFacade fileStorageFacade;

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
        return repository.findById(id).map(personsDtoMapper::mapToPersonInfo).orElseThrow(() ->
                new BPException.NotFound("Person not found id: " + id));
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
                        .orElseThrow(() -> new BPException.NotFound("Person not found id: " + id))
                ).map(person -> entityMapper.toEntity(person, rq))
                .orElseGet(() ->
                        repository.save(entityMapper.toEntity(new Person(), rq))
                );
    }

    /**
     * Upload {@link MultipartFile} photo to file storage service.
     * <p>
     * Example:
     * <pre>
     *     uploadPhoto(multipartFile);
     * </pre>
     *
     * @param multipartFile {@link MultipartFile} photo for save to file storage
     * @return {@link UploadPhotoResponse} object with file id
     */
    public UploadPhotoResponse uploadPhoto(MultipartFile multipartFile) {
        UploadResponse rs = fileStorageFacade.upload(multipartFile, FileType.PHOTO_PERSON);
        if (rs.error() != null) {
            throw new BPException.ServiceUnavailable("Upload photo error: " + rs.error());
        }
        return new UploadPhotoResponse(rs.uuid());
    }

    /**
     * Find photo by person id in file storage.
     * <p>
     * Example:
     * <pre>
     *     findPhoto(3);
     * </pre>
     *
     * @param personId {@link Long} person id
     * @return InputStreamResource with searching file
     */
    public InputStreamResource findPhoto(Long personId) {
        UUID uuid = repository.findPhotoUuidByPersonId(personId).orElseThrow(() ->
                new BPException.NotFound("Photo uuid not found in person id: " + personId));
        return fileStorageFacade.download(uuid, FileType.PHOTO_PERSON);
    }
}
