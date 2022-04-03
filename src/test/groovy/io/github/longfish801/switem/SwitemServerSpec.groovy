/*
 * SwitemServerSpec.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.switem

import groovy.util.logging.Slf4j
import io.github.longfish801.clmap.Clmap
import io.github.longfish801.clmap.ClmapMaker
import io.github.longfish801.tpac.TpacMaker
import spock.lang.Specification
import spock.lang.Unroll

/**
 * SwitemServerのテスト。
 * @author io.github.longfish801
 */
@Slf4j('LOG')
class SwitemServerSpec extends Specification {
	def 'getTagdsl'(){
		given:
		Clmap tagdsl
		StringWriter writer
		
		when:
		tagdsl = new SwitemServer().tagdsl
		writer = new StringWriter()
		tagdsl.write(writer)
		LOG.debug('---BGN TAGDSL---')
		LOG.debug(writer.toString())
		LOG.debug('---END TAGDSL---')
		
		then:
		noExceptionThrown()
		tagdsl.lowers.keySet() as List == ['dec:dflt', 'map:switem', 'map:parse', 'map:parseOperator', 'map:formatOperator']
	}
	
	@Unroll
	def 'newMaker'(){
		given:
		SwitemServer switemServer = new SwitemServer()
		
		expect:
		result.isInstance(switemServer.newMaker(tag)) == true
		
		where:
		tag			|| result
		'switem'	|| SwitemMaker
		'clmap'		|| ClmapMaker
		'tpac'		|| TpacMaker
	}
}
