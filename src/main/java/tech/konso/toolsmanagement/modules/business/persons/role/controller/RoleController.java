package tech.konso.toolsmanagement.modules.business.persons.role.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.konso.toolsmanagement.modules.business.persons.role.controller.dto.RoleFilterResponse;
import tech.konso.toolsmanagement.modules.business.persons.role.controller.dto.RoleRequest;
import tech.konso.toolsmanagement.modules.business.persons.role.persistence.dao.Role;
import tech.konso.toolsmanagement.modules.business.persons.role.persistence.specification.RoleSpecification;
import tech.konso.toolsmanagement.modules.business.persons.role.service.RoleService;
import tech.konso.toolsmanagement.system.commons.specification.AbstractSpecification;

import java.net.URI;

import static tech.konso.toolsmanagement.system.commons.specification.AbstractSpecification.specBuilder;

@Validated
@RestController("PersonsRoleController")
@CrossOrigin(origins = "*", allowedHeaders = "*", maxAge = 1800L,
        methods = {RequestMethod.POST, RequestMethod.GET, RequestMethod.PUT})
@RequestMapping("/v1/persons/roles")
@Tag(name="persons-roles-resource", description = "API resource for management persons roles")
public class RoleController {

    @Autowired
    private RoleService service;


    @Operation(summary = "Get role by id")
    @Parameters({
            @Parameter(name = "id", description = "id of role to be searched", example = "7", required = true)
    })
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Role find(@PathVariable("id") Long id) {
        return service.findById(id);
    }

    @Operation(summary = "List roles by filter")
    @Parameters({
            @Parameter(name = "page", description = "page number of result dataset, min value is 1", example = "1", required = true),
            @Parameter(name = "size", description = "size of result dataset page, min value is 1, max value is 50", example = "20", required = true),
            @Parameter(name = "name", description = "name of role, min length is 3", example = "Attention"),
            @Parameter(name = "isArchived", description = "Archived flag, false by default", example = "false"),
            @Parameter(name = "sort", description = "Sorting filter supports: name(role name), createdat(created date), updatedat(updated date)." +
                    " Every filter supports asc and desc order. By default sorts by create date in desc order. " +
                    "To choose sorting order type filter name and, by comma separator, order (asd, desc)", example = "name,asc")
    })
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public RoleFilterResponse findAll(@RequestParam(value = "page") @Min(1) int page,
                                      @RequestParam(value = "size") @Min(1) @Max(50) int size,
                                      @RequestParam(value = "name", required = false) String name,
                                      @RequestParam(value = "isArchived", required = false, defaultValue = "false") Boolean isArchived,
                                      @RequestParam(value = "sort", required = false) String sort) {
        // UI pages starts with 1
        Page<Role> roles = service.findAll(page - 1, size, AbstractSpecification.specBuilder(RoleSpecification.isArchivedSpec(isArchived)
                .and(RoleSpecification.likeSpec(name))
                .and(RoleSpecification.sortSpec(sort)))
                .build());
        return new RoleFilterResponse(roles.getContent(), roles.getTotalElements());
    }

    @Operation(summary = "Update existing role by id")
    @Parameters({
            @Parameter(name = "RoleRequest", description = "Request body fo update role", required = true,
                    schema = @Schema(implementation = RoleRequest.class))
    })
    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> update(@Valid @RequestBody RoleRequest rq) {
        service.save(rq);
        return ResponseEntity.noContent().build();
    }

    @Parameters({
            @Parameter(name = "RoleRequest", description = "Request body for save new role", required = true,
                    schema = @Schema(implementation = RoleRequest.class))
    })
    @Operation(summary = "Create role")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> save(@Valid @RequestBody RoleRequest rq) {
        Role role = service.save(rq);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(role.getId())
                .toUri();
        return ResponseEntity.created(location).build();
    }
}