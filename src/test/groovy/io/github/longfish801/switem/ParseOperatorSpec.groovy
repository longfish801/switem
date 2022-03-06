/*
 * ParseOperatorSpec.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.switem

import io.github.longfish801.tpac.TpacHandle
import io.github.longfish801.tpac.TpacRefer
import io.github.longfish801.tpac.tea.TeaHandle
import spock.lang.Shared
import spock.lang.Specification

/**
 * ParseOperatorのテスト。
 * @version 0.1.00 2020/07/10
 * @author io.github.longfish801
 */
class ParseOperatorSpec extends Specification {
	/** FormatOperator */
	@Shared ParseOperator operator
	
	def setup(){
		SwitemParse switemParse = new SwitemParse(tag: 'parse', tagdsl: new SwitemServer().tagdsl.cl('parse'))
		Switem switem = new Switem(tagdsl: new SwitemServer().tagdsl.cl('switem'), maker: new SwitemMaker(server: new SwitemServer()))
		switem << switemParse
		switem.validateRecursive()
		switem.visitRecursive()
		operator = new ParseOperator(tag: 'enclose', name:'enc', tagdsl: new SwitemServer().tagdsl.cl('parseOperator'))
		switemParse << operator
	}
	
	def 'validate'(){
		when:
		operator.bgn = '---'
		operator.refer = TpacRefer.newInstance(operator, '..')
		operator.validate()
		then:
		noExceptionThrown()
	}
	
	def 'visit'(){
		when:
		operator.map.remove('refer')
		operator.visit()
		then:
		operator.parseRefer == operator.upper
		operator.coverNum == 0
		
		when:
		operator.refer = null
		operator.visit()
		then:
		operator.parseRefer == null
		
		when:
		operator.refer = TpacRefer.newInstance(operator, '..')
		operator.visit()
		then:
		operator.parseRefer == operator.upper
	}
	
	def 'detectRange'(){
		given:
		int size
		
		when:
		operator.bgn = '---'
		operator.end = null
		operator.visit()
		size = operator.detectRange(['aaa', '---', 'bbb', '---', 'ccc'], 1)
		then:
		size == 3
	}
	
	def 'createCoverHandle'(){
		given:
		List elems
		TeaHandle coverHandle
		
		when:
		operator.bgn = '---'
		operator.end = null
		operator.visit()
		coverHandle = operator.createCoverHandle([ '---', 'bbb', '---'])
		then:
		coverHandle.key == 'enc:1'
		coverHandle.top == '---'
		coverHandle.btm == '---'
		coverHandle.lowers.keySet() as List == [ 'chunk:1' ]
		coverHandle.solve('chunk:1').dflt == [ 'bbb' ]
	}
	
	def 'parseRecursive'(){
		given:
		TeaHandle coverHandle
		
		when:
		operator.bgn = '---'
		operator.end = null
		operator.visit()
		operator.parseRefer = null
		coverHandle = new TpacHandle(tag: 'cover')
		operator.parseRecursive(coverHandle, [ 'bbb' ])
		then:
		coverHandle.lowers.keySet() as List == [ 'chunk:1' ]
		coverHandle.solve('chunk:1').dflt ==[ 'bbb' ]
		
		when:
		operator.bgn = '---'
		operator.end = null
		operator.visit()
		operator.parseRefer = operator.upper
		coverHandle = new TpacHandle(tag: 'cover')
		operator.parseRecursive(coverHandle, [ '---', 'aaa', '---' ])
		then:
		coverHandle.lowers.keySet() as List == [ 'enc:1' ]
		coverHandle.solve('enc:1').lowers.keySet() as List == [ 'chunk:1' ]
		coverHandle.solve('enc:1/chunk:1').dflt == [ 'aaa' ]
	}
}
