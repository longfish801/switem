# switem

## Overview

Switch template of text.  
Execute a script, which parse, format, and switch to new form for text.

This is individual development, for self-learning.  
No support such as troubleshooting, answering inquiries, and so on.

## Features

* Describe text processing scripts with switem notation.  
  The switem notation is a DSL using [tpac](/tpac/).

* The purpose is to replace the formatting of the text.  
  It is difficult to change the structure significantly.  
  For example, aggregation and sorting of rows are not supported.

The name of this library comes from "SWItch TEMplate".

## Sample Code

Here is a sample switem document (src/test/resources/sample.tpac).  
Convert text written in Markdown format to HTML format.  
It parses text into a tpac document and formats it into HTML format.

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
	hndl.gap = ''
	hndl.lowers.values().findAll { it.tag == 'chunk' }.each { it.gap = '' }
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

Here is a script that reads the above switem document, and make sure to get the expected return value with assert (src/test/groovy/Sample.groovy).

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

Target text for conversion by switem document (src/test/resources/target.md).

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

Here is a tpac document that parse the above text with switem document (src/test/resources/parsed.tpac).

```
#! doc
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

Result of formatting the above tpac document with switem document (src/test/resources/formatted.html).

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

This sample code is executed in the execSamples task, see build.gradle.

## Next Step

Please see the [documents](https://longfish801.github.io/switem/) for more detail.

