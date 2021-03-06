package com.bu.selfstudy.tool.myselectiontracker

import androidx.recyclerview.selection.ItemDetailsLookup

class IdItemDetails(private val id: Long, private val _position: Int)
    : ItemDetailsLookup.ItemDetails<Long>() {
    override fun getPosition() = _position
    override fun getSelectionKey() = id
}