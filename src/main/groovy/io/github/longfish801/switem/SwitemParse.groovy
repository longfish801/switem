/*
 * SwitemParse.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.switem

import io.github.longfish801.clmap.ClmapMap
import io.github.longfish801.switem.SwitemConst as cnst
import io.github.longfish801.switem.SwitemMsg as msgs
import io.github.longfish801.tpac.TpacHandle
import io.github.longfish801.tpac.tea.TeaHandle

/**
 * parseハンドルです。
 * @version 0.1.00 2020/07/10
 * @author io.github.longfish801
 */
class SwitemParse implements TeaHandle {
	/** タグDSL */
	ClmapMap tagdsl
	
	/**
	 * このハンドラの妥当性を検証します。
	 */
	@Override
	void validate(){
		validateKeys(cnst.conds.parse)
	}
	
	/**
	 * このハンドラのメンバ変数を初期化します。
	 */
	@Override
	void visit(){
		// outキーが未指定ならばswitemハンドルのoutキーの値を設定します
		if (map.out == null) map.out = upper.out
	}
	
	/**
	 * 行リストを解析してテキストハンドルのリストに変換します。
	 * @param lines 解析対象の行リスト
	 * @return テキストハンドルのリスト
	 * @exception ParseException 範囲の行数が大きすぎます。
	 */
	void parse(TeaHandle coverHandle, List lines){
		// 範囲を取得します
		List ranges = []
		for (int lineIdx = 0; lineIdx < lines.size(); lineIdx ++){
			for (String opeKey in lowers.keySet()){
				int size = lowers[opeKey].detectRange(lines, lineIdx)
				if (size > 0){
					ranges << [
						'lineIdx': lineIdx,
						'size': size,
						'opeKey': opeKey
					]
					lineIdx += size
					if (lineIdx > lines.size()){
						throw new ParseException(String.format(msgs.exc.invalidRangeSize, ranges.last().lineIdx, size, opeKey))
					}
					lineIdx --
					break
				}
			}
		}
		
		// 範囲内/外の行をテキストハンドルに変換します
		int curIdx = 0
		int chunkNum = 0
		List handles = []
		ranges.each { Map range ->
			// 範囲外の行をチャンクハンドルに変換します
			if (range.lineIdx > curIdx){
				chunkNum = outRange(coverHandle , lines.subList(curIdx, range.lineIdx).collect(), chunkNum)
			}
			// 範囲内の行をカバーハンドルに変換します
			coverHandle << lowers[range.opeKey].createCoverHandle(lines.subList(range.lineIdx, range.lineIdx + range.size).collect())
			curIdx = range.lineIdx + range.size
		}
		if (curIdx < lines.size()){
			// 末尾にある範囲外の行をチャンクハンドルに変換します
			outRange(coverHandle , lines.subList(curIdx, lines.size()).collect(), chunkNum)
		}
	}
	
	/**
	 * すべての操作ハンドルで解析の範囲外となった行を処理します。
	 * @param coverHandle 親となるカバーハンドル
	 * @param lines 解析の範囲外となった行リスト
	 * @param chunkNum チャンクの通し番号
	 * @return チャンクの通し番号（チャンクハンドルを作成するごとにインクリメント）
	 * @exception ParseException outキーの値が不正です。
	 */
	int outRange(TeaHandle coverHandle, List lines, int chunkNum){
		def cl = tagdsl.cl("outRange#${map.out}")
		if (cl == null) throw new ParseException(String.format(msgs.exc.invalidOutRange, map.out))
		return cl.call(coverHandle, lines, chunkNum)
	}
}
