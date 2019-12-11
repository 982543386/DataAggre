package com.iiot.utils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.iiot.domain.HarborRepositoryObj;
import com.iiot.domain.RepositoryTags;

import io.ku8.docker.registry.FsLayer;
import io.ku8.docker.registry.HTTPCallResult;
import io.ku8.docker.registry.ImageManifest;
import io.ku8.docker.registry.RegistryUtil;
import io.ku8.docker.registry.Repositories;
import io.ku8.docker.registry.SHA256Digit;
import io.ku8.docker.registry.Util;
import io.ku8.docker.registry.V1JsonObj;
import io.ku8.docker.registry.V1LayerJson;

/**
 * docker registry 2.0 client
 * 
 * @author wuzhih
 *
 */
public class DockerRegistry {

	private String EXCEPTION = "connection reset by peer";
	private int MAXRETRY=3;
	private int countRetry=0;
	private String registryURL;
	public String getRegistryURL() {
		return registryURL;
	}

	private CloseableHttpAsyncClient httpclient;
	Logger logger = LoggerFactory.getLogger(DockerRegistry.class);

	private Future<HttpResponse> asynHttpCall(HttpUriRequest request) {
		if (!httpclient.isRunning()) {
			httpclient.start();
		}
		return httpclient.execute(request, null);
	}

	private HTTPCallResult httpCall(HttpUriRequest request, File writeFile) throws Exception {
		Future<HttpResponse> response = asynHttpCall(request);
		HttpResponse httpResp=null;
		try{
			httpResp = response.get(300, TimeUnit.SECONDS);
		}catch(Exception e){
			if(countRetry++<MAXRETRY&&e.getMessage().equalsIgnoreCase(EXCEPTION)){
				logger.warn(e.getMessage()+", retry "+countRetry);
				return httpCall(request, writeFile);
			}else{
				logger.warn(e.getMessage()+" throw");
				throw e;
			}
		}
		HTTPCallResult result = new HTTPCallResult();
		result.setCode(httpResp.getStatusLine().getStatusCode());
		HttpEntity respEntity = httpResp.getEntity();
		Header[] respHead = httpResp.getAllHeaders();
		if(respHead.length>0){
			result.setHeader(respHead);
		}
		if (respEntity != null) {
			String mineType = respEntity.getContentType().getValue();
			if (mineType.startsWith("application/octet-stream") || writeFile != null) {

				Util.writeFile(respEntity.getContent(), writeFile);
				// System.out.println(EntityUtils.toString(respEntity));
			} else {
				result.setContent(EntityUtils.toString(respEntity));
			}
			EntityUtils.consume(respEntity);
		}
		return result;

	}

	public boolean putImageManifest(String imageName, String tagName, File menifestFile) throws Exception {
		final HttpPut request = new HttpPut(registryURL + "/v2/" + imageName + "/manifests/" + tagName);

		HttpEntity fileEntity = EntityBuilder.create().setContentType(ContentType.APPLICATION_JSON)
				.setFile(menifestFile).build();
		request.setEntity(fileEntity);
		Future<HttpResponse> response = asynHttpCall(request);
		HttpResponse httpResp = response.get(300, TimeUnit.SECONDS);
		int retCode = httpResp.getStatusLine().getStatusCode();
		HttpEntity respEntity = httpResp.getEntity();
		if (respEntity != null) {
			if (logger.isDebugEnabled()) {
				logger.debug("put image manifest result " + EntityUtils.toString(respEntity) + " from registry "
						+ this.registryURL);
			}
		}
		return retCode == 201;

	}

	public boolean pullImage(File unzipedImagePath, String imageName, String tag) throws Exception {
		File manifestFile = new File(unzipedImagePath, "manifest.json");
		int retCode = getImageManifest(imageName, tag, manifestFile).getCode();
		if (retCode == 200) {
			byte[] manifestData = Util.readFile(manifestFile);
			ImageManifest manifest = Util.toObject(manifestData, ImageManifest.class);
			for (int i = 0; i < manifest.getHistory().length; i++) {

				V1LayerJson jsonLayer = manifest.getHistory()[i];
				String v1Json = jsonLayer.getV1Compatibility();
				V1JsonObj v1Obj = Util.toObject(v1Json, V1JsonObj.class);
				File layerDir = new File(unzipedImagePath, v1Obj.getId());
				Util.writeFile(new ByteArrayInputStream(v1Json.getBytes("utf-8")), new File(layerDir, "json"));
				String layBlobSum = manifest.getFsLayers()[i].getBlobSum();
				logger.info(" pull image layer " + layBlobSum);
				pullLayer(imageName, layBlobSum, new File(layerDir, "layer.tar"));

			}
			return true;
		} else {
			logger.warn("cant' find image " + imageName + " TAG:" + tag + " in registry " + this.registryURL);
			return false;
		}
	}

