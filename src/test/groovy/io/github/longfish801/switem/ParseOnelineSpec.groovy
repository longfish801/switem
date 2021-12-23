/*
 * ParseOnelineSpec.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.switem

import io.github.longfish801.switem.SwitemMsg as msgs
import io.github.longfish801.tpac.TpacHandle
import io.github.longfish801.tpac.tea.TeaHandle
import spock.lang.Shared
import spock.lang.Specification

/**
 * onelineハンドルのテスト。
 * @version 0.1.00 2020/07/10
 * @author io.github.longfish801
 */
class ParseOnelineSpec extends Specification {
	/** ParseOperator */
	@Shared ParseOperator operator
	
	def setup(){
		operator = new ParseOperator(tag: 'oneline', tagdsl: new SwitemServer().tagdsl.cl('parseOperator'))
	}
	
	def 'visit'(){
		when:
		operator.bullet = '# '
		operator.pattern = null
		operator.tagdsl.cl("visit#oneline").call(operator)
		then:
		operator.isTarget.call('# abc') == true
		
		when:
		operator.bullet = null
		operator.pattern = /\[.+\]/
		operator.tagdsl.cl("visit#oneline").call(operator)
		then:
		operator.isTarget.call('[abc]') == true
		
		when: 'bulletキーの指定が優先されます'
		operator.bullet = '# '
		operator.pattern = /\[.+\]/
		operator.tagdsl.cl("visit#oneline").call(operator)
		then:
		operator.isTarget.call('# abc') == true
		operator.isTarget.call('[abc]') == false
	}
	
	def 'visit - exception'(){
		given:
		ParseException exc
		
		when:
		operator.bullet = null
		operator.pattern = null
		operator.tagdsl.cl("visit#oneline").call(operator)
		operator.isTarget.call('# abc')
		then:
		exc = thrown(ParseException)
		exc.message == msgs.exc.noOnelineKey
	}
	
	def 'detectRange'(){
		given:
		int size
		
		when:
		operator.bullet = '# '
		operator.pattern = null
		operator.visit()
		size = operator.tagdsl.cl("detectRange#oneline").call(operator, ['aaa', '# abc', 'bbb'], 0)
		then:
		size == 0
		
		when:
		operator.bullet = '# '
		operator.pattern = null
		operator.visit()
		size = operator.tagdsl.cl("detectRange#oneline").call(operator, ['aaa', '# abc', 'bbb'], 1)
		then:
		size == 1
		
		when:
		operator.bullet = null
		operator.pattern = /\[.+\]/
		operator.visit()
		size = operator.tagdsl.cl("detectRange#oneline").call(operator, ['aaa', 'bbb', '[abc]', 'ccc'], 2)
		then:
		size == 1
	}
	
	def 'createCoverHandle'(){
		given:
		TeaHandle coverHandle
		
		when:
		operator.bullet = '# '
		operator.pattern = null
		operator.visit()
		coverHandle = new TpacHandle(tag: 'parse')
		operator.tagdsl.cl('createCoverHandle#oneline').call(operator, coverHandle, ['# abc'])
		then:
		coverHandle.bullet == '# '
		coverHandle.solvePath('chunk:1').dflt == [ 'abc' ]
		
		when:
		operator.bullet = null
		operator.pattern = /\[.+\]/
		operator.visit()
		coverHandle = new TpacHandle(tag: 'parse')
		operator.tagdsl.cl('createCoverHandle#oneline').call(operator, coverHandle, ['[abc]'])
		then:
		coverHandle.solvePath('chunk:1').dflt == [ '[abc]' ]
		
		when:
		operator.bullet = null
		operator.pattern = /\[(.+)\]/
		operator.visit()
		coverHandle = new TpacHandle(tag: 'parse')
		operator.tagdsl.cl('createCoverHandle#oneline').call(operator, coverHandle, ['[abc]'])
		then:
		coverHandle.solvePath('chunk:1').dflt == [ 'abc' ]
	}
}
