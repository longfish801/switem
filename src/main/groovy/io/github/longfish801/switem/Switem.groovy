/*
 * Switem.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.switem

import groovy.util.logging.Slf4j
import io.github.longfish801.clmap.ClmapMap
import io.github.longfish801.switem.SwitemConst as cnst
import io.github.longfish801.switem.SwitemMsg as msgs
import io.github.longfish801.tpac.TpacDec
import io.github.longfish801.tpac.TpacHandle
import io.github.longfish801.tpac.tea.TeaDec
import io.github.longfish801.tpac.tea.TeaHandle

/**
 * switem文書に基づき文字列を変換します。
 * @author io.github.longfish801
 */
@Slf4j('LOG')
class Switem implements TeaDec {
	/** 解析結果の出力先 */
	Writer parsedWriter
	/** タグDSL */
	ClmapMap tagdsl
	/** SwitemMaker */
	SwitemMaker maker
	
	/**
	 * このハンドラの妥当性を検証します。
	 */
	@Override
	void validate(){
		validateKeys(cnst.conds.switem)
	}
	
	/**
	 * switem文書に基づきファイル内の文字列を変換します。<br/>
	 * 文字コードは環境変数file.encodingで指定された値を使用します。
	 * @param inFile 処理対象ファイル
	 * @param outFile 処理結果出力ファイル
	 * @return 自インスタンス
	 */
	Switem run(File inFile, File outFile){
		return run(inFile, outFile, System.getProperty('file.encoding'))
	}
	
	/**
	 * switem文書に基づきファイル内の文字列を変換します。
	 * @param inFile 処理対象ファイル
	 * @param outFile 処理結果出力ファイル
	 * @param enc Switemを記述したファイルの文字コード
	 * @return 自インスタンス
	 */
	Switem run(File inFile, File outFile, String enc){
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(inFile), enc))
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFile), enc))
		return run(reader, writer)
	}
	
	/**
	 * switem文書に基づき文字列を変換します。
	 * @param text 処理対象文字列
	 * @return 処理結果
	 */
	String run(String text){
		BufferedReader reader = new BufferedReader(new StringReader(text))
		StringWriter writer = new StringWriter();
		run(reader, new BufferedWriter(writer));
		return writer.toString();
	}
	
	/**
	 * switem文書に基づき BufferedReaderから読みこんだ文字列を変換し、BufferdWriterに出力します。<br/>
	 * メンバ変数parsedWriterの指定があればparseハンドルによる解析結果を出力します。
	 * @param reader 処理対象のBufferedReader
	 * @param writer 処理結果のBufferdWriter
	 * @throws SwitemRuntimeException switemスクリプト実行中に問題が起きました。
	 * @return 自インスタンス
	 */
	Switem run(BufferedReader reader, BufferedWriter writer){
		LOG.debug('BGN run: key={}', key)
		try {
			// ハンドルを初期化します
			visitRecursive()
			
			// 解析します
			List lines = reader.readLines()
			TeaDec dec = new TpacDec(tag: 'doc')
			dec.gap = ''
			if (solve('parse') == null){
				// parseタグが未定の場合は追加します
				TeaHandle parseHndl = maker.newTeaHandle('parse', 'dflt', this)
				parseHndl.tag = 'parse'
				parseHndl.name = 'dflt'
				this << parseHndl
				parseHndl.validate()
				parseHndl.visit()
			}
			solve('parse').parse(dec, lines)
			
			// 解析結果を出力します
			if (parsedWriter != null) dec.write(parsedWriter)
			
			// 通し番号を振ります
			numbering(dec)
			
			// 整形します
			solve('format')?.format(dec)
			
			// 出力します
			writer.withWriter { Writer wrtr -> tagdsl.cl('output#dflt').call(wrtr, dec) }
		} catch (exc){
			throw new SwitemRuntimeException(String.format(msgs.exc.faileScript, key), exc)
		}
		LOG.debug('END run: key={}', key)
		return this
	}
	
	/**
	 * カバーハンドル配下の各ハンドルに通し番号などのキーを追加します。
	 * @param coverHandle カバーハンドル
	 */
	static void numbering(TeaHandle coverHandle){
		// 同じタグが連続するときの通し番号、総数のキーを準備します
		List tagNums = []
		List tagTotals = []
		int tagNum = 0
		String preTag = null
		coverHandle.lowers.values().each { def hndl ->
			if (preTag != null && hndl.tag != preTag){
				tagNum.times { tagTotals << tagNum }
				tagNum = 0
			}
			tagNums << ++ tagNum
			preTag = hndl.tag
		}
		
		// 通し番号などのキーを追加します
		tagNum.times { tagTotals << tagNum }
		int num = 1
		coverHandle.lowers.values().each { def hndl ->
			hndl.num = num
			hndl.total = coverHandle.lowers.size()
			hndl.tagnum = tagNums[num - 1]
			hndl.tagtotal = tagTotals[num - 1]
			num ++
		}
		
		// 下位のカバーハンドルにも再帰的に実行します
		coverHandle.lowers.values().each { def hndl ->
			if (hndl.tag != 'chunk') numbering(hndl)
		}
	}
}