	public boolean pushImage(File unzipedImagePath, String imageName, String tag, boolean regenManifest)
			throws Exception {
		if (regenManifest) {
			RegistryUtil.genImageManifestFile(unzipedImagePath, imageName, tag);
		}
		File[] layerFiles = unzipedImagePath.listFiles();
		ArrayList<Future<HttpResponse>> layeruploadResponses = new ArrayList<Future<HttpResponse>>(layerFiles.length);
		boolean havelayer=false;
		for (File theFile : layerFiles) {
			havelayer=true;
			if (theFile.getName().contains(".") || theFile.getName().equals("repositories")) {
				continue;
			}
			File layFile = new File(theFile, "layer.tar");
			String sharDigit = SHA256Digit.hash(layFile);
			String layerDigit = "sha256%3A" + sharDigit;
			FsLayer curLayer = new FsLayer();
			curLayer.setBlobSum("sha256:" + sharDigit);
			if (isLayerExists(imageName, layerDigit)) {
				logger.info("layer exists ,skip it :" + layFile.getParent());
				continue;
			}
			String url = postLayerUpload(imageName);
			logger.info("upload layer "+layFile.getParent()+",url " + url + " to registry " + this.registryURL);
			if(url!=null){
				Future<HttpResponse> httpResp = uploadLayerFile(url, layerDigit, layFile);
				layeruploadResponses.add(httpResp);
			}
		}

		boolean allFinished = false;
		while (!allFinished) {
			allFinished = true;
			for (Future<HttpResponse> httpResp : layeruploadResponses) {
				if (!httpResp.isDone()) {
					allFinished = false;
					break;
				}
			}
			Thread.sleep(1000);
		}
		logger.info("upload image layers Finished ");
		boolean allSuccess = true;
		for (Future<HttpResponse> httpResp : layeruploadResponses) {
			HttpResponse resp = httpResp.get();
			int errCode = resp.getStatusLine().getStatusCode();
			if (errCode != 201) {
				logger.warn("some layer(s) upload failed ");
				allSuccess = false;
				break;
			}
		}
		if (!allSuccess) {
			return false;
		}
		File meinfestFile = new File(unzipedImagePath, "manifest.json");
		logger.info("put image manifest file ");
		boolean suc = putImageManifest(imageName, tag, meinfestFile);
		logger.info(
				"push image " + imageName + " TAG " + tag + (suc ? " success " : " failed ") + " to " + this.registryURL);
		return suc&&havelayer;
	}

	/**
	 * return code 200,means success
	 * 
	 * @param imageName
	 * @param tagName
	 * @return
	 * @throws Exception
	 */
	public HTTPCallResult getImageManifest(String imageName, String tagName, File manifestFile) throws Exception {
		final HttpGet request = new HttpGet(registryURL + "/v2/" + imageName + "/manifests/" + tagName);
		HTTPCallResult result = httpCall(request, manifestFile);
		return result;
	}
	
	public boolean deleteHarborImage(String imageName, String tagName,int tagsCount) throws Exception {
		boolean success = true;
		if(tagsCount > 1){
			success=deleteHarborImageTag(imageName,tagName);
		}else{
			success=deleteHarborImage(imageName,tagName);
		}

		return success;
	}
	
