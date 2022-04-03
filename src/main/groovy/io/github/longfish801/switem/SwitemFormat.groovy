/*
 * SwitemFormat.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.switem

import groovy.util.logging.Slf4j
import io.github.longfish801.switem.SwitemConst as cnst
import io.github.longfish801.tpac.tea.TeaHandle

/**
 * formatハンドルです。
 * @author io.github.longfish801
 */
@Slf4j('LOG')
class SwitemFormat implements TeaHandle {
	/**
	 * チャンクを整形します。
	 * @param coverHandle カバーハンドル
	 */
	void format(TeaHandle coverHandle){
		// parseタグの下位ハンドルの記述に基づきカバーハンドルを整形します
		lowers.values().findAll { it.tag != 'subformat' }.each { def operator ->
			(operator as FormatOperator).formatTextHandle(coverHandle)
		}
		
		// 下位のカバーハンドルについて再帰的に整形処理を呼びます
		coverHandle.lowers.values().findAll { it.tag != 'chunk' }.each { TeaHandle hndl ->
			List formatRefers = lowers.values().findAll {
				it.tag == 'subformat' && it.isTarget(coverHandle)
			}.collect { it.formatRefer }
			if (formatRefers.size() == 0) formatRefers << this
			formatRefers.each { it?.format(hndl) }
		}
	}
}
