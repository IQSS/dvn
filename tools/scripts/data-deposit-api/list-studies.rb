#!/usr/bin/env ruby
require "rexml/document"
include REXML
service_document_xml = `tools/scripts/data-deposit-api/test-service-document`
#puts service_document_xml
sd = Document.new service_document_xml
#deposit_target = sd.root.elements["workspace"].elements["collection"].attributes["href"]
#deposit_target = XPath.first( sd, "/service/workspace/collection/@href" )
deposit_target = XPath.first(sd, "//collection/@href")

feed_of_studies_xml = `tools/scripts/data-deposit-api/test-collection-get #{deposit_target}`
feed = Document.new feed_of_studies_xml
XPath.each( feed, "//entry" ) { |entry|
    #puts entry
    #puts entry.attributes
    #id = entry.elements["id"].text
    #title = entry.elements["title"].text
    #puts "- " + entry.attributes["xml:base"] + " #{title} (#{id})"
    puts "- " + entry.attributes["xml:base"]
}
