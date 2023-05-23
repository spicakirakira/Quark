package vazkii.quark.base.module;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Hint {

	/**
	 * Flag value to check before applying this Hint 
	 */
	String value() default "";
	
}