	public boolean deleteHarborImage(String imageName, String tags) throws Exception {
//		DELETE https://10.1.24.90/api/repositories/library%2Fbusytest/tags
		HttpClientUtils util = new HttpClientUtils();
		imageName = imageName.replace("/", "%2F");
		final HTTPCallResult result = 
				util.httpDelete("https://10.1.24.90/api/repositories/"+imageName+"/tags", null, 6000);
		
		if(result.getCode()==404){
			logger.warn("the repository name " + imageName + ", tags " + tags + " had already been deleted or did not exist from "+this.registryURL);
			return false;
		}else if(result.getCode()==202){
			logger.info("the repository name " + imageName + ", tags " + tags + " exists and has been successfully deleted from "+this.registryURL);
			return false;
		}else{
			logger.warn(result.getCode()+"");
			logger.warn(result.getContent());
		}
		return true;
		
	}

	public boolean deleteImage(String imageName, String tagName) throws Exception {
		HTTPCallResult imageManifestCallResult=getImageManifest(imageName, tagName,null);
		Header[] headers = imageManifestCallResult.getHeader();
		String imageManifestDigest="";
		for(Header header:headers){
			if(header.getName().equals("Docker-Content-Digest")){
				imageManifestDigest=header.getValue();
				break;
			}
		}
		if(imageManifestDigest.equals("")){
			logger.warn("the image " + imageName + " TAG " + tagName + " is unknown to the registry "+this.registryURL);
			return true;
		}
		boolean success=deleteImageManifest(imageName,imageManifestDigest);
		if(!success){
			return success;
		}
		//
//		String content = imageManifestCallResult.getContent();
//		ImageManifest manifest = Util.toObject(content, ImageManifest.class);
//		FsLayer[] fsLayers = manifest.getFsLayers();
//		String layer="";
//		//由于删除镜像只是删除REPOSITORY的link并没有删除元数据，所以这里不用判断其他REPOSITORY镜像是否在使用该layer,需要判断自己(REPOSITORY)是否存在其他tag，存在其他tag镜像则有可能使用该layer
//		Repositories repositories = getCatalog();
//		String[] repositoryArray = repositories.getRepositories();
//		for(FsLayer fsLayer:fsLayers){
//			if(layer.contains(fsLayer.getBlobSum())){
//				continue;
//			}
//			boolean isUsed=false;
//			for(String repository:repositoryArray){
//				if(!repository.equals(imageName)){
//					isUsed= isLayerExists(repository, fsLayer.getBlobSum());
//					if(isUsed){
//						break;
//					}
//				}
//			}
//			if(!isUsed){
//				layer+=fsLayer.getBlobSum();
//				int retCode = deleteLayer(imageName, fsLayer.getBlobSum());
//				if(retCode==404){
//					logger.warn("the blob digest "+fsLayer.getBlobSum()+" had already been deleted or did not exist from registry "+this.registryURL);
//				}else if(retCode==202){
//					logger.info("the layer iamge "+imageName+" digest "+fsLayer.getBlobSum()+" exists and has been successfully deleted from registry "+this.registryURL);
//				}else{
//					logger.warn("the layer iamge "+imageName+" digest "+fsLayer.getBlobSum()+" delete fail from registry "+this.registryURL);
//				}
//			}
//			
//		}

		return success;
	}
	
	@SuppressWarnings("unused")
	private int deleteLayer(String imageName, String digest) throws Exception {
		final HttpDelete request = new HttpDelete(registryURL + "/v2/" + imageName + "/blobs/" + digest);
		HTTPCallResult result = httpCall(request, null);
		return result.getCode();
	}
	
	public boolean deleteHarborImageTag(String imageName, String tags) throws Exception {
//		DELETE http://10.1.24.90/api/repositories/library%2Ftomcat/tags/9-jre11
		HttpClientUtils util = new HttpClientUtils();
		imageName = imageName.replace("/", "%2F");
		final HTTPCallResult result = 
				util.httpDelete("https://10.1.24.90/api/repositories/"+imageName+"/tags/"+tags, null, 6000);
		
		if(result.getCode()==404){
			logger.warn("the repository name " + imageName + ", tags " + tags + " had already been deleted or did not exist from "+this.registryURL);
			return false;
		}else if(result.getCode()==202){
			logger.info("the repository name " + imageName + ", tags " + tags + " exists and has been successfully deleted from "+this.registryURL);
			return false;
		}else{
			logger.warn(result.getCode()+"");
			logger.warn(result.getContent());
		}
		return true;
		
	}

