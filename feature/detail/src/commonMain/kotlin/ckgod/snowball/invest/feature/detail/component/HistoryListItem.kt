package ckgod.snowball.invest.feature.detail.component

import com.ckgod.snowball.model.TradeHistoryResponse


sealed class HistoryListItem {
    data class Single(val item: TradeHistoryResponse) : HistoryListItem()
    data class CrashProtectionGroup(val items: List<TradeHistoryResponse>) : HistoryListItem()
}

fun List<TradeHistoryResponse>.toHistoryListItems(): List<HistoryListItem> {
    val normalItems = filter { it.crashRate == null }
    val crashProtectionItems = filter { it.crashRate != null }

    val result = mutableListOf<HistoryListItem>()

    normalItems.forEach { result.add(HistoryListItem.Single(it)) }

    if (crashProtectionItems.isNotEmpty()) {
        result.add(HistoryListItem.CrashProtectionGroup(crashProtectionItems))
    }

    return result
}
