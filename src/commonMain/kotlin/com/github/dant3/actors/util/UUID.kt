package com.github.dant3.actors.util

import com.benasher44.uuid.uuid4

object UUID {
    fun generate(): String = uuid4().toString()
}