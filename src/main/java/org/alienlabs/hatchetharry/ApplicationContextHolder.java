package org.alienlabs.hatchetharry;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;


public final class ApplicationContextHolder implements ApplicationContextAware {

	/**
	 * Contexte Spring qui sera injecte par Spring directement
	 */
	private static ApplicationContext context;

	private ApplicationContextHolder() {
	}

	/**
	 * Méthode de ApplicationContextAware, qui sera appellée automatiquement par
	 * le conteneur
	 */
	@Override
	@edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "ST_WRITE_TO_STATIC_FROM_INSTANCE_METHOD", justification = "Spring requires this for ApplicationContextHolder.getContext().getBean();")
	public void setApplicationContext(final ApplicationContext ctx) throws BeansException {
		ApplicationContextHolder.context = ctx;
	}


	/**
	 * Methode statique pour récupérer le contexte
	 */
	public static ApplicationContext getContext() {
		return ApplicationContextHolder.context;
	}

}
