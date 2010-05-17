<!--
IMS Metadata to OAI DC crosswalk

Implementation of the Dublin Core Mappings from the 
IMS Learning Resource Meta-Data Best Practice and Implementation Guide,
Version 1.2.1 Final Specification
http://www.imsglobal.org/metadata/imsmdv1p2p1/imsmd_bestv1p2p1.html#1242547

Possible divergences from the IMS Guide:
1. String comparisons are case insensitive, namely "Author", "Publisher", and
   "IsBasedOn"
2. imsmd:resource/imsmd:identifier maps to oai_dc:source
   The guide merely states that resource should map to source, but
   does not specify if the mapping should be to one or all of 
   resource/identifier, resource/description, or resource/catalogentry
3. Similarly, the mapping from resource to relation is ambiguous

Copyright (c) 2010 MediaShelf, LLC http://www.yourmediashelf.com/
-->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  version="1.0" 
  xmlns:imsmd="http://www.imsglobal.org/xsd/imsmd_rootv1p2p1" 
  xmlns:dc="http://purl.org/dc/elements/1.1/"
  xmlns:oai_dc="http://www.openarchives.org/OAI/2.0/oai_dc/"
  exclude-result-prefixes="dc oai_dc">
  <xsl:output method="xml" indent="yes" encoding="UTF-8" />

  <xsl:template match="imsmd:lom">
    <oai_dc:dc xmlns:dc="http://purl.org/dc/elements/1.1/"
      xmlns:oai_dc="http://www.openarchives.org/OAI/2.0/oai_dc/"
      xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
      xsi:schemaLocation="http://www.openarchives.org/OAI/2.0/oai_dc/ http://www.openarchives.org/OAI/2.0/oai_dc.xsd">

      <xsl:apply-templates />
    </oai_dc:dc>
  </xsl:template>

  <xsl:template name="title"
    match="//imsmd:general/imsmd:title/imsmd:langstring">
    <dc:title>
      <xsl:value-of select="." />
    </dc:title>
  </xsl:template>

  <xsl:template name="creator">
    <dc:creator>
      <xsl:value-of select="./imsmd:centity/imsmd:vcard" />
    </dc:creator>
  </xsl:template>

  <xsl:template name="subject"
    match="//imsmd:general/imsmd:keyword/imsmd:langstring">
    <xsl:for-each select=".">
      <dc:subject>
        <xsl:value-of select="." />
      </dc:subject>
    </xsl:for-each>
  </xsl:template>

  <xsl:template name="description"
    match="//imsmd:general/imsmd:description/imsmd:langstring">
    <dc:description>
      <xsl:value-of select="." />
    </dc:description>
  </xsl:template>

  <xsl:template name="publisher">
    <dc:publisher>
      <xsl:value-of select="./imsmd:centity/imsmd:vcard" />
    </dc:publisher>
  </xsl:template>

  <xsl:template name="contributor">
    <dc:contributor>
      <xsl:value-of select="./imsmd:centity/imsmd:vcard" />
    </dc:contributor>
  </xsl:template>

  <xsl:template name="date">
    <dc:date>
      <xsl:value-of select="./imsmd:date/imsmd:datetime" />
    </dc:date>
  </xsl:template>

  <xsl:template name="type"
    match="//imsmd:educational/imsmd:learningresourcetype/imsmd:value/imsmd:langstring">
    <dc:type>
      <xsl:value-of select="." />
    </dc:type>
  </xsl:template>

  <xsl:template name="format" match="//imsmd:technical/imsmd:format">
    <dc:format>
      <xsl:value-of select="." />
    </dc:format>
  </xsl:template>

  <xsl:template name="identifier"
    match="//imsmd:general/imsmd:catalogentry/imsmd:entry/imsmd:langstring">
    <dc:identifier>
      <xsl:value-of select="." />
    </dc:identifier>
  </xsl:template>

  <xsl:template name="source">
    <xsl:if test="./imsmd:resource/imsmd:identifier">
      <dc:source>
        <xsl:value-of select="./imsmd:resource/imsmd:identifier" />
      </dc:source>
    </xsl:if>
  </xsl:template>

  <xsl:template name="language" match="//imsmd:general/imsmd:language">
    <dc:language>
      <xsl:value-of select="." />
    </dc:language>
  </xsl:template>

  <xsl:template name="relation">
    <xsl:param name="rel" />
    <dc:relation>
      <xsl:value-of select="$rel" />
    </dc:relation>
  </xsl:template>

  <xsl:template name="coverage"
    match="//imsmd:general/imsmd:coverage/imsmd:langstring">
    <dc:coverage>
      <xsl:value-of select="." />
    </dc:coverage>
  </xsl:template>

  <xsl:template name="rights"
    match="//imsmd:rights/imsmd:description/imsmd:langstring">
    <dc:rights>
      <xsl:value-of select="." />
    </dc:rights>
  </xsl:template>

  <xsl:template match="//imsmd:lifecycle/imsmd:contribute">
    <xsl:choose>
      <xsl:when
        test="translate(./imsmd:role/imsmd:value/imsmd:langstring, $lc, $uc)='AUTHOR'">
        <xsl:call-template name="creator" />
      </xsl:when>
      <xsl:when
        test="translate(./imsmd:role/imsmd:value/imsmd:langstring, $lc, $uc)='PUBLISHER'">
        <xsl:call-template name="publisher" />
        <xsl:call-template name="date" />
      </xsl:when>
      <xsl:otherwise>
        <xsl:call-template name="contributor" />
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="//imsmd:relation">
    <xsl:if
      test="translate(./imsmd:kind/imsmd:value/imsmd:langstring, $lc, $uc)='ISBASEDON'">
      <xsl:call-template name="source" />
    </xsl:if>
    <xsl:if test="./imsmd:resource/imsmd:identifier">
      <xsl:call-template name="relation">
        <xsl:with-param name="rel"
          select="./imsmd:resource/imsmd:identifier" />
      </xsl:call-template>
    </xsl:if>
    <xsl:if test="./imsmd:kind/imsmd:value/imsmd:langstring">
      <xsl:call-template name="relation">
        <xsl:with-param name="rel"
          select="./imsmd:kind/imsmd:value/imsmd:langstring" />
      </xsl:call-template>
    </xsl:if>
  </xsl:template>

  <!-- override default template for text nodes and output nothing -->
  <xsl:template match="text()" />

  <!--  helpers to workaround case sensitive comparisons -->
  <xsl:variable name="uc" select="'ABCDEFGHIJKLMNOPQRSTUVWXYZ'" />
  <xsl:variable name="lc" select="'abcdefghijklmnopqrstuvwxyz'" />
  
</xsl:stylesheet>
