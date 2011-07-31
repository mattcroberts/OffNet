package mattcroberts.offnet.resources;

import java.net.MalformedURLException;




public class ImageResource extends Resource {

	public ImageResource(String src,Resource parent) {
		super(src);
		
		
			
		try {
			
			this.setResourcePath(DATA_DIR + "/" + this.getShortPath());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
	
}
