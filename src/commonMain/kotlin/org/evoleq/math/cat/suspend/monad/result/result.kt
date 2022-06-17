/**
 * Copyright (c) 2020 Dr. Florian Schmidt
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.evoleq.math.cat.suspend.monad.result

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import org.evoleq.math.cat.adt.Either
import org.evoleq.math.cat.adt.Left
import org.evoleq.math.cat.adt.Right
import org.evoleq.math.cat.marker.MathCatDsl


sealed class Result<out S, F> {
    
    data class Success<S, F>(val value: S) : Result<S, F>()
    data class Failure<S, F>(val value: F) : Result<S, F>()
    
    @MathCatDsl
    suspend infix fun <S1> map(f: suspend CoroutineScope.(S)-> S1): Result<S1, F> = coroutineScope {
        when (this@Result) {
            is Success -> Success<S1, F>(
                this.f(value)
            )
            is Failure -> Failure<S1,F>(
                value
            )
        }
    }
    
    @MathCatDsl
    suspend infix fun <S1> bind(arrow: suspend CoroutineScope.(S)-> Result<S1, F>): Result<S1, F> = coroutineScope {
        when (this@Result) {
            is Failure -> Failure(
                value
            )
            is Success -> with(map(arrow)) {
                when (this) {
                    is Failure -> Failure(
                        value
                    )
                    is Success -> value
                }
            }
        }
    }
    
    companion object {
        fun <S, F> ret(value: S): Success<S, F> =
            Success(value)
        fun <S, F> fail(value: F): Failure<S, F> =
            Failure(value)
    }
    
}

/**
 * Apply function of the applicative [Result]
 */
@MathCatDsl
suspend  fun <S,T, F> Result<suspend CoroutineScope.(S)->T, F>.apply(): suspend CoroutineScope.(Result<S, F>)->Result<T, F> = {
    resultS -> this@apply bind {f -> resultS map f}
}


/**
 * Apply function of the applicative [Result]
 */
@MathCatDsl
suspend infix fun <S,T, F> Result<suspend CoroutineScope.(S)->T, F>.apply(next: Result<S, F>): Result<T, F> = coroutineScope { apply()(next) }



@MathCatDsl
suspend fun <S, T, F> ResultList<suspend CoroutineScope.(S)->T,F>.applyMonoidal(): suspend CoroutineScope.(ResultList<S, F>)->(ResultList<T,F>) = {
    result -> when(val currentFailures = this@applyMonoidal) {
    is Result.Failure -> when(result) {
        is Result.Failure -> with(result.value) failures@{
            with(arrayListOf<F>()) {
                addAll(currentFailures.value)
                addAll(this@failures)
                Result.failList<T, F>(this)
            }
        }
        is Result.Success -> {
            Result.failList(currentFailures.value)
        }
    }
    is Result.Success -> this@applyMonoidal.apply(result)
}
}

@MathCatDsl
suspend infix fun <S, T, F> ResultList<suspend CoroutineScope.(S)->T,F>.applyMonoidal(next: ResultList<S, F>): (ResultList<T,F>) = coroutineScope { applyMonoidal()(next) }

@MathCatDsl
fun <T, F> Result<T, F>.toEither(): Either<F, T> = when(this){
    is Result.Failure -> Left(value)
    is Result.Success -> Right(value)
}

@MathCatDsl
fun <F, T> Either<F, T>.toResult(): Result<T, F> = when(this) {
    is Left -> Result.Failure(value)
    is Right -> Result.Success(value)
}