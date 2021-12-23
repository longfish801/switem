/*
 * ParseIndentSpec.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.switem

import io.github.longfish801.tpac.TpacHandle
import io.github.longfish801.tpac.tea.TeaHandle
import java.util.regex.Pattern
import spock.lang.Shared
import spock.lang.Specification

/**
 * indentハンドルのテスト。
 * @version 0.1.00 2020/07/10
 * @author io.github.longfish801
 */
class ParseIndentSpec extends Specification {
	/** ParseOperator */
	@Shared ParseOperator operator
	
	def setup(){
		SwitemParse switemParse = new SwitemParse(tag: 'parse', tagdsl: new SwitemServer().tagdsl.cl('parse'))
		Switem switem = new Switem(tagdsl: new SwitemServer().tagdsl.cl('switem'), maker: new SwitemMaker(server: new SwitemServer()))
		switem << switemParse
		switem.validateRecursive()
		switem.visitRecursive()
		operator = new ParseOperator(tag: 'indent', tagdsl: new SwitemServer().tagdsl.cl('parseOperator'))
		switemParse << operator
	}
	
	def 'visit'(){
		when:
		operator.bullet = '- '
		operator.more = null
		operator.tagdsl.cl("visit#indent").call(operator)
		then:
		operator.getIndent.call('- abc') == '- '
		operator.getIndentMore.call('- def') == '- '
		
		when:
		operator.bullet = '* '
		operator.more = '  '
		operator.tagdsl.cl("visit#indent").call(operator)
		then:
		operator.getIndent.call('* abc') == '* '
		operator.getIndentMore.call('  def') == '  '
		
		when:
		operator.bullet = Pattern.compile(/\* +/)
		operator.more = Pattern.compile(/ {2}/)
		operator.tagdsl.cl("visit#indent").call(operator)
		then:
		operator.getIndent.call('* abc') == '* '
		operator.getIndentMore.call('  def') == '  '
	}
	
	def 'detectRange'(){
		given:
		int size
		
		when:
		operator.bullet = '- '
		operator.more = null
		operator.visit()
		size = operator.tagdsl.cl("detectRange#indent").call(operator, ['aaa', '- bbb', '- ccc', 'ddd'], 1)
		then:
		size == 2
		
		when:
		operator.bullet = '- '
		operator.more = null
		operator.visit()
		size = operator.tagdsl.cl("detectRange#indent").call(operator, ['aaa', '- bbb', '- ccc'], 1)
		then:
		size == 2
	}
	
	def 'createCoverHandle'(){
		given:
		TeaHandle coverHandle
		
		when:
		operator.bullet = '- '
		operator.more = null
		operator.visit()
		coverHandle = new TpacHandle(tag: 'format')
		operator.tagdsl.cl("createCoverHandle#indent").call(operator, coverHandle, ['- bbb', '- ccc'])
		then:
		coverHandle.bullet == '- '
		coverHandle.more == '- '
	}
}
