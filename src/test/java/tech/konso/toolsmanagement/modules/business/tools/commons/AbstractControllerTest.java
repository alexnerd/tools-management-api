package tech.konso.toolsmanagement.modules.business.tools.commons;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import tech.konso.toolsmanagement.PostgreSQLContainerExtension;

import java.util.function.Function;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ExtendWith(PostgreSQLContainerExtension.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public abstract class AbstractControllerTest {

    @Value("http://localhost:${local.server.port}")
    protected String url;

    @Autowired
    protected ObjectMapper objectMapper;

    protected <T> Matcher<String> dtoMatcher(Class<T> clazz, Function<T, Boolean> func) {
        return new BaseMatcher<>() {
            @Override
            public boolean matches(Object o) {
                try {
                    return func.apply(objectMapper.readValue(o.toString(), clazz));
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("that the code is right written");
            }
        };
    }
}
