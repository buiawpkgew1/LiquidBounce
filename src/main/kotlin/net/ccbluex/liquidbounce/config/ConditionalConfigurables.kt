/*
 * This file is part of LiquidBounce (https://github.com/CCBlueX/LiquidBounce)
 *
 * Copyright (c) 2015 - 2024 CCBlueX
 *
 * LiquidBounce is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * LiquidBounce is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with LiquidBounce. If not, see <https://www.gnu.org/licenses/>.
 */

package net.ccbluex.liquidbounce.config

import net.ccbluex.liquidbounce.config.util.Exclude
import net.ccbluex.liquidbounce.event.Listenable
import net.ccbluex.liquidbounce.features.module.QuickImports
import net.ccbluex.liquidbounce.script.ScriptApi
import net.ccbluex.liquidbounce.web.socket.protocol.ProtocolExclude

/**
 * 处理启用时的事件。允许客户端用户切换功能（如模块）。
 */
abstract class ToggleableConfigurable(
    @Exclude @ProtocolExclude val parent: Listenable? = null,
    name: String,
    enabled: Boolean
) : Listenable, Configurable(name, valueType = ValueType.TOGGLEABLE), QuickImports {

    // TODO: 使 enabled 的改变也调用 newState
    var enabled by boolean("Enabled", enabled)

    /**
     * 根据状态启用或禁用配置项。
     */
    fun newState(state: Boolean) {
        if (!enabled) {
            return
        }

        if (state) {
            enable()
        } else {
            disable()
        }

        inner.filterIsInstance<ChoiceConfigurable<*>>().forEach { it.newState(state) }
        inner.filterIsInstance<ToggleableConfigurable>().forEach { it.newState(state) }
    }

    /**
     * 启用配置项。
     */
    open fun enable() {}

    /**
     * 禁用配置项。
     */
    open fun disable() {}

    /**
     * 处理事件。如果上级 Listenable 或当前配置项未启用，则返回 false。
     */
    override fun handleEvents() = super.handleEvents() && enabled

    /**
     * 返回上级 Listenable。
     */
    override fun parent() = parent

    /**
     * 获取启用的值。
     */
    @ScriptApi
    @Suppress("unused")
    fun getEnabledValue(): Value<*> = this.inner[0]
}

/**
 * 允许配置和管理模式。
 */
class ChoiceConfigurable<T : Choice>(
    @Exclude @ProtocolExclude val listenable: Listenable,
    name: String,
    activeChoiceCallback: (ChoiceConfigurable<T>) -> T,
    choicesCallback: (ChoiceConfigurable<T>) -> Array<T>
) : Configurable(name, valueType = ValueType.CHOICE) {

    var choices: MutableList<T> = choicesCallback(this).toMutableList()
    private var defaultChoice: T = activeChoiceCallback(this)
    var activeChoice: T = defaultChoice

    /**
     * 根据状态启用或禁用当前活动选择。
     */
    fun newState(state: Boolean) {
        if (state) {
            this.activeChoice.enable()
        } else {
            this.activeChoice.disable()
        }

        inner.filterIsInstance<ChoiceConfigurable<*>>().forEach { it.newState(state) }
        inner.filterIsInstance<ToggleableConfigurable>().forEach { it.newState(state) }
    }

    /**
     * 根据名称设置活动选择。
     */
    override fun setByString(name: String) {
        val newChoice = choices.firstOrNull { it.choiceName == name }

        if (newChoice == null) {
            throw IllegalArgumentException("ChoiceConfigurable `${this.name}` has no option named $name" +
                " (available options are ${this.choices.joinToString { it.choiceName }})")
        }

        if (this.activeChoice.handleEvents()) {
            this.activeChoice.disable()
        }

        // 不要移除这个！这很重要。我们需要调用选择的监听器以更新其他系统。
        set(mutableListOf(newChoice), apply = {
            this.activeChoice = it[0] as T
        })

        if (this.activeChoice.handleEvents()) {
            this.activeChoice.enable()
        }
    }

    /**
     * 恢复默认选择。
     */
    override fun restore() {
        if (this.activeChoice.handleEvents()) {
            this.activeChoice.disable()
        }

        set(mutableListOf(defaultChoice), apply = {
            this.activeChoice = it[0] as T
        })

        if (this.activeChoice.handleEvents()) {
            this.activeChoice.enable()
        }
    }

    /**
     * 获取所有选择的字符串表示。
     */
    @ScriptApi
    fun getChoicesStrings(): Array<String> = this.choices.map { it.name }.toTypedArray()

}

/**
 * 模式是子模块，用于将不同的绕过方法分离到额外的类中。
 */
abstract class Choice(name: String) : Configurable(name), Listenable, NamedChoice, QuickImports {

    override val choiceName: String
        get() = this.name

    val isActive: Boolean
        get() = this.parent.activeChoice === this

    abstract val parent: ChoiceConfigurable<*>

    /**
     * 启用模式。
     */
    open fun enable() {}

    /**
     * 禁用模式。
     */
    open fun disable() {}

    /**
     * 处理事件。如果父级或模式未激活，则返回 false。
     */
    override fun handleEvents() = super.handleEvents() && isActive

    /**
     * 返回上级 Listenable。
     */
    override fun parent() = this.parent.listenable

    /**
     * 创建选择配置项。
     */
    protected fun <T: Choice> choices(name: String, active: T, choices: Array<T>) =
        choices(this, name, active, choices)

    /**
     * 创建选择配置项。
     */
    protected fun <T: Choice> choices(
        name: String,
        activeCallback: (ChoiceConfigurable<T>) -> T,
        choicesCallback: (ChoiceConfigurable<T>) -> Array<T>
    ) = choices(this, name, activeCallback, choicesCallback)
}

/**
 * 空选择。
 * 它什么都不做。
 * 当您希望客户端用户禁用某个功能时使用它。
 */
class NoneChoice(override val parent: ChoiceConfigurable<*>) : Choice("None")
