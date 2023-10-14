package tech.konso.toolsmanagement.modules.persons.business.person.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import tech.konso.toolsmanagement.PostgreSQLContainerExtension;
import tech.konso.toolsmanagement.modules.persons.business.label.persistence.dao.Label;
import tech.konso.toolsmanagement.modules.persons.business.person.controller.dto.LabelShort;
import tech.konso.toolsmanagement.modules.persons.business.person.controller.dto.PersonInfo;
import tech.konso.toolsmanagement.modules.persons.business.person.controller.dto.PersonRequest;
import tech.konso.toolsmanagement.modules.persons.business.person.controller.dto.RoleShort;
import tech.konso.toolsmanagement.modules.persons.business.person.persistence.dao.Person;
import tech.konso.toolsmanagement.modules.persons.business.role.persistence.dao.Role;
import tech.konso.toolsmanagement.modules.tools.business.tool.controller.dto.ToolRequest;
import tech.konso.toolsmanagement.modules.tools.business.tool.persistence.dao.Tool;
import tech.konso.toolsmanagement.system.commons.exceptions.BPException;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Person service layer tests.
 * For every test, the given-when-then is used.
 * The given part sets the stage for the actual test and captures all prerequisites for executing the functionality
 * we want to test. The when part triggers the operation that we actually want to test. And in the then part, we
 * assert that the result that when the trigger produced is actually what we expect.
 * <p> Example:
 * <p> given - a calculator showing the number 2
 * <p> when - adding the number 3
 * <p> then - it should show the number 5
 */
