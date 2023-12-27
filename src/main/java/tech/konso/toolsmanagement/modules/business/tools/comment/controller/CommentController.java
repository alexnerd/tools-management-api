package tech.konso.toolsmanagement.modules.business.tools.comment.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.konso.toolsmanagement.modules.business.tools.brand.controller.dto.BrandRequest;
import tech.konso.toolsmanagement.modules.business.tools.comment.controller.dto.CommentFilterInfo;
import tech.konso.toolsmanagement.modules.business.tools.comment.controller.dto.CommentFilterResponse;
import tech.konso.toolsmanagement.modules.business.tools.comment.controller.dto.CommentRequest;
import tech.konso.toolsmanagement.modules.business.tools.comment.persistence.dao.Comment;
import tech.konso.toolsmanagement.modules.business.tools.comment.service.CommentService;

import java.net.URI;

import static tech.konso.toolsmanagement.modules.business.tools.comment.persistence.specification.CommentSpecification.sortSpec;
import static tech.konso.toolsmanagement.modules.business.tools.comment.persistence.specification.CommentSpecification.toolSpec;
import static tech.konso.toolsmanagement.system.commons.specification.AbstractSpecification.specBuilder;

@Validated
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*", maxAge = 1800L,
        methods = {RequestMethod.POST, RequestMethod.GET, RequestMethod.PUT, RequestMethod.DELETE})
@RequestMapping("/v1/tools/comments")
@Tag(name = "tools-comments-resource", description = "API resource for management tools comments")
public class CommentController {

    @Autowired
    private CommentService service;

    @Operation(summary = "List comments by filter")
    @Parameters({
            @Parameter(name = "page", description = "page number of result dataset, min value is 1", example = "1", required = true),
            @Parameter(name = "size", description = "size of result dataset page, min value is 1, max value is 50", example = "20", required = true),
            @Parameter(name = "toolId", description = "comments tool id", example = "1"),
            @Parameter(name = "sort", description = "Sorting filter supports: createdat(created date), updatedat(updated date)." +
                    " Every filter supports asc and desc order. By default sorts by create date in desc order. " +
                    "To choose sorting order type filter name and, by comma separator, order (asd, desc)", example = "createdat,asc")
    })
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public CommentFilterResponse findAll(@RequestParam(value = "page") @Min(1) int page,
                                         @RequestParam(value = "size") @Min(1) @Max(50) int size,
                                         @RequestParam(value = "toolId") long toolId,
                                         @RequestParam(value = "sort", required = false) String sort) {
        // UI pages starts with 1
        Page<CommentFilterInfo> comments = service.findAll(page - 1, size, specBuilder(toolSpec(toolId)
                .and(sortSpec(sort)))
                .build());
        return new CommentFilterResponse(comments.getContent(), comments.getTotalElements());
    }


    @Operation(summary = "Delete comment by id")
    @Parameters({
            @Parameter(name = "id", description = "id of comment to be deleted", example = "7", required = true)
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public void delete(@NotNull @PathVariable("id") Long id) {
        service.deleteById(id);
    }


    @Operation(summary = "Update existing comment by id")
    @Parameters({
            @Parameter(name = "rq", description = "Request body for update comment", required = true,
                    schema = @Schema(implementation = CommentRequest.class))
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public void update(@Valid @RequestBody CommentRequest rq) {
        service.save(rq);
    }

    @Parameters({
            @Parameter(name = "rq", description = "Request body for save new brand", required = true,
                    schema = @Schema(implementation = BrandRequest.class))
    })
    @Operation(summary = "Create comment")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> save(@Valid @RequestBody CommentRequest rq) {
        Comment comment = service.save(rq);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(comment.getId())
                .toUri();
        return ResponseEntity.created(location).build();
    }
}
