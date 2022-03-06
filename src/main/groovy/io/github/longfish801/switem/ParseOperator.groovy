/*
 * ParseOperator.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.switem

import io.github.longfish801.clmap.ClmapMap
import io.github.longfish801.switem.SwitemConst as cnst
import io.github.longfish801.tpac.TpacHandle
import io.github.longfish801.tpac.tea.TeaHandle

/**
 * parseハンドルの下位ハンドルです。
 * @version 0.1.00 2020/07/10
 * @author io.github.longfish801
 */
class ParseOperator implements TeaHandle {
	/** タグDSL */
	ClmapMap tagdsl
	/** 下位のハンドルに適用する parseハンドル */
	SwitemParse parseRefer
	/** カバーハンドルの通番 */
	int coverNum
	
	/**
	 * このハンドラの妥当性を検証します。
	 */
	@Override
	void validate(){
		validateKeys(cnst.conds.parseCmn)
		ConfigObject conds = tagdsl.solve("map:conds/config:${tag}")?.config()
		if (conds != null) validateKeys(conds)
	}
	
	/**
	 * このハンドラのメンバ変数を初期化します。
	 */
	@Override
	void visit(){
		// referキーから下位のハンドルに適用する parseハンドルを初期化します
		if (map.containsKey('refer')){
			parseRefer = (map.refer == null)? null : map.refer.refer()
		} else {
			parseRefer = upper
		}
		// カバーハンドルの通番を初期化します
		coverNum = 0
		// 初期化のためのクロージャを呼びます
		tagdsl.cl("visit#${tag}")?.call(this)
	}
	
	/**
	 * 行リストから範囲を検出します。
	 * @param lines 行リスト
	 * @return 範囲の行数（範囲外の場合は0）
	 */
	int detectRange(List lines, int lineIdx){
		return tagdsl.cl("detectRange#${tag}").call(this, lines, lineIdx)
	}
	
	/**
	 * 行リストからカバーハンドルを生成します。
	 * @param lines 行リスト
	 * @return カバーハンドル
	 */
	TeaHandle createCoverHandle(List lines){
		TeaHandle coverHandle = new TpacHandle(tag: name, name: "${++ coverNum}")
		tagdsl.cl("createCoverHandle#${tag}").call(this, coverHandle, lines)
		return coverHandle
	}
	
	/**
	 * 行リストを再帰的に解析します。<br/>
	 * 解析結果のテキストハンドルのリストをカバーハンドルに下位要素として追加します。
	 * @param coverHandle カバーハンドル
	 * @param lines 行リスト
	 */
	void parseRecursive(TeaHandle coverHandle, List lines){
		if (parseRefer == null){
			upper.outRange(coverHandle , lines, 0)
		} else {
			parseRefer.parse(coverHandle, lines)
		}
	}
}
