# switem記法

[TOC levels=2-6]

## 概要

　switem記法は、テキストを解析し整形するスクリプトのための記法です。
　テキストの書式を差し替えるような加工ができます。
　一部の処理に [clmap](/clmap/)で定義したクロージャを使用できます。

　switem記法は tpacを利用したDSLです。tpacの詳細は [tpac](/tpac/)を参照してください。
　以下、tpac記法と重複する説明は省いています。

## 処理の流れ

　switem文書は二種類のハンドル(parse, format)とその配下のハンドルで構成されます。
　対象のテキストを解析(parse)して構造化し、整形(format)した上で再びテキストとして出力します。
　便宜上、対象のテキストから一部を切りとった行の集まりをチャンクと呼びます。

　parseハンドルにはテキストをどう解析するか記述します。
　テキストをチャンクに分割し、チャンクごとに名前を与えます。
　また、チャンク同士にツリー構造を持たせます。

　formatハンドルはテキストをどう整形するか記述します。
　指定した名前のチャンクに対し置換などの文字列加工処理をします。
　最後に各チャンクを連結し、出力します。

## テキストハンドル

　parseハンドルで解析した結果はひとつのtpac文書として保持します。
　このtpac文書の宣言のタグは "doc"です。
　tpac文書を構成する各ハンドルを便宜上「テキストハンドル」と呼びます。
　テキストハンドルには以下の二種類があります。

* チャンクハンドル
	* 解析によってテキストから抽出したチャンクをデフォルトキーに格納します。
	  タグは "chunk"、名前は通番を用います。
* カバーハンドル
	* チャンクハンドルあるいは他のカバーハンドルを下位のハンドルとして保持します。
	  タグは parse操作ハンドルの名前、名前は通番を用います。

## 宣言

　宣言のタグは「switem」です。
　可能な子要素は parse, formatハンドルです。
　以下のキーを指定できます。

* outキー
	* 配下のparseハンドルのoutキーのデフォルト値です。
	  省略時の値は"para"です。

## parseハンドル

　parseハンドルは配下にテキストの解析方法を定義したハンドルを記述します。
　解析時はまず名前を省略した parseハンドルを参照します。
　再帰的な解析をするとき、名前を付与した parseハンドルを呼ぶことができます。
　以下のキーを指定できます。

* outキー
	* parse操作ハンドルすべての解析対象にならなかった行の扱いを指定します。
	  省略時の値は"para"です。
	  以下の値のいずれかを指定できます。
		* para
			* 空行を区切り行としてチャンクを分割し、それぞれチャンクハンドルを作成します。
		* whole
			* 空行を含むすべての行をひとつのチャンクとしてチャンクハンドルを作成します。
		* none
			* チャンクハンドルを作成しません。

## parse操作ハンドル

　parseハンドル配下のハンドルを「parse操作ハンドル」と呼びます。
　parse操作ハンドルはすべて名前が必須です。
　以下のキーを指定できます。

* referキー
	* 下位のカバーハンドルに適用する parseハンドルを指定します。
	  参照を指定してください。
	  省略時は自parseハンドルを用います。
	  下位のカバーハンドルを解析対象外にしたい場合は nullを指定してください。

### onelineハンドル

　onelineハンドルは一行のみ解析します。

　特定の先頭記号で始まる一行、あるいは正規表現とマッチする一行を解析します。

　以下のキーを指定できます。どちらか一方だけを指定してください。
　両方を指定した場合、patternキーは無視されます。どちらも指定しなかった場合は解析エラーとなります。

* bulletキー
	* 先頭記号を指定します。
	  先頭記号を除いた残りの箇所をテキストとみなします。
	  文字列で指定してください。
* patternキー
	* 正規表現を指定します。
	  グループがある場合、1番目のグループをテキストとみなします。
	  グループが無い場合、行全体をテキストとみなします。

　カバーハンドルに以下のキーを保持します。

* bulletキー
	* 先頭記号を保持します。

### encloseハンドル

