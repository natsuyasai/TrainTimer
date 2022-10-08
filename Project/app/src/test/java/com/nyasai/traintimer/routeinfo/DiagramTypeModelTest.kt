package com.nyasai.traintimer.routeinfo

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.Response
import com.github.kittinunf.fuel.core.ResponseResultOf
import com.github.kittinunf.fuel.core.requests.DefaultRequest
import com.nyasai.traintimer.util.YahooRouteInfoGetter
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.mockkObject
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.runner.RunWith
import org.mockito.kotlin.*
import java.util.*

@ExtendWith(MockKExtension::class)
internal class DiagramTypeModelTest {
    @BeforeEach
    fun setUp() {
    }

    @AfterEach
    fun tearDown() {
    }

    @Test
    fun `次のダイア種別の取得 現在：平日`() {
        val target = DiagramTypeModel(Calendar.getInstance())
        val result = target.getNextDiagramType(YahooRouteInfoGetter.Companion.DiagramType.Weekday)
        Assertions.assertEquals(YahooRouteInfoGetter.Companion.DiagramType.Saturday, result)
    }

    @Test
    fun `次のダイア種別の取得 現在：土曜`() {
        val target = DiagramTypeModel(Calendar.getInstance())
        val result = target.getNextDiagramType(YahooRouteInfoGetter.Companion.DiagramType.Saturday)
        Assertions.assertEquals(YahooRouteInfoGetter.Companion.DiagramType.Holiday, result)
    }

    @Test
    fun `次のダイア種別の取得 現在：日曜`() {
        val target = DiagramTypeModel(Calendar.getInstance())
        val result = target.getNextDiagramType(YahooRouteInfoGetter.Companion.DiagramType.Holiday)
        Assertions.assertEquals(YahooRouteInfoGetter.Companion.DiagramType.Weekday, result)
    }

    @Test
    fun `今日のダイア種別を取得 祝日判定無し 現在：平日`() {
        val calendarMock = mock<Calendar>(){
            on { get(any()) } doReturn 2
        }
        val target = DiagramTypeModel(calendarMock)
        Calendar.getInstance()
        val result = target.getTodayDiagramType(false)
        Assertions.assertEquals(YahooRouteInfoGetter.Companion.DiagramType.Weekday, result)
    }

    @Test
    fun `今日のダイア種別を取得 祝日判定無し 現在：土曜`() {
        val calendarMock = mock<Calendar>(){
            on { get(any()) } doReturn 7
        }
        val target = DiagramTypeModel(calendarMock)
        val result = target.getTodayDiagramType(false)
        Assertions.assertEquals(YahooRouteInfoGetter.Companion.DiagramType.Saturday, result)
    }

    @Test
    fun `今日のダイア種別を取得 祝日判定無し 現在：日曜`() {
        val calendarMock = mock<Calendar>(){
            on { get(any()) } doReturn 1
        }
        val target = DiagramTypeModel(calendarMock)
        val result = target.getTodayDiagramType(false)
        Assertions.assertEquals(YahooRouteInfoGetter.Companion.DiagramType.Holiday, result)
    }

    @Test
    fun `今日のダイア種別を取得 祝日判定有り 現在：平日`() {
        val calendarMock = mock<Calendar>(){
            on { get(any()) } doReturn 2
        }
        val target = DiagramTypeModelTesting(calendarMock)
        target.isHolidayMockReturn = false
        val result = target.getTodayDiagramType(true)
        Assertions.assertEquals(YahooRouteInfoGetter.Companion.DiagramType.Weekday, result)
    }

    @Test
    fun `今日のダイア種別を取得 祝日判定有り 現在：土曜`() {
        val calendarMock = mock<Calendar>(){
            on { get(any()) } doReturn 7
        }
        val target = DiagramTypeModelTesting(calendarMock)
        target.isHolidayMockReturn = false
        val result = target.getTodayDiagramType(true)
        Assertions.assertEquals(YahooRouteInfoGetter.Companion.DiagramType.Saturday, result)
    }

    @Test
    fun `今日のダイア種別を取得 祝日判定有り 現在：日曜`() {
        val calendarMock = mock<Calendar>(){
            on { get(any()) } doReturn 1
        }
        val target = DiagramTypeModelTesting(calendarMock)
        target.isHolidayMockReturn = false
        val result = target.getTodayDiagramType(true)
        Assertions.assertEquals(YahooRouteInfoGetter.Companion.DiagramType.Holiday, result)
    }

    @Test
    fun `今日のダイア種別を取得 祝日判定有り 現在：平日（祝日）`() {
        val calendarMock = mock<Calendar>(){
            on { get(any()) } doReturn 2
        }
        val target = DiagramTypeModelTesting(calendarMock)
        target.isHolidayMockReturn = true
        val result = target.getTodayDiagramType(true)
        Assertions.assertEquals(YahooRouteInfoGetter.Companion.DiagramType.Holiday, result)
    }


    internal class DiagramTypeModelTesting(calendar: Calendar): DiagramTypeModel(calendar) {
        var isHolidayMockReturn: Boolean = false
        override fun isHoliday(): Boolean {
            return isHolidayMockReturn
        }
    }
}