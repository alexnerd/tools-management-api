package tech.konso.toolsmanagement.modules.business.persons.person.service.mappers;

import tech.konso.toolsmanagement.modules.business.persons.role.persistence.dao.Role;
import tech.konso.toolsmanagement.modules.business.persons.person.controller.dto.LabelShort;
import tech.konso.toolsmanagement.modules.business.persons.person.controller.dto.PersonFilterInfo;
import tech.konso.toolsmanagement.modules.business.persons.person.controller.dto.PersonInfo;
import tech.konso.toolsmanagement.modules.business.persons.person.controller.dto.RoleShort;
import tech.konso.toolsmanagement.modules.business.persons.person.persistence.dao.Person;
import tech.konso.toolsmanagement.modules.business.persons.label.persistence.dao.Label;

import java.util.stream.Collectors;

public class PersonsDtoMapper {
    public PersonFilterInfo mapToPersonFilterInfo(Person person) {
        return PersonFilterInfo.builder()
                .id(person.getId())
                .uuid(person.getUuid())
                .phoneNumber(person.getPhoneNumber())
                .companyUuid(person.getCompanyUuid())
                .surname(person.getSurname())
                .name(person.getName())
                .patronymic(person.getPatronymic())
                .jobTitle(person.getJobTitle())
                .isArchived(person.getIsArchived())
                .isUnregistered(person.getIsUnregistered())
                .createdAt(person.getCreatedAt())
                .updatedAt(person.getUpdatedAt())
                .roles(person.getRoles().stream().map(Role::getName).collect(Collectors.toSet()))
                .labels(person.getLabels().stream().map(Label::getName).collect(Collectors.toSet()))
                .build();
    }

    public PersonInfo mapToPersonInfo(Person person) {
        return PersonInfo.builder()
                .id(person.getId())
                .uuid(person.getUuid())
                .phoneNumber(person.getPhoneNumber())
                .companyUuid(person.getCompanyUuid())
                .surname(person.getSurname())
                .name(person.getName())
                .patronymic(person.getPatronymic())
                .jobTitle(person.getJobTitle())
                .isArchived(person.getIsArchived())
                .isUnregistered(person.getIsUnregistered())
                .createdAt(person.getCreatedAt())
                .updatedAt(person.getUpdatedAt())
                .roles(person.getRoles().stream().map(this::convertRole).collect(Collectors.toSet()))
                .labels(person.getLabels().stream().map(this::convertLabel).collect(Collectors.toSet()))
                .build();
    }

    private LabelShort convertLabel(Label label) {
        return LabelShort.builder()
                .id(label.getId())
                .name(label.getName())
                .build();
    }

    private RoleShort convertRole(Role role) {
        return RoleShort.builder()
                .id(role.getId())
                .name(role.getName())
                .build();
    }
}
