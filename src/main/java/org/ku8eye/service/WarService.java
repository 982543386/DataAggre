package org.ku8eye.service;

import org.ku8eye.domain.WarRegistry;
import org.ku8eye.mapping.WarRegistryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 
 * @author wuzhi
 *
 */
@Service
public class WarService {
	@Autowired
	private WarRegistryMapper WarDao;


	/**
	 * Insert war
	 * amlRegistry.setId((byte) 1);
	 * @param imageName
	 * @return
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public int insertContent(String warlocation) {
		WarRegistry warRegistry = new WarRegistry();
		/*warRegistry.setId((byte) 1);*/
		warRegistry.setLocation(warlocation);
		return WarDao.insert(warRegistry);
	}

	/**
	 * select images
	 * 
	 * @param imageName
	 * @return
	 *//*
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public List<DockerImage> selectImages(String category,Integer versionType,Integer source) {
		return YamlDao.selectByCategoryAndVersionTypeAndSource(category,versionType,source);
	}
//	public List<DockerImage> selectImages() {
//		return imgeDao.selectByCategoryAndVersionTypeAndSource();
//	}

	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public DockerImage getImagesId(int dockerId) {
		return YamlDao.selectByPrimaryKey(dockerId);
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void deleteImagesId(int dockerId) {
		YamlDao.deleteByPrimaryKey(dockerId);
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public String createImage(DockerImage image) {
		YamlDao.insert(image);
		return "SUCCESS:";
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void updateImage(DockerImage image) {
		YamlDao.updateByPrimaryKey(image);
	}
*/
	/*
	*//**
	 * 同步镜像库于私库之间的镜像信息
	 * @return
	 *//*
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
			List<RepositoryTags> repositoriesList = new ArrayList<RepositoryTags>();
			DockerRegistry dockerregistry = null;
			try {
				dockerregistry = new DockerRegistry("http://"+registryUrl);
				repositoriesList = dockerregistry.listAvailableImages();
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
			syncImageReg2DB(repositoriesList,registryUrl,registryId,clusterId);
			ckDBImage(repositoriesList);
		} catch (Exception e) {
			log.error(e);
			e.printStackTrace();
			imageResponse.setStatus(1);
			imageResponse.setMessage("fail to sync docker image");
		}
		return imageResponse;
	}

	*//**
	 * 检查db中镜像信息是否存在于私库中，不存在则删除db中的数据
	 * @param repositoriesList
	 *//*
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	private void ckDBImage(List<RepositoryTags> repositoriesList) {
		List<DockerImage> dockerImageList = YamlDao.selectAll();
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
				YamlDao.deleteByPrimaryKey(dockerImage.getId());
			}
		}
	}

	*//**
	 * 检查镜像库中的数据是否存在于db中，不存在则增加
	 * 
	 * @param repositoriesList
	 * @param registryUrl
	 * @param registryId
	 * @param clusterId
	 * @throws Exception
	 *//*
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	private void syncImageReg2DB(List<RepositoryTags> repositoriesList,String registryUrl,int registryId,int clusterId) throws Exception {
		for (RepositoryTags repositories : repositoriesList) {
			String[] tags = repositories.getTags();
			for (String tag : tags) {
				if (!(yamlContent.existImage(registryUrl,
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
*/
}
