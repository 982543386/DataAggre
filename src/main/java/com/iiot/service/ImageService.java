package com.iiot.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.iiot.dao.DockerImageMapper;
import com.iiot.domain.DockerImage;
import com.iiot.domain.HarborRepositoryObj;
import com.iiot.domain.ImageResponse;
import com.iiot.domain.RepositoryTags;
import com.iiot.utils.DBOperator;
import com.iiot.utils.DockerRegistry;
import com.iiot.utils.SystemUtil;

/**
 * 
 * @author wuzhi
 *
 */
@Service
public class ImageService {
	private Logger log = Logger.getLogger(this.toString());
	private static final String BASEIMAGE = "google_containers";
	@Autowired
	private DockerImageMapper imgeDao;
	@Autowired
	private ImageRegistry imageRegistry;

	/**
	 * search images
	 * 
	 * @param imageName
	 * @return
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public List<DockerImage> searchImages(String imageName) {
		return imgeDao.selectByImageName("%" + imageName + "%");
	}
	
	/**
	 * search images
	 * 
	 * @param imageName
	 * @return
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public List<DockerImage> searchImagesNew(String imageName,String flag) {
		//return imgeDao.selectByImageName("%" + imageName + "%");
		if (flag !=null && (flag.equals("1")||flag.equals("0"))){
			return imgeDao.selectHarborByImageName("%" + imageName + "%");
		}else if(flag.equals("2")){
			return imgeDao.selectHarborDetailByImageName(imageName);
		}else{
			return imgeDao.selectHarborDetailByImageName("%" + imageName + "%");
		}
		
	}

	/**
	 * select images
	 * 
	 * @param imageName
	 * @return
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public List<DockerImage> selectImages(String category,Integer versionType,Integer source) {
		return imgeDao.selectByCategoryAndVersionTypeAndSource(category,versionType,source);
	}
//	public List<DockerImage> selectImages() {
//		return imgeDao.selectByCategoryAndVersionTypeAndSource();
//	}

	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public DockerImage getImagesId(int dockerId) {
		return imgeDao.selectByPrimaryKey(dockerId);
	}
	
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public DockerImage getImagesName(String key) {
		return imgeDao.selectByName(key);
	}


	@Transactional(propagation = Propagation.REQUIRED)
	public void deleteImagesId(int dockerId) {
		imgeDao.deleteByPrimaryKey(dockerId);
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public void deleteAll() {
		imgeDao.deleteAll();
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public String createImage(DockerImage image) {
		imgeDao.insert(image);
		return "SUCCESS:";
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public String createHarborImage(DockerImage image) {
		imgeDao.insertHarbor(image);
		return "SUCCESS:";
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void updateImage(DockerImage image) {
		imgeDao.updateByPrimaryKey(image);
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public void updateImageTagsCount(DockerImage image) {
		imgeDao.updateByImangeName(image);
	}

	
	/**
	 * 同步镜像库于私库之间的镜像信息
	 * @return
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public ImageResponse syncImage() {
		ImageResponse imageResponse = new ImageResponse(0);
		try {
			log.info("registry sync");
			int clusterId = 0;
			String[] dbArray = DBOperator.getDBInfo(clusterId);
			if (dbArray[0].isEmpty()) {
				String warn = "warn:库表中没有私库信息，请先设置镜像私库";
				log.warn(warn);
				imageResponse.setStatus(1);
				imageResponse.setMessage(warn);
				return imageResponse;
			}
			int registryId = Integer.parseInt(dbArray[0]);
			String registryUrl = dbArray[1];
			//List<RepositoryTags> repositoriesList = new ArrayList<RepositoryTags>();
			List<HarborRepositoryObj> repoList = new ArrayList<HarborRepositoryObj>();
			//List<HarborProjectObj> proList = new ArrayList<HarborProjectObj>();
			DockerRegistry dockerregistry = null;
			try {
				dockerregistry = new DockerRegistry("http://"+registryUrl);
				//repositoriesList = dockerregistry.listAvailableImages();
				repoList = dockerregistry.listHarborImages();
				//proList = map.get("projectInfo");
				//repoList = map.get("repoInfo");
			} catch (Exception e) {
				log.error(e);
				e.printStackTrace();
				imageResponse.setStatus(1);
				imageResponse.setMessage("fail to connect to registry <br/>" + registryUrl);
				return imageResponse;
			} finally {
				if (dockerregistry != null) {
					dockerregistry.close();
				}
			}
			//syncImageReg2DB(repoList,registryUrl,registryId,clusterId);
			deleteAll();
			syncHarborImageReg2DB(repoList,registryUrl,registryId,clusterId);
			//ckDBHarborImage(repoList);
		} catch (Exception e) {
			log.error(e);
			e.printStackTrace();
			imageResponse.setStatus(1);
			imageResponse.setMessage("fail to sync docker image");
		}
		return imageResponse;
	}
	
	/**
	 * 检查db中镜像信息是否存在于私库中，不存在则删除db中的数据
	 * @param repositoriesList
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	private void ckDBHarborImage(List<HarborRepositoryObj> repositoriesList) {
		List<DockerImage> dockerImageList = imgeDao.selectAll();
		for (DockerImage dockerImage : dockerImageList) {
			boolean exist = false;
			for (HarborRepositoryObj repositories2 : repositoriesList) {
				String[] tags = repositories2.getTags();
				for (String tag : tags) {
					tag = tag.trim();
					tag = tag.replace("\"", "");
					if (repositories2.getRepositoryName().equals(
							dockerImage.getImageName())
							&& tag.equals(dockerImage.getVersion())) {
						exist = true;
						break;
					}
				}
				if (exist) {
					break;
				}
			}
			if (!exist) {
				imgeDao.deleteByPrimaryKey(dockerImage.getId());
			}
		}
	}
	

	/**
	 * 检查db中镜像信息是否存在于私库中，不存在则删除db中的数据
	 * @param repositoriesList
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	private void ckDBImage(List<RepositoryTags> repositoriesList) {
		List<DockerImage> dockerImageList = imgeDao.selectAll();
		for (DockerImage dockerImage : dockerImageList) {
			boolean exist = false;
			for (RepositoryTags repositories2 : repositoriesList) {
				String[] tags = repositories2.getTags();
				for (String tag : tags) {
					if (repositories2.getName().equals(
							dockerImage.getImageName())
							&& tag.equals(dockerImage.getVersion())) {
						exist = true;
						break;
					}
				}
				if (exist) {
					break;
				}
			}
			if (!exist) {
				imgeDao.deleteByPrimaryKey(dockerImage.getId());
			}
		}
	}
	
	/**
	 * 检查镜像库中的数据是否存在于db中，不存在则增加
	 * 
	 * @param repositoriesList
	 * @param registryUrl
	 * @param registryId
	 * @param clusterId
	 * @throws Exception
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	private void syncHarborImageReg2DB(List<HarborRepositoryObj> repositoriesList,String registryUrl,int registryId,int clusterId) 
			throws Exception {
		for (HarborRepositoryObj repositories : repositoriesList) {
			String[] tags = repositories.getTags();
			for (String tag : tags) {
				tag = tag.trim();
				tag = tag.replace("\"", "");
				if (!(imageRegistry.existImage(registryUrl,
						repositories.getRepositoryName(), tag))) {
					// 将私库信息插入到数据库中
					DockerImage image = new DockerImage();
					image.setImageUrl(registryUrl);
					image.setImageName(repositories.getRepositoryName());
					image.setVersion(tag);
					image.setRegistryId(registryId);
					image.setClusterId(clusterId);
					image.setStatus(new Byte("0"));// 状态默认为0，代表有效
					image.setAutoBuild(new Byte("0"));
					image.setTitle(repositories.getRepositoryName() + ":" + tag);
					// title默认的起名方法为镜像名字加对应tag名字
					image.setCategory("no define");
					image.setNote("registry Sync");
					image.setLastUpdated(new Date());
					//harbor
					image.setProjectId(repositories.getProjectId());
					image.setProjectName(repositories.getProjectName());
					image.setTagsCount(repositories.getTagsCount());
					image.setPullCount(repositories.getPullCount());
					image.setProjectPublic(repositories.getProjectPublic());
					
					// 设置图片 repositories.getName()
					createHarborImage(image);

				}
			}
		}
		
	}

	/**
	 * 检查镜像库中的数据是否存在于db中，不存在则增加
	 * 
	 * @param repositoriesList
	 * @param registryUrl
	 * @param registryId
	 * @param clusterId
	 * @throws Exception
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	private void syncImageReg2DB(List<RepositoryTags> repositoriesList,String registryUrl,int registryId,int clusterId) 
			throws Exception {
		for (RepositoryTags repositories : repositoriesList) {
			String[] tags = repositories.getTags();
			for (String tag : tags) {
				if (!(imageRegistry.existImage(registryUrl,
						repositories.getName(), tag))) {
					// 将私库信息插入到数据库中
					DockerImage image = new DockerImage();
					image.setImageUrl(registryUrl);
					image.setImageName(repositories.getName());
					image.setVersion(tag);
					image.setRegistryId(registryId);
					image.setClusterId(clusterId);
					image.setStatus(new Byte("0"));// 状态默认为0，代表有效
					image.setAutoBuild(new Byte("0"));
					image.setTitle(repositories.getName() + ":" + tag);
					// title默认的起名方法为镜像名字加对应tag名字
					image.setCategory("no define");
					image.setNote("registry Sync");
					image.setLastUpdated(new Date());
					// 设置图片 repositories.getName()
					if (repositories.getName().indexOf(BASEIMAGE) >= 0) {
						image.setImageIconUrl(SystemUtil
								.getSpringAppProperties().getProperty(
										"ku8.uploadedPicturePath")
								+ "/ku8.png");
					} else {
						image.setImageIconUrl(SystemUtil
								.getSpringAppProperties().getProperty(
										"ku8.uploadedPicturePath")
								+ "/nodef.png");
					}
					createImage(image);

				}
			}
		}
		
	}



}
