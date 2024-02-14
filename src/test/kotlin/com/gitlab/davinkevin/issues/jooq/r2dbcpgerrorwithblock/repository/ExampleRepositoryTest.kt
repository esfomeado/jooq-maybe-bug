package com.gitlab.davinkevin.issues.jooq.r2dbcpgerrorwithblock.repository

import io.r2dbc.spi.ConnectionFactory
import org.jooq.DSLContext
import org.jooq.impl.DSL
import org.jooq.impl.DSL.insertInto
import org.jooq.impl.DSL.truncate
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.ImportAutoConfiguration
import org.springframework.boot.autoconfigure.r2dbc.R2dbcAutoConfiguration
import org.springframework.boot.test.autoconfigure.OverrideAutoConfiguration
import org.springframework.context.annotation.Import
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.containers.PostgreSQLR2DBCDatabaseContainer
import org.testcontainers.containers.wait.strategy.HostPortWaitStrategy
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName
import reactor.core.publisher.Hooks
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import reactor.test.StepVerifier
import java.lang.annotation.Inherited
import java.util.*

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Inherited
@ExtendWith(SpringExtension::class)
@OverrideAutoConfiguration(enabled = false)
@Import(JooqConfig::class)
@ImportAutoConfiguration(R2dbcAutoConfiguration::class)
@Testcontainers
annotation class JooqR2DBCTest


/**
 * Created by kevin on 30/12/2021
 */
@JooqR2DBCTest
@Import(ExampleRepository::class)
internal class ExampleRepositoryTest(
    @Autowired val cf: ConnectionFactory,
    @Autowired val repository: ExampleRepository


) {

    @BeforeEach
    fun createTable() {
        DSL.using(cf)
            .createTableIfNotExists(EXAMPLE)
            .column(ID)
            .column(DESC)
            .primaryKey(ID)
            .toMono()
            .block()
    }

    @Nested
    @DisplayName("should find by id")
    inner class ShouldFindById {

        @BeforeEach
        fun beforeEach() {
            Mono.from(cf.create())
                .flatMap { connection ->
                    Mono.from(DSL.using(connection).transactionPublisher {
                        it.dsl().insertInto(EXAMPLE)
                            .set(ID, UUID.fromString("34c5aada-6e9e-4fe2-96aa-421c8a8715ec"))
                            .set(DESC, "FOO")
                    }).thenReturn(connection)
                }
                .flatMap { Mono.from(it.close()) }
                .block();
        }

        @Test
        fun `with success`() {
            /* Given */
            /* When */
            StepVerifier.create(repository.findOne(UUID.fromString("34c5aada-6e9e-4fe2-96aa-421c8a8715ec")))
                /* Then */
                .expectSubscription()
                .expectNext(
                    Example(
                        id = UUID.fromString("34c5aada-6e9e-4fe2-96aa-421c8a8715ec"),
                        desc = "FOO"
                    )
                )
                .verifyComplete()
        }

    }

    companion object {
        @JvmStatic
        private val pgContainer = PostgreSQLContainer(DockerImageName.parse("postgres:latest"))
            .withUsername("username")
            .withPassword("password")
            .withDatabaseName("db")

        @JvmStatic
        private val pgContainerReactive = PostgreSQLR2DBCDatabaseContainer(pgContainer)

        @BeforeAll
        @JvmStatic
        fun beforeAll() {
            pgContainerReactive.start()
        }

        @JvmStatic
        @DynamicPropertySource
        fun registerDynamicProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.r2dbc.url") { pgContainer.jdbcUrl.replace("jdbc", "r2dbc") }
            registry.add("spring.r2dbc.username", pgContainer::getUsername)
            registry.add("spring.r2dbc.password", pgContainer::getPassword)
            registry.add("spring.r2dbc.driver-class-name", pgContainer::getDriverClassName);
        }
    }
}
