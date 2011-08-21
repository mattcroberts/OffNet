package mattcroberts.offnet.utils;

import java.util.HashMap;

import mattcroberts.offnet.resources.CSSResource;
import mattcroberts.offnet.resources.ImageResource;
import mattcroberts.offnet.resources.ScriptResource;
import mattcroberts.offnet.resources.WebResource;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class HTMLHandler extends DefaultHandler{

	private WebResource resource;
	private HashMap<String, String> elementToAttMap;

	public HTMLHandler(WebResource webResource) {
		this.resource = webResource;
		
		this.elementToAttMap = new HashMap<String,String>();
		this.elementToAttMap.put("a", "href");
		this.elementToAttMap.put("img", "src");
		this.elementToAttMap.put("link", "href");
		this.elementToAttMap.put("script", "src");
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		super.startElement(uri, localName, qName, attributes);
		
		boolean inceptionMode= false;//Hard coded, implement this later
		
		String content = getContent(qName,attributes);
	
		if(!Utils.isValidResource(content)){
			return;
		}else if(qName.equalsIgnoreCase("img")){
			
			this.resource.addSubResource(new ImageResource(content, this.resource));
		}else if(qName.equalsIgnoreCase("link") && attributes.getValue("rel").equalsIgnoreCase("stylesheet")){
			this.resource.addSubResource(new CSSResource(content, this.resource));
		}else if(qName.equalsIgnoreCase("script")){
			this.resource.addSubResource(new ScriptResource(content, this.resource));
		}else if(qName.equalsIgnoreCase("a") && inceptionMode && Utils.isExternalResource(content, resource)){
			
			//Implement inception mode later
			//this.resource.addSubResoure(new WebResource(content, this.resource));
		}
	}

	private String getContent(String qName, Attributes attributes) {
		String attName = this.elementToAttMap.get(qName);
		return attributes.getValue(attName);
	}

}
