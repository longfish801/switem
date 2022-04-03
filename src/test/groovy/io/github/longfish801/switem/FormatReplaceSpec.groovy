/*
 * FormatReplaceSpec.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.switem

import io.github.longfish801.tpac.TpacHandle
import io.github.longfish801.tpac.TpacSemanticException
import io.github.longfish801.tpac.tea.TeaHandle
import spock.lang.Shared
import spock.lang.Specification

/**
 * replaceハンドルのテスト。
 * @author io.github.longfish801
 */
class FormatReplaceSpec extends Specification {
	/** FormatOperator */
	@Shared FormatOperator operator
	
	def setup(){
		operator = new FormatOperator(tag: 'replace', tagdsl: new SwitemServer().tagdsl.cl('formatOperator'))
	}
	
	def 'visit'(){
		when:
		operator.dflt = [ "hello\tbye", "right\tleft" ]
		operator.tagdsl.cl("visit#replace").call(operator)
		then:
		operator.repMap == [ hello: 'bye', right: 'left' ]
		
		when: 'タブを含まない行は無視します'
		operator.dflt = [ "hello", "right\tleft" ]
		operator.tagdsl.cl("visit#replace").call(operator)
		then:
		operator.repMap == [ right: 'left' ]
	}
	
	def 'formatTextHandle'(){
		given:
		TeaHandle chunkHandle
		TeaHandle coverHandle
		
		when:
		chunkHandle = new TpacHandle(tag: 'chunk')
		chunkHandle.dflt = [ 'hello world.', 'hello right world.' ]
		coverHandle = new TpacHandle(tag: 'test')
		coverHandle << chunkHandle
		operator.repMap = [ hello: 'bye', right: 'left' ]
		operator.formatTextHandle(coverHandle)
		then:
		coverHandle.solve('chunk').dflt == [ 'bye world.', 'bye left world.' ]
	}
}
