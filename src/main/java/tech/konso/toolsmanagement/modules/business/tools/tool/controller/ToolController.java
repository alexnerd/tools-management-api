package tech.konso.toolsmanagement.modules.business.tools.tool.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.konso.toolsmanagement.modules.business.tools.tool.controller.dto.*;
import tech.konso.toolsmanagement.modules.business.tools.tool.persistence.dao.Tool;
import tech.konso.toolsmanagement.modules.business.tools.tool.service.ToolService;

import java.net.URI;

import static tech.konso.toolsmanagement.modules.business.tools.tool.persistence.specification.ToolSpecification.*;
import static tech.konso.toolsmanagement.system.commons.specification.AbstractSpecification.specBuilder;

@Validated
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*", maxAge = 1800L,
        methods = {RequestMethod.POST, RequestMethod.GET, RequestMethod.PUT})
@RequestMapping("/v1/tools/tools")
@Tag(name="tools-resource", description = "API resource for management tools")
public class ToolController {

    @Autowired
    private ToolService service;


    @Operation(summary = "Get tool by id")
    @Parameters({
            @Parameter(name = "id", description = "id of tool to be searched", example = "7", required = true)
    })
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ToolInfo find(@PathVariable("id") Long id) {
        return service.findById(id);
    }

    @Operation(summary = "List tools by filter")
    @Parameters({
            @Parameter(name = "page", description = "page number of result dataset, min value is 1", example = "1", required = true),
            @Parameter(name = "size", description = "size of result dataset page, min value is 1, max value is 50", example = "20", required = true),
            @Parameter(name = "name", description = "name of tool, min length is 3", example = "Makita MTK24"),
            @Parameter(name = "isArchived", description = "Archived flag, false by default", example = "false"),
            @Parameter(name = "sort", description = "Sorting filter supports: name(tool name), createdat(created date), updatedat(updated date)." +
                    " Every filter supports asc and desc order. By default sorts by create date in desc order. " +
                    "To choose sorting order type filter name and, by comma separator, order (asd, desc)", example = "name,asc")
    })
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ToolFilterResponse findAll(@RequestParam(value = "page") @Min(1) int page,
                                      @RequestParam(value = "size") @Min(1) @Max(50) int size,
                                      @RequestParam(value = "name", required = false) String name,
                                      @RequestParam(value = "isArchived", required = false, defaultValue = "false") Boolean isArchived,
                                      @RequestParam(value = "sort", required = false) String sort) {
        // UI pages starts with 1
        Page<ToolFilterInfo> tools = service.findAll(page - 1, size, specBuilder(isArchivedSpec(isArchived)
                .and(likeSpec(name))
                .and(sortSpec(sort)))
                .build());
        return new ToolFilterResponse(tools.getContent(), tools.getTotalElements());
    }

    @Operation(summary = "Update existing tool by id")
    @Parameters({
            @Parameter(name = "rq", description = "Request body fo update tool", required = true,
                    schema = @Schema(implementation = ToolRequest.class))
    })
    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> update(@Valid @RequestBody ToolRequest rq) {
        service.save(rq);
        return ResponseEntity.noContent().build();
    }

    @Parameters({
            @Parameter(name = "rq", description = "Request body for save new tool", required = true,
                    schema = @Schema(implementation = ToolRequest.class))
    })
    @Operation(summary = "Create tool")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> save(@Valid @RequestBody ToolRequest rq) {
        Tool tool = service.save(rq);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(tool.getId())
                .toUri();
        return ResponseEntity.created(location).build();
    }

    @Operation(summary = "Get tool photo by tool id")
    @Parameters({
            @Parameter(name = "id", description = "id of tool", example = "7", required = true)
    })
    @GetMapping(value = "/{id}/photo", produces = MediaType.IMAGE_JPEG_VALUE)
    public InputStreamResource findPhoto(@PathVariable("id") Long id) {
        return service.findPhoto(id);
    }

    @Operation(summary = "Upload tool photo")
    @PostMapping(value = "/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public UploadPhotoResponse uploadPhoto(@RequestPart("attachment") MultipartFile multipartFile) {
        return service.uploadPhoto(multipartFile);
    }
}
