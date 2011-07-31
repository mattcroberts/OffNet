package mattcroberts.offnet.utils;

import mattcroberts.offnet.resources.WebResource;


public class Utils {

	public static boolean isValidResource(String url) {
		
		if(url == null || url.length() == 0 || url.startsWith("#")){
			return false;
		}
		return true;
	}

	public static boolean isExternalResource(String content,
			WebResource resource) {
		return false;
	}

}
