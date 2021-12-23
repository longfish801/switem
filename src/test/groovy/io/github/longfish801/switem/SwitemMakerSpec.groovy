/*
 * SwitemMakerSpec.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.switem

import io.github.longfish801.clmap.Clmap
import io.github.longfish801.clmap.ClmapServer
import io.github.longfish801.gonfig.GropedResource
import io.github.longfish801.switem.SwitemMsg as msgs
import io.github.longfish801.tpac.TpacHandle
import io.github.longfish801.tpac.TpacSemanticException
import io.github.longfish801.tpac.tea.TeaHandle
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

/**
 * SwitemMakerのテスト。
 * @version 0.1.00 2020/07/10
 * @author io.github.longfish801
 */
class SwitemMakerSpec extends Specification implements GropedResource {
	/** 自クラス */
	static final Class clazz = SwitemMakerSpec.class
	/** SwitemMaker */
	@Shared SwitemMaker switemMaker
	
	def setup(){
		switemMaker = new SwitemMaker(server: new SwitemServer())
	}
	
	def 'newTeaDec'(){
		expect:
		switemMaker.newTeaDec('swtem', 'test') instanceof Switem
	}
	
	@Unroll
	def 'newTeaHandle'(){
		given:
		Closure getHandle = { String tag, String upperTag ->
			TeaHandle upper = new TpacHandle(tag: upperTag)
			return switemMaker.newTeaHandle(tag, 'some', upper)
		}
		
		expect:
		result.isInstance(getHandle(tag, upperTag)) == true
		
		where:
		tag			| upperTag	|| result
		'parse'		| 'switem'	|| SwitemParse
		'enclose'	| 'parse'	|| ParseOperator
		'indent'	| 'parse'	|| ParseOperator
		'format'	| 'switem'	|| SwitemFormat
		'replace'	| 'format'	|| FormatOperator
		'reprex'	| 'format'	|| FormatOperator
		'call'		| 'format'	|| FormatOperator
	}
	
	def 'newTeaHandle - exception'(){
		given:
		TpacSemanticException exc
		TeaHandle upper
		
		when:
		upper = new TpacHandle(tag: 'switem')
		switemMaker.newTeaHandle('switem', 'some', upper)
		then:
		exc = thrown(TpacSemanticException)
		exc.message == String.format(msgs.exc.invalidHierarchy, 'switem', 'switem')
	}
}
