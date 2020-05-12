package org.evoleq.math.cat.suspend.monad.result

typealias ResultT<S> = Result<S, Throwable>
fun <S> Result.Companion.retT(s: S): Result.Success<S, Throwable> = ret(s)
fun <S> Result.Companion.failT(throwable: Throwable): Result.Failure<S, Throwable> = fail(throwable)