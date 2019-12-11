package org.ku8eye;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import javax.sql.DataSource;

import org.ku8eye.service.deploy.Ku8InstallTool;
import org.ku8eye.service.image.ImageTool;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.iiot.utils.SystemUtil;

import io.prometheus.client.spring.boot.EnablePrometheusEndpoint;

@EnablePrometheusEndpoint
@SpringBootApplication
@EnableAsync
@ComponentScan(basePackages={"org.ku8eye","com.cudcos","com.iiot"})
public class App extends WebMvcConfigurerAdapter {

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		try {
			Properties props = SystemUtil.getSpringAppProperties();
			String extFile = props.getProperty("ku8.externalRes");
			if (extFile != null) {
				registry.addResourceHandler(Constants.EXTERNAL_URL_ROOT + "/**").addResourceLocations(extFile + "/");
				System.out.println("mapping external resources " + extFile);
				super.addResourceHandlers(registry);
			}
		} catch (IOException e) {
			System.out.println(e);
		}

		super.addResourceHandlers(registry);
	}

	@Bean
	@ConfigurationProperties(prefix = "spring.datasource")
	public DataSource primaryDataSource() {
		System.out.println("-------------------- DataSource init ---------------------");
		return DataSourceBuilder.create().build();
	}
	//iiot项目 dao
	@Bean
	MapperScannerConfigurer mpperScannnerConfigurerIiot() {
		MapperScannerConfigurer msc = new MapperScannerConfigurer();
		msc.setBasePackage("com.iiot.dao");
		return msc;
	}
	//iiot项目 domain
	@Bean(name = "sqlSessionFactory")
	SqlSessionFactoryBean sqlSessionFactoryIiot(DataSource dataSource) {
		SqlSessionFactoryBean ssfb = new SqlSessionFactoryBean();
		ssfb.setDataSource(dataSource);
		ssfb.setTypeAliasesPackage("com.iiot.domain");
		return ssfb;
	}
	@Bean
	MapperScannerConfigurer mpperScannnerConfigurer() {
		MapperScannerConfigurer msc = new MapperScannerConfigurer();
		msc.setBasePackage("org.ku8eye.mapping");
		return msc;
	}

	@Bean(name = "sqlSessionFactory")
	SqlSessionFactoryBean sqlSessionFactory(DataSource dataSource) {
		SqlSessionFactoryBean ssfb = new SqlSessionFactoryBean();
		ssfb.setDataSource(dataSource);
		ssfb.setTypeAliasesPackage("org.ku8eye.domain");
		return ssfb;
	}

	@Bean
	PlatformTransactionManager transactionManager(DataSource dataSource) {
		DataSourceTransactionManager transactionManager = new DataSourceTransactionManager();
		transactionManager.setDataSource(dataSource);
		return transactionManager;
	}

	@Bean
	SchedulerFactoryBean schedulerFactory() {
		SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();
		return schedulerFactoryBean;
	}
	
	private static int findArg(String[] args, String arg) {
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals(arg)) {
				return i;
			}
		}
		return -1;

	}

	public static void main(String[] args) throws Exception {
		int index = findArg(args, "tool");
		if (index > 0) {
			String[] newArgs = new String[args.length - index - 1];
			System.arraycopy(args, index + 1, newArgs, 0, newArgs.length);
			System.out.println(Arrays.toString(newArgs));
			Ku8InstallTool.main(newArgs);
			return;
		}
		index = findArg(args, "image");
		if (index > 0) {
			String[] newArgs = new String[args.length - index - 1];
			System.arraycopy(args, index + 1, newArgs, 0, newArgs.length);
			System.out.println(Arrays.toString(newArgs));
			ImageTool.main(newArgs);
			return;
		}
		SpringApplication app = new SpringApplication(App.class);
		app.setWebEnvironment(true);
		//app.setShowBanner(false);
		Set<Object> set = new HashSet<Object>();
		// set.add("classpath:applicationContext.xml");
		app.setSources(set);
		app.run(args);
	}
}