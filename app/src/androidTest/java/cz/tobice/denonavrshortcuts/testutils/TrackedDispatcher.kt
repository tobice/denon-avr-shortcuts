package cz.tobice.denonavrshortcuts.testutils

import androidx.compose.ui.test.IdlingResource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.Job
import java.util.Collections
import java.util.WeakHashMap
import kotlin.coroutines.CoroutineContext

/**
 * A special [CoroutineDispatcher] wrapper that keeps track of its running jobs and exposes an
 * [IdlingResource] so that tests can detect (in)activity.
 *
 * This allows tests to wait for all asynchronous operations (typically network requests) to finish
 * before making assertions.
 *
 * Source: https://github.com/Kotlin/kotlinx.coroutines/issues/242#issuecomment-458046292
 *
 * @property wrappedDispatcher an actual dispatcher whose activity we want to keep track of.
 */
class TrackedDispatcher(
    private val wrappedDispatcher: CoroutineDispatcher
) : CoroutineDispatcher() {
    private val jobs = Collections.newSetFromMap(WeakHashMap<Job, Boolean>())

    override fun dispatch(context: CoroutineContext, block: Runnable) {
        context[Job]?.let { addNewJob(it) }
        wrappedDispatcher.dispatch(context, block)
    }

    @InternalCoroutinesApi
    override fun dispatchYield(context: CoroutineContext, block: Runnable) {
        context[Job]?.let { addNewJob(it) }
        wrappedDispatcher.dispatchYield(context, block)
    }

    private fun addNewJob(job: Job): Boolean {
        return jobs.add(job)
    }

    override fun isDispatchNeeded(context: CoroutineContext): Boolean {
        context[Job]?.let { addNewJob(it) }
        return wrappedDispatcher.isDispatchNeeded(context)
    }

    private fun isIdleNow(): Boolean {
        jobs.removeAll { !it.isActive }
        return jobs.isEmpty()
    }

    fun getIdlingResource(): IdlingResource {
        return object : IdlingResource {
            override val isIdleNow: Boolean
                get() = this@TrackedDispatcher.isIdleNow()
        }
    }
}
