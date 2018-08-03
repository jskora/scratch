<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:template match="keymap">
        keymap.name=<xsl:value-of select="name"/>
        <xsl:for-each select="action">
            action.id=<xsl:value-of select="id"/>
            keyboard-shortcut=<xsl:value-of select="keyboard-shortcut"/>
        </xsl:for-each>
    </xsl:template>
</xsl:stylesheet>