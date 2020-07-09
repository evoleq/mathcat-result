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

typealias ResultT<S> = Result<S, Throwable>
fun <S> Result.Companion.retT(s: S): Result.Success<S, Throwable> = ret(s)
fun <S> Result.Companion.failT(throwable: Throwable): Result.Failure<S, Throwable> = fail(throwable)

typealias ResultList<S, F> = Result<S, List<F>>
fun <S, F> Result.Companion.retList(s: S): Result.Success<S, List<F>> = ret(s)
fun <S, F> Result.Companion.failList(list: List<F>): Result.Failure<S, List<F>> = fail(list)

typealias ResultListT<S> = Result<S, List<Throwable>>
fun <S> Result.Companion.retListT(s: S): Result.Success<S, List<Throwable>> = ret(s)
fun <S> Result.Companion.failListT(list: List<Throwable>): Result.Failure<S, List<Throwable>> = fail(list)