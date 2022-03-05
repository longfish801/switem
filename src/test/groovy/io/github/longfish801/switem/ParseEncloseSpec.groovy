/*
 * ParseEncloseSpec.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.switem

import io.github.longfish801.clmap.ClmapClosureCallException
import io.github.longfish801.switem.SwitemMsg as msgs
import io.github.longfish801.tpac.TpacHandle
import io.github.longfish801.tpac.tea.TeaHandle
import java.util.regex.Pattern
import spock.lang.Shared
import spock.lang.Specification

/**
 * encloseハンドルのテスト。
 * @version 0.1.00 2020/07/10
 * @author io.github.longfish801
 */
class ParseEncloseSpec extends Specification {
	/** ParseOperator */
	@Shared ParseOperator operator
	
	def setup(){
		SwitemParse switemParse = new SwitemParse(tag: 'parse', tagdsl: new SwitemServer().tagdsl.cl('parse'))
		Switem switem = new Switem(tagdsl: new SwitemServer().tagdsl.cl('switem'), maker: new SwitemMaker(server: new SwitemServer()))
		switem << switemParse
		switem.validateRecursive()
		switem.visitRecursive()
		operator = new ParseOperator(tag: 'enclose', tagdsl: new SwitemServer().tagdsl.cl('parseOperator'))
		switemParse << operator
	}
	
	def 'visit'(){
		when:
		operator.bgn = '---'
		operator.tagdsl.cl("visit#enclose").call(operator)
		then:
		operator.isBgn.call('---') == true
		operator.isEnd.call('---', '---') == true
		
		when:
		operator.bgn = '/---'
		operator.end = '---/'
		operator.tagdsl.cl("visit#enclose").call(operator)
		then:
		operator.isBgn.call('/---') == true
		operator.isEnd.call('---/', '***') == true
		
		when:
		operator.bgn = Pattern.compile(/#-+/)
		operator.end = Pattern.compile(/-+#/)
		operator.tagdsl.cl("visit#enclose").call(operator)
		then:
		operator.isBgn.call('#-----') == true
		operator.isEnd.call('-----#', '***') == true
	}
	
	def 'detectRange'(){
		given:
		int size
		
		when:
		operator.bgn = '---'
		operator.end = null
		operator.visit()
		size = operator.tagdsl.cl("detectRange#enclose").call(operator, ['aaa', '---', 'bbb', '---', 'ccc'], 1)
		then:
		size == 3
		
		when:
		operator.bgn = Pattern.compile(/-+/)
		operator.visit()
		size = operator.tagdsl.cl("detectRange#enclose").call(operator, ['-----', 'aaa', '---', 'bbb', '---', 'ccc', '-----'], 0)
		then:
		size == 7
	}
	
	def 'detectRange - exception'(){
		given:
		ClmapClosureCallException exc
		
		when:
		operator.bgn = '---'
		operator.end = null
		operator.visit()
		operator.tagdsl.cl('detectRange#enclose').call(operator, ['aaa', '---', 'bbb'], 1)
		then:
		exc = thrown(ClmapClosureCallException)
		exc.cause instanceof ParseException
		exc.cause.message == String.format(msgs.exc.noEndLine, '---')
	}
	
	def 'createCoverHandle'(){
		given:
		TeaHandle coverHandle
		
		when:
		operator.bgn = '---'
		operator.end = null
		operator.visit()
		coverHandle = new TpacHandle(tag: 'parse')
		operator.tagdsl.cl('createCoverHandle#enclose').call(operator, coverHandle, ['---', 'bbb', '---'])
		then:
		coverHandle.top == '---'
		coverHandle.btm == '---'
	}
}
