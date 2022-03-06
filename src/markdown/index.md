# switem

[TOC levels=2-6]

## 概要

　テキストのテンプレートを差し替えます。
　テキストを解析、整形して、新たな書式に差し替えるスクリプトを実行します。

　個人が学習のために開発したものです。
　故障対応や問合せ回答などのサポートはしていません。

## 特徴

* テキストを加工するスクリプトを switem記法で記述します。
  switem記法は [tpac](/maven/tpac/)を利用したDSLです。
* テキストの書式を差し替えることを目的としています。
  構造を大きく変えることは困難です。
  たとえば行の集約、並べ替えには対応していません。

　このライブラリの名称は書式の切り替え（Switch Template）に由来しています。

## サンプルコード

　以下に switem文書のサンプルを示します（src/test/resources/sample.tpac）。
　Markdown形式で記述されたテキストをHTML形式に変換します。
　テキストを解析してtpac文書に変換し、それをHTML形式に整形します。

```
#! switem
#> parse
#>> enclose:pre
#-bgn ```
#>> indent:ul
#-bullet - 
#-more   
#> format
#>> replace:pre
#-include pre
World	Groovy
#>> call:pre
#-include pre
#-cover
{ def hndl ->
	hndl.top = '<pre>'
	hndl.btm = '</pre>'
}
#>> call:ul
#-include ul
#-cover
{ def hndl ->
	if (hndl.tagnum == 1) hndl.top = '<ul>'
	if (hndl.tagnum == hndl.tagtotal) hndl.btm = '</ul>'
	hndl.bgn = '<li>'
	hndl.end = '</li>'
	hndl.bullet = (hndl.level == 1)? '' : '  '
	hndl.more = (hndl.level == 1)? '' : '  '
	hndl.nogap = true
}
#>> call:p
#-include doc
#-chunk
{ def hndl ->
	def lines = hndl.dflt.collect()
	lines[0] = "<p>${lines[0]}"
	lines[lines.size() - 1] = "${lines[lines.size() - 1]}</p>"
	return lines
}
#>> call:br
#-chunk
{ def hndl ->
	return hndl.dflt.join("<br/>${System.lineSeparator()}").split(System.lineSeparator())
}
```

　上記の switem文書を読みこんでクロージャを実行し、期待する戻り値を得られるか assertで確認するスクリプトです（src/test/groovy/Sample.groovy）。

```
import io.github.longfish801.switem.SwitemServer

File dir = new File('src/test/resources')
String text = new File(dir, 'target.md').text
String tpac = new File(dir, 'parsed.tpac').text
String html = new File(dir, 'formatted.html').text

try {
	def switem = new SwitemServer().soak(new File('src/test/resources/sample.tpac')).switem
	switem.parsedWriter = new StringWriter()
	assert html == switem.run(text).normalize()
	assert tpac == switem.parsedWriter.toString().normalize()
} catch (exc){
	exc.printStackTrace()
}
```

　switem文書による変換の対象となるテキストです（src/test/resources/target.md）。

``````
This is sample text.
Please read this carefully.

```
println 'Hello, World!'
println 'Hello, Groovy!'
```

- List
  - Elem1
    Elem2
- Map
  - Key
  - Value
``````

　switem文書で上記のテキストを解析した結果であるtpac文書です（src/test/resources/parsed.tpac）。

```
#! doc
#-gap _
#>

#> chunk:1
This is sample text.
Please read this carefully.
#>

#> pre:1
#-top ```
#-btm ```
#>

#>> chunk:1
println 'Hello, World!'
println 'Hello, Groovy!'
#>

#> ul:1
#-bullet - 
#-more   
#>

#>> chunk:1
List
#>

#>> ul:2
#-bullet - 
#-more   
#>

#>>> chunk:1
Elem1
Elem2
#>

#> ul:3
#-bullet - 
#-more   
#>

#>> chunk:1
Map
#>

#>> ul:4
#-bullet - 
#>

#>>> chunk:1
Key
#>

#>> ul:5
#-bullet - 
#>

#>>> chunk:1
Value
#>

#!
```

　switem文書で上記のtpac文書を整形した結果です（src/test/resources/formatted.html）。

```
<p>This is sample text.<br/>
Please read this carefully.</p>

<pre>
println 'Hello, Groovy!'<br/>
println 'Hello, Groovy!'
</pre>

<ul>
<li>List
<ul>
  <li>Elem1<br/>
  Elem2</li>
</ul></li>
<li>Map
<ul>
  <li>Key</li>
  <li>Value</li>
</ul></li>
</ul>
```

　このサンプルコードは build.gradle内の execSamplesタスクで実行しています。

## ドキュメント

* [Groovydoc](groovydoc/)
* [switem記法](notation.html)
* [タグDSL](tagdsl.html)

## GitHubリポジトリ

* [switem](https://github.com/longfish801/switem)

## Mavenリポジトリ

　本ライブラリの JARファイルを [GitHub上の Mavenリポジトリ](https://github.com/longfish801/maven)で公開しています。
　build.gradleの記述例を以下に示します。

```
repositories {
	mavenCentral()
	maven { url 'https://longfish801.github.io/maven/' }
}

dependencies {
	implementation group: 'io.github.longfish801', name: 'switem', version: '0.3.00'
}
```

## 改版履歴

0.0.01
: ドキュメントはmavenリポジトリに出力するよう修正しました。

0.0.02
: 出力処理にnogapキーを追加し、top, btmキーのList指定に対応しました。

0.0.03
: tpac 0.3.12とclmap 0.3.06に対応しました。

0.0.04
: tpac 0.3.13に対応しました。
