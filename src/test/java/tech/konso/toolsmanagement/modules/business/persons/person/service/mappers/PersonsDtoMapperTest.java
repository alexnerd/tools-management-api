package tech.konso.toolsmanagement.modules.business.persons.person.service.mappers;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import tech.konso.toolsmanagement.modules.business.persons.label.persistence.dao.Label;
import tech.konso.toolsmanagement.modules.business.persons.person.controller.dto.LabelShort;
import tech.konso.toolsmanagement.modules.business.persons.person.controller.dto.PersonFilterInfo;
import tech.konso.toolsmanagement.modules.business.persons.person.controller.dto.PersonInfo;
import tech.konso.toolsmanagement.modules.business.persons.person.controller.dto.RoleShort;
import tech.konso.toolsmanagement.modules.business.persons.person.persistence.dao.Person;
import tech.konso.toolsmanagement.modules.business.persons.role.persistence.dao.Role;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

/**
 * Tests for PersonsDtoMapper. Test for mapping fields and null values.
 * For every test, the given-when-then is used.
 * The given part sets the stage for the actual test and captures all prerequisites for executing the functionality
 * we want to test. The when part triggers the operation that we actually want to test. And in the then part, we
 * assert that the result that when the trigger produced is actually what we expect.
 * <p> Example:
 * <p> given - a calculator showing the number 2
 * <p> when - adding the number 3
 * <p> then - it should show the number 5
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PersonsDtoMapperTest {
    private PersonsDtoMapper mapper;

    @BeforeAll
    public void init() {
        mapper = new PersonsDtoMapper();
    }

    /**
     * {@link PersonsDtoMapper#mapToPersonFilterInfo(Person)} should map {@link Person} uuid field.
     * Test creates object {@link Person} with non-null test field and try to map it to {@link PersonFilterInfo} object.
     * Then checks by equality test field before mapping and after.
     */
    @Test
    public void map_to_person_filter_info_should_map_uuid() {
        UUID uuid = UUID.fromString("f3d50cfe-4efa-4b70-ac6d-75c37ad8f6c8");
        Person person = new Person();
        person.setUuid(uuid);

        PersonFilterInfo personFilterInfo = mapper.mapToPersonFilterInfo(person);

        assertEquals(uuid, personFilterInfo.uuid());
    }

    /**
     * {@link PersonsDtoMapper#mapToPersonFilterInfo(Person)} should map {@link Person} phoneNumber field.
     * Test creates object {@link Person} with non-null test field and try to map it to {@link PersonFilterInfo} object.
     * Then checks by equality test field before mapping and after.
     */
    @Test
    public void map_to_person_filter_info_should_map_phone_number() {
        String phoneNumber = "+7-988-378-22-22";
        Person person = new Person();
        person.setPhoneNumber(phoneNumber);

        PersonFilterInfo personFilterInfo = mapper.mapToPersonFilterInfo(person);

        assertEquals(phoneNumber, personFilterInfo.phoneNumber());
    }

    /**
     * {@link PersonsDtoMapper#mapToPersonFilterInfo(Person)} should map {@link Person} companyUuid field.
     * Test creates object {@link Person} with non-null test field and try to map it to {@link PersonFilterInfo} object.
     * Then checks by equality test field before mapping and after.
     */
    @Test
    public void map_to_person_filter_info_should_map_company_uuid() {
        UUID companyUuid = UUID.fromString("f3d50cfe-4efa-4b70-ac6d-75c37ad8f6c8");
        Person person = new Person();
        person.setCompanyUuid(companyUuid);

        PersonFilterInfo personFilterInfo = mapper.mapToPersonFilterInfo(person);

        assertEquals(companyUuid, personFilterInfo.companyUuid());
    }

    /**
     * {@link PersonsDtoMapper#mapToPersonFilterInfo(Person)} should map {@link Person} surname field.
     * Test creates object {@link Person} with non-null test field and try to map it to {@link PersonFilterInfo} object.
     * Then checks by equality test field before mapping and after.
     */
    @Test
    public void map_to_person_filter_info_should_map_surname() {
        String surname = "Smith";
        Person person = new Person();
        person.setSurname(surname);

        PersonFilterInfo personFilterInfo = mapper.mapToPersonFilterInfo(person);

        assertEquals(surname, personFilterInfo.surname());
    }

    /**
     * {@link PersonsDtoMapper#mapToPersonFilterInfo(Person)} should map {@link Person} name field.
     * Test creates object {@link Person} with non-null test field and try to map it to {@link PersonFilterInfo} object.
     * Then checks by equality test field before mapping and after.
     */
    @Test
    public void map_to_person_filter_info_should_map_name() {
        String name = "Alex";
        Person person = new Person();
        person.setName(name);

        PersonFilterInfo personFilterInfo = mapper.mapToPersonFilterInfo(person);

        assertEquals(name, personFilterInfo.name());
    }

    /**
     * {@link PersonsDtoMapper#mapToPersonFilterInfo(Person)} should map {@link Person} patronymic field.
     * Test creates object {@link Person} with non-null test field and try to map it to {@link PersonFilterInfo} object.
     * Then checks by equality test field before mapping and after.
     */
    @Test
    public void map_to_person_filter_info_should_map_patronymic() {
        String patronymic = "Alex";
        Person person = new Person();
        person.setPatronymic(patronymic);

        PersonFilterInfo personFilterInfo = mapper.mapToPersonFilterInfo(person);

        assertEquals(patronymic, personFilterInfo.patronymic());
    }

    /**
     * {@link PersonsDtoMapper#mapToPersonFilterInfo(Person)} should map {@link Person} jobTitle field.
     * Test creates object {@link Person} with non-null test field and try to map it to {@link PersonFilterInfo} object.
     * Then checks by equality test field before mapping and after.
     */
    @Test
    public void map_to_person_filter_info_should_map_job_title() {
        String jobTitle = "Alex";
        Person person = new Person();
        person.setJobTitle(jobTitle);

        PersonFilterInfo personFilterInfo = mapper.mapToPersonFilterInfo(person);

        assertEquals(jobTitle, personFilterInfo.jobTitle());
    }

    /**
     * {@link PersonsDtoMapper#mapToPersonFilterInfo(Person)} should map {@link Person} isArchived field.
     * Test creates object {@link Person} with non-null test field and try to map it to {@link PersonFilterInfo} object.
     * Then checks by equality test field before mapping and after.
     */
    @Test
    public void map_to_person_filter_info_should_map_is_archived() {
        Boolean isArchived = Boolean.TRUE;
        Person person = new Person();
        person.setIsArchived(isArchived);

        PersonFilterInfo personFilterInfo = mapper.mapToPersonFilterInfo(person);

        assertEquals(isArchived, personFilterInfo.isArchived());
    }

    /**
     * {@link PersonsDtoMapper#mapToPersonFilterInfo(Person)} should map {@link Person} isUnregistered field.
     * Test creates object {@link Person} with non-null test field and try to map it to {@link PersonFilterInfo} object.
     * Then checks by equality test field before mapping and after.
     */
    @Test
    public void map_to_person_filter_info_should_map_is_unregistered() {
        Boolean isUnregistered = Boolean.TRUE;
        Person person = new Person();
        person.setIsUnregistered(isUnregistered);

        PersonFilterInfo personFilterInfo = mapper.mapToPersonFilterInfo(person);

        assertEquals(isUnregistered, personFilterInfo.isUnregistered());
    }

    /**
     * {@link PersonsDtoMapper#mapToPersonFilterInfo(Person)} should map {@link Person} createdAt field.
     * Test creates object {@link Person} with non-null test field and try to map it to {@link PersonFilterInfo} object.
     * Then checks by equality test field before mapping and after.
     */
    @Test
    public void map_to_person_filter_info_should_map_created_at() {
        LocalDateTime createdAt = LocalDateTime.now();
        Person person = new Person();
        person.setCreatedAt(createdAt);

        PersonFilterInfo personFilterInfo = mapper.mapToPersonFilterInfo(person);

        assertEquals(createdAt, personFilterInfo.createdAt());
    }

    /**
     * {@link PersonsDtoMapper#mapToPersonFilterInfo(Person)} should map {@link Person} updatedAt field.
     * Test creates object {@link Person} with non-null test field and try to map it to {@link PersonFilterInfo} object.
     * Then checks by equality test field before mapping and after.
     */
    @Test
    public void map_to_person_filter_info_should_map_updated_at() {
        LocalDateTime updatedAt = LocalDateTime.now();
        Person person = new Person();
        person.setUpdatedAt(updatedAt);

        PersonFilterInfo personFilterInfo = mapper.mapToPersonFilterInfo(person);

        assertEquals(updatedAt, personFilterInfo.updatedAt());
    }

    /**
     * {@link PersonsDtoMapper#mapToPersonFilterInfo(Person)} should map {@link Person} labels field to set of label names.
     * Test creates object {@link Person} with non-null test field and try to map it to {@link PersonFilterInfo} object.
     * Then checks by equality test field it should contain label names.
     */
    @Test
    public void map_to_person_filter_info_should_map_labels() {
        String labelName1 = "new_label_1";
        String labelName2 = "new_label_2";
        Label label1 = new Label();
        Label label2 = new Label();
        label1.setName(labelName1);
        label2.setName(labelName2);
        Person person = new Person();
        person.addLabel(label1);
        person.addLabel(label2);

        PersonFilterInfo personFilterInfo = mapper.mapToPersonFilterInfo(person);

        assertIterableEquals(Stream.of(labelName1, labelName2).sorted().collect(Collectors.toSet()),
                personFilterInfo.labels().stream().sorted().collect(Collectors.toSet()));
    }

    /**
     * {@link PersonsDtoMapper#mapToPersonFilterInfo(Person)} should map {@link Person} roles field to set of label names.
     * Test creates object {@link Person} with non-null test field and try to map it to {@link PersonFilterInfo} object.
     * Then checks by equality test field it should contain label names.
     */
    @Test
    public void map_to_person_filter_info_should_map_roles() {
        String roleName1 = "new_role_1";
        String roleName2 = "new_role_2";
        Role role1 = new Role();
        Role role2 = new Role();
        role1.setName(roleName1);
        role2.setName(roleName2);
        Person person = new Person();
        person.addRole(role1);
        person.addRole(role2);

        PersonFilterInfo personFilterInfo = mapper.mapToPersonFilterInfo(person);

        assertIterableEquals(Stream.of(roleName1, roleName2).sorted().collect(Collectors.toSet()),
                personFilterInfo.roles().stream().sorted().collect(Collectors.toSet()));
    }

    /**
     * {@link PersonsDtoMapper#mapToPersonInfo(Person)} should map {@link Person} uuid field.
     * Test creates object {@link Person} with non-null test field and try to map it to {@link PersonInfo} object.
     * Then checks by equality test field before mapping and after.
     */
    @Test
    public void map_to_person_info_should_map_uuid() {
        UUID uuid = UUID.fromString("f3d50cfe-4efa-4b70-ac6d-75c37ad8f6c8");
        Person person = new Person();
        person.setUuid(uuid);

        PersonInfo personInfo = mapper.mapToPersonInfo(person);

        assertEquals(uuid, personInfo.uuid());
    }

    /**
     * {@link PersonsDtoMapper#mapToPersonInfo(Person)} should map {@link Person} phoneNumber field.
     * Test creates object {@link Person} with non-null test field and try to map it to {@link PersonInfo} object.
     * Then checks by equality test field before mapping and after.
     */
    @Test
    public void map_to_person_info_should_map_phone_number() {
        String phoneNumber = "+7-988-378-22-22";
        Person person = new Person();
        person.setPhoneNumber(phoneNumber);

        PersonInfo personInfo = mapper.mapToPersonInfo(person);

        assertEquals(phoneNumber, personInfo.phoneNumber());
    }

    /**
     * {@link PersonsDtoMapper#mapToPersonInfo(Person)} should map {@link Person} companyUuid field.
     * Test creates object {@link Person} with non-null test field and try to map it to {@link PersonInfo} object.
     * Then checks by equality test field before mapping and after.
     */
    @Test
    public void map_to_person_info_should_map_company_uuid() {
        UUID companyUuid = UUID.fromString("f3d50cfe-4efa-4b70-ac6d-75c37ad8f6c8");
        Person person = new Person();
        person.setCompanyUuid(companyUuid);

        PersonInfo personInfo = mapper.mapToPersonInfo(person);

        assertEquals(companyUuid, personInfo.companyUuid());
    }

    /**
     * {@link PersonsDtoMapper#mapToPersonInfo(Person)} should map {@link Person} surname field.
     * Test creates object {@link Person} with non-null test field and try to map it to {@link PersonInfo} object.
     * Then checks by equality test field before mapping and after.
     */
    @Test
    public void map_to_person_info_should_map_surname() {
        String surname = "Smith";
        Person person = new Person();
        person.setSurname(surname);

        PersonInfo personInfo = mapper.mapToPersonInfo(person);

        assertEquals(surname, personInfo.surname());
    }

    /**
     * {@link PersonsDtoMapper#mapToPersonInfo(Person)} should map {@link Person} name field.
     * Test creates object {@link Person} with non-null test field and try to map it to {@link PersonInfo} object.
     * Then checks by equality test field before mapping and after.
     */
    @Test
    public void map_to_person_info_should_map_name() {
        String name = "Alex";
        Person person = new Person();
        person.setName(name);

        PersonInfo personInfo = mapper.mapToPersonInfo(person);

        assertEquals(name, personInfo.name());
    }

    /**
     * {@link PersonsDtoMapper#mapToPersonInfo(Person)} should map {@link Person} patronymic field.
     * Test creates object {@link Person} with non-null test field and try to map it to {@link PersonInfo} object.
     * Then checks by equality test field before mapping and after.
     */
    @Test
    public void map_to_person_info_should_map_patronymic() {
        String patronymic = "Alex";
        Person person = new Person();
        person.setPatronymic(patronymic);

        PersonInfo personInfo = mapper.mapToPersonInfo(person);

        assertEquals(patronymic, personInfo.patronymic());
    }

    /**
     * {@link PersonsDtoMapper#mapToPersonInfo(Person)} should map {@link Person} jobTitle field.
     * Test creates object {@link Person} with non-null test field and try to map it to {@link PersonInfo} object.
     * Then checks by equality test field before mapping and after.
     */
    @Test
    public void map_to_person_info_should_map_job_title() {
        String jobTitle = "Alex";
        Person person = new Person();
        person.setJobTitle(jobTitle);

        PersonInfo personInfo = mapper.mapToPersonInfo(person);

        assertEquals(jobTitle, personInfo.jobTitle());
    }

    /**
     * {@link PersonsDtoMapper#mapToPersonInfo(Person)} should map {@link Person} isArchived field.
     * Test creates object {@link Person} with non-null test field and try to map it to {@link PersonInfo} object.
     * Then checks by equality test field before mapping and after.
     */
    @Test
    public void map_to_person_info_should_map_is_archived() {
        Boolean isArchived = Boolean.TRUE;
        Person person = new Person();
        person.setIsArchived(isArchived);

        PersonInfo personInfo = mapper.mapToPersonInfo(person);

        assertEquals(isArchived, personInfo.isArchived());
    }

    /**
     * {@link PersonsDtoMapper#mapToPersonInfo(Person)} should map {@link Person} isUnregistered field.
     * Test creates object {@link Person} with non-null test field and try to map it to {@link PersonInfo} object.
     * Then checks by equality test field before mapping and after.
     */
    @Test
    public void map_to_person_info_should_map_is_unregistered() {
        Boolean isUnregistered = Boolean.TRUE;
        Person person = new Person();
        person.setIsUnregistered(isUnregistered);

        PersonInfo personInfo = mapper.mapToPersonInfo(person);

        assertEquals(isUnregistered, personInfo.isUnregistered());
    }

    /**
     * {@link PersonsDtoMapper#mapToPersonInfo(Person)} should map {@link Person} createdAt field.
     * Test creates object {@link Person} with non-null test field and try to map it to {@link PersonInfo} object.
     * Then checks by equality test field before mapping and after.
     */
    @Test
    public void map_to_person_info_should_map_created_at() {
        LocalDateTime createdAt = LocalDateTime.now();
        Person person = new Person();
        person.setCreatedAt(createdAt);

        PersonInfo personInfo = mapper.mapToPersonInfo(person);

        assertEquals(createdAt, personInfo.createdAt());
    }

    /**
     * {@link PersonsDtoMapper#mapToPersonInfo(Person)} should map {@link Person} updatedAt field.
     * Test creates object {@link Person} with non-null test field and try to map it to {@link PersonInfo} object.
     * Then checks by equality test field before mapping and after.
     */
    @Test
    public void map_to_person_info_should_map_updated_at() {
        LocalDateTime updatedAt = LocalDateTime.now();
        Person person = new Person();
        person.setUpdatedAt(updatedAt);

        PersonInfo personInfo = mapper.mapToPersonInfo(person);

        assertEquals(updatedAt, personInfo.updatedAt());
    }

    /**
     * {@link PersonsDtoMapper#mapToPersonInfo(Person)} should map {@link Person} labels field to set of label names.
     * Test creates object {@link Person} with non-null test field and try to map it to {@link PersonInfo} object.
     * Then checks by equality test field it should contain label names.
     */
    @Test
    public void map_to_person_info_should_map_labels() {
        String labelName1 = "new_label_1";
        String labelName2 = "new_label_2";
        Label label1 = new Label();
        Label label2 = new Label();
        label1.setName(labelName1);
        label2.setName(labelName2);
        Person person = new Person();
        person.addLabel(label1);
        person.addLabel(label2);

        PersonInfo personInfo = mapper.mapToPersonInfo(person);

        assertIterableEquals(Stream.of(labelName1, labelName2).sorted().collect(Collectors.toSet()),
                personInfo.labels().stream().map(LabelShort::name).sorted().collect(Collectors.toSet()));
    }

    /**
     * {@link PersonsDtoMapper#mapToPersonInfo(Person)} should map {@link Person} roles field to set of label names.
     * Test creates object {@link Person} with non-null test field and try to map it to {@link PersonInfo} object.
     * Then checks by equality test field it should contain label names.
     */
    @Test
    public void map_to_person_info_should_map_roles() {
        String roleName1 = "new_role_1";
        String roleName2 = "new_role_2";
        Role role1 = new Role();
        Role role2 = new Role();
        role1.setName(roleName1);
        role2.setName(roleName2);
        Person person = new Person();
        person.addRole(role1);
        person.addRole(role2);

        PersonInfo personInfo = mapper.mapToPersonInfo(person);

        assertIterableEquals(Stream.of(roleName1, roleName2).sorted().collect(Collectors.toSet()),
                personInfo.roles().stream().map(RoleShort::name).sorted().collect(Collectors.toSet()));
    }
}