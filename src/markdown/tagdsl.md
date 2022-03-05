# タグDSL

[TOC levels=2-6]

## 概要

　parse操作ハンドル、format操作ハンドルはタグDSLで実現しています。
　タグDSLの詳細について以下に記します。

## タグDSLのリソース

　タグDSLの実体はclmap文書に記述されたクロージャの集合です。
　clmapの詳細は[clmap](/maven/clmap/)を参照してください。
　タグDSLのclmap文書はリソース（クラスパス上のファイル）として実装しています。
　リソースパスは「tagdsl」配下です。

　clmap文書は複数あります。
　SwitemServerクラスによるswitem文書の解析時にclmap文書を読みこみます。
　初めに読みこむinterface.tpacに関数名や引数、戻り値を定義しています。
　Javaのインタフェースに相当します。
　switem.tpacには出力に用いるクロージャを定義しています。
　それ以外は操作ハンドルを実装するためのファイルです。
　たとえばformat操作ハンドルのひとつであるcallハンドルは、format-call.tpacで実装しています。

## 操作ハンドルの作成

　操作ハンドルを新しく作成するには以下の作業が必要です。

* interface.tpacなどを参考に操作ハンドルを実装するclmap文書を作成します。
* clmap文書のリソースパスをSwitemServerクラスのメンバ変数List tagdslPathsに追加します。

