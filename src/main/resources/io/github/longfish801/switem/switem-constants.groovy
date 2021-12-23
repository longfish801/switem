import io.github.longfish801.tpac.TpacRefer
import java.util.regex.Pattern

// タグDSL
tagdsl = [
	'tagdsl/interface.tpac',
	'tagdsl/switem.tpac',
	'tagdsl/parse.tpac',
	'tagdsl/parse-oneline.tpac',
	'tagdsl/parse-enclose.tpac',
	'tagdsl/parse-indent.tpac',
	'tagdsl/format-subformat.tpac',
	'tagdsl/format-call.tpac',
	'tagdsl/format-replace.tpac',
	'tagdsl/format-reprex.tpac'
]

// タグ間の親子関係（キーが親タグ、値は可能な子タグのリスト）
// 操作ハンドルのタグについてはtagdslから判定します
hierarchy {
	switem = [ 'parse', 'format' ]
}

// GroovyShell#evaluate実行時のファイル名
evalFname {
	cover = '%s_cover.groovy'
	chunk = '%s_chunk.groovy'
}

// 各ハンドルのキーの条件
conds {
	switem {
		out {
			dflt = 'para'
			types = [ String ]
		}
	}
	parse {
		out {
			types = [ String ]
		}
	}
	parseCmn {
		refer {
			types = [ TpacRefer, null ]
		}
	}
	formatCmn {
		include {
			types = [ String, List ]
		}
		exclude {
			types = [ String, List ]
		}
	}
}
