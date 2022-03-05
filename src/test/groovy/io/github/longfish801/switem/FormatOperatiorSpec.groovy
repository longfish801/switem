/*
 * FormatOperatiorSpec.groovy
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
 * FormatOperatiorのテスト。
 * @version 0.1.00 2020/07/10
 * @author io.github.longfish801
 */
class FormatOperatiorSpec extends Specification {
	/** FormatOperator */
	@Shared FormatOperator operator
	
	def setup(){
		operator = new FormatOperator(tag: 'call', tagdsl: new SwitemServer().tagdsl.cl('formatOperator'), shell: new GroovyShell())
		SwitemFormat switemFormat = new SwitemFormat(tag: 'format')
		switemFormat << operator
	}
	
	def 'validate'(){
		when:
		operator.include = 'column'
		operator.exclude = 'mask'
		operator.validate()
		then:
		noExceptionThrown()
		
		when:
		operator.include = [ 'column', 'memo' ]
		operator.exclude = [ 'mask', 'secret' ]
		operator.validate()
		then:
		noExceptionThrown()
	}
	
	def 'visit'(){
		when:
		operator.visit()
		then:
		operator.includeList == []
		operator.excludeList == []
		
		when:
		operator.include = 'column'
		operator.exclude = 'mask'
		operator.visit()
		then:
		operator.includeList == [ 'column' ]
		operator.excludeList == [ 'mask' ]
		
		when:
		operator.include = [ 'column', 'memo' ]
		operator.exclude = [ 'mask', 'secret' ]
		operator.visit()
		then:
		operator.includeList == [ 'column', 'memo' ]
		operator.excludeList == [ 'mask', 'secret' ]
	}
	
	def 'isTarget'(){
		given:
		boolean judge
		TeaHandle coverHandle = new TpacHandle(tag: 'cover')
		TeaHandle chunkHandle = new TpacHandle(tag: 'chunk')
		coverHandle << chunkHandle
		
		when: 'includeが空の場合'
		operator.includeList = []
		operator.excludeList = []
		then:
		true == operator.isTarget(coverHandle)
		
		when: 'includeに指定されたタグ名のひとつと一致する場合'
		operator.includeList = [ 'cover', 'cover2' ]
		operator.excludeList = []
		then:
		true == operator.isTarget(coverHandle)
		
		when: 'excludeに指定されたタグ名のひとつと一致する場合'
		operator.includeList = [ 'cover1', 'cover' ]
		operator.excludeList = [ 'cover', 'cover3' ]
		operator.formatTextHandle(coverHandle)
		then:
		false == operator.isTarget(coverHandle)
	}
	
	def 'formatTextHandle'(){
		given:
		operator.coverCl = { def hndl -> return hndl.bgn = '---' }
		operator.chunkCl = { def hndl -> return hndl.dflt.collect { "> ${it}" } }
		TeaHandle coverHandle = new TpacHandle(tag: 'cover')
		TeaHandle chunkHandle = new TpacHandle(tag: 'chunk')
		chunkHandle.dflt = [ 'aaa' ]
		coverHandle << chunkHandle
		
		when:
		operator.includeList = [ 'cover' ]
		operator.excludeList = [ ]
		operator.formatTextHandle(coverHandle)
		then:
		coverHandle.bgn == '---'
		coverHandle.solvePath('chunk').dflt == [ '> aaa' ]
	}
}
