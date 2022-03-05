/*
 * SwitemFormatSpec.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.switem

import io.github.longfish801.tpac.TpacHandle
import io.github.longfish801.tpac.tea.TeaHandle
import spock.lang.Shared
import spock.lang.Specification

/**
 * SwitemFormatのテスト。
 * @version 0.1.00 2020/07/10
 * @author io.github.longfish801
 */
class SwitemFormatSpec extends Specification {
	/** FormatOperator */
	@Shared SwitemFormat switemFormat
	
	def setup(){
		switemFormat = new SwitemFormat(tag: 'test')
	}
	
	def 'format'(){
		given:
		TeaHandle chunkHandleA
		TeaHandle chunkHandleB
		TeaHandle coverHandle
		FormatOperator operator
		
		when:
		chunkHandleA = new TpacHandle(tag: 'chunk', name: '1')
		chunkHandleA.dflt = [ 'a', 'a', 'a' ]
		chunkHandleB = new TpacHandle(tag: 'chunk', name: '2')
		chunkHandleB.dflt = [ 'b', 'b', 'b' ]
		coverHandle = new TpacHandle(tag: 'some')
		coverHandle << chunkHandleA
		coverHandle << chunkHandleB
		operator = new FormatOperator(tag: 'call', tagdsl: new SwitemServer().tagdsl.cl('formatOperator'), shell: new GroovyShell())
		operator.chunkCl = { def hndl -> return [ hndl.dflt.join("-") ] }
		switemFormat << operator
		switemFormat.format(coverHandle)
		then:
		coverHandle.solvePath('chunk:1').dflt == [ 'a-a-a' ]
		coverHandle.solvePath('chunk:2').dflt == [ 'b-b-b' ]
	}
}
