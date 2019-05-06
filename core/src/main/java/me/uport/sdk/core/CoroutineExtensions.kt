package me.uport.sdk.core

import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * Shorthand for the UI thread that is also a mockable context in unit tests
 */
val UI by lazy { coroutineUiContextInitBlock() }

private var coroutineUiContextInitBlock: () -> CoroutineContext = { Dispatchers.Main }

/**
 * Only useful in tests
 * Call this in @Before methods where you need to interact with UI context
 *
 * @hide
 */
fun stubUiContext() {
    coroutineUiContextInitBlock = { EmptyCoroutineContext }
}
