package tech.konso.toolsmanagement.modules.tools.commons;

import org.springframework.data.jpa.domain.Specification;

public abstract class AbstractSpecification {

    public static final String FILTER_SEPARATOR = ",";
    public static final int LIKE_NAME_MIN_LENGTH = 3;

    public static <T> SpecBuilder<T> specBuilder(Class<T> clazz) {
        return new SpecBuilder<>();
    }

    public static <T> SpecBuilder<T> specBuilder(Specification<T> spec) {
        return new SpecBuilder<>(spec);
    }

    public static class SpecBuilder<T> {
        private Specification<T> specification;

        private SpecBuilder() {
        }

        private SpecBuilder(Specification<T> specification) {
            this.specification = specification;
        }

        public SpecBuilder<T> and(Specification<T> spec) {
            specification = specification == null ? spec : specification.and(spec);
            return this;
        }

        public Specification<T> build() {
            return specification;
        }
    }
}

