@file:Suppress("DEPRECATION")

package me.uport.sdk.testhelpers

import assertk.Assert
import assertk.Result
import assertk.assertions.support.expected
import assertk.assertions.support.show
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking
import kotlin.reflect.KClass

/**
 * simple wrapper to make assertions on coroutine blocks
 */
fun <T> coAssert(block: suspend CoroutineScope.() -> T): Assert<Result<T>> {
    return assertk.assertThat {
        runBlocking {
            block()
        }
    }
}

/**
 * assert that an object is an instance of one of a list of classes
 */
fun <T : Any> Assert<T>.isInstanceOf(classes: List<KClass<*>>) = transform { actual ->
    val matches = classes.filter { kClass ->
        kClass.isInstance(actual)
    }

    if (matches.isEmpty()) {
        expected("to be instance of one of:[${show(classes)}] but had class:${show(actual::class)}")
    }

    actual
}
