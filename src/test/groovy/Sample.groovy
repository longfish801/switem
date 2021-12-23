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
