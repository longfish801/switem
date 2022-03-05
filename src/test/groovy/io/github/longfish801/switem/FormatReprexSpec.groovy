/*
 * FormatReprexSpec.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.switem

import io.github.longfish801.tpac.TpacHandle
import io.github.longfish801.tpac.TpacSemanticException
import io.github.longfish801.tpac.tea.TeaHandle
import java.util.regex.Pattern
import spock.lang.Shared
import spock.lang.Specification

/**
 * reprexハンドルのテスト。
 * @version 0.1.00 2020/07/10
 * @author io.github.longfish801
 */
class FormatReprexSpec extends Specification {
	/** FormatOperator */
	@Shared FormatOperator operator
	
	def setup(){
		operator = new FormatOperator(tag: 'reprex', tagdsl: new SwitemServer().tagdsl.cl('formatOperator'))
	}
	
	def 'visit'(){
		when:
		operator.dflt = [ /(h\w+o)	bye/, /right	left/ ]
		operator.tagdsl.cl("visit#reprex").call(operator)
		then:
		operator.repMap.keySet().collect { it.pattern() } == [ /(h\w+o)/, /right/ ]
		operator.repMap.values() as List == [ 'bye', 'left' ]
		
		when: 'タブを含まない行は無視します'
		operator.dflt = [ "hello", "right\tleft" ]
		operator.tagdsl.cl("visit#reprex").call(operator)
		then:
		operator.repMap.keySet().collect { it.pattern() } == [ /right/ ]
		operator.repMap.values() as List == [ 'left' ]
	}
	
	def 'formatTextHandle'(){
		given:
		TeaHandle chunkHandle
		TeaHandle coverHandle
		
		when:
		chunkHandle = new TpacHandle(tag: 'chunk')
		chunkHandle.dflt = [ 'hello world.', 'hey-ho right world!' ]
		coverHandle = new TpacHandle(tag: 'test')
		coverHandle << chunkHandle
		operator.repMap = [:]
		operator.repMap[Pattern.compile(/^h([\w\-]+)o/, Pattern.MULTILINE)] = /H$1o/
		operator.repMap[Pattern.compile(/right/, Pattern.MULTILINE)] = /left/
		operator.formatTextHandle(coverHandle)
		then:
		coverHandle.solvePath('chunk').dflt == [ 'Hello world.', 'Hey-ho left world!' ]
	}
}
