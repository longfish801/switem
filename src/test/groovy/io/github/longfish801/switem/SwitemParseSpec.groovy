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
 * @version 0.1.00 2020/07/10
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
		coverHandle.solvePath('chunk:1').dflt == [ 'aaa', 'bbb', 'ccc' ]
		
		when:
		switemParse.visit()
		coverHandle = new TpacHandle(tag: 'cover')
		switemParse.parse(coverHandle, [ '', 'aaa', '', 'bbb', '', '', 'ccc', '' ])
		then:
		coverHandle.lowers.keySet() as List == [ 'chunk:1', 'chunk:2', 'chunk:3' ]
		coverHandle.solvePath('chunk:1').dflt == [ 'aaa' ]
		coverHandle.solvePath('chunk:2').dflt == [ 'bbb' ]
		coverHandle.solvePath('chunk:3').dflt == [ 'ccc' ]
		
		when:
		parseEnclose = new ParseOperator(tag: 'enclose', name:'enc', tagdsl: new SwitemServer().tagdsl.cl('parseOperator'))
		parseEnclose.bgn = '---'
		switemParse << parseEnclose
		switemParse.visitRecursive()
		coverHandle = new TpacHandle(tag: 'cover')
		switemParse.parse(coverHandle, [ 'aaa', '---', 'bbb', '---', '---', 'ccc', '---', 'ddd' ])
		then:
		coverHandle.lowers.keySet() as List == [ 'chunk:1', 'enc:1', 'enc:2', 'chunk:2' ]
		coverHandle.solvePath('chunk:1').dflt == [ 'aaa' ]
		coverHandle.solvePath('enc:1').top == '---'
		coverHandle.solvePath('enc:1').btm == '---'
		coverHandle.solvePath('enc:1').lowers.keySet() as List == [ 'chunk:1' ]
		coverHandle.solvePath('enc:1/chunk:1').dflt == [ 'bbb' ]
		coverHandle.solvePath('enc:2').top == '---'
		coverHandle.solvePath('enc:2').btm == '---'
		coverHandle.solvePath('enc:2').lowers.keySet() as List == [ 'chunk:1' ]
		coverHandle.solvePath('enc:2/chunk:1').dflt == [ 'ccc' ]
		coverHandle.solvePath('chunk:2').dflt == [ 'ddd' ]
	}
}
