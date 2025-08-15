@file:Suppress("NonAsciiCharacters")

package com.nyasai.traintimer.routeinfo

import com.github.kittinunf.fuel.core.Response
import com.nyasai.traintimer.http.IHttpClient
import com.nyasai.traintimer.util.YahooRouteInfoGetter
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import io.mockk.*
import java.util.*

@ExtendWith(MockKExtension::class)
internal class DiagramTypeModelTest {

    private lateinit var _httpClientMock: IHttpClient

    @BeforeEach
    fun setUp() {
        _httpClientMock = mockk(relaxed = true)
    }

    @AfterEach
    fun tearDown() {
    }

    @Test
    fun `次のダイア種別の取得 現在：平日`() {
        val target = DiagramTypeModel(Calendar.getInstance(), _httpClientMock)
        val result = target.getNextDiagramType(YahooRouteInfoGetter.Companion.DiagramType.Weekday)
        Assertions.assertEquals(YahooRouteInfoGetter.Companion.DiagramType.Saturday, result)
    }

    @Test
    fun `次のダイア種別の取得 現在：土曜`() {
        val target = DiagramTypeModel(Calendar.getInstance(), _httpClientMock)
        val result = target.getNextDiagramType(YahooRouteInfoGetter.Companion.DiagramType.Saturday)
        Assertions.assertEquals(YahooRouteInfoGetter.Companion.DiagramType.Holiday, result)
    }

    @Test
    fun `次のダイア種別の取得 現在：日曜`() {
        val target = DiagramTypeModel(Calendar.getInstance(), _httpClientMock)
        val result = target.getNextDiagramType(YahooRouteInfoGetter.Companion.DiagramType.Holiday)
        Assertions.assertEquals(YahooRouteInfoGetter.Companion.DiagramType.Weekday, result)
    }

    @Test
    fun `今日のダイア種別を取得 祝日判定無し 現在：平日`() {
        val calendarMock = mockk<Calendar>()
        every { calendarMock.get(any()) } returns 2
        val target = DiagramTypeModel(calendarMock, _httpClientMock)
        Calendar.getInstance()
        val result = target.getTodayDiagramType(false)
        Assertions.assertEquals(YahooRouteInfoGetter.Companion.DiagramType.Weekday, result)
    }

    @Test
    fun `今日のダイア種別を取得 祝日判定無し 現在：土曜`() {
        val calendarMock = mockk<Calendar>()
        every { calendarMock.get(any()) } returns 7
        val target = DiagramTypeModel(calendarMock, _httpClientMock)
        val result = target.getTodayDiagramType(false)
        Assertions.assertEquals(YahooRouteInfoGetter.Companion.DiagramType.Saturday, result)
    }

    @Test
    fun `今日のダイア種別を取得 祝日判定無し 現在：日曜`() {
        val calendarMock = mockk<Calendar>()
        every { calendarMock.get(any()) } returns 1
        val target = DiagramTypeModel(calendarMock, _httpClientMock)
        val result = target.getTodayDiagramType(false)
        Assertions.assertEquals(YahooRouteInfoGetter.Companion.DiagramType.Holiday, result)
    }

    @Test
    fun `今日のダイア種別を取得 祝日判定有り 現在：平日`() {
        val calendarMock = mockk<Calendar>(relaxed = true)
        every { calendarMock.get(Calendar.DAY_OF_WEEK) } returns 2 // Calendar.MONDAY
        val response = mockk<Response>(relaxed = true)
        every { response.isSuccessful } returns true
        every { response.data } returns ("else".toByteArray())
        every { _httpClientMock.httpGet(any(), null) } returns(response)
        val target = DiagramTypeModel(calendarMock, _httpClientMock)
        val result = target.getTodayDiagramType(true)
        Assertions.assertEquals(YahooRouteInfoGetter.Companion.DiagramType.Weekday, result)
    }

    @Test
    fun `今日のダイア種別を取得 祝日判定有り 現在：土曜`() {
        val calendarMock = mockk<Calendar>(relaxed = true)
        every { calendarMock.get(Calendar.DAY_OF_WEEK) } returns 7 // Calendar.SATURDAY
        val response = mockk<Response>(relaxed = true)
        every { response.isSuccessful } returns true
        every { response.data } returns ("else".toByteArray())
        every { _httpClientMock.httpGet(any(), null) } returns(response)
        val target = DiagramTypeModel(calendarMock, _httpClientMock)
        val result = target.getTodayDiagramType(true)
        Assertions.assertEquals(YahooRouteInfoGetter.Companion.DiagramType.Saturday, result)
    }

    @Test
    fun `今日のダイア種別を取得 祝日判定有り 現在：日曜`() {
        val calendarMock = mockk<Calendar>(relaxed = true)
        every { calendarMock.get(Calendar.DAY_OF_WEEK) } returns 1 // Calendar.SUNDAY
        val response = mockk<Response>(relaxed = true)
        every { response.isSuccessful } returns true
        every { response.data } returns ("else".toByteArray())
        every { _httpClientMock.httpGet(any(), null) } returns(response)
        val target = DiagramTypeModel(calendarMock, _httpClientMock)
        val result = target.getTodayDiagramType(true)
        Assertions.assertEquals(YahooRouteInfoGetter.Companion.DiagramType.Holiday, result)
    }

    @Test
    fun `今日のダイア種別を取得 祝日判定有り 現在：平日（祝日）`() {
        val calendarMock = mockk<Calendar>(relaxed = true)
        every { calendarMock.get(Calendar.DAY_OF_WEEK) } returns 2 // Calendar.MONDAY
        val response = mockk<Response>(relaxed = true)
        every { response.isSuccessful } returns true
        every { response.data } returns ("holiday".toByteArray())
        every { _httpClientMock.httpGet(any(), null) } returns(response)
        val target = DiagramTypeModel(calendarMock, _httpClientMock)
        val result = target.getTodayDiagramType(true)
        Assertions.assertEquals(YahooRouteInfoGetter.Companion.DiagramType.Holiday, result)
    }

    @Test
    fun `今日のダイア種別を取得 祝日判定有り 祝日判定失敗 現在：平日`() {
        val calendarMock = mockk<Calendar>(relaxed = true)
        every { calendarMock.get(Calendar.DAY_OF_WEEK) } returns 2 // Calendar.MONDAY
        val response = mockk<Response>(relaxed = true)
        every { response.isSuccessful } returns false
        every { response.data } returns ("error".toByteArray())
        every { _httpClientMock.httpGet(any(), null) } returns(response)
        val target = DiagramTypeModel(calendarMock, _httpClientMock)
        val result = target.getTodayDiagramType(true)
        Assertions.assertEquals(YahooRouteInfoGetter.Companion.DiagramType.Weekday, result)
    }
}