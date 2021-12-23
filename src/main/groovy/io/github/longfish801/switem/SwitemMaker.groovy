/*
 * SwitemMaker.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.switem

import io.github.longfish801.clmap.Clmap
import io.github.longfish801.switem.SwitemConst as cnst
import io.github.longfish801.switem.SwitemMsg as msgs
import io.github.longfish801.tpac.TpacSemanticException
import io.github.longfish801.tpac.tea.TeaDec
import io.github.longfish801.tpac.tea.TeaHandle
import io.github.longfish801.tpac.tea.TeaMaker

/**
 * switem記法の文字列の解析にともない、各要素を生成します。
 * @version 0.1.00 2020/07/10
 * @author io.github.longfish801
 */
class SwitemMaker implements TeaMaker {
	/** SwitemServer */
	SwitemServer server
	
	/**
	 * 宣言を生成します。<br/>
	 * {@link Switem}インスタンスを生成して返します。
	 * @param tag タグ
	 * @param name 名前
	 * @return 宣言
	 */
	@Override
	TeaDec newTeaDec(String tag, String name){
		return new Switem(tagdsl: server.tagdsl.cl('switem'), maker: this)
	}
	
	/**
	 * ハンドルを生成します。
	 * @param tag タグ
	 * @param name 名前
	 * @param upper 上位ハンドル
	 * @return ハンドル
	 * @exception TpacSemanticException タグの親子関係が不正です。
	 */
	@Override
	TeaHandle newTeaHandle(String tag, String name, TeaHandle upper){
		switch (upper.tag) {
			case 'parse':
				if (server.tagdsl.cl("parseOperator/exist#${tag}") == null) throw new TpacSemanticException(String.format(msgs.exc.noTagDslDoc, tag, upper.tag))
				return new ParseOperator(tagdsl: server.tagdsl.cl('parseOperator'))
			case 'format':
				if (server.tagdsl.cl("formatOperator/exist#${tag}") == null) throw new TpacSemanticException(String.format(msgs.exc.noTagDslDoc, tag, upper.tag))
				return new FormatOperator(tagdsl: server.tagdsl.cl('formatOperator'), shell: server.shell)
		}
		if (cnst.hierarchy.get(upper.tag) != null
		 && !cnst.hierarchy.get(upper.tag).contains(tag)){
			throw new TpacSemanticException(String.format(msgs.exc.invalidHierarchy, tag, upper.tag))
		}
		switch (tag) {
			case 'parse': return new SwitemParse(tagdsl: server.tagdsl.cl('parse'))
			case 'format': return new SwitemFormat()
		}
		throw new InternalError(String.format(msgs.exc.invalidTag, tag, upper.tag))
	}
}
