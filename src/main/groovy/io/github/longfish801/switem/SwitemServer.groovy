/*
 * SwitemServer.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.switem

import groovy.util.logging.Slf4j
import io.github.longfish801.clmap.Clmap
import io.github.longfish801.clmap.ClmapMaker
import io.github.longfish801.clmap.ClmapServer
import io.github.longfish801.gonfig.GropedResource
import io.github.longfish801.switem.SwitemConst as cnst
import io.github.longfish801.tpac.tea.TeaServer
import io.github.longfish801.tpac.tea.TeaMaker

/**
 * switem文書を保持します。
 * @version 0.1.00 2020/07/10
 * @author io.github.longfish801
 */
@Slf4j('LOG')
class SwitemServer implements TeaServer, GropedResource {
	/** 自クラス */
	static final Class clazz = SwitemServer.class
	/** GroovyShell */
	GroovyShell shell = new GroovyShell()
	/** タグDSL定義ファイルへのリソースパス一覧 */
	List tagdslPaths = []
	/** タグDSLのClmap */
	Clmap tagdslClmap
	
	/**
	 * タグDSLを返します。<br/>
	 * 定数tagdslおよびメンバ変数tagdslPathsに定義された
	 * タグDSL定義ファイルへのパス上にあるファイルを読みこみます。
	 * @return タグDSL
	 */
	Clmap getTagdsl(){
		if (tagdslClmap != null) return tagdslClmap
		ClmapServer server = new ClmapServer()
		(cnst.tagdsl + tagdslPaths).each {
			LOG.debug('soak for tagdsl:path={}', it)
			server.soak(grope(it))
		}
		return server.cl('/tagdsl')
	}
	
	/**
	 * 宣言のタグに対応する生成器を返します。<br/>
	 * switemタグに対し {@link SwitemMaker}のインスタンスを生成して返します。<br/>
	 * clmapタグに対し {@link ClmapMaker}のインスタンスを生成して返します。<br/>
	 * それ以外はオーバーライド元のメソッドの戻り値を返します。
	 * @param tag 宣言のタグ
	 * @return TeaMaker
	 */
	@Override
	TeaMaker newMaker(String tag){
		if (tag == 'switem') return new SwitemMaker(server: this)
		if (tag == 'clmap') return new ClmapMaker()
		return TeaServer.super.newMaker(tag)
	}
}
