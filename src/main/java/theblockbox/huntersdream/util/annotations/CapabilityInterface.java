package theblockbox.huntersdream.util.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * This annotation is just here so that all the normal interfaces don't get
 * confused with interfaces for capabilities
 */
@Retention(RetentionPolicy.CLASS)
@Documented
public @interface CapabilityInterface {

}
