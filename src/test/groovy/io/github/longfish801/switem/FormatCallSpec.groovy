/*
 * FormatCallSpec.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.switem

import io.github.longfish801.clmap.ClmapMap
import io.github.longfish801.clmap.ClmapServer
import io.github.longfish801.tpac.TpacHandle
import io.github.longfish801.tpac.TpacRefer
import io.github.longfish801.tpac.tea.TeaHandle
import spock.lang.Shared
import spock.lang.Specification

/**
 * callハンドルのテスト。
 * @version 0.1.00 2020/07/10
 * @author io.github.longfish801
 */
class FormatCallSpec extends Specification {
	/** FormatOperator */
	@Shared FormatOperator operator
	
	def setup(){
		operator = new FormatOperator(tag: 'call', tagdsl: new SwitemServer().tagdsl.cl('formatOperator'), shell: new GroovyShell())
	}
	
	def 'visit'(){
		given:
		String clmapScript
		ClmapMap clmap
		
		when:
		operator.tagdsl.cl("visit#call").call(operator)
		then:
		operator.coverCl == null
		operator.chunkCl == null
		
		when:
		operator.cover = [ '{ String name -> return "Hello ${name}" }' ]
		operator.chunk = [ '{ String name -> return "Bye ${name}" }' ]
		operator.tagdsl.cl("visit#call").call(operator)
		then:
		operator.coverCl.call('Mike') == 'Hello Mike'
		operator.chunkCl.call('Taro') == 'Bye Taro'
		
		when:
		clmapScript = '''\
			#! clmap
			#> map
			#>> args
				String name
			#>> closure:hello
				return "Hello ${name}"
			#>> closure:bye
				return "Bye ${name}"
			'''.stripIndent()
		clmap = new ClmapServer().soak(clmapScript).cl('/dflt/dflt')
		operator.cover = TpacRefer.newInstance(clmap, 'closure:hello')
		operator.chunk = TpacRefer.newInstance(clmap, 'closure:bye')
		operator.tagdsl.cl("visit#call").call(operator)
		then:
		operator.coverCl.call('Becky') == 'Hello Becky'
		operator.chunkCl.call('Kumi') == 'Bye Kumi'
	}
	
	def 'formatTextHandle'(){
		given:
		TeaHandle chunkHandle
		TeaHandle coverHandle
		
		when:
		chunkHandle = new TpacHandle(tag: 'chunk', name: '1')
		chunkHandle.dflt = [ 'a', 'b', 'c' ]
		coverHandle = new TpacHandle(tag: 'test')
		coverHandle << chunkHandle
		operator.formatTextHandle(coverHandle)
		then:
		coverHandle.solvePath('chunk:1').dflt == [ 'a', 'b', 'c' ]
		
		when:
		chunkHandle = new TpacHandle(tag: 'chunk', name: '1')
		chunkHandle.dflt = [ 'a', 'b', 'c' ]
		coverHandle = new TpacHandle(tag: 'test')
		coverHandle << chunkHandle
		operator.chunkCl = { def hndl -> return [ hndl.dflt.join("-") ] }
		operator.formatTextHandle(coverHandle)
		then:
		coverHandle.solvePath('chunk:1').dflt == [ 'a-b-c' ]
		
		when:
		coverHandle = new TpacHandle(tag: 'some')
		operator.coverCl = { def hndl -> hndl.some = 'buff' }
		operator.formatTextHandle(coverHandle)
		then:
		coverHandle.some == 'buff'
	}
}
