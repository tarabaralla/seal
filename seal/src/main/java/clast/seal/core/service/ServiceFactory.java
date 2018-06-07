package clast.seal.core.service;

import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.Annotated;
import javax.enterprise.inject.spi.InjectionPoint;

import clast.seal.core.service.role.RS_Type;
import clast.seal.core.service.role.RoleService;
import clast.seal.core.service.role.RoleServiceProducer;
import clast.seal.core.service.session.SS_Type;
import clast.seal.core.service.session.SessionService;
import clast.seal.core.service.session.SessionServiceProducer;
import clast.seal.core.service.user.US_Type;
import clast.seal.core.service.user.UserService;
import clast.seal.core.service.user.UserServiceProducer;

public class ServiceFactory {
	
	@Produces
	@SessionServiceProducer
	public SessionService createSessionService(@Any Instance<SessionService> instance, InjectionPoint injectionPoint) {
		Annotated annotated = injectionPoint.getAnnotated();
		SS_Type ssTypeAnnotation = annotated.getAnnotation(SS_Type.class);
		Class<? extends SessionService> sessionService = ssTypeAnnotation.value().getService();
		return instance.select(sessionService).get();
	}
	
	@Produces
	@UserServiceProducer
	public UserService createUserService(@Any Instance<UserService> instance, InjectionPoint injectionPoint) {
		Annotated annotated = injectionPoint.getAnnotated();
		US_Type usTypeAnnotation = annotated.getAnnotation(US_Type.class);
		Class<? extends UserService> userService = usTypeAnnotation.value().getService();
		return instance.select(userService).get();
	}
	
	@Produces
	@RoleServiceProducer
	public RoleService createRoleService(@Any Instance<RoleService> instance, InjectionPoint injectionPoint) {
		Annotated annotated = injectionPoint.getAnnotated();
		RS_Type rsTypeAnnotation = annotated.getAnnotation(RS_Type.class);
		Class<? extends RoleService> roleService = rsTypeAnnotation.value().getService();
		return instance.select(roleService).get();
	}

}