	public boolean deleteImageManifest(String imageName, String digest) throws Exception {
//		DELETE /v2/<name>/manifests/<reference>
		final HttpDelete request = new HttpDelete(registryURL + "/v2/" + imageName + "/manifests/" + digest);
		HTTPCallResult result = httpCall(request, null);
		if(result.getCode()==404){
			logger.warn("the manifest " + imageName + " digest " + digest + " had already been deleted or did not exist from "+this.registryURL);
			return true;
		}else if(result.getCode()==202){
			logger.info("the manifest " + imageName + " digest " + digest + " exists and has been successfully deleted from "+this.registryURL);
			return true;
		}else{
			logger.warn(result.getCode()+"");
			logger.warn(result.getContent());
		}
		return false;
		
	}
	
	public String getImageManifestDigest(String imageName, String tagName) throws Exception {
		final HttpGet request = new HttpGet(registryURL + "/v2/" + imageName + "/manifests/" + tagName);
		Future<HttpResponse> response = asynHttpCall(request);
		HttpResponse httpResp = response.get(300, TimeUnit.SECONDS);
		int retCode = httpResp.getStatusLine().getStatusCode();
		String imageManifestDigest="";
		if(retCode==200){
			imageManifestDigest = httpResp.getLastHeader("Docker-Content-Digest").getValue();
		}
		return imageManifestDigest;
	}
	
	public boolean isLayerExists(String imageName, String layerdigest) throws Exception {
		final HttpHead request = new HttpHead(registryURL + "/v2/" + imageName + "/blobs/" + layerdigest);
		HTTPCallResult result = httpCall(request, null);
		return result.getCode() == 200;
	}

	public String postLayerUpload(String imageName) throws Exception {
		final HttpPost request = new HttpPost(registryURL + "/v2/" + imageName + "/blobs/uploads/");
		Future<HttpResponse> response = asynHttpCall(request);
		HttpResponse httpResp = response.get(300, TimeUnit.SECONDS);
		int retCode = httpResp.getStatusLine().getStatusCode();
		if (202 == retCode) {
			return httpResp.getLastHeader("Location").getValue();
		} else {
			logger.error(httpResp.getStatusLine().toString()+" to registry " + this.registryURL);
			return null;
		}
	}

	public Future<HttpResponse> uploadLayerFile(String uploadURL, String layerDigit, File layerFile)
			throws FileNotFoundException {
		// uploadURL=uploadURL.substring(0,uploadURL.indexOf('?'))+"?digest="+layerDigit;
		uploadURL = uploadURL + "&digest=" + layerDigit;
		if (logger.isDebugEnabled()) {
			logger.debug("upload layer file " + uploadURL + " to registry " + this.registryURL);
		}

		final HttpPut request = new HttpPut(uploadURL);
		HttpEntity fileEntity = EntityBuilder.create().setContentType(ContentType.APPLICATION_OCTET_STREAM).chunked()
				.setFile(layerFile).build();
		request.setEntity(fileEntity);
		return asynHttpCall(request);
	}

	public int pullLayer(String imageName, String layerdigest, File layerFile) throws Exception {
		final HttpGet request = new HttpGet(registryURL + "/v2/" + imageName + "/blobs/" + layerdigest);
		HTTPCallResult result = httpCall(request, layerFile);
		return result.getCode();
	}
	
	public HTTPCallResult getHarborImage(String imageName, String tagName) throws Exception {
	       
		HttpClientUtils util = new HttpClientUtils();
		HTTPCallResult result = 
				util.httpGet("https://10.1.24.90/api/search?q=library", null, 6000);
		return result;
	}
	
	public List<HarborRepositoryObj> listHarborImages() throws Exception {
		HTTPCallResult result = getHarborImage("", "");
		
    	String body = result.getContent();
    	com.alibaba.fastjson.JSONObject jSONObject = JSON.parseObject(body);
    	
    	String repo = jSONObject.getString("repository");
    	List<HarborRepositoryObj> repoList = JSON.parseArray(repo, HarborRepositoryObj.class);
    	
    	for (HarborRepositoryObj obj : repoList){
    		String[] tags = getTags(obj);
    		obj.setTags(tags);
    	}
    	//String projectL = jSONObject.getString("project");
    	//List<HarborProjectObj> proArr = JSON.parseArray(projectL, HarborProjectObj.class);
    	
    	//Map<String,List> map = new HashMap<String,List>();
    	//map.put("projectInfo", proArr);
    	//map.put("repoInfo", repoArr);
    	
    	return repoList;
	}
	
