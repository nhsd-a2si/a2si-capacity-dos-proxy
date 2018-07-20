package com.nhsd.a2si.endpoint.dosproxy;

public class Utils {

	public static String getXmlContent(String xml, String xmlTag, int startingAt) {
		String s = "";
		String xmlEndTag = "</" + xmlTag.substring(1);
		int idxStart = xml.indexOf(xmlTag, startingAt);
		if (idxStart > -1) {
			int idxEnd = xml.indexOf(xmlEndTag, idxStart+1);
			if (idxEnd > -1) {
				s = xml.substring(idxStart+xmlTag.length(), idxEnd);
			}
		}
		return s;
	}

	public static String getXmlContent(String xml, String xmlTag) {
		return getXmlContent(xml, xmlTag, 0);
	}
	
}
