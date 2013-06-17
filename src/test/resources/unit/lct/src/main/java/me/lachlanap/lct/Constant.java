package me.lachlanap.lct;

import java.lang.annotation.*;

/**
 * Specifies a field is a constant.
 * <p/>
 * @author lachlan
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Constant {

    /**
     * The human readable name of the constant.
     */
    public String name();

    /**
     * Use this (optionally) to limit the possible values. Examples include (excluding quotes):
     * <ul>
     * <li>"1,2" - to limit a number to between 1 and 2</li>
     * <li>"1" - to limit a number to be greater that 1</li>
     * <li>",2" - to limit a number to be less that 2</li>
     * </ul>
     * Other possibilities exist for different field types.
     * <p/>
     */
    public String constraints() default "";
}
