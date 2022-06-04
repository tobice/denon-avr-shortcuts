package cz.tobice.denonavrshortcuts.utils

/** Flatten nested results when chaining. */
suspend fun <T, R> Result<T>.flatMap(block: suspend (T) -> (Result<R>)): Result<R> {
    return this.mapCatching {
        block(it).getOrThrow()
    }
}
