package tech.konso.toolsmanagement.modules.business.persons.person.service.mappers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.konso.toolsmanagement.modules.business.persons.label.service.LabelService;
import tech.konso.toolsmanagement.modules.business.persons.person.controller.dto.PersonRequest;
import tech.konso.toolsmanagement.modules.business.persons.person.persistence.dao.Person;
import tech.konso.toolsmanagement.modules.business.persons.role.service.RoleService;

import java.util.UUID;

/**
 * Mapper for person entity
 */
@Service
public class PersonsEntityMapper {

    @Autowired
    private RoleService roleService;

    @Autowired
    private LabelService labelService;

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
    public Person toEntity(Person person, PersonRequest rq) {
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
        person.setPhotoUuid(rq.photoUuid());

        person.removeLabels();
        rq.labels().stream().map(labelId -> labelService.getReference(labelId)).forEach(person::addLabel);

        person.removeRoles();
        rq.roles().stream().map(roleId -> roleService.getReference(roleId)).forEach(person::addRole);

        return person;
    }
}
