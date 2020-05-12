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
package org.evoleq.math.cat.monad.result



sealed class Result<out S, F> {
    
    data class Success<S, F>(val value: S) : Result<S, F>()
    data class Failure<S, F>(val value: F) : Result<S, F>()
    
    infix fun <S1> map(f: (S)-> S1): Result<S1, F> = when(this){
        is Success -> Success(
            f(value)
        )
        is Failure -> Failure(
            value
        )
    }
    
    infix fun <S1> bind(arrow: (S)-> Result<S1, F>): Result<S1, F> = when(this){
        is Failure -> Failure(
            value
        )
        is Success -> with(map(arrow)){
            when(this){
                is Failure -> Failure(
                    value
                )
                is Success -> value
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