　encloseハンドルは特定の開始行と終端行とで挟まれた行を解析します。

　まず開始行を探します。
　開始行がみつかったなら、その次行以降から終端行を探します。
　開始行の次行以降と終端行の間に他の開始行があった場合は入れ子と判断し、整合する終端行を探します。
　終端行がみつからなかった場合はエラーとします。

　以下のキーを指定できます。

* bgnキー
	* 開始行を指定します。
	  必須です。
	  文字列あるいは正規表現で指定してください。
* endキー
	* 終端行を指定します。
	  省略時は終端行を開始行と同じ文字列とみなします。
	  文字列あるいは正規表現で指定してください。

　カバーハンドルに以下のキーを保持します。

* topキー
	* 開始行を保持します。
* btmキー
	* 終端行を保持します。

### indentハンドル

　indentハンドルはインデントされた行を解析します。

　先頭記号で行の先頭が開始する行を探します。
　次行以降、同じ先頭記号が続いているか確認します。
　同じ先頭記号が続く行をチャンクとして格納します。

　以下のキーを指定できます。

* bulletキー
	* 先頭記号を指定します。
	  必須です。
	  文字列あるいは正規表現で指定してください。
* moreキー
	* 先頭記号が二行目以降から異なるときに指定します。
	  省略時は bulletキーと同じ値が指定されたものとみなします。
	  文字列あるいは正規表現で指定してください。

　カバーハンドルに以下のキーを保持します。

* bulletキー
	* 先頭記号を保持します。
* moreキー
	* 二行目の先頭記号を保持します。

## formatハンドル

　formatハンドルは配下にテキストの加工方法を定義したハンドルを記述します。
　加工時、まず名前を省略した formatハンドルを参照します。
　下位のチャンクについて加工するとき、名前を付与した formatハンドルを呼ぶことができます。

## format操作ハンドル

　formatハンドル配下のハンドルを「format操作ハンドル」と呼びます。
　format操作ハンドルはすべて名前が必須です。
　以下のキーを指定できます。

* includeキー
	* 処理対象としたいカバーハンドルのタグを記述します。
	  文字列で指定してください。
	  複数指定したい場合はテキストとして改行区切りで指定してください。
* excludeキー
	* 処理対象外とするカバーハンドルのタグを記述します。
	  文字列で指定してください。
	  複数指定したい場合はテキストとして改行区切りで指定してください。

　include, excludeキーで指定されたタグについて、カバーハンドル（チャンクハンドルなら上位のカバーハンドル）のタグが、以下の条件をどちらも満たすときに処理対象となります。

* includeが空、もしくは includeのいずれかひとつでもタグが一致する
* excludeのどれともタグが一致しない

### subformatハンドル

　subformatハンドルは整形に利用するformatハンドルを指定します。
　指定したカバーハンドルの下位のカバーハンドルの整形に用いる formatハンドルを指定します。
　指定がなければsubformatハンドルが属すformatハンドルを利用します。
　複数のsubformatハンドルで対象となるカバーハンドルの場合、それぞれのformatハンドルから整形処理がされてしまうことに注意してください。

　以下のキーを指定できます。

* referキー
	* 下位のカバーハンドルに適用する formatハンドルを指定します。
	  参照を指定してください。
	  必須です。
	  下位のカバーハンドルを整形対象外にしたい場合は nullを指定してください。

### replaceハンドル

　replaceハンドルはチャンク内の固定文字列を置換します。
　処理対象はチャンクハンドルのみです。

　テキストで検索文字列、置換文字列をタブ区切りで指定します。
　改行区切りで検索文字列、置換文字列の組を複数指定できます。
　検索文字列と一致する文字列が複数あった場合、すべて置換します。

　以下は文字列「Hello」を「Goodbye」に、「テスト」を「試験」に置換する例です。

```
#>> replace
Hello	Goodbye
テスト	試験
```

### reprexハンドル

　reprexハンドルは正規表現でチャンク内の文字列を置換します。
　処理対象はチャンクハンドルのみです。

