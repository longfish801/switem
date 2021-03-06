/*
 * SwitemParseSpec.groovy
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
 * SwitemParseのテスト。
 * @author io.github.longfish801
 */
class SwitemParseSpec extends Specification {
	/** FormatOperator */
	@Shared SwitemParse switemParse
	
	def setup(){
		switemParse = new SwitemParse(tag: 'test', tagdsl: new SwitemServer().tagdsl.cl('parse'))
		Switem switem = new Switem(tagdsl: new SwitemServer().tagdsl.cl('switem'), maker: new SwitemMaker(server: new SwitemServer()))
		switem << switemParse
		switem.validateRecursive()
	}
	
	def 'parse'(){
		given:
		ParseOperator parseEnclose
		TeaHandle coverHandle
		
		when:
		switemParse.visit()
		coverHandle = new TpacHandle(tag: 'cover')
		switemParse.parse(coverHandle, [ 'aaa', 'bbb', 'ccc' ])
		then:
		coverHandle.lowers.keySet() as List == [ 'chunk:1' ]
		coverHandle.solve('chunk:1').dflt == [ 'aaa', 'bbb', 'ccc' ]
		
		when:
		switemParse.visit()
		coverHandle = new TpacHandle(tag: 'cover')
		switemParse.parse(coverHandle, [ '', 'aaa', '', 'bbb', '', '', 'ccc', '' ])
		then:
		coverHandle.lowers.keySet() as List == [ 'chunk:1', 'chunk:2', 'chunk:3' ]
		coverHandle.solve('chunk:1').dflt == [ 'aaa' ]
		coverHandle.solve('chunk:2').dflt == [ 'bbb' ]
		coverHandle.solve('chunk:3').dflt == [ 'ccc' ]
		
		when:
		parseEnclose = new ParseOperator(tag: 'enclose', name:'enc', tagdsl: new SwitemServer().tagdsl.cl('parseOperator'))
		parseEnclose.bgn = '---'
		switemParse << parseEnclose
		switemParse.visitRecursive()
		coverHandle = new TpacHandle(tag: 'cover')
		switemParse.parse(coverHandle, [ 'aaa', '---', 'bbb', '---', '---', 'ccc', '---', 'ddd' ])
		then:
		coverHandle.lowers.keySet() as List == [ 'chunk:1', 'enc:1', 'enc:2', 'chunk:2' ]
		coverHandle.solve('chunk:1').dflt == [ 'aaa' ]
		coverHandle.solve('enc:1').top == '---'
		coverHandle.solve('enc:1').btm == '---'
		coverHandle.solve('enc:1').lowers.keySet() as List == [ 'chunk:1' ]
		coverHandle.solve('enc:1/chunk:1').dflt == [ 'bbb' ]
		coverHandle.solve('enc:2').top == '---'
		coverHandle.solve('enc:2').btm == '---'
		coverHandle.solve('enc:2').lowers.keySet() as List == [ 'chunk:1' ]
		coverHandle.solve('enc:2/chunk:1').dflt == [ 'ccc' ]
		coverHandle.solve('chunk:2').dflt == [ 'ddd' ]
	}
}