@SpringBootTest
@ExtendWith(PostgreSQLContainerExtension.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class PersonServiceTest {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private PersonService service;

    @BeforeEach
    public void setUp() {
        jdbcTemplate.update("INSERT INTO persons_person (surname, name, job_title, uuid) VALUES ('surname_1', 'name_1', 'job_title_1', '935921a7-692e-4ee4-a089-2695b68e9801')");
        jdbcTemplate.update("INSERT INTO persons_person (surname, name, job_title, uuid) VALUES ('surname_2', 'name_2', 'job_title_2', '935921a7-692e-4ee4-a089-2695b68e9802')");
        jdbcTemplate.update("INSERT INTO persons_person (surname, name, job_title, uuid) VALUES ('surname_3', 'name_3', 'job_title_3', '935921a7-692e-4ee4-a089-2695b68e9803')");
        jdbcTemplate.update("INSERT INTO persons_person (surname, name, job_title, uuid) VALUES ('surname_4', 'name_4', 'job_title_4', '935921a7-692e-4ee4-a089-2695b68e9804')");
        jdbcTemplate.update("INSERT INTO persons_person (surname, name, job_title, uuid) VALUES ('surname_5', 'name_5', 'job_title_5', '935921a7-692e-4ee4-a089-2695b68e9805')");
        jdbcTemplate.update("INSERT INTO persons_person (surname, name, job_title, uuid, is_archived) VALUES ('surname_6', 'name_6', 'job_title_6', '935921a7-692e-4ee4-a089-2695b68e9806', 'true')");
    }

    @AfterEach
    public void tearDown() {
        jdbcTemplate.update("DELETE FROM persons_person_label");
        jdbcTemplate.update("DELETE FROM persons_person_role");
        jdbcTemplate.update("DELETE FROM persons_person");
        jdbcTemplate.update("DELETE FROM persons_label");
        jdbcTemplate.update("DELETE FROM persons_role");

    }

    /**
     * Create {@link PersonRequest.PersonRequestBuilder} object with required non-null fields.
     */
    private PersonRequest.PersonRequestBuilder getDefaultPersonRequest() {
        return PersonRequest.builder()
                .surname("Surname")
                .name("Name")
                .jobTitle("Job title")
                .isArchived(false)
                .isUnregistered(false)
                .roles(Collections.emptySet())
                .labels(Collections.emptySet());
    }

    /**
     * {@link PersonService#save(PersonRequest)}} should save {@link Person} object.
     * Test creates dto object {@link PersonRequest} and then using {@link PersonService#save(PersonRequest)}
     * try to save new {@link Person} object to database.
     * Then checks returns {@link Person} object if id, uuid, createdAt and updatedAt fields are not null.
     */
    @Test
    public void save_should_save_person_test() {
        PersonRequest rq = getDefaultPersonRequest()
                .build();

        Person savedPerson = service.save(rq);

        assertNotNull(savedPerson.getId());
        assertNotNull(savedPerson.getUuid());
        assertNotNull(savedPerson.getCreatedAt());
        assertNotNull(savedPerson.getUpdatedAt());
    }

    /**
     * {@link PersonService#save(PersonRequest)}} should save {@link Person} object with phone number.
     * Test creates dto object {@link PersonRequest} and then using {@link PersonService#save(PersonRequest)}
     * try to save new {@link Tool} object to database.
     * Then checks returns {@link Tool} object if id not null and phone number field equals this field
     * from dto object {@link ToolRequest}.
     */
    @Test
    public void save_should_save_person_phone_number_test() {
        String phoneNumber = "n+7-909-456-78-90";
        PersonRequest rq = getDefaultPersonRequest()
                .phoneNumber(phoneNumber)
                .build();

        Person savedPerson = service.save(rq);

        assertNotNull(savedPerson.getId());
        assertEquals(rq.phoneNumber(), savedPerson.getPhoneNumber());
    }

    /**
     * {@link PersonService#save(PersonRequest)}} should save {@link Person} object with company uuid.
     * Test creates dto object {@link PersonRequest} and then using {@link PersonService#save(PersonRequest)}
     * try to save new {@link Tool} object to database.
     * Then checks returns {@link Tool} object if id not null and company uuid field equals this field
     * from dto object {@link ToolRequest}.
     */
    @Test
    public void save_should_save_person_company_uuid_test() {
        UUID companyUuid = UUID.fromString("935921a7-692e-4ee4-a089-2695b68e9801");
        PersonRequest rq = getDefaultPersonRequest()
                .companyUuid(companyUuid)
                .build();

        Person savedPerson = service.save(rq);

        assertEquals(rq.companyUuid(), savedPerson.getCompanyUuid());
    }

    /**
     * {@link PersonService#save(PersonRequest)}} should save {@link Person} object with surname.
     * Test creates dto object {@link PersonRequest} and then using {@link PersonService#save(PersonRequest)}
     * try to save new {@link Tool} object to database.
     * Then checks returns {@link Tool} object if surname field equals this field
     * from dto object {@link ToolRequest}.
     */
    @Test
    public void save_should_save_person_surname_test() {
        String surname = "Rose";
        PersonRequest rq = getDefaultPersonRequest()
                .surname(surname)
                .build();

        Person savedPerson = service.save(rq);

        assertEquals(rq.surname(), savedPerson.getSurname());
    }

    /**
     * {@link PersonService#save(PersonRequest)}} should save {@link Person} object with name.
     * Test creates dto object {@link PersonRequest} and then using {@link PersonService#save(PersonRequest)}
     * try to save new {@link Tool} object to database.
     * Then checks returns {@link Tool} object if name field equals this field
     * from dto object {@link ToolRequest}.
     */
    @Test
    public void save_should_save_person_name_test() {
        String name = "Kevin";
        PersonRequest rq = getDefaultPersonRequest()
                .name(name)
                .build();

        Person savedPerson = service.save(rq);

        assertEquals(rq.name(), savedPerson.getName());
    }

    /**
     * {@link PersonService#save(PersonRequest)}} should save {@link Person} object with patronymic.
     * Test creates dto object {@link PersonRequest} and then using {@link PersonService#save(PersonRequest)}
     * try to save new {@link Tool} object to database.
     * Then checks returns {@link Tool} object if patronymic field equals this field
     * from dto object {@link ToolRequest}.
     */
    @Test
    public void save_should_save_person_patronymic_test() {
        String patronymic = "Robertson";
        PersonRequest rq = getDefaultPersonRequest()
                .patronymic(patronymic)
                .build();

        Person savedPerson = service.save(rq);

        assertEquals(rq.patronymic(), savedPerson.getPatronymic());
    }

    /**
     * {@link PersonService#save(PersonRequest)}} should save {@link Person} object with job title.
     * Test creates dto object {@link PersonRequest} and then using {@link PersonService#save(PersonRequest)}
     * try to save new {@link Tool} object to database.
     * Then checks returns {@link Tool} object if job title field equals this field
     * from dto object {@link ToolRequest}.
     */
    @Test
    public void save_should_save_person_job_title_test() {
        String jobTitle = "Doctor";
        PersonRequest rq = getDefaultPersonRequest()
                .jobTitle(jobTitle)
                .build();

        Person savedPerson = service.save(rq);

        assertEquals(rq.jobTitle(), savedPerson.getJobTitle());
    }

    /**
     * {@link PersonService#save(PersonRequest)}} should save {@link Person} object with is archived.
     * Test creates dto object {@link PersonRequest} and then using {@link PersonService#save(PersonRequest)}
     * try to save new {@link Tool} object to database.
     * Then checks returns {@link Tool} object if is archived field equals this field
     * from dto object {@link ToolRequest}.
     */
    @Test
    public void save_should_save_person_is_archived_test() {
        Boolean isArchived = Boolean.TRUE;
        PersonRequest rq = getDefaultPersonRequest()
                .isArchived(isArchived)
                .build();

        Person savedPerson = service.save(rq);

        assertEquals(rq.isArchived(), savedPerson.getIsArchived());
    }

    /**
     * {@link PersonService#save(PersonRequest)}} should save {@link Person} object with is unregistered.
     * Test creates dto object {@link PersonRequest} and then using {@link PersonService#save(PersonRequest)}
     * try to save new {@link Tool} object to database.
     * Then checks returns {@link Tool} object if is unregistered field equals this field
     * from dto object {@link ToolRequest}.
     */
    @Test
    public void save_should_save_person_is_unregistered_test() {
        Boolean isUnregistered = Boolean.TRUE;
        PersonRequest rq = getDefaultPersonRequest()
                .isUnregistered(isUnregistered)
                .build();

        Person savedPerson = service.save(rq);

        assertEquals(rq.isUnregistered(), savedPerson.getIsUnregistered());
    }

    /**
     * {@link PersonService#save(PersonRequest)}} should save {@link Person} object with labels.
     * Test creates dto object {@link PersonRequest} and then using {@link PersonService#save(PersonRequest)}
     * try to save new {@link Person} object with {@link Label} to database.
     * Then checks returns {@link Person} object if labels not null, is labels size is two and if labels ids equals.
     */
    @Test
    public void save_should_save_person_with_labels_test() {
        jdbcTemplate.update("INSERT INTO persons_label (name) VALUES ('label_1')");
        jdbcTemplate.update("INSERT INTO persons_label (name) VALUES ('label_2')");
        Long labelId1 = jdbcTemplate.queryForObject("SELECT label_id FROM persons_label WHERE name = 'label_1'", Long.class);
        Long labelId2 = jdbcTemplate.queryForObject("SELECT label_id FROM persons_label WHERE name = 'label_2'", Long.class);
        PersonRequest rq = getDefaultPersonRequest()
                .labels(Set.of(labelId1, labelId2))
                .build();

        Person savedPerson = service.save(rq);

        assertNotNull(savedPerson.getLabels());
        assertEquals(2, savedPerson.getLabels().size());
        Set<Long> labelsIdFromDB = savedPerson.getLabels().stream().map(Label::getId).sorted().collect(Collectors.toSet());
        Set<Long> labelsIdFromRq = Stream.of(labelId1, labelId2).sorted().collect(Collectors.toSet());
        assertIterableEquals(labelsIdFromRq, labelsIdFromDB);
    }

    /**
     * {@link PersonService#save(PersonRequest)}} should save {@link Person} object with roles.
     * Test creates dto object {@link PersonRequest} and then using {@link PersonService#save(PersonRequest)}
     * try to save new {@link Person} object with {@link Role} to database.
     * Then checks returns {@link Person} object if roles not null, is roles size is two and if roles ids equals.
     */
    @Test
    public void save_should_save_person_with_roles_test() {
        jdbcTemplate.update("INSERT INTO persons_role (name) VALUES ('role_1')");
        jdbcTemplate.update("INSERT INTO persons_role (name) VALUES ('role_2')");
        Long roleId1 = jdbcTemplate.queryForObject("SELECT role_id FROM persons_role WHERE name = 'role_1'", Long.class);
        Long roleId2 = jdbcTemplate.queryForObject("SELECT role_id FROM persons_role WHERE name = 'role_2'", Long.class);
        PersonRequest rq = getDefaultPersonRequest()
                .roles(Set.of(roleId1, roleId2))
                .build();

        Person savedPerson = service.save(rq);

        assertNotNull(savedPerson.getLabels());
        assertEquals(2, savedPerson.getRoles().size());
        Set<Long> rolesIdFromDB = savedPerson.getRoles().stream().map(Role::getId).sorted().collect(Collectors.toSet());
        Set<Long> rolesIdFromRq = Stream.of(roleId1, roleId2).sorted().collect(Collectors.toSet());
        assertIterableEquals(rolesIdFromRq, rolesIdFromDB);
    }

    /**
     * {@link PersonService#findById(Long)} should return {@link PersonInfo} by id from database.
     * Test checks equality person id (received from jdbcTemplate request)
     * with id of tool object received from {@link PersonService#findById(Long)}
     */
    @Test
    public void findById_should_return_person_test() {
        long personId = jdbcTemplate.queryForObject("SELECT person_id FROM persons_person WHERE uuid = '935921a7-692e-4ee4-a089-2695b68e9801' AND is_archived IS FALSE", Long.class);

        PersonInfo personInfo = service.findById(personId);

        assertEquals(personId, personInfo.id());
    }

    /**
     * {@link PersonService#findById(Long)} should return {@link PersonInfo} by id from database
     * with {@link Label} objects.
     * Test prepare data. Insert person and labels objects into database.
     * Then associate person and label objects by join table persons_person_label.
     * Test checks equality personId (received from jdbcTemplate request).
     * Then checks if labels object not null.
     * Then checks size of labels from person object.
     */
    @Test
    public void findById_should_return_person_with_labels_test() {
        String labelName = "label_1";
        long personId = jdbcTemplate.queryForObject("SELECT person_id FROM persons_person WHERE uuid = '935921a7-692e-4ee4-a089-2695b68e9801' AND is_archived IS FALSE", Long.class);
        jdbcTemplate.update("INSERT INTO persons_label (name) VALUES ('label_1')");
        Long labelId = jdbcTemplate.queryForObject("SELECT label_id FROM persons_label WHERE name = '" + labelName + "'", Long.class);
        jdbcTemplate.update("INSERT INTO persons_person_label (person_id, label_id) VALUES (" + personId + ", " + labelId + ")");

        PersonInfo personInfo = service.findById(personId);

        assertEquals(personId, personInfo.id());
        assertNotNull(personInfo.labels());
        assertEquals(1, personInfo.labels().size());
        boolean isExists = personInfo.labels().stream()
                .map(LabelShort::name)
                .anyMatch(name -> name.equals(labelName));
        assertTrue(isExists);
    }

    /**
     * {@link PersonService#findById(Long)} should return {@link PersonInfo} by id from database
     * with {@link Role} objects.
     * Test prepare data. Insert person and roles objects into database.
     * Then associate person and role objects by join table persons_person_role.
     * Test checks equality personId (received from jdbcTemplate request).
     * Then checks if roles object not null.
     * Then checks size of roles from person object.
     */
    @Test
    public void findById_should_return_person_with_roles_test() {
        String roleName = "role_1";
        long personId = jdbcTemplate.queryForObject("SELECT person_id FROM persons_person WHERE uuid = '935921a7-692e-4ee4-a089-2695b68e9801' AND is_archived IS FALSE", Long.class);
        jdbcTemplate.update("INSERT INTO persons_role (name) VALUES ('role_1')");
        Long roleId = jdbcTemplate.queryForObject("SELECT role_id FROM persons_role WHERE name = '" + roleName + "'", Long.class);
        jdbcTemplate.update("INSERT INTO persons_person_role (person_id, role_id) VALUES (" + personId + ", " + roleId + ")");

        PersonInfo personInfo = service.findById(personId);

        assertEquals(personId, personInfo.id());
        assertNotNull(personInfo.roles());
        assertEquals(1, personInfo.roles().size());
        boolean isExists = personInfo.roles().stream()
                .map(RoleShort::name)
                .anyMatch(name -> name.equals(roleName));
        assertTrue(isExists);
    }

    /**
     * {@link PersonService#findById(Long)} should throw {@link BPException} exception
     * if {@link Person} with id not exist in database.
     * Test try to find tool whit id = -1 (negative num,ber guaranties, that no such id exists in database)
     * and check if {@link BPException} is thrown.
     */
    @Test
    public void findById_should_throw_exception_on_not_found_person_test() {
        long personId = -1;

        assertThrows(BPException.class, () -> service.findById(personId));
    }
}
