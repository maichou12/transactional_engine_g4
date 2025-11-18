package com.groupeisi.m2gl;

import com.groupeisi.m2gl.config.AsyncSyncConfiguration;
import com.groupeisi.m2gl.config.EmbeddedSQL;
import com.groupeisi.m2gl.config.JacksonConfiguration;
import com.groupeisi.m2gl.config.TestSecurityConfiguration;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Base composite annotation for integration tests.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest(
    classes = {
        TransactionalEngineBackendApp.class, JacksonConfiguration.class, AsyncSyncConfiguration.class, TestSecurityConfiguration.class,
    }
)
@EmbeddedSQL
public @interface IntegrationTest {
}
