package org.evoleq.math.cat.monad.result

import kotlin.test.Test
import kotlin.test.assertEquals

class ResultTest {
    
    @Test
    fun `map functions` () {
        val x = Result.fail<Int,String>("fail")
        val y = x map {2}
        assertEquals(true, y is Result.Failure<Int,String>)
    }
    
    @Test
    fun `applicative functions`() {
        val f: (Boolean) -> (Int) -> Pair<Boolean,Int> = {b -> {x -> Pair(b, x)}}
        
        val bRes = Result.ret<Boolean,String>(true)
        val iRes = Result.ret<Int,String>(1)
        
        val f1 = Result.ret<(Boolean) -> (Int) -> Pair<Boolean,Int>,String>(f)
        
        val res1 = f1 apply bRes apply iRes
        require(res1 is Result.Success)
        assertEquals(Pair(true,1), res1.value)
        
        val failingF = Result.fail<(Boolean) -> (Int) -> Pair<Boolean,Int>,String>("Failing F")
    
        val res2 = failingF apply bRes apply iRes
        require(res2 is Result.Failure)
        assertEquals("Failing F", res2.value)
    
    
        val res3 = failingF apply Result.fail("Failing Boolean") apply iRes
        require(res3 is Result.Failure)
        assertEquals("Failing F", res3.value)
    
        val res4 = failingF apply Result.fail("Failing Boolean") apply Result.fail("Failing Int")
        require(res4 is Result.Failure)
        assertEquals("Failing F", res4.value)
    
        val res5 = f1 apply Result.fail("Failing Boolean") apply iRes
        require(res5 is Result.Failure)
        assertEquals("Failing Boolean", res5.value)
    
        val res6 = f1 apply Result.fail("Failing Boolean") apply Result.fail("Failing Int")
        require(res6 is Result.Failure)
        assertEquals("Failing Boolean", res6.value)
    
        val res7 = f1 apply bRes apply Result.fail("Failing Int")
        require(res7 is Result.Failure)
        assertEquals("Failing Int", res7.value)
    
    
    
    
    }
    
    @Test
    fun `apply monoidal failures`() {
        val result = Result.fail<Int,List<Int>>(listOf(1))
        
        val fResult = Result.ret<(Int)->Int,List<Int>>{x -> x+1}
        
        val r = fResult applyMonoidal result
        
        println(r)
        
        val failingF = Result.fail<(Int)->Int,List<Int>>(listOf(0))
        val r1 = failingF applyMonoidal result
        println(r1)
    }
    
    @Test
    fun `apply monoidal on long chains` () {
        val fRes: ResultList<(Boolean)->(String)->(Int)->Triple<Boolean,String,Int>,String> = Result.retList<(Boolean)->(String)->(Int)->Triple<Boolean,String,Int>,String>{ b ->{ s->{ x-> Triple(b,s,x) }}}
        val fFail = ResultList.fail<(Boolean)->(String)->(Int)->Triple<Boolean,String,Int>,List<String>>(listOf("Function Failure"))
        val bRes = ResultList.ret<Boolean,List<String>>(true)
        val bFail = ResultList.fail<Boolean,List<String>>(listOf("Boolean Failure"))
    
        val stringRes = ResultList.ret<String,List<String>>("")
        val stringFail = ResultList.fail<String,List<String>>(listOf("String Failure"))
    
        val intRes = ResultList.ret<Int,List<String>>(0)
        val intFail = ResultList.fail<Int,List<String>>(listOf("Int Failure"))
        
        // success
        val succRes = fRes applyMonoidal bRes applyMonoidal stringRes applyMonoidal intRes
        require(succRes is Result.Success)
        assertEquals(Triple(true,"",0), succRes.value)
        
        val completeFailure = fFail applyMonoidal bFail applyMonoidal stringFail applyMonoidal intFail
        require(completeFailure is Result.Failure)
        assertEquals(listOf("Function Failure","Boolean Failure","String Failure","Int Failure"), completeFailure.value)
        
    }
}