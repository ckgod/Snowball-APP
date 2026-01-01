package ckgod.snowball.invest.feature.detail.component

import ckgod.snowball.invest.domain.model.HistoryItem


sealed class HistoryListItem {
    data class Single(val item: HistoryItem) : HistoryListItem()
    data class CrashProtectionGroup(val items: List<HistoryItem>) : HistoryListItem()
}

fun List<HistoryItem>.toHistoryListItems(): List<HistoryListItem> {
    val normalItems = filter { it.crashRate == null }
    val crashProtectionItems = filter { it.crashRate != null }

    val result = mutableListOf<HistoryListItem>()

    normalItems.forEach { result.add(HistoryListItem.Single(it)) }

    if (crashProtectionItems.isNotEmpty()) {
        result.add(HistoryListItem.CrashProtectionGroup(crashProtectionItems))
    }

    return result
}
