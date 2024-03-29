package com.gitlab.davinkevin.issues.jooq.r2dbcpgerrorwithblock.repository

import io.r2dbc.spi.ConnectionFactory
import org.jooq.DSLContext
import org.jooq.impl.DSL
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import reactor.core.publisher.Mono

/**
 * Created by kevin on 29/12/2021
 */
@Configuration
@Import(ExampleRepository::class)
class JooqConfig {

    @Bean
    fun dslContext(cf: ConnectionFactory): DSLContext? = Mono.from(cf.create()).map {
        DSL.using(it)
    }.block()
}
