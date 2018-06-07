package clast.seal.core.service;

import javax.enterprise.context.SessionScoped;

@SessionScoped
public interface Service {

	abstract String getDescription();

}
