<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
 xmlns:lom="http://ltsc.ieee.org/xsd/LOM"
 xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:oai_dc="http://www.openarchives.org/OAI/2.0/oai_dc/"
 exclude-result-prefixes="dc oai_dc">
    <xsl:output method="xml" indent="yes" encoding="UTF-8"/>
    
    <xsl:template match="oai_dc:dc">
        <lom:lom xmlns:lom="http://ltsc.ieee.org/xsd/LOM"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://ltsc.ieee.org/xsd/LOM http://ltsc.ieee.org/xsd/lomv1.0/lom.xsd">
            <lom:general>
                <!-- *** description *** -->
                <xsl:if test="dc:description">
                    <lom:description>
                        <lom:string>
                            <xsl:value-of select="dc:description"/>
                        </lom:string>
                    </lom:description>
                </xsl:if>
                <!-- *** keywords *** -->
                <xsl:for-each select="dc:subject">
                    <lom:keyword>
                        <lom:string>
                            <xsl:value-of select="."/>
                        </lom:string>
                    </lom:keyword>
                </xsl:for-each>
                <!-- *** language *** -->
                <xsl:if test="dc:language">
                    <lom:language>
                        <xsl:value-of select="dc:language"/>
                    </lom:language>
                </xsl:if>
                <!-- *** title *** -->
                <xsl:if test="dc:title">
                    <lom:title>
                        <lom:string>
                            <xsl:value-of select="dc:title"/>
                        </lom:string>
                    </lom:title>
                </xsl:if>
                
                
            </lom:general>
            
            <lom:lifeCycle>
                <!-- *** authors *** -->
                <xsl:for-each select="dc:creator">
                    <lom:contribute>
                        <lom:role>
                            <lom:source>
                                LOMv1.0
                            </lom:source>
                            <lom:value>
                                author
                            </lom:value>
                        </lom:role>
                        <lom:entity>
                            BEGIN:VCARD
                            FN:<xsl:value-of select="."/>
                            END:VCARD
                        </lom:entity>
                    </lom:contribute>
                </xsl:for-each>
                <!-- *** publishers *** -->
                <xsl:if test="dc:publisher">
                    <lom:contribute>
                        <lom:role>
                            <lom:source>
                                LOMv1.0
                            </lom:source>
                            <lom:value>
                                publisher
                            </lom:value>
                        </lom:role>
                        <lom:entity>
                            BEGIN:VCARD
                            ORG:<xsl:value-of select="dc:publisher"/>
                            END:VCARD
                        </lom:entity>
                        <lom:date>
                            <lom:dateTime>
                                <xsl:value-of select="dc:date"/>
                            </lom:dateTime>
                        </lom:date>
                    </lom:contribute>
                </xsl:if>
            </lom:lifeCycle>
            
            <xsl:if test="dc:type">
                <lom:educational>
                    <lom:learningResourceType>
                        <lom:source>DCMIType</lom:source>
                        <lom:value>
                            <xsl:value-of select="dc:type"/>
                        </lom:value>
                    </lom:learningResourceType>
                </lom:educational>
            </xsl:if>
            
            <lom:technical>
                <xsl:if test="dc:format">
                    <lom:location>
                        <xsl:value-of select="substring-after(dc:format, ' ')"/>
                    </lom:location>
                </xsl:if>
                <xsl:if test="dc:identifier">
                    <lom:location>
                        <xsl:value-of select="dc:identifier"/>
                    </lom:location>
                </xsl:if>
                <xsl:if test="dc:format">
                    <lom:format>
                        <xsl:value-of select="substring-before(dc:format, ' ')"/>
                    </lom:format>
                </xsl:if>
            </lom:technical>
            
            <xsl:if test="dc:rights">
                <lom:rights>
                    <lom:description>
                        <lom:string>
                            <xsl:value-of select="dc:rights"/>
                        </lom:string>
                    </lom:description>
                </lom:rights>
            </xsl:if>
        </lom:lom>
    </xsl:template>
</xsl:stylesheet>
