package mattcroberts.offnet.resources;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class CSSResource extends Resource {

	public CSSResource(String url, Resource parent) {
		super(url, parent);
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
	}

	private void replaceURLs() {
	}
}
