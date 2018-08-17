package com.season.movie.admin.config;

import com.fasterxml.classmate.TypeResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.AlternateTypeRule;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableSwagger2
public class Swagger2 {

	@Bean
    public Docket createRestApi() {

		ParameterBuilder authorization = new ParameterBuilder();
		List<Parameter> parameters = new ArrayList<>();
		authorization.name("Authorization").description("user token")
				.modelRef(new ModelRef("string"))
				.parameterType("header")
				.required(false);
		//添加全局默认参数
		parameters.add(authorization.build());

		return new Docket(DocumentationType.SWAGGER_2)
				.groupName("mycloud-movie-admin")
				.apiInfo(apiInfo())
				.select()
				.apis(RequestHandlerSelectors.basePackage("com.season.movie.admin.controller"))
				.paths(PathSelectors.any())
				.build()
				.globalOperationParameters(parameters);
	}
	
	private ApiInfo apiInfo() {
		return new ApiInfoBuilder()
				.title("mycloud-movie-admin服务测试")
				.description("电影管理员版")
//				.contact(new Contact("season","http://www.baidu.com",""))
				.termsOfServiceUrl("http://mycloud.ui")
				.version("1.0")
				.build();
	}

}