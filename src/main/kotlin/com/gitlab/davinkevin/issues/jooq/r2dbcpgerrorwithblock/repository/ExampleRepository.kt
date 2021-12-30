package com.gitlab.davinkevin.issues.jooq.r2dbcpgerrorwithblock.repository

import org.jooq.DSLContext
import org.jooq.impl.DSL.field
import org.jooq.impl.DSL.table
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import java.util.*

val EXAMPLE = table("EXAMPLE")
val ID = field("ID", UUID::class.java)
val DESC = field("DESCRIPTION", String::class.java)

/**
 * Created by kevin on 30/12/2021
 */
class ExampleRepository(private val query: DSLContext) {

    fun findOne(id: UUID): Mono<Example> {
        return query.select(ID, DESC)
            .from(EXAMPLE)
            .where(ID.eq(id))
            .toMono()
            .map { (id, desc) -> Example(id, desc) }
    }

}

data class Example(val id: UUID, val desc: String)
