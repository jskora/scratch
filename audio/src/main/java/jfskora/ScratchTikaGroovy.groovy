package jfskora

import org.apache.tika.io.TikaInputStream
import org.apache.tika.metadata.Metadata
import org.apache.tika.parser.AutoDetectParser
import org.apache.tika.parser.ParseContext
import org.apache.tika.sax.BodyContentHandler
import org.apache.tika.sax.ToXMLContentHandler

File dir = new File((String)System.getProperty("user.dir"))
files = dir.listFiles()

files.each() { File fn ->
    println "------------------------------------------------------------"
    println fn

    metadata = new Metadata()
    metadata.set(Metadata.RESOURCE_NAME_KEY, fn.getName())
    context = new ParseContext()

    src = new TikaInputStream(fn)
    handler = new BodyContentHandler(new ToXMLContentHandler())
    parser = new AutoDetectParser()

    parser.parse(src, handler, metadata, context)
    mimetype = ("Content-Type" in metadata.names()) ? metadata.get("Content-Type") : "unknown"
    println fn.getName() + "\n   mimetype: " + mimetype

    metadata.names().each() { String key ->
        println "   " + key + ": " + (String) metadata.get(key)
    }

}

print "done"