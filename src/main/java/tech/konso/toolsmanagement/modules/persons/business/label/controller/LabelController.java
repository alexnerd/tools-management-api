package tech.konso.toolsmanagement.modules.persons.business.label.controller;

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
import tech.konso.toolsmanagement.modules.persons.business.label.controller.dto.LabelFilterResponse;
import tech.konso.toolsmanagement.modules.persons.business.label.controller.dto.LabelRequest;
import tech.konso.toolsmanagement.modules.persons.business.label.persistence.dao.Label;
import tech.konso.toolsmanagement.modules.persons.business.label.service.LabelService;

import java.net.URI;

import static tech.konso.toolsmanagement.modules.persons.business.label.persistence.specification.LabelSpecification.*;
import static tech.konso.toolsmanagement.system.commons.specification.AbstractSpecification.specBuilder;

@Validated
@RestController("PersonsLabelController")
@CrossOrigin(origins = "*", allowedHeaders = "*", maxAge = 1800L,
        methods = {RequestMethod.POST, RequestMethod.GET, RequestMethod.PUT})
@RequestMapping("/v1/persons/labels")
@Tag(name="persons-labels-resource", description = "API resource for management persons labels")
public class LabelController {

    @Autowired
    private LabelService service;


    @Operation(summary = "Get label by id")
    @Parameters({
            @Parameter(name = "id", description = "id of label to be searched", example = "7", required = true)
    })
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Label find(@PathVariable("id") Long id) {
        return service.findById(id);
    }

    @Operation(summary = "List labels by filter")
    @Parameters({
            @Parameter(name = "page", description = "page number of result dataset, min value is 1", example = "1", required = true),
            @Parameter(name = "size", description = "size of result dataset page, min value is 1, max value is 50", example = "20", required = true),
            @Parameter(name = "name", description = "name of label, min length is 3", example = "Attention"),
            @Parameter(name = "isArchived", description = "Archived flag, false by default", example = "false"),
            @Parameter(name = "sort", description = "Sorting filter supports: name(label name), createdat(created date), updatedat(updated date)." +
                    " Every filter supports asc and desc order. By default sorts by create date in desc order. " +
                    "To choose sorting order type filter name and, by comma separator, order (asd, desc)", example = "name,asc")
    })
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public LabelFilterResponse findAll(@RequestParam(value = "page") @Min(1) int page,
                                       @RequestParam(value = "size") @Min(1) @Max(50) int size,
                                       @RequestParam(value = "name", required = false) String name,
                                       @RequestParam(value = "isArchived", required = false, defaultValue = "false") Boolean isArchived,
                                       @RequestParam(value = "sort", required = false) String sort) {
        // UI pages starts with 1
        Page<Label> labels = service.findAll(page - 1, size, specBuilder(isArchivedSpec(isArchived)
                .and(likeSpec(name))
                .and(sortSpec(sort)))
                .build());
        return new LabelFilterResponse(labels.getContent(), labels.getTotalElements());
    }

    @Operation(summary = "Update existing label by id")
    @Parameters({
            @Parameter(name = "LabelRequest", description = "Request body fo update label", required = true,
                    schema = @Schema(implementation = LabelRequest.class))
    })
    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> update(@Valid @RequestBody LabelRequest rq) {
        service.save(rq);
        return ResponseEntity.noContent().build();
    }

    @Parameters({
            @Parameter(name = "LabelRequest", description = "Request body for save new label", required = true,
                    schema = @Schema(implementation = LabelRequest.class))
    })
    @Operation(summary = "Create label")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> save(@Valid @RequestBody LabelRequest rq) {
        Label label = service.save(rq);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(label.getId())
                .toUri();
        return ResponseEntity.created(location).build();
    }
}

