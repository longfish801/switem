/*
 * FormatOperator.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.switem

import groovy.util.logging.Slf4j
import io.github.longfish801.clmap.ClmapMap
import io.github.longfish801.switem.SwitemConst as cnst
import io.github.longfish801.tpac.tea.TeaHandle

/**
 * formatハンドルの下位ハンドルです。
 * @version 0.1.00 2020/07/10
 * @author io.github.longfish801
 */
@Slf4j('LOG')
class FormatOperator implements TeaHandle {
	/** タグDSL */
	ClmapMap tagdsl
	/** 下位のハンドルに適用する formatハンドル */
	SwitemFormat formatRefer
	/** 処理対象タグ一覧 */
	List includeList = []
	/** 処理対象外タグ一覧 */
	List excludeList = []
	/** GroovyShell */
	GroovyShell shell
	
	/**
	 * このハンドラの妥当性を検証します。
	 */
	@Override
	void validate(){
		validateKeys(cnst.conds.formatCmn)
		ConfigObject conds = tagdsl.solve("map:conds/config:${tag}")?.config()
		if (conds != null) validateKeys(conds)
	}
	
	/**
	 * このハンドラのメンバ変数を初期化します。
	 */
	@Override
	void visit(){
		// includeキーから処理対象タグ一覧を初期化します
		if (map.include != null){
			switch (map.include){
				case String: includeList = [ map.include ]; break
				case List: includeList = map.include; break
			}
		}
		// excludeキーから処理対象外タグ一覧を初期化します
		if (map.exclude != null){
			switch (map.exclude){
				case String: excludeList = [ map.exclude ]; break
				case List: excludeList = map.exclude; break
			}
		}
		// 初期化のためのクロージャを呼びます
		tagdsl.cl("visit#${tag}")?.call(this)
	}
	
	/**
	 * include, excludeキーの指定に従って処理対象のカバーハンドルか否か判定を返します。
	 * @param coverHandle カバーハンドル
	 * @return 処理対象のカバーハンドルか否か
	 */
	boolean isTarget(TeaHandle coverHandle){
		return ((includeList.empty || includeList.any { it == coverHandle.tag }) && excludeList.every { it != coverHandle.tag })
	}
	
	/**
	 * ハンドル内のチャンクを整形し、下位ハンドルの整形処理を呼びます。
	 * @param coverHandle カバーハンドル
	 */
	void formatTextHandle(TeaHandle coverHandle){
		// 対象外のカバーハンドルであればなにもしません
		if (!isTarget(coverHandle)) return
		// カバーハンドルを整形します
		tagdsl.cl("formatCoverHandle#${tag}")?.call(this, coverHandle)
		// 下位のチャンクハンドルを整形します
		coverHandle.lowers.values().findAll { it.tag == 'chunk' }.each { TeaHandle hndl ->
			tagdsl.cl("formatChunkHandle#${tag}")?.call(this, hndl)
		}
	}
}
