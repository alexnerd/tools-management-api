package tech.konso.toolsmanagement.modules.business.tools.tool.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import tech.konso.toolsmanagement.modules.business.tools.tool.controller.dto.*;
import tech.konso.toolsmanagement.modules.business.tools.tool.persistence.dao.Tool;
import tech.konso.toolsmanagement.modules.business.tools.tool.persistence.repository.ToolRepository;
import tech.konso.toolsmanagement.modules.business.tools.tool.persistence.specification.ToolSpecification;
import tech.konso.toolsmanagement.modules.business.tools.tool.service.mappers.ToolsDtoMapper;
import tech.konso.toolsmanagement.modules.business.tools.tool.service.mappers.ToolsEntityMapper;
import tech.konso.toolsmanagement.modules.integration.facade.FileStorageFacade;
import tech.konso.toolsmanagement.modules.integration.facade.FileType;
import tech.konso.toolsmanagement.modules.integration.facade.dto.UploadResponse;
import tech.konso.toolsmanagement.system.commons.exceptions.BPException;
import tech.konso.toolsmanagement.system.commons.specification.AbstractSpecification;

import java.util.Optional;
import java.util.UUID;

import static tech.konso.toolsmanagement.system.commons.specification.AbstractSpecification.specBuilder;

/**
 * Service layer for working with tools.
 */
@Service
public class ToolService {

    @Autowired
    private ToolRepository repository;

    @Autowired
    private ToolsEntityMapper entityMapper;

    @Autowired
    private FileStorageFacade fileStorageFacade;

    private ToolsDtoMapper toolsDtoMapper;

    @PostConstruct
    public void init() {
        toolsDtoMapper = new ToolsDtoMapper();
    }

    /**
     * Find tool in database by unique id. Tool must exist in database
     * <p>
     * Example:
     * <pre>
     *     Tool tool = findById(2L);
     * </pre>
     *
     * @param id of tool, must exist in database
     * @return tool from database
     * @throws BPException if tool not exists in database
     */
    public ToolInfo findById(Long id) {
        return repository.findById(id).map(toolsDtoMapper::mapToToolInfo).orElseThrow(() -> new BPException.NotFound("Tool not found id: " + id));
    }

    /**
     * Finds tools by tool specification and returns it in pageable format.
     * By default, result set sorts by create date from newer to older and without archived tools.
     * <p>
     * Example:
     * <pre>
     *     Specification&lt;Tool> spec = specBuilder(sortSpec("name,desc")).build();
     *     ToolFilterResponse foundedTools = service.findAll(0, 100, spec);
     * </pre>
     *
     * @param page number of returned result set
     * @param size of the returned page
     * @param spec set of tool specification
     * @return {@link ToolFilterResponse} object for resulting dataset in pageable format
     * @see ToolSpecification tool specifications
     */
    public Page<ToolFilterInfo> findAll(int page, int size, Specification<Tool> spec) {
        AbstractSpecification.SpecBuilder<Tool> builder = specBuilder(Tool.class);
        Pageable pageable = PageRequest.of(page, size);
        return repository.findAll(builder.and(spec).build(), pageable).map(toolsDtoMapper::mapToToolFilterInfo);
    }

    /**
     * Save new tool to database or update existing.
     * Run under transaction.
     * <p>
     * Example:
     * <pre>
     *     ToolRequest rq = new ToolRequest(null, "new_tool", null, null, false);
     *     Tool savedTool = service.save(rq);
     * </pre>
     *
     * @param rq {@link ToolRequest} object for creating tool
     * @return {@link Tool} saved object
     */
    @Transactional
    public Tool save(ToolRequest rq) {
        return Optional.ofNullable(rq.id())
                .map(id -> repository.findById(rq.id())
                        .orElseThrow(() -> new BPException.NotFound("Tool not found id: " + id))
                ).map(tool -> entityMapper.toEntity(tool, rq))
                .orElseGet(() ->
                        repository.save(entityMapper.toEntity(new Tool(), rq))
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
    public  UploadPhotoResponse uploadPhoto(MultipartFile multipartFile) {
        UploadResponse rs = fileStorageFacade.upload(multipartFile, FileType.PHOTO_TOOL);
        if (rs.error() != null) {
            throw new BPException.ServiceUnavailable("Upload photo error: " + rs.error());
        }
        return new UploadPhotoResponse(rs.uuid());
    }

    /**
     * Find photo by tool id in file storage.
     * <p>
     * Example:
     * <pre>
     *     findPhoto(3);
     * </pre>
     *
     * @param toolId {@link Long} tool id
     * @return InputStreamResource with searching file
     */
    public InputStreamResource findPhoto(Long toolId) {
        UUID uuid = repository.findPhotoUuidByToolId(toolId)
                .orElseThrow(() -> new BPException.NotFound("Photo uuid not found in tool id: " + toolId));
        return fileStorageFacade.download(uuid, FileType.PHOTO_TOOL);
    }
}