package com.back.standard.util

import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.core.types.dsl.SimpleExpression
import com.querydsl.core.types.dsl.StringExpression

// Comparison
infix fun <T> SimpleExpression<T>.eq(value: T?): BooleanExpression = this.eq(value)
infix fun <T> SimpleExpression<T>.ne(value: T?): BooleanExpression = this.ne(value)
infix fun <T : Comparable<*>> com.querydsl.core.types.dsl.ComparableExpression<T>.gt(value: T): BooleanExpression = this.gt(value)
infix fun <T : Comparable<*>> com.querydsl.core.types.dsl.ComparableExpression<T>.goe(value: T): BooleanExpression = this.goe(value)
infix fun <T : Comparable<*>> com.querydsl.core.types.dsl.ComparableExpression<T>.lt(value: T): BooleanExpression = this.lt(value)
infix fun <T : Comparable<*>> com.querydsl.core.types.dsl.ComparableExpression<T>.loe(value: T): BooleanExpression = this.loe(value)

// String
infix fun StringExpression.like(value: String): BooleanExpression = this.like(value)
infix fun StringExpression.contains(value: String): BooleanExpression = this.contains(value)
infix fun StringExpression.startsWith(value: String): BooleanExpression = this.startsWith(value)
infix fun StringExpression.endsWith(value: String): BooleanExpression = this.endsWith(value)
infix fun StringExpression.containsIgnoreCase(value: String): BooleanExpression = this.containsIgnoreCase(value)

// Boolean
infix fun BooleanExpression.and(other: BooleanExpression?): BooleanExpression = this.and(other)
infix fun BooleanExpression.or(other: BooleanExpression?): BooleanExpression = this.or(other)

// Collection
infix fun <T> SimpleExpression<T>.isIn(values: Collection<T>): BooleanExpression = this.`in`(values)
infix fun <T> SimpleExpression<T>.notIn(values: Collection<T>): BooleanExpression = this.notIn(values)
