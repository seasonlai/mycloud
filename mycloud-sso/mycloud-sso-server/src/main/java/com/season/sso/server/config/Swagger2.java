package com.season.sso.server.config;

import com.fasterxml.classmate.TypeResolver;
import com.season.sso.server.entity.App;
import com.season.sso.server.entity.Permission;
import com.season.sso.server.entity.Role;
import com.season.sso.server.entity.User;
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

		//防止无限递归
		TypeResolver typeResolver = new TypeResolver();
		AlternateTypeRule[] rules = {
				new AlternateTypeRule(typeResolver.resolve(Role.class),typeResolver.resolve(Object.class)),
				new AlternateTypeRule(typeResolver.resolve(App.class),typeResolver.resolve(Object.class)),
				new AlternateTypeRule(typeResolver.resolve(Permission.class),typeResolver.resolve(Object.class)),
				new AlternateTypeRule(typeResolver.resolve(User.class),typeResolver.resolve(Object.class))
		};

		return new Docket(DocumentationType.SWAGGER_2)
				.groupName("mycloud-upms")
				.apiInfo(apiInfo())
				.alternateTypeRules(rules)
				.select()
				.apis(RequestHandlerSelectors.basePackage("com.season.sso.server.controller"))
				.paths(PathSelectors.any())
				.build()
				.globalOperationParameters(parameters);
	}
	
	private ApiInfo apiInfo() {
		return new ApiInfoBuilder()
				.title("mycloud-sso服务测试")
				.description("单点登录")
//				.contact(new Contact("season","http://www.baidu.com",""))
				.termsOfServiceUrl("http://mycloud.ui")
				.version("1.0")
				.build();
	}

}