　テキストで検索文字列、置換文字列をタブ区切りで指定します。
　改行区切りで検索文字列、置換文字列の組を複数指定できます。
　検索文字列と一致する文字列が複数あった場合、すべて置換します。
　各行の末尾に改行コードを追加して連結した文字列に対し置換します。
　このため改行を含む正規表現を指定することができます。

　以下は正規表現「H([\w+])」「W([\w+])」とマッチする文字列を「h$1」「w$1」に置換する例です。
　たとえば「Hello World」は「hello world」に置換します。

```
#>> reprex
H([\w+])	h$1
W([\w+])	w$1
```

### callハンドル

　callハンドルはクロージャでチャンクを加工します。
　チャンクハンドル、カバーハンドルどちらも処理対象とします。

　チャンクをクロージャで加工します。
　またチャンクに対応するハンドルの、bgnキー、endキーの値を加工することもできます。

　以下のキーを指定できます。

* coverキー
	* カバーハンドルが保持する値を加工したいとき指定します。
	  テキストでクロージャを指定してください。
	  もしくは参照で clmap文書のクロージャを指定してください。
	  クロージャの引数としてカバーハンドルを渡します。
	  戻り値は不要です。
	  省略時は加工をしません。
* chunkキー
	* チャンクハンドルを加工するためのクロージャを指定します。
	  テキストでクロージャを指定してください。
	  もしくは参照で clmap文書のクロージャを指定してください。
	  クロージャの引数としてチャンクハンドルを渡します。
	  戻り値として加工したチャンク（文字列のリスト）を返してください。
	  省略時は加工をしません。

　以下はタグ名「コラム」のカバーハンドルについて bgnキーを「【コラム：ここから】」に、endキーを「【コラム：ここまで】」に、このカバーハンドル配下のチャンクハンドルについてチャンクの末尾に「written by TARO」という行を追加する例です。

```
#>> call:コラム
#-include コラム
#-cover
{ def hndl ->
	hndl.bgn = '【コラム：ここから】'
	hndl.end = '【コラム：ここまで】'
}
#-chunk
{ def hndl ->
	return [ hndl.dflt, "written by TARO" ].flatten()
}
```

## ナンバリング

　formatハンドルでの整形処理の前に、通し番号などのキーをテキストハンドルに追加します。
　具体的には以下のキーを追加します。

* numキー
	* 同じ親ハンドルに属す下位ハンドルの通し番号（１始まり）を保持します。
* totalキー
	* 同じ親ハンドルに属す下位ハンドルの総数を保持します。
* tagnumキー
	* 同じ親ハンドルに属す下位ハンドルで、同じタグが連続するときの通し番号（１始まり）を保持します。
* tagtotalキー
	* 同じ親ハンドルに属す下位ハンドルで、同じタグが連続するハンドルの総数を保持します。

## 出力

　formatハンドルでの整形処理の後で出力処理をします。

　各チャンクハンドルのデフォルトキーに格納されたチャンクをツリー構造に沿って連結することで生成したテキストを出力します。
　このときチャンクを構成する各行の末尾にはシステム固有の改行コードを付与します。

　カバーハンドルについてキー毎に以下の処理をします。
　キーが未指定ならば、なにもしません。

* bgnキー
	* カバーハンドル内のテキストの先頭行の行頭に記述します。
	  topキーも指定された場合は先頭行（topキー指定値）の次の行を対象にします。
* endキー
	* カバーハンドル内のテキストの終端行の末尾に記述します。
	  btmキーも指定された場合は終端行（btmキー指定値）の前の行を対象にします。
* topキー
	* カバーハンドル内のテキストに先頭行として挿入します。
* btmキー
	* カバーハンドル内のテキストに終端行として挿入します。
* bulletキー
	* カバーハンドル内のテキストの先頭行の行頭に付与します。
* moreキー
	* カバーハンドル内のテキストの二行目以降の行頭に付与します。