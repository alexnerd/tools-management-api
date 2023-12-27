package tech.konso.toolsmanagement.modules.business.tools.comment.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.konso.toolsmanagement.modules.business.tools.comment.controller.dto.CommentFilterInfo;
import tech.konso.toolsmanagement.modules.business.tools.comment.controller.dto.CommentRequest;
import tech.konso.toolsmanagement.modules.business.tools.comment.persistence.dao.Comment;
import tech.konso.toolsmanagement.modules.business.tools.comment.persistence.repository.CommentRepository;
import tech.konso.toolsmanagement.modules.business.tools.comment.persistence.specification.CommentSpecification;
import tech.konso.toolsmanagement.modules.business.tools.comment.service.mappers.CommentDtoMapper;
import tech.konso.toolsmanagement.modules.business.tools.comment.service.mappers.CommentEntityMapper;
import tech.konso.toolsmanagement.system.commons.exceptions.BPException;
import tech.konso.toolsmanagement.system.commons.specification.AbstractSpecification;

import java.util.Optional;

import static tech.konso.toolsmanagement.system.commons.specification.AbstractSpecification.specBuilder;

/**
 * Service layer for working with tools comments.
 */
@Service
public class CommentService {

    @Autowired
    private CommentRepository repository;

    @Autowired
    private CommentEntityMapper entityMapper;

    private CommentDtoMapper dtoMapper;

    @PostConstruct
    public void init() {
        dtoMapper = new CommentDtoMapper();
    }

    /**
     * Delete comment from database by unique id.
     * <p>
     * Example:
     * <pre>
     *     deleteById(2L);
     * </pre>
     *
     * @param id of comment
     */
    @Transactional
    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    /**
     * Save new comment to database or update existing.
     * <p>
     * Example:
     * <pre>
     *     CommentRequest rq = new CommentRequest(1L, "new_comment", "935921a7-692e-4ee4-a089-2695b68e9804");
     *     Comment savedComment = service.save(rq);
     * </pre>
     *
     * @param rq {@link CommentRequest} object for creating comment
     * @return {@link Comment} saved object
     */
    @Transactional
    public Comment save(CommentRequest rq) {
        return Optional.ofNullable(rq.id())
                .map(id -> repository.findById(rq.id())
                        .orElseThrow(() -> new BPException.NotFound("Comment not found id: " + id))
                ).map(comment -> entityMapper.updateContent(comment, rq))
                .orElseGet(() ->
                        repository.save(entityMapper.toEntity(new Comment(), rq))
                );
    }

    /**
     * Find comments by comment specification and returns it in pageable format.
     * By default, result set sorts by create date from newer to older.
     * <p>
     * Example:
     * <pre>
     *     Specification<Comment> spec = specBuilder(sortSpec("name,desc")).build();
     *     Page<CommentFilterInfo> foundedComments = service.findAll(0, 100, spec);
     * </pre>
     *
     * @param page number of returned result set
     * @param size of the returned page
     * @param spec set of comment specification
     * @return {@link Page < CommentFilterInfo >} object for resulting dataset in pageable format
     * @see CommentSpecification comment specifications
     */
    public Page<CommentFilterInfo> findAll(int page, int size, Specification<Comment> spec) {
        AbstractSpecification.SpecBuilder<Comment> builder = specBuilder(Comment.class);
        Pageable pageable = PageRequest.of(page, size);
        return repository.findAll(builder.and(spec).build(), pageable).map(dtoMapper::mapToCommentFilterInfo);
    }
}
