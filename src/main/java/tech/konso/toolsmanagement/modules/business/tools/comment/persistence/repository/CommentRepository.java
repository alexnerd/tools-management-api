package tech.konso.toolsmanagement.modules.business.tools.comment.persistence.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import tech.konso.toolsmanagement.modules.business.tools.comment.persistence.dao.Comment;

/**
 * Repository for comment entity.
 * <p>Supports CRUD operations, searching by specification and pageable format.
 * <p>By default, uses read only transactions, if you want another mode, you must directly specify it on your method.
 */
@Repository
@Transactional(readOnly = true)
public interface CommentRepository extends JpaRepository<Comment, Long> {
    Page<Comment> findAll(@Nullable Specification<Comment> spec, Pageable pageable);
}