	 public  String[] getTags(HarborRepositoryObj obj) throws Exception{
	    	String[] tags = null;
			HttpClientUtils util = new HttpClientUtils();
			String repoName = obj.getRepositoryName();
			if (repoName.contains("/")){
				repoName = repoName.replace("/", "%2F");
			}
			HTTPCallResult result = 
					util.httpGet("https://10.1.24.90/api/repositories/"+repoName+"/tags", null, 6000);
			if (result.getCode()==200){
				int arrSize = Integer.parseInt(obj.getTagsCount());
				String arrStr = result.getContent().trim();
				arrStr = arrStr.replace("[", "");
				arrStr = arrStr.replace("]", "");
				tags = new String[arrSize];
				if (arrSize > 1){
					tags = arrStr.split(",");
				}else{
					tags[0] = arrStr;
				}
			}
			return tags;
		}

	public List<RepositoryTags> listAvailableImages() throws Exception {
		
		List<RepositoryTags> repositoryTags = getTags(getCatalog());
		List<RepositoryTags> availableRepositoryTags = new ArrayList<RepositoryTags>();
		
		for(RepositoryTags repositoryTag : repositoryTags){
			List<String> availableTagList = new ArrayList<String>();
			String[] tags = repositoryTag.getTags();
			if(tags!=null){
				for(String tag:tags){
					HTTPCallResult imageManifestCallResult=getImageManifest(repositoryTag.getName(), tag,null);
					if(imageManifestCallResult.getCode()==200){
						//注释原因：以下代码检测镜像中的layer是否存在，由于检测每一个layer都需要发送一个请求，比较耗费网络，所以注释（一般情况下，manifest存在则镜像就存在）
//						String content = imageManifestCallResult.getContent();
//						ImageManifest manifest = Util.toObject(content, ImageManifest.class);
//						FsLayer[] fsLayers = manifest.getFsLayers();
//						String layer="";
//						boolean exists = true;
//						for(FsLayer fsLayer:fsLayers){
//							if(layer.contains(fsLayer.getBlobSum())){
//								continue;
//							}
//							if(!isLayerExists(repositoryTag.getName(), fsLayer.getBlobSum())){
//								exists=false;
//								break;
//							}
//						}
//						if(exists){
							availableTagList.add(tag);
//						}
					}
					
				}
			}
			if(availableTagList.size()>0){
				RepositoryTags  availablerepositoryTag = new RepositoryTags();
				availablerepositoryTag.setName(repositoryTag.getName());
				availablerepositoryTag.setTags((String[])availableTagList.toArray(new String[availableTagList.size()]));
				availableRepositoryTags.add(availablerepositoryTag);
			}
		}
		return availableRepositoryTags;
	}
	
	public Repositories getCatalog() throws Exception {
		final HttpGet request = new HttpGet(registryURL + "/v2/_catalog");
		HTTPCallResult result = httpCall(request, null);
		Repositories repositories = Util.toObject(result.getContent(), Repositories.class);
		return repositories;
	}
	
	public List<RepositoryTags> getTags(Repositories repositories) throws Exception {
		HttpGet request =null;
		HTTPCallResult result=null;
		List<RepositoryTags> reRepositoryTagList = new ArrayList<RepositoryTags>();
		String[] repositoryArray = repositories.getRepositories();
		for(String repository:repositoryArray){
			request = new HttpGet(registryURL + "/v2/"+repository+"/tags/list");
			result = httpCall(request, null);
			if(result.getCode()==200){
				RepositoryTags repositoryTags = Util.toObject(result.getContent(), RepositoryTags.class);
				reRepositoryTagList.add(repositoryTags);
			}
		}
		return reRepositoryTagList;
	}
	
	public DockerRegistry(String registryURL) {
		this.registryURL = registryURL;
		this.httpclient = HttpAsyncClients.createDefault();
	}

	public void close() {
		try {
			if(this.httpclient!=null&&this.httpclient.isRunning()){
				this.httpclient.close();
				this.httpclient=null;
			}
		} catch (Exception e) {
			logger.warn("close http client err " + e);
		}
	}
}
