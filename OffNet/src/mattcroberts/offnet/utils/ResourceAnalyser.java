package mattcroberts.offnet.utils;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import mattcroberts.offnet.resources.ImageResource;
import mattcroberts.offnet.resources.Resource;
import mattcroberts.offnet.resources.WebResource;

import org.xml.sax.SAXException;



public class ResourceAnalyser {





	private static String getAbsolutePath(String urlStr, String src) {
		if(src.startsWith("/")){
			//relative path
			try {
				URL url = new URL(urlStr);
				return url.getProtocol() + "://" + url.getHost() + src;
			} catch (MalformedURLException e) {
				e.printStackTrace();
				return "";
			}
		}else{
			return src;
		}
	}

	private static void extractTags(String nodeName, String attributeName, Resource resource) {
		String tags[] = resource.getContent().split("\\<" + nodeName);
		
		for(String tag :tags){
			String match = attributeName + "=\"";
			int start = tag.indexOf(match) + match.length();
			if(tag.length() <= start)
				continue;
			
			String src = tag.substring(start, tag.indexOf("\"", start));
		
			src = cleanUrl(src);
			src = getAbsolutePath(resource.getUrl(),src);
			if(isValidPath(src)){
				Resource ir = new ImageResource(src,resource);
				
				resource.addSubResource(ir);
			}
		}
		
	}

	private static boolean isValidPath(String src) {
		try {
			new URL(src);
		} catch (MalformedURLException e) {
			System.out.println("Invalid URL:" + src);
			return false;
		}
		return true;
	}

	private static String cleanUrl(String src) {
		String clean = src.replaceAll("\\\\?\\\\|<>", "_");
		return clean;
	}

	public static String analyseHTML(WebResource webResource)  {
		
		//extractTags("img","src",webResource);
		//extractTags("script","src",webResource);
		//extractTags("a","href",webResource);
		//extractTags("link","href",webResource);
		
		SAXParserFactory spf = SAXParserFactory.newInstance("org.ccil.cowan.tagsoup.jaxp.SAXFactoryImpl", null);
		try {
			SAXParser parser = spf.newSAXParser();
			parser.parse(new ByteArrayInputStream(webResource.getContent().getBytes()), new HTMLHandler(webResource));
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return webResource.getContent();
	}

}
