


String textOrig = new File("keymaps/Mac OS X.xml").text
//String textNew = new File("keymaps/Mac OS X 10.5+.xml").text

def rootOrig = new XmlSlurper().parse("keymaps/Mac OS X.xml")

println(rootOrig.action[0].@id)
println(rootOrig.action[1].@id)
println(rootOrig.action[rootOrig.childNodes().size()-1].@id)

rootOrig.childNodes[0..3].each { node ->
    printf("   %s\n", node.name())
}