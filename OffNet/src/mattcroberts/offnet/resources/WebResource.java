package mattcroberts.offnet.resources;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import mattcroberts.offnet.utils.ResourceAnalyser;




public class WebResource extends Resource{

	public WebResource(String url) {
		super(url);
	}
	
	
	public boolean persist()throws IOException{
		File file = new File(this.getResourcePath());
		
		file.getParentFile().mkdirs();
		
		
		String content = this.fetchAsString();
		this.setContent(content);
		this.analyse();
		this.replaceURLs();
			
		FileWriter fw = new FileWriter(file);
		
		fw.write(this.getContent());
		fw.close();
		
		this.persistSubResources();
		return true;
	}
	
	private void persistSubResources() {
		
		for(Resource res : this.getSubResources()){
			
			if(!res.equals(this)){
				try {
					res.persist();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
		}
	}


	private void replaceURLs() {
		
		String content = this.getContent();
		for(Resource res: this.getSubResources()){
			//System.out.println("Replacing:" + res.getUrl() + " With:" + res.getResourcePath());
			content = content.replaceAll("\"" + res.getUrl() + "\"", "\"" + res.getResourcePath() +"\"");			
		}
		
		this.setContent(content);
	}


	protected void analyse(){
		
		ResourceAnalyser.analyseHTML(this);
	}

}
