/*
 * SwitemSpec.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.switem

import groovy.util.logging.Slf4j
import io.github.longfish801.gonfig.GropedResource
import io.github.longfish801.tpac.TpacHandle
import io.github.longfish801.tpac.tea.TeaHandle
import spock.lang.Specification
import spock.lang.Unroll

/**
 * Switemのテスト。
 * @author io.github.longfish801
 */
@Slf4j('LOG')
class SwitemSpec extends Specification implements GropedResource {
	/** 自クラス */
	static final Class clazz = SwitemSpec.class
	
	def 'numbering'(){
		given:
		TeaHandle coverHandle
		TeaHandle coverHandle1
		TeaHandle coverHandle2
		TeaHandle coverHandle11
		TeaHandle chunkHandle1
		TeaHandle chunkHandle2
		TeaHandle chunkHandle3
		TeaHandle chunkHandle11
		TeaHandle chunkHandle12
		
		when:
		coverHandle = new TpacHandle(tag: 'cover')
		chunkHandle1 = new TpacHandle(tag: 'chunk', name: '1')
		coverHandle << chunkHandle1
		Switem.numbering(coverHandle)
		then:
		coverHandle.solve('chunk:1').tag == 'chunk'
		coverHandle.solve('chunk:1').num == 1
		coverHandle.solve('chunk:1').total == 1
		coverHandle.solve('chunk:1').tagnum == 1
		coverHandle.solve('chunk:1').tagtotal == 1
		
		when:
		coverHandle = new TpacHandle(tag: 'cover')
		coverHandle1 = new TpacHandle(tag: 'cover', name: '1')
		coverHandle2 = new TpacHandle(tag: 'cover', name: '2')
		coverHandle11 = new TpacHandle(tag: 'cover', name: '11')
		chunkHandle1 = new TpacHandle(tag: 'chunk', name: '1')
		chunkHandle2 = new TpacHandle(tag: 'chunk', name: '2')
		chunkHandle3 = new TpacHandle(tag: 'chunk', name: '3')
		chunkHandle11 = new TpacHandle(tag: 'chunk', name: '11')
		chunkHandle12 = new TpacHandle(tag: 'chunk', name: '12')
		coverHandle << coverHandle1
		coverHandle << chunkHandle1
		coverHandle << chunkHandle2
		coverHandle << coverHandle2
		coverHandle << chunkHandle3
		coverHandle1 << chunkHandle11
		coverHandle1 << chunkHandle12
		coverHandle1 << coverHandle11
		Switem.numbering(coverHandle)
		then:
		coverHandle.solve('cover:1').num == 1
		coverHandle.solve('cover:1').total == 5
		coverHandle.solve('cover:1').tagnum == 1
		coverHandle.solve('cover:1').tagtotal == 1
		coverHandle.solve('chunk:1').num == 2
		coverHandle.solve('chunk:1').total == 5
		coverHandle.solve('chunk:1').tagnum == 1
		coverHandle.solve('chunk:1').tagtotal == 2
		coverHandle.solve('chunk:2').num == 3
		coverHandle.solve('chunk:2').total == 5
		coverHandle.solve('chunk:2').tagnum == 2
		coverHandle.solve('chunk:2').tagtotal == 2
		coverHandle.solve('cover:2').num == 4
		coverHandle.solve('cover:2').total == 5
		coverHandle.solve('cover:2').tagnum == 1
		coverHandle.solve('cover:2').tagtotal == 1
		coverHandle.solve('chunk:3').num == 5
		coverHandle.solve('chunk:3').total == 5
		coverHandle.solve('chunk:3').tagnum == 1
		coverHandle.solve('chunk:3').tagtotal == 1
		coverHandle.solve('cover:1/chunk:11').num == 1
		coverHandle.solve('cover:1/chunk:11').total == 3
		coverHandle.solve('cover:1/chunk:11').tagnum == 1
		coverHandle.solve('cover:1/chunk:11').tagtotal == 2
		coverHandle.solve('cover:1/chunk:12').num == 2
		coverHandle.solve('cover:1/chunk:12').total == 3
		coverHandle.solve('cover:1/chunk:12').tagnum == 2
		coverHandle.solve('cover:1/chunk:12').tagtotal == 2
		coverHandle.solve('cover:1/cover:11').num == 3
		coverHandle.solve('cover:1/cover:11').total == 3
		coverHandle.solve('cover:1/cover:11').tagnum == 1
		coverHandle.solve('cover:1/cover:11').tagtotal == 1
	}
	
	@Unroll
	def 'run - parse'(){
		given:
		Closure getParsed = { ->
			Switem switem = new SwitemServer().soak(grope("${tag}/${detail}/script.tpac")).switem
			switem.parsedWriter = new StringWriter()
			switem.run(toString("${tag}/${detail}/target.txt"))
			return switem.parsedWriter.toString().normalize()
		}
		
		expect:
		getParsed.call() == toString("${tag}/${detail}/parsed.tpac")
		
		where:
		tag	| detail
		'parse'	| 'switem01'	// parse, formatなしの最小スクリプト
		'parse'	| 'switem02'	// outキー指定
		'parse'	| 'parse01'	// parseタグのみ
		'parse'	| 'parse02'	// referキー指定
		'parse'	| 'parse03'	// outキー指定
		'parse'	| 'oneline01'	// oneline bulletキー指定
		'parse'	| 'oneline02'	// oneline patternキー指定
		'parse'	| 'enclose01'	// enclose 最小
		'parse'	| 'enclose02'	// enclose endキー指定
		'parse'	| 'enclose03'	// enclose bgn, endキーを正規表現で指定、入れ子あり
		'parse'	| 'indent01'	// indent 最小
		'parse'	| 'indent02'	// indent  moreキー指定
		'parse'	| 'indent03'	// indent  bullet,moreキーを正規表現で指定、複数項目
		'parse'	| 'indent04'	// indent  bullet,moreキーを正規表現で指定、入れ子あり
		'parse'	| 'refer01'	// referキー指定
	}
	
	@Unroll
	def 'run - format'(){
		given:
		Closure getResult = { ->
			Switem switem = new SwitemServer().soak(grope("${tag}/${detail}/script.tpac")).switem
			switem.parsedWriter = new StringWriter()
			String result = switem.run(toString("${tag}/${detail}/target.txt"))
			LOG.info("${tag} ${detail} : ${switem.parsedWriter.toString()}")
			return result.normalize()
		}
		
		expect:
		getResult.call() == toString("${tag}/${detail}/result.txt")
		
		where:
		tag	| detail
		'format'	| 'switem01'	// parse, formatなしの最小スクリプト
		'format'	| 'format01'	// formatタグのみ
		'format'	| 'replace01'	// replace 最小
		'format'	| 'reprex01'	// reprex 最小
		'format'	| 'call01'		// call 最小
		'format'	| 'include01'	// include String指定
		'format'	| 'include02'	// include List指定
		'format'	| 'exclude01'	// exclude String指定
		'format'	| 'exclude02'	// exclude List指定
		'format'	| 'subformat01'	// subformat
		'format'	| 'indent01'	// 箇条書きの整形
		'format'	| 'indent02'	// 複雑な箇条書きの整形
		'format'	| 'indent03'	// tpp, btm List指定
	}
}
