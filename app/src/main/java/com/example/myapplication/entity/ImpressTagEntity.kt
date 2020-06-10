package com.example.myapplication.entity

import java.io.Serializable

/**
 * Copyright (C), 2018-2018
 * FileName:
 * Author:       肖冲
 * Date:         2019/2/18 17:00
 * Description:  ${DESCRIPTION}
 */
class ImpressTagEntity : Serializable {
    /**
     * id : 1
     * enName : Charming
     * color : #2CC4EA
     */

    private var id: Long = 0
    private var enName: String? = null
    private var color: String? = null
    private var isSelector = true//标志是否选中此标签

    fun isSelector(): Boolean {
        return isSelector
    }

    fun setSelector(selector: Boolean) {
        isSelector = selector
    }

    fun getId(): Long {
        return id
    }

    fun setId(id: Long) {
        this.id = id
    }

    fun getEnName(): String? {
        return enName
    }

    fun setEnName(enName: String) {
        this.enName = enName
    }

    fun getColor(): String? {
        return color
    }

    fun setColor(color: String) {
        this.color = color
    }
}