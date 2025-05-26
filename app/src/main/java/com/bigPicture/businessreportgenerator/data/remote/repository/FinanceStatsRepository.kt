package com.bigPicture.businessreportgenerator.data.remote.repository

import com.bigPicture.businessreportgenerator.data.domain.GraphData
import com.bigPicture.businessreportgenerator.data.domain.GraphType
import com.bigPicture.businessreportgenerator.data.remote.api.FinanceApiService
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class FinanceStatsRepository(
    private val api: FinanceApiService
) {
    suspend fun fetchFinanceGraphs(stockName: String = "삼성전자"): List<GraphData> = coroutineScope {
        val exchange = async { api.getExchanges().data }
        val stock = async { api.getStocks(stockName).data }
        val us = async { api.getUsInterests().data }
        val kr = async { api.getKrInterests().data }

        val exchangeList = exchange.await().sortedBy { it.exchangeDate }.takeLast(30)
        val stockList = stock.await().sortedBy { it.stockDate }.takeLast(30)
        val usList = us.await().sortedBy { it.interestDate }.takeLast(30)
        val krList = kr.await().sortedBy { it.interestDate }.takeLast(30)

        listOfNotNull(
            if (exchangeList.isNotEmpty())
                GraphData(
                    title = "원/달러 환율 추이",
                    type = GraphType.LINE_CHART,
                    data = exchangeList.associate { it.exchangeDate to it.exchangeRate },
                    description = "최근 한 달간 원/달러 환율 변동"
                ) else null,
            if (stockList.isNotEmpty())
                GraphData(
                    title = "${stockName} 주가 추이",
                    type = GraphType.LINE_CHART,
                    data = stockList.associate { it.stockDate to it.stockPrice },
                    description = "최근 한 달간 ${stockName} 주가 변동"
                ) else null,
            if (usList.isNotEmpty())
                GraphData(
                    title = "미국 기준금리 추이",
                    type = GraphType.LINE_CHART,
                    data = usList.associate { it.interestDate to it.interestRate },
                    description = "최근 한 달간 미국 기준금리 변화"
                ) else null,
            if (krList.isNotEmpty())
                GraphData(
                    title = "한국 기준금리 추이",
                    type = GraphType.LINE_CHART,
                    data = krList.associate { it.interestDate to it.interestRate },
                    description = "최근 한 달간 한국 기준금리 변화"
                ) else null
        )
    }
}
