/*
 * FormatSubformatSpec.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.switem

import io.github.longfish801.clmap.ClmapClosureCallException
import io.github.longfish801.switem.SwitemMsg as msgs
import io.github.longfish801.tpac.TpacRefer
import io.github.longfish801.tpac.TpacSemanticException
import spock.lang.Shared
import spock.lang.Specification

/**
 * subformatハンドルのテスト。
 * @version 0.1.00 2021/11/18
 * @author io.github.longfish801
 */
class FormatSubformatSpec extends Specification {
	/** FormatOperator */
	@Shared FormatOperator operator
	
	def setup(){
		operator = new FormatOperator(tag: 'subformat', tagdsl: new SwitemServer().tagdsl.cl('formatOperator'), shell: new GroovyShell())
		SwitemFormat switemFormat = new SwitemFormat(tag: 'format')
		switemFormat << operator
	}
	
	def 'visit'(){
		when:
		operator.refer = null
		operator.tagdsl.cl("visit#subformat").call(operator)
		then:
		operator.formatRefer == null
		
		when:
		operator.refer = TpacRefer.newInstance(operator, '..')
		operator.tagdsl.cl("visit#subformat").call(operator)
		then:
		operator.formatRefer instanceof SwitemFormat
	}
	
	def 'visit - exception'(){
		given:
		ClmapClosureCallException exc
		
		when:
		operator.refer = TpacRefer.newInstance(operator, 'noSuchPath')
		operator.tagdsl.cl("visit#subformat").call(operator)
		then:
		exc = thrown(ClmapClosureCallException)
		exc.cause instanceof TpacSemanticException
		exc.cause.message == String.format(msgs.exc.invalidKeyType, 'refer', operator.refer)
	}
}
