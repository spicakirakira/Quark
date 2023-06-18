package vazkii.quark.base.module.hint;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * For JEI integration. ItemLike objects with Hint applied in a QuarkModule will automatically
 * be added to JEI Information
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Hint {

	/**
	 * Flag value to check before applying this Hint
	 */
	String value() default "";
	boolean negate() default false;

	/**
	 * Translation key
	 */
	String key() default "";

	/**
	 * Extra content to put in the translation, reference with %s
	 */
	String[] content() default "";

}
